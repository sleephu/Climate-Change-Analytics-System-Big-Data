package com.climate.dao;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapColumnTo;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapRowTo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.io.Serializable;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.rdd.RDD;

import com.climate.bean.ClusterRecord;

public class Cluster implements Serializable{
    
    private JavaPairRDD<String, ClusterRecord> yearAverageGSOD(int year)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd");
        Date time = sdf.parse(year + "-01-01");
//        Date end = sdf.parse((year + 1) + "-01-01");
        System.out.println("getting avg");
        JavaPairRDD<String, ClusterRecord> avg = javaFunctions(Driver.getContext())
                .cassandraTable("climate", "average", mapColumnTo(String.class),
                        mapRowTo(ClusterRecord.class))
                .select("station", "dewp", "max", "min", "prcp", "temp")
                .where("time=?", time).cache();
        System.out.println("finished average");
        return avg;
    }

    public Map<String, Integer> kmeansFit(int year,int kmean)
            throws ParseException {       
        JavaPairRDD<String, ClusterRecord> rdd = yearAverageGSOD(year);
        JavaPairRDD<String, Vector> trainingRdd = rdd.mapValues(
                new Function<ClusterRecord, Vector>() {
                    @Override
                    public Vector call(ClusterRecord v1) throws Exception {
                        return Vectors.dense(v1.toVector());
                    }
                }).cache();
        RDD<Vector> trainingSet = trainingRdd.values().rdd();
        KMeansModel model = KMeans.train(trainingSet, kmean, 20, 8);
        double wssse = model.computeCost(trainingSet);
        System.out.println(wssse);
        JavaPairRDD<String, Integer> result = trainingRdd
                .mapValues(new Function<Vector, Integer>() {
                    @Override
                    public Integer call(Vector v1) throws Exception {
                        return model.predict(v1);
                    }
                });        
        return result.collectAsMap();
    }
    

}

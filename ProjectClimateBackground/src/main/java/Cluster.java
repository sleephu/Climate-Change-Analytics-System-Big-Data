import static com.datastax.spark.connector.japi.CassandraJavaUtil.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

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


public class Cluster {
    private static final String host = "ec2-54-174-80-24.compute-1.amazonaws.com";
    private static final String path = "d:/ClimateData/";
    private static SparkConf conf = new SparkConf().set(
            "spark.cassandra.connection.host", host);
    private static JavaSparkContext sc = new JavaSparkContext("local[8]",
            "test", conf);
    private static final SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyy-MM-dd");

    private static JavaPairRDD<String, ClusterRecord> yearAverageGSOD(int year)
            throws ParseException {
        Date start = sdf.parse(year + "-01-01");
        Date end = sdf.parse((year + 1) + "-01-01");
        JavaPairRDD<String, ClusterRecord> avg = javaFunctions(sc)
                .cassandraTable("climate", "gsod", mapColumnTo(String.class),
                        mapRowTo(ClusterRecord.class))
                .select("station", "time","dewp", "max", "min", "prcp", "temp")
                .where("time>=? AND time<?", start, end)
                .reduceByKey(
                        new Function2<ClusterRecord, ClusterRecord, ClusterRecord>() {

                            @Override
                            public ClusterRecord call(ClusterRecord v1,
                                    ClusterRecord v2) throws Exception {
                                // TODO Auto-generated method stub
                                ClusterRecord r = new ClusterRecord();
                                if (v1.getDewp() == null)
                                    System.out.println("v1 is null");
                                if (v2.getDewp() == null)
                                    System.out.println("v2 is null");
                                r.setStation(v1.getStation());
                                r.setTime(start);
                                r.setDewp(v1.getDewp() + v2.getDewp());
                                r.setMax(v1.getMax() + v2.getMax());
                                r.setMin(v1.getMin() + v2.getMin());
                                r.setPrcp(v1.getPrcp() + v2.getPrcp());
                                r.setTemp(v1.getDewp() + v2.getTemp());
                                r.newcount(v1.count() + v2.count());
                                return r;
                            }
                        });
        avg = avg.mapValues(new Function<ClusterRecord, ClusterRecord>() {
            @Override
            public ClusterRecord call(ClusterRecord v1) throws Exception {
                return v1.average();
            }
        });
        javaFunctions(avg.values()).writerBuilder("climate", "average", mapToRow(ClusterRecord.class)).saveToCassandra();
//        for (Entry<String, ClusterRecord> e : avg.collectAsMap().entrySet()) {
//            e.getValue().average();
//            e.getValue().setStation(e.getKey());
//            System.out.println(e.getKey() + " , " + e.getValue());
//        }
        return avg;
    }
    
    private static JavaPairRDD<String, ClusterRecord> yearAverage(int year)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd");
        Date time = sdf.parse(year + "-01-01");
//        Date end = sdf.parse((year + 1) + "-01-01");
        System.out.println("getting avg");
        JavaPairRDD<String, ClusterRecord> avg = javaFunctions(sc)
                .cassandraTable("climate", "average", mapColumnTo(String.class),
                        mapRowTo(ClusterRecord.class))
                .select("station","time", "dewp", "max", "min", "prcp", "temp")
                .where("time=?", time).cache();
        System.out.println("finished average");
        return avg;
    }

    public static Map<String, Integer> kmeansFit(int year)
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
        KMeansModel model = KMeans.train(trainingSet, 5, 20, 8);
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

    // private static Map<String, Tuple2<Station, Integer>> getCoordinate(
    // JavaPairRDD<String, Integer> result) {
    // CassandraJavaPairRDD<String, Station> stationInfo = javaFunctions(sc)
    // .cassandraTable("climate", "stationInfo",
    // mapColumnTo(String.class), mapRowTo(Station.class));
    // Map<String, Tuple2<Station, Integer>> map = stationInfo.join(result)
    // .collectAsMap();
    // for (Entry<String, Tuple2<Station, Integer>> e : map.entrySet()) {
    // System.out.println(e.getKey() + " , " + e.getValue()._1() + " , "
    // + e.getValue()._2());
    // }
    // return map;
    // }

    // public static void main(String[] args) throws ParseException {
    // // int year = 2013;
    // // JavaPairRDD<String, ClusterRecord> avg = yearAverageGSOD(year);
    // // JavaPairRDD<String, Integer> result = kmeansFit(avg);
    // // Map<String, Tuple2<Station, Integer>> map = getCoordinate(result);
    // CassandraJavaPairRDD<String, Station> st = javaFunctions(sc)
    // .cassandraTable("climate", "stationInfo",
    // mapColumnTo(String.class), mapRowTo(Station.class));
    // System.out.println(st.first());
    // }
    
    public static void main(String[] args) throws ParseException {
        System.out.println("start:");
        long s = System.currentTimeMillis();
        kmeansFit(2013);
        long ss = System.currentTimeMillis();
        System.out.println("finished, used: "+(ss-s)+" ms");
    }

}

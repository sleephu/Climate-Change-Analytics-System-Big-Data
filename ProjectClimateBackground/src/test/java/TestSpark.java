import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.cassandra.api.java.JavaCassandraSQLContext;

import com.datastax.spark.connector.japi.CassandraRow;
import com.datastax.spark.connector.japi.rdd.CassandraJavaRDD;


public class TestSpark {
    private static final String host = "ec2-54-174-80-24.compute-1.amazonaws.com";
    private static final String path = "d:/ClimateData/";
//    private static final String[] jars = new String[]{
//        "C:/Users/leonli0326/Dropbox/workspace/simple-client/target/dependency/cassandra-driver-core-2.1.3.jar",
//        "C:/Users/leonli0326/Dropbox/workspace/simple-client/target/dependency/spark-cassandra-connector_2.10-1.1.0.jar",
//        "C:/Users/leonli0326/Dropbox/workspace/simple-client/target/dependency/spark-cassandra-connector-java_2.10-1.1.0.jar"};

    private static void getFirstRow(String station, JavaSparkContext sc) {
        CassandraJavaRDD<CassandraRow> rdd = javaFunctions(sc).cassandraTable(
                "climate", "gsod");
        
        System.out.println(rdd.first().toString());
    }
    
    private static void getRowByStation(String station,JavaCassandraSQLContext cc){
        CassandraJavaRDD<CassandraRow> rdd;
    }

    public static void main(String[] args) {
        SparkConf conf;
        JavaSparkContext sc;
        JavaCassandraSQLContext cc;
        conf = new SparkConf()
                .set("spark.cassandra.connection.host", host);
        conf.set("spark.authenticate", "false");
        conf.set("spark.acls.enable", "true");
        conf.set("spark.admin.acls", "leonli0326");
        sc = new JavaSparkContext("local[8]","test",conf);
        cc = new JavaCassandraSQLContext(sc);
        sc.stop();
    }
}

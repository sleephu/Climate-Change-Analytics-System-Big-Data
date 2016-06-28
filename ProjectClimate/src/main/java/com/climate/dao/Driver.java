package com.climate.dao;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class Driver {
    private static final String host = "ec2-54-174-80-24.compute-1.amazonaws.com";
    private static final String path = "d:/ClimateData/";
    private static Cluster cluster = Cluster.builder().addContactPoint(host)
            .build();
    private static Session session = cluster.connect("climate");
    private static SparkConf conf = new SparkConf().set(
            "spark.cassandra.connection.host", host);
    private static JavaSparkContext sc = new JavaSparkContext("local[4]",
            "test", conf);
    
    public static Session getSession(){
        return session;
    }
    
    public static JavaSparkContext getContext(){
        return sc;
    }
}

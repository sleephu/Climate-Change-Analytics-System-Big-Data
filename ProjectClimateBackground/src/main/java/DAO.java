import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class DAO {
    private static final String host = "ec2-54-174-80-24.compute-1.amazonaws.com";
    private static final String path = "d:/ClimateData/";
    private static Cluster cluster = Cluster.builder().addContactPoint(host)
            .build();
    private static Session session = cluster.connect("climate");
    private static PreparedStatement selectStation = session
            .prepare("SELECT * FROM gsod WHERE station=? AND time>=? AND time<?");
    private static PreparedStatement selectTime = session
            .prepare("SELECT * FROM gsod WHERE time=? ALLOW FILTERING");
    private static final SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyy-MM-dd");

    private static void getDataByStation(String station, List<String> attrbs,
            int startYear, int endYear) throws ParseException,
            FileNotFoundException {
        System.out.println("start query...");
        long ms = System.currentTimeMillis();
        Date start = sdf.parse(String.valueOf(startYear) + "-01-01");
        Date end = sdf.parse(String.valueOf(endYear) + "-01-01");
        // List<ResultSetFuture> futures = new LinkedList<>();
        // while(start.getYear()<=endYear){
        // futures.add(session.executeAsync(select.bind(station,start,end)));
        // start.setYear(start.getYear()+1);
        // end.setYear(end.getYear()+1);
        // }
        List<Row> rows = session.execute(
                selectStation.bind(station, start, end)).all();
        StringBuilder sb = new StringBuilder();
        for (Row row : rows) {
            sb.append(sdf.format(row.getDate("time")));
            for (String attrb : attrbs) {
                sb.append(",").append(row.getFloat(attrb));
            }
            sb.append("\n");
        }
        PrintWriter pw = new PrintWriter(
                new File(path + station + "_query.csv"));
        pw.print(sb);
        pw.close();
        long mss = System.currentTimeMillis();
        System.out.println("query completed, used " + (mss - ms) + " ms");
    }

    private static void getDataByTime(String timeS) throws ParseException,
            FileNotFoundException {
        System.out.println("start query...");
        long ms = System.currentTimeMillis();
        Date time = sdf.parse(timeS);
        List<Row> rows = session.execute(selectTime.bind(time)).all();
        StringBuilder sb = new StringBuilder();
        for (Row row : rows) {
            sb.append(row.getString(0));
            for (int i = 2; i < 15; i++) {
                if (i == 3)
                    continue;
                sb.append(",").append(row.getFloat(i));
            }
            sb.append("\n");
        }
        PrintWriter pw = new PrintWriter(new File(path + timeS + "_query.csv"));
        pw.print(sb);
        pw.close();
        long mss = System.currentTimeMillis();
        System.out.println("query completed, used " + (mss - ms) + " ms");
    }

    private static void getAllStationCoord() throws FileNotFoundException {
        System.out.println("start query...");
        long ms = System.currentTimeMillis();

        List<Row> rows = session.execute(
                "SELECT station,latitude,longitude FROM stationInfo").all();
        StringBuilder sb = new StringBuilder();
        for (Row row : rows) {
            sb.append(row.getString(0));
            for (int i = 1; i < 3; i++) {
                sb.append(",").append(row.getFloat(i));
            }
            sb.append("\n");
        }
        PrintWriter pw = new PrintWriter(new File(path + "allStation.csv"));
        pw.print(sb);
        pw.close();
        long mss = System.currentTimeMillis();
        System.out.println("query completed, used " + (mss - ms) + " ms");
    }

    public static void main(String[] args) throws FileNotFoundException,
            ParseException {
         List<String> l = new ArrayList<String>();
         l.addAll(Arrays.asList("dewp","max","min","temp"));
         getDataByStation("150800", l, 1974, 2014);
//        getDataByTime("2013-01-01");
//        getAllStationCoord();
    }
}

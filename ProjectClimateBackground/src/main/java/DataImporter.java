import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class DataImporter {
    private static final String host = "ec2-54-174-80-24.compute-1.amazonaws.com";
    private static final String path = "d:/ClimateData/";
    private static Cluster cluster = Cluster.builder().addContactPoint(host)
            .build();
    private static Session session = cluster.connect("climate");
    private static final int[] dataIndex = new int[] { 0, 2, 3, 5, 7, 9, 11,
            13, 15, 16, 17, 18, 19, 20, 21 };
    private static final PreparedStatement insert = session
            .prepare("INSERT INTO gsod("
                    + "station, time,temp, dewp, slp, stp, visib, "
                    + "wdsp, mxspd, gust,max, min, prcp, sndp, frshtt) VALUES "
                    + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private static final String[] frshtt = new String[] { "fog", "rain",
            "snow", "hail", "thunder", "tornado" };

    private static BoundStatement bindStatement(String line)
            throws ParseException {
        BoundStatement bound = new BoundStatement(insert);
        String[] token = line.split("\\s+");
        for (int tablei = 0; tablei < dataIndex.length; tablei++) {
            int tokeni = dataIndex[tablei];
            if (token[tokeni].equals("9999.9") || token[tokeni].equals("999.9")) {
                bound.setToNull(tablei);
                continue;
            }
            if (tokeni == 0) {
                bound.setString(tablei, token[tokeni]);
            } else if (tokeni == 2) {
                Date time = sdf.parse(token[tokeni]);
                bound.setDate(tablei, time);
            } else if (tokeni == 21) {
                Set<String> set = new HashSet<String>();
                for (int j = 0; j < 6; j++) {
                    if (token[tokeni].charAt(j) == '1') {
                        set.add(frshtt[j]);
                    }
                }
                if (!set.isEmpty())
                    bound.setSet(tablei, set);
                else
                    bound.setToNull(tablei);
            } else {
                float f = Float.parseFloat(token[tokeni].replaceAll("[^-\\d.]",
                        ""));
                if (tokeni == 19 && f == 99.99)
                    f = 0.0f;
                bound.setFloat(tablei, f);
            }
        }
        return bound;
    }

    private static void readFile(File dir, Session session)
            throws FileNotFoundException, ParseException {
        LinkedList<ResultSetFuture> futures = new LinkedList<ResultSetFuture>();
        int ct = 0;
        for (File f : dir.listFiles()) {
            long timeS = System.currentTimeMillis();
            if (f.getName().matches("999999.*"))
                continue;
            Scanner sc = new Scanner(f);
            sc.nextLine();
            while (sc.hasNext()) {
                ResultSetFuture future = session.executeAsync(bindStatement(sc
                        .nextLine()));
                ct++;
                if(ct>1000){
                    futures.removeFirst();
                }
                futures.add(future);
            }
            sc.close();
            long timeE = System.currentTimeMillis();
            System.out.println("done file: " + f.getName() + " in "
                    + (timeE - timeS) + "ms");
        }
        for (ResultSetFuture future : futures) {
            future.getUninterruptibly();
        }
    }

    private static void execQuery(int year, Session session)
            throws FileNotFoundException, ParseException {
        File dir = new File(path + year);
        long timeS = System.currentTimeMillis();
        System.out.println("start year: " + dir.getName());
        readFile(dir, session);
        long timeE = System.currentTimeMillis();
        System.out.println("end year: " + dir.getName() + ", time: "
                + (timeE - timeS) + "ms");
    }

    public static void main(String[] args) throws FileNotFoundException,
            InterruptedException, ParseException {
        for (int year = 2011; year >= 1930; year--) {
            execQuery(year, session);
        }
        cluster.close();
    }
}

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class StationImporter {
    private static final String host = "ec2-54-174-80-24.compute-1.amazonaws.com";
    private static final String path = "d:/ClimateData/";
    private static Cluster cluster = Cluster.builder().addContactPoint(host)
            .build();
    private static Session session = cluster.connect("climate");
    private static PreparedStatement insert = session
            .prepare("INSERT INTO stationInfo "
                    + "(station,stationName, country,countryName,"
                    + "call,latitude,longitude,elevation) "
                    + "VALUES (?,?,?,?,?,?,?,?)");
    private static final int[] dataIndex = new int[] { 0, 2, 3, 4, 6, 7, 8, 9 };

    private static HashSet<String> getStationSet() throws FileNotFoundException {
        Scanner sc = new Scanner(new File(path + "consistentYear2.csv"));
        HashSet<String> set = new HashSet<String>();
        while (sc.hasNext()) {
            String station = sc.nextLine().substring(0, 6);
            set.add(station);
        }
        System.out.println(set.size());
        sc.close();
        return set;
    }

    // private static HashMap<String, String> getCountry()
    // throws FileNotFoundException {
    // Scanner sc = new Scanner(new File(path + "country-list.txt"));
    // HashMap<String, String> map = new HashMap<>();
    // sc.nextLine();
    // while (sc.hasNext()) {
    // String s = sc.nextLine();
    // String ccode = s.substring(0, 2);
    // String cname = s.substring(2).trim();
    // // String[] country = sc.nextLine().split("\\s+");
    // // assert country.length == 2;
    // // System.out.println("*"+ccode+"*,*"+cname+"*");
    // map.put(ccode, cname);
    // }
    // sc.close();
    // return map;
    // }

    // private static HashMap<String, String> fixCountry()
    // throws FileNotFoundException {
    // Scanner sc = new Scanner(new File(path + "country_map.csv"));
    // HashMap<String, String> map = new HashMap<>();
    // while (sc.hasNext()) {
    // String[] s = sc.nextLine().split(",");
    // map.put(s[0], s[1]);
    // }
    // sc.close();
    // // for (Entry<String, String> e : map.entrySet()) {
    // // System.out.println(e.getKey() + " " + e.getValue());
    // // }
    // return map;
    // }
    //
    // private static void repair() throws FileNotFoundException{
    // HashMap<String, String> countryFix = fixCountry();
    // // boolean b = true;
    // for(Entry<String,String> e:countryFix.entrySet()){
    // // if(e.getKey().equals("PO")) b=false;
    // // if(b) continue;
    // long timeS = System.currentTimeMillis();
    // System.out.println("start...");
    // ResultSet r =
    // session.execute("SELECT station FROM climate.stationInfo WHERE country='"+e.getKey()+"'");
    // List<ResultSetFuture> futures = new ArrayList<ResultSetFuture>();
    // for(Row row:r.all()){
    // ResultSetFuture f =
    // session.executeAsync("UPDATE climate.stationInfo SET country='"+e.getValue()+"' WHERE station='"+row.getString("station")+"'");
    // futures.add(f);
    // }
    // for(ResultSetFuture f:futures){
    // f.getUninterruptibly();
    // }
    // long timeE = System.currentTimeMillis();
    // System.out.println("finished "+e.getKey()+", time: " + (timeE - timeS) +
    // "ms");
    // }
    // System.out.println("finished");
    // }

    private static BoundStatement bindStatement(String line) {
        // HashMap<String, String> countyFix) {
        // line = line.replace("\"", "");
        String[] tokens = line.split(",",10);
//        System.out.print(tokens.length+"---");
        BoundStatement statement = new BoundStatement(insert);
        for (int tablei = 0; tablei < dataIndex.length; tablei++) {
            
            int tokeni = dataIndex[tablei];
            String token = tokens[tokeni].replace("\"", "");
//            System.out.print("*"+token+"*,");
            if (token.isEmpty()) {
                statement.setToNull(tablei);
                continue;
            }
            // if (tablei == 2) {
            // String s = countyFix.get(token)==null?token:countyFix.get(token);
            // statement.setString(tablei,s );
            // System.out.println(s);
            // }
            // if (tablei == 3) {
            // statement.setString(tablei, countryName.get(token));
            // } else
            if (tablei == 5 || tablei == 6) {
                float f = Float.parseFloat(token);
                if ((tablei == 5 && f <= -99998)
                        || (tablei == 6 && f <= -999998))
                    statement.setToNull(tablei);
                else
                    statement.setFloat(tablei, f / 1000);
            } else if (tablei == 7) {
                float f = Float.parseFloat(token);
                if (f <= -99998)
                    statement.setToNull(tablei);
                else
                    statement.setFloat(tablei, f / 10);
            } else {
                statement.setString(tablei, token);
            }
        }
        System.out.println();
        return statement;
    }

    private static void readFile() throws FileNotFoundException {
        // HashMap<String, String> countryName = getCountry();
        // HashMap<String, String> countryFix = fixCountry();
        HashSet<String> stationSet = getStationSet();
        Scanner sc = new Scanner(new File(path + "ish-history.csv"));
        sc.nextLine();
        List<ResultSetFuture> futures = new LinkedList<>();
        while (sc.hasNext()) {
            String line = sc.nextLine();
//            System.out.println(line);
            if (!stationSet.contains(line.substring(0, 6))){
                System.out.println(line.substring(0, 6));
                continue;
            }
            ResultSetFuture future = session.executeAsync(bindStatement(line));
            futures.add(future);
        }
        for (ResultSetFuture future : futures) {
            future.getUninterruptibly();
        }
        sc.close();
    }

    private static void execQuery() throws FileNotFoundException {
        long timeS = System.currentTimeMillis();
        System.out.println("start...");
        readFile();
        long timeE = System.currentTimeMillis();
        System.out.println("finished, time: " + (timeE - timeS) + "ms");
    }

    public static void main(String[] args) throws FileNotFoundException {
        // createCountryCF();
        execQuery();
        // repair();
        // getCountry();
    }

}

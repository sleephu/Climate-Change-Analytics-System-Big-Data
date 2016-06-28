import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.TreeSet;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class Average {
    private static final String host = "ec2-54-174-80-24.compute-1.amazonaws.com";
    private static final String path = "d:/ClimateData/";
    private static Cluster cluster = Cluster.builder().addContactPoint(host)
            .build();
    private static Session session = cluster.connect("climate");
    private static PreparedStatement select = session
            .prepare("SELECT * FROM gsod WHERE station=? AND time>=? AND time<?;");
    private static final PreparedStatement insert = session
            .prepare("INSERT INTO average(" + "station, time,temp, dewp, "
                    + "max, min, prcp) VALUES " + "(?,?,?,?,?,?,?)");
    private static final SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyy-MM-dd");

    private static ResultSetFuture calculateAverage(String station, int year)
            throws ParseException {
        
        Date start = sdf.parse(year + "-01-01");
        Date end = sdf.parse((year + 1) + "-01-01");

        ResultSet r = session.execute(select.bind(station, start, end));
        // Map<String, ClusterRecord> map = new HashMap<String,
        // ClusterRecord>();
        float dwep = 0;
        float max = 0;
        float min = 0;
        float prcp = 0;
        float temp = 0;
        List<Row> rs = r.all();
        if(rs.size()<1) return null;
        for (Row row :rs) {
            // String station = row.getString("station");
            dwep += row.getFloat("dewp");
            max += row.getFloat("max");
            min += row.getFloat("min");
            prcp += row.getFloat("prcp");
            temp += row.getFloat("temp");
        }
        // List<ResultSetFuture> futures = new ArrayList<ResultSetFuture>();
        // for (Entry<String, ClusterRecord> e : map.entrySet()) {
        // ClusterRecord c = e.getValue().average();
        // futures.add(session.executeAsync(insert.bind(e.getKey(), start,
        // c.getTemp(), c.getDewp(), c.getMax(), c.getMin(),
        // c.getPrcp())));
        // }
        // for(ResultSetFuture f:futures){
        // f.getUninterruptibly();
        // }
        ResultSetFuture f = session.executeAsync(insert.bind(station, start,
                temp, dwep, max, min, prcp));
        
        return f;
    }

    public static void main(String[] args) throws ParseException,
            FileNotFoundException {
        Scanner sc = new Scanner(new File(path + "consistentYear2.csv"));
        TreeSet<String> set = new TreeSet<String>();
        while (sc.hasNext()) {
            String station = sc.nextLine().substring(0, 6);
            set.add(station);
        }
        System.out.println(set.size());
        sc.close();
        for (int year = 2013; year > 2003; year--) {
            System.out.println("start query...");
            long ms = System.currentTimeMillis();
            List<ResultSetFuture> futures = new ArrayList<ResultSetFuture>();
            for (String station : set) {
                ResultSetFuture rsf = calculateAverage(station, year);
                if(rsf!=null) futures.add(rsf);
            }
            for (ResultSetFuture f : futures) {
                f.getUninterruptibly();
            }
            long mss = System.currentTimeMillis();
            System.out.println("query completed, used " + (mss - ms) + " ms");
        }

        // calculateAverage(2013);
    }
}

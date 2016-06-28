import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class Cleaner {

    private static final String host = "ec2-54-174-80-24.compute-1.amazonaws.com";
    private static final String path = "d:/ClimateData/";
    private static Cluster cluster = Cluster.builder().addContactPoint(host)
            .build();
    private static Session session = cluster.connect("climate");
    private static PreparedStatement select = session
            .prepare("SELECT * FROM gsod WHERE station=? AND time=?");
    private static PreparedStatement selectAll = session
            .prepare("SELECT * FROM gsod WHERE station=? AND time>=? AND time<?");
    private static PreparedStatement insert = session
            .prepare("INSERT INTO gsod(station,time,dewp,frshtt,gust,max,min,mxspd,prcp,slp,sndp,stp,temp,visib,wdsp)"
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    private static boolean scanYear(String station, int year)
            throws ParseException {
        System.out
                .println("**************************************************");
        System.out.println("scanning year: " + year+" , station: "+station);
        long ms = System.currentTimeMillis();

        Calendar cs = Calendar.getInstance();
        cs.setTime(sdf.parse(String.valueOf(year) + "01" + "01"));
        Calendar ce = (Calendar) cs.clone();
        ce.add(Calendar.YEAR, 1);
        ResultSet rs = session.execute(selectAll.bind(station,cs.getTime(),ce.getTime()));
        List<Row> list = rs.all();
        Set<Date> set = new HashSet<>();
        for(Row r:list){
            set.add(r.getDate("time"));
        }
        List<ResultSetFuture> futures = new LinkedList<>();
        boolean success = true;
        while (cs.get(Calendar.YEAR) == year) {
            if (!set.contains(cs.getTime())) {                
                success = success && repairData(station, cs, futures);
            }
            cs.add(Calendar.DATE, 1);
        }
        for(ResultSetFuture future:futures){
            future.getUninterruptibly();
        }
        long mss = System.currentTimeMillis();
        System.out.println("scan completed: " + (mss - ms) + " ms");
        System.out
                .println("**************************************************");
        return success;
    }

    private static Row selectByTime(String station, Calendar time) {
        ResultSet rs = session.execute(select.bind(station, time.getTime()));
        return rs.one();
    }

    private static boolean repairData(String station, Calendar missingDate, List<ResultSetFuture> futures) {
        System.out.println("repair: " + sdf.format(missingDate.getTime()));
        long ms = System.currentTimeMillis();
        
        Calendar tempB = (Calendar) missingDate.clone();
        tempB.add(Calendar.YEAR, -1);
        Calendar tempA = (Calendar) missingDate.clone();
        tempA.add(Calendar.YEAR, 1);
        Row before = null;
        Row after = null;
        
        for(int i=0;i<10;i++){
            if(before==null){
                before = selectByTime(station, tempB);
                tempB.add(Calendar.YEAR, -1);
            }
            if(after==null){
                after = selectByTime(station, tempA);
                tempA.add(Calendar.YEAR, 1);
            }
            if(before!=null && after!=null)
                break;
        }
        int beforeY = tempB.get(Calendar.YEAR) + 1;
        int afterY = tempA.get(Calendar.YEAR) - 1;        
        
        if (before == null || after == null) {
            long mss = System.currentTimeMillis();
            System.out.println("repair failed using: " + (mss - ms) + " ms");
            return false;
        }

        float wgt = (missingDate.get(Calendar.YEAR) - beforeY)
                / (afterY - beforeY);
        BoundStatement bs = new BoundStatement(insert);
        bs.setString(0, station);
        bs.setDate(1, missingDate.getTime());
        for (int i = 2; i < 15; i++) {
            if (i == 3) {
                bs.setToNull(i);
            } else if (!before.isNull(i) && !after.isNull(i)) {
                float v = (after.getFloat(i) - before.getFloat(i)) * wgt
                        + before.getFloat(i);
                bs.setFloat(i, v);
            } else if (!before.isNull(i)) {
                bs.setFloat(i, before.getFloat(i));
            } else if (!after.isNull(i)) {
                bs.setFloat(i, after.getFloat(i));
            } else {
                bs.setToNull(i);
            }
        }
        ResultSetFuture future = session.executeAsync(bs);
        futures.add(future);
//        System.out.println(bs.preparedStatement().getQueryString());
        long mss = System.currentTimeMillis();
        System.out.println("completed in: " + (mss - ms) + " ms");
        return true;
    }
    
    private static void cleanAll() throws ParseException, FileNotFoundException{
        Scanner sc = new Scanner(new File(path+"requireClean.csv"));
        StringBuffer sb = new StringBuffer();
        while(sc.hasNext()){
            String[] tokens = sc.nextLine().split(",");
            if(tokens.length<=5 || tokens[1].equals("2013")){
                sb.append(tokens[0]+","+tokens[1]+"\n");
                continue;
            }
            String station = tokens[0];
            String consistYear = tokens[1];
            for(int i=1;i<tokens.length-4;i++){
                boolean success = scanYear(station, Integer.parseInt(tokens[i]));
                if(success){
                    consistYear = tokens[i+1];
                }else{
                    consistYear = tokens[i];
                    break;
                }
//                System.out.println("fixing year: "+tokens[i]);
            }
            sb.append(station+","+consistYear+"\n");
        }
        PrintWriter pw = new PrintWriter(path+"consistentYear2.csv");
        pw.print(sb);
        pw.close();
        sc.close();
    }

    public static void main(String[] args) throws IOException, ParseException {
        cleanAll();
    }
}

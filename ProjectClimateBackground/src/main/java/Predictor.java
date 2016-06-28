import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import arimaEst.ARIMAPredictor;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

public class Predictor {
    private static final String host = "ec2-54-174-80-24.compute-1.amazonaws.com";
    private static final String path = "d:/ClimateData/";
    private static Cluster cluster = Cluster.builder().addContactPoint(host)
            .build();
    private static Session session = cluster.connect("climate");
    private static PreparedStatement selectStation = session
            .prepare("SELECT time,dewp,max,min,temp FROM gsod WHERE station=? AND time>=? AND time<?");
    private static PreparedStatement insert = session
            .prepare("INSERT INTO prediction(station,time,dewp,max,min,temp) VALUES (?,?,?,?,?,?)");
    private static final SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyy-MM-dd");
    
    private static List<Row> getDataByStation(String station, int startYear,
            int endYear) throws ParseException, FileNotFoundException, MWException {       
        Date start = sdf.parse(String.valueOf(startYear) + "-01-01");
        Date end = sdf.parse(String.valueOf(endYear) + "-01-01");
        List<Row> rows = session.execute(
                selectStation.bind(station, start, end)).all();
        return rows;       
    }
    
    private static double[] applyARIMA(double[] y) throws MWException{
        ARIMAPredictor arima = new ARIMAPredictor();
        Object[] prediction = arima.arimaEst(1,y,1095.00);
        double[] predictionA = ((MWNumericArray)prediction[0]).getDoubleData();
        return predictionA;
    }
    
    private static void applyPrediction(String station, int startYear,
            int endYear) throws FileNotFoundException, ParseException, MWException{
        List<Row> rows = getDataByStation(station,startYear,endYear);
        int T = rows.size();
        double[] dewp = new double[T];
        double[] temp = new double[T];
        double[] min = new double[T];
        double[] max = new double[T];
        ArrayList<Date> times = new ArrayList<>();
        for (int i=0;i<T;i++) {
            times.add(rows.get(i).getDate("time"));
            dewp[i]=(double)rows.get(i).getFloat("dewp");
            max[i]=(double)rows.get(i).getFloat("max");
            min[i]=(double)rows.get(i).getFloat("min");
            temp[i]=(double)rows.get(i).getFloat("temp");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(times.get(times.size()-1));
        c.add(Calendar.DATE, 1);
        T+=1095;
        dewp=applyARIMA(dewp);
        max=applyARIMA(max);
        min=applyARIMA(min);
        temp=applyARIMA(temp);
        List<ResultSetFuture> futures = new LinkedList<ResultSetFuture>();
        for(int i=0;i<T;i++){
            BoundStatement bs = new BoundStatement(insert);
            bs.setString("station", station);
            if(i<times.size()){
                bs.setDate("time", times.get(i));                
            }else{
                bs.setDate("time", c.getTime());
                c.add(Calendar.DATE, 1);
            }
            bs.setFloat("dewp", (float)dewp[i]);
            bs.setFloat("max", (float)max[i]);
            bs.setFloat("min", (float)min[i]);
            bs.setFloat("temp", (float)temp[i]);
            futures.add(session.executeAsync(bs));
        }
        for(ResultSetFuture future:futures){
            future.getUninterruptibly();
        }
    }
    
    private static void predictAll(String fileName) throws FileNotFoundException, ParseException, MWException{
        Scanner sc = new Scanner(new File(path+fileName));
        boolean b = false;
        while(sc.hasNext()){
            String[] tokens = sc.nextLine().split(",");
            String station = tokens[0];
            int start = Integer.parseInt(tokens[1])+1;
            if(station.equals("724510")) b=true;
            if(start>1971 && start<=1973 && b){
                System.out.println("start station: "+station);
                long ms = System.currentTimeMillis();
                applyPrediction(station,start,2014);
                long mss = System.currentTimeMillis();
                System.out.println(station+" completed, used " + (mss - ms) + " ms");
            }
        }
        sc.close();
    }
    
    public static void main(String[] args) throws FileNotFoundException, ParseException, MWException {
        System.out.println("start prediction...");
        long ms = System.currentTimeMillis();
        
        predictAll("consistentYear2.csv");
        
        long mss = System.currentTimeMillis();
        System.out.println("prediction completed, used " + (mss - ms) + " ms");
    }
}

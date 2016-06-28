import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.lang3.ArrayUtils;

import arimaEst.ARIMAPredictor;

import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;


public class Test {
    // private static final String host =
    // "ec2-54-174-80-24.compute-1.amazonaws.com";
    // private static final String path = "d:/ClimateData/";
    // private static Cluster cluster = Cluster.builder().addContactPoint(host)
    // .build();
    // private static Session session = cluster.connect("climate");
    private static final String path = "C:\\Users\\leonli0326\\Dropbox\\ClimateData\\";

    private static void select() {
        // ResultSet r = session.execute("SELECT * FROM gsod LIMIT 300;");
        // System.out.println(r.one().getString(0));
    }

    private static void arima() throws FileNotFoundException, ParseException, MWException {
        Scanner sc = new Scanner(new File(path + "723440_query.csv"));
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<LocalDate> time = new ArrayList<>();
        ArrayList<Double> temp = new ArrayList<>();
        while (sc.hasNext()) {
            String[] tk = sc.nextLine().split(",");
            time.add(LocalDate.parse(tk[0]));
            temp.add(Double.parseDouble(tk[1]));
        }
        double[] y = ArrayUtils.toPrimitive(temp.toArray(new Double[temp
                                                                     .size()]));
        ARIMAPredictor arima = new ARIMAPredictor();
//        MWNumericArray array = new MWNumericArray(y);
//        Object[] prediction = arima.arimaEst(1,array);
        Object[] prediction = arima.arimaEst(1,y,365.00);
        double[] predictionA = ((MWNumericArray)prediction[0]).getDoubleData();
//        for(Object o:prediction)
//            System.out.println(o);
        
//        double[] ts = ArrayUtils.toPrimitive(temp.toArray(new Double[temp
//                .size()]));
//        Rengine r = new Rengine(new String[10], false, null);
//        int year = time.get(0).getYear();
//        // System.out.println(r.eval("sqrt(36)"));
//        // r.eval("log<-file('D:\\ClimateData\\log.txt')");
//        // r.eval("sink(log, append=TRUE)");
//        // r.eval("sink(log, append=TRUE, type='message')");
//        r.assign("temp", ts);
//        r.eval("require(forecast)");
//        r.eval("tempts<-ts(temp,start=c("+year+",1,1),end=c(2013,12,31),frequency=365)");
//        r.eval("fit1<-stl(tempts, s.window='periodic')");
//         r.eval("plot(fit1)");
//        // r.eval("ee<-seasadj(fit)");
//        r.eval("res<- fit1$time.series[,'remainder']");
//        r.eval("fit2<- arima(res,order=c(2,0,0))");
//        r.eval("p1<- forecast(fit1,h=730)");
//        r.eval("p2<- forecast(fit2,h=730)");
//        r.eval("t1<- p1$mean");
//        r.eval("t2<- p2$mean");
//        r.eval("t3<- t1+t2");
//        r.eval("m3<- tempts-res");
//        double[] prediction = (r.eval("t3")).asDoubleArray();
//        double[] modelFit = (r.eval("m3")).asDoubleArray();
        
        
        
        PrintWriter pw = new PrintWriter(new File(path + "723440_predict.csv"));
        StringBuilder sb = new StringBuilder();
        LocalDate last = time.get(time.size() - 1);
//        last.setTime(time.get(time.size() - 1));
        last.plusDays(1);
        for(int i=0;i<predictionA.length;i++){
            if(i<temp.size()){
                sb.append(time.get(i).toString()).append(",").append(temp.get(i))
                .append(",").append(predictionA[i]).append("\n");
            }else{
                sb.append(last.toString()).append(",,").append(predictionA[i]).append("\n");
                last.plusDays(1);
            }
            
        }
        
//        for (int i = 0; i < temp.size(); i++) {
//            sb.append(sdf.format(time.get(i))).append(",").append(temp.get(i))
//                    .append(",").append(prediction[i]).append("\n");
//        }
//        Calendar last = Calendar.getInstance();
//        last.setTime(time.get(time.size() - 1));
//        last.add(Calendar.DATE, 1);
//        for (double d : prediction){
//            sb.append(sdf.format(last.getTime())).append(",,").append(d).append("\n");
//            last.add(Calendar.DATE, 1);
//        }
        pw.println(sb.toString());
        System.out.println("complete");
        pw.close();
        sc.close();
        // r.eval("fit5 <- Arima(tempts, order=c(2,1,1), xreg=fourier(1:length(tempts),4,365))");
        // r.eval("plot(forecast(fit5, h=2*365, xreg=fourier(length(tempts)+1:(2*365),4,365)))");
        // r.eval("fit2<-auto.arima(tempts,D=1,max.p=3,max.P=2, max.Q=2, max.order=5,max.d=2, max.D=1,stationary=FALSE,seasonal=TRUE,)");
        // r.eval("plot(forecast(fit2,h=365))");
        // r.eval("plot(fit)");
        // r.eval("plot(temp)");

        // MADecomposition decp = new MADecomposition(ts, 10, 365);
        // double[] random = decp.getRandom();
        // double[] seasonal = decp.getSeasonal();
        // double[] trend = decp.getTrend();
        // for(int i=0;i<random.length;i++){
        // System.out.println("r:"+random[i]+"  s:"+seasonal[i]+"  t:"+trend[i]);
        // }
    }

    public static void main(String[] args) throws FileNotFoundException,
            ParseException, MWException {
        // ARIMAModel a = new ARIMAModel(new double[]{1.0,365.0}, 1, new
        // double[]{1.0,365.0});
        arima();
        // System.out.println(System.getProperty("java.library.path"));
        // select();
    }
}

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

public class Identifier {
    
    private static int countLine(File f) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(f));
        int i=0;
        while(br.readLine()!=null) i++;
        br.close();
        return i;
    }
    
    private static void yearRecord(String path) throws FileNotFoundException{
        int[] cts = new int[81];
        int[] nts = new int[81];
        for(int year = 2009;year>=1929;year--){
            File dir = new File(path+year);
            int ct = 0;
            int nt = 0;
            for(File f:dir.listFiles()){
                String k = f.getName().substring(0, 6);
                if(!k.equals("999999")) ct++;
                else nt++;
            }
            cts[2009-year]=ct;
            nts[2009-year]=nt;
        }
        File result = new File(path+"yearRecord.csv");
        PrintWriter pw = new PrintWriter(result);
        for(int i=0;i<81;i++){
            pw.println((2009-i)+","+cts[i]+","+nts[i]);
        }
        pw.close();
    }
    
    private static void outputLine(String path) throws IOException{
        HashMap<String, int[]> map = new HashMap<String, int[]>();
        for(int i=0;i<=85;i++){
            int year = 2013-i;
            File dir = new File(path+year);
            for(File f:dir.listFiles()){
                String k = f.getName().substring(0, 6);
                int lc = countLine(f);
                if(k.equals("999999")) continue;
                if(!map.containsKey(k)) map.put(k, new int[81]);
                map.get(k)[i]=lc;
            }
            System.out.println(year+" finished");
        }
        File result = new File(path+"lineTable2.csv");
        PrintWriter pw = new PrintWriter(result);
        for(Entry<String, int[]> entry:map.entrySet()){
            StringBuilder sb = new StringBuilder(entry.getKey());
            for(int lc:entry.getValue()) sb.append(",").append(lc);
            pw.println(sb);
        }
        pw.close();
    }
    
    private static void countID(int yearLimit, int dayLimit, String path) throws FileNotFoundException{
        File rec = new File(path+"lineTable2.csv");
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        Scanner sc = new Scanner(rec);
        while(sc.hasNext()){
            String[] line = sc.nextLine().split(",");
            String id = "000000".substring(line[0].length())+line[0];
            int ct = 0;
            for(int i=1;i<=85;i++){
                int days = Integer.parseInt(line[i]);
                if(days<dayLimit)
                    ct++;
                else
                    ct=0;
                if(ct>yearLimit){
                    map.put(id, 2014-i);
                    break;
                }
            }
            
        }
        List<Entry<String,Integer>> l = new ArrayList<>(map.entrySet());
        Collections.sort(l,new Comparator<Entry<String,Integer>>() {

            @Override
            public int compare(Entry<String,Integer> a, Entry<String,Integer> b) {
                // TODO Auto-generated method stub
                if(a.getValue()==b.getValue())
                    return a.getKey().compareTo(b.getKey());
                else
                    return a.getValue()-b.getValue();
            }
        });
        PrintWriter pw = new PrintWriter(new File(path+"countID.csv"));
        StringBuffer sb = new StringBuffer();
        for(Entry<String,Integer> e:l){
            sb.append(e.getKey()).append(",").append(e.getValue()).append("\n");
            System.out.println(e.getKey());
        }
        pw.println(sb.toString());
        pw.close();
        sc.close();
    }
    
    private static void requireClean(String path) throws FileNotFoundException{
        File rec = new File(path+"lineTable2.csv");
        HashMap<String, List<Integer>> map = new HashMap<>();
        Scanner sc = new Scanner(rec);
        while(sc.hasNext()){
            String[] line = sc.nextLine().split(",");
            String id = "000000".substring(line[0].length())+line[0];
            List<Integer> lint = new ArrayList<Integer>();
            int ct = 0;
            for(int year = 2013;year>=1929;year--){
                int days = Integer.parseInt(line[2014-year]);
                if(days<(year%4==0?367:366)){
                    lint.add(year);
                    ct++;
                }else{
                    ct=0;
                }
                if(ct>3){
                    map.put(id, lint);
                    break;
                }
            }
            
        }
        List<Entry<String,List<Integer>>> l = new ArrayList<>(map.entrySet());
        Collections.sort(l,new Comparator<Entry<String,List<Integer>>>() {

            @Override
            public int compare(Entry<String,List<Integer>> a, Entry<String,List<Integer>> b) {
                // TODO Auto-generated method stub
                if(a.getValue()==b.getValue())
                    return a.getKey().compareTo(b.getKey());
                else
                    return a.getValue().size()-b.getValue().size();
            }
        });
        PrintWriter pw = new PrintWriter(new File(path+"requireClean.csv"));
        StringBuffer sb = new StringBuffer();
        for(Entry<String,List<Integer>> e:l){
            sb.append(e.getKey());
            for(int i:e.getValue()) sb.append(",").append(i);
            sb.append("\n");
        }
        pw.println(sb.toString());
        pw.close();
        sc.close();
    }
    
    public static void main(String[] args) throws IOException {
//        outputLine("d:/ClimateData/");
//        countID(2,350,"d:/ClimateData/");
        requireClean("d:/ClimateData/");
    }
}

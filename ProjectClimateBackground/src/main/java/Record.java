import java.io.Serializable;
import java.util.Date;
import java.util.Set;


public class Record implements Serializable{
    private String station = null;
    private Date time = null;
    private Double dewp=0.0;
    private Double gust=0.0;
    private Double max=0.0;
    private Double min=0.0;
    private Double mxspd=0.0;
    private Double prcp=0.0;
    private Double slp=0.0;
    private Double sndp=0.0;
    private Double stp=0.0;
    private Double temp=0.0;
    private Double visib=0.0;
    private Double wdsp=0.0;
    private int count = 1;
    private Set<String> frshtt = null;
    
    public Record(String station, Date time, Double dewp, Double max,
            Double min, Double prcp, Double temp) {
        super();
        this.station = station;
        this.time = time;
        this.dewp = dewp;
        this.max = max;
        this.min = min;
        this.prcp = prcp;
        this.temp = temp;
    }

    public Record(String station, Date time, Double dewp, Double gust,
            Double max, Double min, Double mxspd, Double prcp, Double slp,
            Double sndp, Double stp, Double temp, Double visib, Double wdsp,
            Set<String> frshtt) {
        super();
        this.station = station;
        this.time = time;
        this.dewp = dewp;
        this.gust = gust;
        this.max = max;
        this.min = min;
        this.mxspd = mxspd;
        this.prcp = prcp;
        this.slp = slp;
        this.sndp = sndp;
        this.stp = stp;
        this.temp = temp;
        this.visib = visib;
        this.wdsp = wdsp;
        this.frshtt = frshtt;
    }

    public Record() {
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Double getDewp() {
        return dewp;
    }

    public void setDewp(Double dewp) {
        if(dewp==null)
            return;
        this.dewp = dewp;
    }

    public Double getGust() {
        return gust;
    }

    public void setGust(Double gust) {
        if(gust==null)
            return;
        this.gust = gust;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        if(max==null)
            return;
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        if(min==null)
            return;
        this.min = min;
    }

    public Double getMxspd() {
        return mxspd;
    }

    public void setMxspd(Double mxspd) {
        if(mxspd==null)
            return;
        this.mxspd = mxspd;
    }

    public Double getPrcp() {
        return prcp;
    }

    public void setPrcp(Double prcp) {
        if(prcp==null)
            return;
        this.prcp = prcp==99.99?0:prcp;
    }

    public Double getSlp() {
        return slp;
    }

    public void setSlp(Double slp) {
        if(slp==null)
            return;
        this.slp = slp;
    }

    public Double getSndp() {
        return sndp;
    }

    public void setSndp(Double sndp) {
        if(sndp==null)
            return;
        this.sndp = sndp;
    }

    public Double getStp() {
        return stp;
    }

    public void setStp(Double stp) {
        if(stp==null)
            return;
        this.stp = stp;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        if(temp==null)
            return;
        this.temp = temp;
    }

    public Double getVisib() {
        return visib;
    }

    public void setVisib(Double visib) {
        if(visib==null)
            return;
        this.visib = visib;
    }

    public Double getWdsp() {
        return wdsp;
    }

    public void setWdsp(Double wdsp) {
        if(wdsp==null)
            return;
        this.wdsp = wdsp;
    }

    public Set<String> getFrshtt() {
        return frshtt;
    }

    public void setFrshtt(Set<String> frshtt) {
        this.frshtt = frshtt;
    }

    public int count() {
        return count;
    }

    public void newcount(int count) {
        this.count = count;
    }
    
    public Record average(){
        this.dewp = dewp/count;
        this.gust = gust/count;
        this.max = max/count;
        this.min = min/count;
        this.mxspd = mxspd/count;
        this.prcp = prcp/count;
        this.slp = slp/count;
        this.sndp = sndp/count;
        this.stp = stp/count;
        this.temp = temp/count;
        this.visib = visib/count;
        this.wdsp = wdsp/count;
        this.count=1;
        return this;
    }
    
    public double[] toVector(){
        return new double[]{dewp,max,min,prcp,temp};
    }

    @Override
    public String toString() {
        return "Record [station=" + station + ", time=" + time + ", dewp="
                + dewp + ", gust=" + gust + ", max=" + max + ", min=" + min
                + ", mxspd=" + mxspd + ", prcp=" + prcp + ", slp=" + slp
                + ", sndp=" + sndp + ", stp=" + stp + ", temp=" + temp
                + ", visib=" + visib + ", wdsp=" + wdsp + ", count=" + count
                + ", frshtt=" + frshtt + "]";
    }
    
}

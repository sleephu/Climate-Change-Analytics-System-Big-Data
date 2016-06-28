import java.io.Serializable;
import java.util.Date;

public class ClusterRecord implements Serializable {
    private String station = null;
    private Date time;
    private Double dewp = 0.0;
    private Double max = 0.0;
    private Double min = 0.0;
    private Double prcp = 0.0;
    private Double temp = 0.0;
    private int count = 1;

    public ClusterRecord(String station, Double dewp, Double max, Double min,
            Double prcp, Double temp) {
        super();
        this.station = station;
        this.dewp = dewp;
        this.max = max;
        this.min = min;
        this.prcp = prcp;
        this.temp = temp;
        this.count = 1;
    }

    public ClusterRecord() {
        station = null;
        dewp = 0.0;
        max = 0.0;
        min = 0.0;
        prcp = 0.0;
        temp = 0.0;
        count = 1;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public Double getDewp() {
        if (dewp == null)
            return 0.0;
        return dewp;
    }

    public void setDewp(Double dewp) {
        if (dewp == null)
            return;
        this.dewp = dewp;
    }

    public Double getMax() {
        if (max == null)
            return 0.0;
        return max;
    }

    public void setMax(Double max) {
        if (max == null)
            return;
        this.max = max;
    }

    public Double getMin() {
        if (min == null)
            return 0.0;
        return min;
    }

    public void setMin(Double min) {
        if (min == null)
            return;
        this.min = min;
    }

    public Double getPrcp() {
        if (prcp == null)
            return 0.0;
        return prcp;
    }

    public void setPrcp(Double prcp) {
        if (prcp == null)
            return;
        this.prcp = prcp;
    }

    public Double getTemp() {
        if (temp == null)
            return 0.0;
        return temp;
    }

    public void setTemp(Double temp) {
        if (temp == null)
            return;
        this.temp = temp;
    }

    public int count() {
        return count;
    }

    public void newcount(int count) {
        this.count = count;
    }

    public ClusterRecord average() {
        this.dewp = getDewp() / count;
        this.max = getMax() / count;
        this.min = getMin() / count;
        this.prcp = getPrcp() / count;
        this.temp = getTemp() / count;
        this.count = 1;
        return this;
    }

    public double[] toVector() {
        return new double[] { getDewp(), getMax(), getMin(), getPrcp(),
                getTemp() };
    }

    public ClusterRecord add(ClusterRecord r) {
        this.dewp += dewp;
        this.max += max;
        this.min += min;
        this.prcp += prcp;
        this.temp += temp;
        this.count += 1;
        return this;
    }

    @Override
    public String toString() {
        return "ClusterRecord [station=" + station + ", dewp=" + dewp
                + ", max=" + max + ", min=" + min + ", prcp=" + prcp
                + ", temp=" + temp + ", count=" + count + "]";
    }

}

package com.climate.bean;

import java.util.Date;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;

@Table(keyspace="climate",name="prediction")
public class Prediction {
    @PartitionKey
    private String station = null;
    private Date time;
    private Float dewp = 0.0f;
    private Float max = 0.0f;
    private Float min = 0.0f;
    private Float prcp = 0.0f;
    private Float temp = 0.0f;
    @Transient
    private int count = 1;

    public Prediction(String station, Float dewp, Float max, Float min,
            Float prcp, Float temp) {
        super();
        this.station = station;
        this.dewp = dewp;
        this.max = max;
        this.min = min;
        this.prcp = prcp;
        this.temp = temp;
        this.count = 1;
    }

    public Prediction() {
        station = null;
        dewp = 0.0f;
        max = 0.0f;
        min = 0.0f;
        prcp = 0.0f;
        temp = 0.0f;
        count = 1;
    }

    public String getStation() {
        return station;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public Float getDewp() {
        if(dewp==null)
            return 0.0f;
        return dewp;
    }

    public void setDewp(Float dewp) {
        if(dewp==null)
            return;
        this.dewp = dewp;
    }

    public Float getMax() {
        if(max==null)
            return 0.0f;
        return max;
    }

    public void setMax(Float max) {
        if(max==null)
            return;
        this.max = max;
    }

    public Float getMin() {
        if(min==null)
            return 0.0f;
        return min;
    }

    public void setMin(Float min) {
        if(min==null)
            return;
        this.min = min;
    }

    public Float getPrcp() {
        if(prcp==null)
            return 0.0f;
        return prcp;
    }

    public void setPrcp(Float prcp) {
        if(prcp==null)
            return;
        this.prcp = prcp;
    }

    public Float getTemp() {
        if(temp==null)
            return 0.0f;
        return temp;
    }

    public void setTemp(Float temp) {
        if(temp==null)
            return;
        this.temp = temp;
    }

    public int count() {
        return count;
    }

    public void newcount(int count) {
        this.count = count;
    }

    public Prediction average() {
        this.dewp = getDewp() / count;
        this.max = getMax() / count;
        this.min = getMin() / count;
        this.prcp = getPrcp() / count;
        this.temp = getTemp() / count;
        this.count = 1;
        return this;
    }

    public double[] toVector() {
        return new double[] { getDewp(), getMax(), getMin(), getPrcp(), getTemp() };
    }

    @Override
    public String toString() {
        return "ClusterRecord [station=" + station + ", dewp=" + dewp
                + ", max=" + max + ", min=" + min + ", prcp=" + prcp
                + ", temp=" + temp + ", count=" + count + "]";
    }

}

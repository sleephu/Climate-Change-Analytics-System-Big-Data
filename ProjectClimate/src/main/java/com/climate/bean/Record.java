package com.climate.bean;


import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;

@Table(keyspace="climate",name="gsod")
public class Record implements Serializable{
    @PartitionKey
    private String station = null;
    private Date time = null;
    private Float dewp=0.0f;
    private Float gust=0.0f;
    private Float max=0.0f;
    private Float min=0.0f;
    private Float mxspd=0.0f;
    private Float prcp=0.0f;
    private Float slp=0.0f;
    private Float sndp=0.0f;
    private Float stp=0.0f;
    private Float temp=0.0f;
    private Float visib=0.0f;
    private Float wdsp=0.0f;
    @Transient
    private int count = 1;
    private Set<String> frshtt = null;
    
    public Record(String station, Date time, Float dewp, Float max,
            Float min, Float prcp, Float temp) {
        super();
        this.station = station;
        this.time = time;
        this.dewp = dewp;
        this.max = max;
        this.min = min;
        this.prcp = prcp;
        this.temp = temp;
    }

    public Record(String station, Date time, Float dewp, Float gust,
            Float max, Float min, Float mxspd, Float prcp, Float slp,
            Float sndp, Float stp, Float temp, Float visib, Float wdsp,
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

    public Float getDewp() {
        return dewp;
    }

    public void setDewp(Float dewp) {
        if(dewp==null)
            return;
        this.dewp = dewp;
    }

    public Float getGust() {
        return gust;
    }

    public void setGust(Float gust) {
        if(gust==null)
            return;
        this.gust = gust;
    }

    public Float getMax() {
        return max;
    }

    public void setMax(Float max) {
        if(max==null)
            return;
        this.max = max;
    }

    public Float getMin() {
        return min;
    }

    public void setMin(Float min) {
        if(min==null)
            return;
        this.min = min;
    }

    public Float getMxspd() {
        return mxspd;
    }

    public void setMxspd(Float mxspd) {
        if(mxspd==null)
            return;
        this.mxspd = mxspd;
    }

    public Float getPrcp() {
        return (Math.abs(prcp-99.99)<=0.1)?0:prcp;
    }

    public void setPrcp(Float prcp) {
        if(prcp==null)
            return;
        this.prcp = (Math.abs(prcp-99.99)<=0.1)?0:prcp;
    }

    public Float getSlp() {
        return slp;
    }

    public void setSlp(Float slp) {
        if(slp==null)
            return;
        this.slp = slp;
    }

    public Float getSndp() {
        return sndp;
    }

    public void setSndp(Float sndp) {
        if(sndp==null)
            return;
        this.sndp = sndp;
    }

    public Float getStp() {
        return stp;
    }

    public void setStp(Float stp) {
        if(stp==null)
            return;
        this.stp = stp;
    }

    public Float getTemp() {
        return temp;
    }

    public void setTemp(Float temp) {
        if(temp==null)
            return;
        this.temp = temp;
    }

    public Float getVisib() {
        return visib;
    }

    public void setVisib(Float visib) {
        if(visib==null)
            return;
        this.visib = visib;
    }

    public Float getWdsp() {
        return wdsp;
    }

    public void setWdsp(Float wdsp) {
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
    
    public Float[] toVector(){
        return new Float[]{dewp,max,min,prcp,temp};
    }
    
    public Record add(Record r){
        this.dewp += dewp;
        this.gust += gust;
        this.max += max;
        this.min += min;
        this.mxspd += mxspd;
        this.prcp += prcp;
        this.slp += slp;
        this.sndp += sndp;
        this.stp += stp;
        this.temp += temp;
        this.visib += visib;
        this.wdsp += wdsp;
        this.count+=1;
        return this;
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

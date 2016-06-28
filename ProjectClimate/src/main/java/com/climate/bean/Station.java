package com.climate.bean;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace="climate",name="stationInfo")
public class Station {
    @PartitionKey
    private String station;
    private String stationName;
    private String country;
    private String countryName;
    private String call;
    private Float latitude;
    private Float longitude;
    private Float elevation;
    
    public Station(String station, String stationName, String country,
            String countryName, String call, Float latitude,
            Float longtitude, Float elevation) {
        super();
        this.station = station;
        this.stationName = stationName;
        this.country = country;
        this.countryName = countryName;
        this.call = call;
        this.latitude = latitude;
        this.longitude = longtitude;
        this.elevation = elevation;
    }

    public Station() {
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longtitude) {
        this.longitude = longtitude;
    }

    public Float getElevation() {
        return elevation;
    }

    public void setElevation(Float elevation) {
        this.elevation = elevation;
    }

    @Override
    public String toString() {
        return "Station [station=" + station + ", stationName=" + stationName
                + ", country=" + country + ", countryName=" + countryName
                + ", call=" + call + ", latitude=" + latitude + ", longtitude="
                + longitude + ", elevation=" + elevation + "]";
    }
}

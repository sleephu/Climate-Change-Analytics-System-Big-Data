import java.io.Serializable;


public class Station implements Serializable{
//station,stationName, country,countryName,"
//        + "call,latitude,longitude,elevation)
    private String station;
    private String stationName;
    private String country;
    private String countryName;
    private String call;
    private Double latitude;
    private Double longtitude;
    private Double elevation;
    
    public Station(String station, String stationName, String country,
            String countryName, String call, Double latitude,
            Double longtitude, Double elevation) {
        super();
        this.station = station;
        this.stationName = stationName;
        this.country = country;
        this.countryName = countryName;
        this.call = call;
        this.latitude = latitude;
        this.longtitude = longtitude;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }

    public Double getElevation() {
        return elevation;
    }

    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }

    @Override
    public String toString() {
        return "Station [station=" + station + ", stationName=" + stationName
                + ", country=" + country + ", countryName=" + countryName
                + ", call=" + call + ", latitude=" + latitude + ", longtitude="
                + longtitude + ", elevation=" + elevation + "]";
    }
}

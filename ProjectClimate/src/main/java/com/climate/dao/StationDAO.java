package com.climate.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.climate.bean.Record;
import com.climate.bean.Station;
import com.datastax.driver.core.Row;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

public class StationDAO {
    @Accessor
    public interface StationAccessor {
        @Query("SELECT * FROM climate.stationInfo")
        public Result<Station> getAllStation();
        
        @Query("SELECT * FROM climate.stationInfo WHERE country=?")
        public Result<Station> getStationByCountry(String country);
        
        @Query("SELECT * FROM climate.stationInfo WHERE station=?")
        public Result<Station> getStation(String station);
    }
    
    StationAccessor accessor = new MappingManager(Driver.getSession())
    .createAccessor(StationAccessor.class);
    
    public List<Station> getAllStation() {
        Result<Station> result = accessor.getAllStation();
        return result.all();
    }
    
    public Map<String,Integer> getCountryCount(){
        List<Row> result = Driver.getSession().execute("SELECT country FROM climate.stationInfo;").all();
        Map<String,Integer> map = new HashMap<String, Integer>();
        for(Row r:result){
            String s = r.getString(0);
            if(s!=null)
                map.put(s, map.get(s)==null?1:map.get(s)+1);
        }
        return map;
    }
    
    public List<Station> getStationByCountry(String country) {
        Result<Station> result = accessor.getStationByCountry(country);
        return result.all();
    } 
    
    public Station getStation(String station) {
        Result<Station> result = accessor.getStation(station);
        return result.one();
    }   
    
}

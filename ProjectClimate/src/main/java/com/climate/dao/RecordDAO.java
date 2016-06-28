package com.climate.dao;

import java.util.Date;
import java.util.List;

import com.climate.bean.Record;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

public class RecordDAO {

    @Accessor
    public interface RecordAccessor {
        @Query("SELECT * FROM climate.gsod WHERE station=? AND time>=? AND time<? ALLOW FILTERING")
        public Result<Record> getRecordByStation(String station, Date start,
                Date end);
        
        @Query("SELECT * FROM climate.gsod WHERE time>=? AND time<?  ALLOW FILTERING")
        public Result<Record> getRecordByYear(Date start,
                Date end);
    }
    
    RecordAccessor accessor = new MappingManager(Driver.getSession())
            .createAccessor(RecordAccessor.class);

    public List<Record> getRecordByStation(String station, Date start, Date end) {
        Result<Record> result = accessor
                .getRecordByStation(station, start, end);
        return result.all();
    }
    
    public List<Record> getRecordByYear(Date start, Date end) {
        Result<Record> result = accessor
                .getRecordByYear(start, end);
        return result.all();
    }

}

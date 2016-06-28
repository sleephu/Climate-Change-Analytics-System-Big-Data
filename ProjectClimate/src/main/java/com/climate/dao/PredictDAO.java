package com.climate.dao;

import java.util.Date;
import java.util.List;

import com.climate.bean.ClusterRecord;
import com.climate.bean.Prediction;
import com.climate.bean.Record;
import com.climate.dao.RecordDAO.RecordAccessor;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

public class PredictDAO {
    @Accessor
    public interface PredictAccessor {
        @Query("SELECT * FROM climate.prediction WHERE station=? AND time>=? AND time<?  ALLOW FILTERING")
        public Result<Prediction> getPredictByStation(String station, Date start,
                Date end);
    }
    
    PredictAccessor accessor = new MappingManager(Driver.getSession())
            .createAccessor(PredictAccessor.class);

    public List<Prediction> getPredictByStation(String station, Date start, Date end) {
        Result<Prediction> result = accessor
                .getPredictByStation(station, start, end);
        return result.all();
    }
    
}

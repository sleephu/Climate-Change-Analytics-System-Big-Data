package com.climate.app;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.climate.bean.ClusterRecord;
import com.climate.bean.Prediction;
import com.climate.bean.Record;
import com.climate.bean.Station;
import com.climate.dao.Cluster;
import com.climate.dao.PredictDAO;
import com.climate.dao.RecordDAO;
import com.climate.dao.StationDAO;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory
            .getLogger(HomeController.class);

    /**
     * Simply selects the home view to render by returning its name.
     * 
     * @throws ParseException
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonGenerationException
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model,
            @RequestParam(required = false) String station)
            throws ParseException, JsonGenerationException,
            JsonMappingException, IOException {
//        if (station != null) {
//            model.addAttribute("station", station);
//            Station s = new StationDAO().getStation(station);
//        } else {
//            model.addAttribute("station", "725090");
//            Station s = ;            
//        }
        String s = station==null?"725090":station;
        Station st = new StationDAO().getStation(s);
        model.addAttribute("station", s);
        model.addAttribute("sName",st.getStationName());
        model.addAttribute("sCountry",st.getCountryName());
        model.addAttribute("sLon",st.getLongitude());
        model.addAttribute("sLat",st.getLatitude());
        model.addAttribute("sElv",st.getElevation());
        return "home";
    }

    @RequestMapping(value = "/geomap", method = RequestMethod.GET)
    public String geomap(Model model) throws ParseException,
            JsonGenerationException, JsonMappingException, IOException {
        // model.addAllAttributes("year",year);
        return "geomap";
    }

    @RequestMapping(value = "records")
    public @ResponseBody List returnRecords(@RequestParam("year") int year,
            @RequestParam("station") String station) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<List> result = new ArrayList<>();

        List<Prediction> pRecords = new PredictDAO().getPredictByStation(
                station, sdf.parse(2014 + "-01-01"),
                sdf.parse((year + 3) + "-01-01"));
        result.add(pRecords);
        // return records;

        List<Record> rRecords = new RecordDAO().getRecordByStation(station,
                sdf.parse(year + "-01-01"), sdf.parse((year + 3) + "-01-01"));
        result.add(rRecords);
        // return records;
        System.out.println(result.size());
        return result;
    }

    @RequestMapping(value = "stations")
    public @ResponseBody List<Station> returnStations() throws ParseException {
        List<Station> stations = new StationDAO().getAllStation();
        return stations;
    }

    @RequestMapping(value = "stationsByCountry")
    public @ResponseBody List<Station> returnStationsByCountry(
            @RequestParam("country") String country,
            @RequestParam("year") int year, @RequestParam("kmean") int kmean)
            throws ParseException {
        System.out.println(country);
        List<Station> stations = new StationDAO().getStationByCountry(country);
        Map<String, Integer> cluster = new Cluster().kmeansFit(year, kmean);
        // Map<Station,Integer> map = new HashMap<Station, Integer>();
        for (Station s : stations) {
            Integer i = cluster.get(s.getStation());
            s.setCall("" + (i == null ? 0 : i));
        }
        return stations;
    }

    @RequestMapping(value = "countryCount")
    public @ResponseBody Map<String, Integer> returnCountryCount() {
        Map<String, Integer> cc = new StationDAO().getCountryCount();
        return cc;
    }

    // @RequestMapping(value = "cluster")
    // public @ResponseBody Map<String, Integer> returnCluster(
    // @RequestParam("year") int year) throws ParseException {
    // Map<String, Integer> cluster = new Cluster().kmeansFit(year);
    // return cluster;
    // }

}

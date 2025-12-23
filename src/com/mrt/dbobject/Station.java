package com.mrt.dbobject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Station {
    private int stationId;
    private String stationCode;
    private String stationName;
    private String city;

    public Station() {}
    public Station(int stationId, String stationCode, String stationName, String city) {
        this.stationId = stationId;
        this.stationCode = stationCode;
        this.stationName = stationName;
        this.city = city;
    }

    public int getStationId() { return stationId; }
    public String getStationCode() { return stationCode; }
    public String getStationName() { return stationName; }
    public String getCity() { return city; }

    public static Station parseResultSet(ResultSet rs) throws SQLException {
        return new Station(
            rs.getInt("station_id"),
            rs.getString("station_code"), 
            rs.getString("station_name"), 
            rs.getString("city")
        );
    }

    @Override
    public String toString() {
        if(stationId == -1) return stationName;
        return stationName + " (" + stationCode + ")";
    }
}

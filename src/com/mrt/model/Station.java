package com.mrt.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mrt.Universal;

public class Station {
    private int stationId;
    private String stationCode;
    private String stationName;
    private int cityId;

    public Station() {}
    public Station(int stationId, String stationCode, String stationName, int cityId) {
        this.stationId = stationId;
        this.stationCode = stationCode;
        this.stationName = stationName;
        this.cityId = cityId;
    }

    public int getStationId() { return stationId; }
    public String getStationCode() { return stationCode; }
    public String getStationName() { return stationName; }
    public int getCityId() { return cityId; }

    public static Station getStationFromId(int stationId) {
        return Universal.db().queryOne(
            """
            SELECT * FROM stations
            WHERE station_id = ?     
            """,
            rs -> parseResultSet(rs),
            stationId
        );
    }

    public static Station parseResultSet(ResultSet rs) throws SQLException {
        return new Station(
            rs.getInt("station_id"),
            rs.getString("station_code"), 
            rs.getString("station_name"), 
            rs.getInt("city_id")
        );
    }

    @Override
    public String toString() {
        if(stationId == -1) return stationName;
        return stationName + " (" + stationCode + ")";
    }
}

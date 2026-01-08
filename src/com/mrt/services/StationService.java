package com.mrt.services;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mrt.Universal;
import com.mrt.models.Station;

public class StationService {

    public static Station getStationById(int stationId) {
        return Universal.db().queryOne(
            """
                SELECT * FROM stations
                WHERE station_id = ?
            """,
            rs -> Station.parseResultSet(rs),
            stationId
        );
    }
}

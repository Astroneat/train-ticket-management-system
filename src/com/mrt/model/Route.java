package com.mrt.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mrt.Universal;

public class Route {
    private int routeId;
    private String routeCode;
    private int originStationId;
    private int destinationStationId;

    public Route(int routeId, String routeCode, int originStationId, int destinationStationId) {
        this.routeId = routeId;
        this.routeCode = routeCode;
        this.originStationId = originStationId;
        this.destinationStationId = destinationStationId;
    }

    public int getRouteId() {
        return routeId;
    }
    public String getRouteCode() {
        return routeCode;
    }
    public int getOriginStationId() {
        return originStationId;
    }
    public int getDestinationStationId() {
        return destinationStationId;
    }
    public String getRouteSummary() {
        return routeCode;
    }

    public static Route getRouteFromId(int routeId) {
        return Universal.db().queryOne(
            """
            SELECT * FROM train_routes
            WHERE route_id = ?
            """,
            rs -> parseResultSet(rs),
            routeId
        );
    }

    public static Route parseResultSet(ResultSet rs) throws SQLException {
        return new Route(
            rs.getInt("route_id"),
            rs.getString("route_code"),
            rs.getInt("origin_station_id"),
            rs.getInt("destination_station_id")
        );
    }
}

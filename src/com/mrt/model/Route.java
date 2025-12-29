package com.mrt.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mrt.Universal;

public class Route {
    private int routeId;
    private String routeCode;
    private int originStationId;
    private int destinationStationId;
    private BigDecimal distanceKm;

    public Route(int routeId, String routeCode, int originStationId, int destinationStationId, BigDecimal distanceKm) {
        this.routeId = routeId;
        this.routeCode = routeCode;
        this.originStationId = originStationId;
        this.destinationStationId = destinationStationId;
        this.distanceKm = distanceKm;
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
    public BigDecimal getDistanceKm() {
        return distanceKm;
    }
    public String getRouteSummary() {
        return routeCode + " (" + distanceKm + " km)";
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
            rs.getInt("destination_station_id"),
            rs.getBigDecimal("distance_km")
        );
    }
}

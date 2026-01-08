package com.mrt.models;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mrt.Universal;
import com.mrt.services.StationService;

public class Route {
    private int routeId;
    private String routeCode;
    private int originStationId;
    private int destinationStationId;
    private String status;

    public Route(int routeId, String routeCode, int originStationId, int destinationStationId, String status) {
        this.routeId = routeId;
        this.routeCode = routeCode;
        this.originStationId = originStationId;
        this.destinationStationId = destinationStationId;
        this.status = status;
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
    public String getStatus() {
        return status;
    }
    public String getRouteSummary() {
        if(routeId == -1) {
            return "All";
        }
        
        String origin = StationService.getStationById(originStationId).getStationName();
        String dest = StationService.getStationById(destinationStationId).getStationName();
        return routeCode + " (" + origin + " → " + dest + ")";
    }

    public static Route parseResultSet(ResultSet rs) throws SQLException {
        return new Route(
            rs.getInt("route_id"),
            rs.getString("route_code"),
            rs.getInt("origin_station_id"),
            rs.getInt("destination_station_id"),
            rs.getString("status")
        );
    }

    @Override
    public String toString() {
        return getRouteSummary();
    }
}

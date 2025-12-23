package com.mrt.dbobject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Route {
    private int routeId;
    private String routeCode;
    private Station originalStation;
    private Station destinationStation;
    private float distanceKm;

    public int getRouteId() {
        return routeId;
    }
    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }
    public String getRouteCode() {
        return routeCode;
    }
    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }
    public Station getOriginalStation() {
        return originalStation;
    }
    public void setOriginalStation(Station originalStation) {
        this.originalStation = originalStation;
    }
    public Station getDestinationStation() {
        return destinationStation;
    }
    public void setDestinationStation(Station destinationStation) {
        this.destinationStation = destinationStation;
    }
    public float getDistanceKm() {
        return distanceKm;
    }
    public void setDistanceKm(float distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Route(int routeId, String routeCode, Station originalStation, Station destinationStation, float distanceKm) {
        this.routeId = routeId;
        this.routeCode = routeCode;
        this.originalStation = originalStation;
        this.destinationStation = destinationStation;
        this.distanceKm = distanceKm;
    }

    public static Station parseResultSet(ResultSet rs) throws SQLException {
        return new Station(
            rs.getInt("station_id"),
            rs.getString("staton_code"), 
            rs.getString("station_name"), 
            rs.getString("city")
        );
    }
}

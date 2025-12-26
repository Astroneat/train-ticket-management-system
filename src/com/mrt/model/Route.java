package com.mrt.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Route {
    private int routeId;
    private String routeCode;
    private Station originalStation;
    private Station destinationStation;
    private BigDecimal distanceKm;

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
    public BigDecimal getDistanceKm() {
        return distanceKm;
    }
    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Route(int routeId, String routeCode, Station originalStation, Station destinationStation, BigDecimal distanceKm) {
        this.routeId = routeId;
        this.routeCode = routeCode;
        this.originalStation = originalStation;
        this.destinationStation = destinationStation;
        this.distanceKm = distanceKm;
    }

    public static Route parseResultSet(ResultSet rs) throws SQLException {
        return new Route(
            rs.getInt("tr.route_id"),
            rs.getString("tr.route_code"),
            new Station(
                rs.getInt("s1.station_id"),
                rs.getString("s1.station_code"),
                rs.getString("s1.station_name"),
                rs.getString("s1.city")
            ),
            new Station(
                rs.getInt("s2.station_id"),
                rs.getString("s2.station_code"),
                rs.getString("s2.station_name"),
                rs.getString("s2.city")
            ),
            rs.getBigDecimal("distance_km")
        );
    }

    @Override
    public String toString() {
        return originalStation.getStationName() + " → " + destinationStation.getStationName();
    }
}

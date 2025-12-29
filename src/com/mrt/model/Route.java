package com.mrt.model;

import java.math.BigDecimal;

public class Route {
    private int routeId;
    private String routeCode;
    private String originName;
    private String destinationName;
    private BigDecimal distanceKm;

    public Route(int routeId, String routeCode, String originName, String destinationName, BigDecimal distanceKm) {
        this.routeId = routeId;
        this.routeCode = routeCode;
        this.originName = originName;
        this.destinationName = destinationName;
        this.distanceKm = distanceKm;
    }

    public int getRouteId() {
        return routeId;
    }
    public String getRouteCode() {
        return routeCode;
    }
    public String getOriginName() {
        return originName;
    }
    public String getDestinationName() {
        return destinationName;
    }
    public BigDecimal getDistanceKm() {
        return distanceKm;
    }
}

package com.mrt.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.mrt.Universal;
import com.mrt.model.Route;
import com.mrt.model.Station;

public class RouteService {
    
    public static final int OK = 0;
    public static final int BLANK_ROUTE_CODE = 1;
    public static final int BLANK_ORIGIN_OR_DESTINATION = 2;
    public static final int SAME_ORIGIN_AND_DESTINATION = 3;
    public static final int ANOTHER_ROUTE_CODE_EXISTS = 4;
    public static final int ANOTHER_ROUTE_EXISTS = 5;

    public static int createRoute(String routeCode, Station origin, Station destination) {
        int state = validateRoute(-1, routeCode, origin, destination);
        if(state != OK) return state;

        Universal.db().execute(
            """
            INSERT INTO train_routes(route_code, origin_station_id, destination_station_id) VALUES (?, ?, ?);
            """,
            routeCode,
            origin.getStationId(),
            destination.getStationId()
        );

        return OK;
    }

    public static int updateRoute(int routeId, String routeCode, Station origin, Station destination, String status) {
        int state = validateRoute(routeId, routeCode, origin, destination);
        if(state != OK) return state;

        Universal.db().execute(
            """
            UPDATE train_routes 
            SET route_code = ?, origin_station_id = ?, destination_station_id = ?, status = ?
            WHERE route_id = ?;
            """,
            routeCode,
            origin.getStationId(),
            destination.getStationId(),
            status,
            routeId
        );
        return OK;
    }

    public static boolean deleteRoute(Route route) {
        if(hasSchedule(route)) {
            return false;
        }

        Universal.db().execute(
            """
                DELETE FROM train_routes WHERE route_id = ?
            """,
            route.getRouteId()
        );
        return true;
    }

    public static List<Route> getAllRoutes() {
        return Universal.db().query(
            """
                SELECT * FROM train_routes;
            """,
            rs -> Route.parseResultSet(rs)
        );
    }
    public static Route getRouteById(int routeId) {
        return Universal.db().queryOne(
            """
                SELECT * FROM train_routes
                WHERE route_id = ?    
            """,
            rs -> Route.parseResultSet(rs),
            routeId
        );
    }

    public static boolean hasSchedule(Route route) {
        Boolean exist = Universal.db().queryOne(
            """
                SELECT 1 AS exist FROM train_routes tr
                INNER JOIN train_schedules ts ON tr.route_id = ts.route_id AND tr.route_id = ?;    
            """,
            rs -> rs.getBoolean("exist"),
            route.getRouteId()
        );
        if(exist != null) return true;
        return false;
    }

    public static int validateRoute(int excludeRouteId, String routeCode, Station origin, Station destination) {
        if(routeCode.isBlank()) {
            return BLANK_ROUTE_CODE;
        }
        if(origin == null || destination == null) {
            // JOptionPane.showMessageDialog(this, "Please select origin and destination stations", "Route Error", JOptionPane.ERROR_MESSAGE);
            return BLANK_ORIGIN_OR_DESTINATION;
        }
        if(origin.getStationId() == destination.getStationId()) {
            // JOptionPane.showMessageDialog(this, "Origin and destination cannot be the same", "Route Error", JOptionPane.ERROR_MESSAGE);
            return SAME_ORIGIN_AND_DESTINATION;
        }
        if(routeExists(excludeRouteId, routeCode)) {
            return ANOTHER_ROUTE_CODE_EXISTS;
        }
        if(routeExists(excludeRouteId, origin, destination)) {
            return ANOTHER_ROUTE_EXISTS;
        }

        return OK;
    }

    public static boolean routeExists(int excludeRouteId, String routeCode) {
        Boolean exists = Universal.db().queryOne(
            """
            SELECT 1 AS exist FROM train_routes
            WHERE route_code = ? AND route_id != ?
            """,
            rs -> rs.getBoolean("exist"),
            routeCode,
            excludeRouteId
        );
        if(exists != null) return true;
        return false;
    }
    public static boolean routeExists(int excludeRouteId, Station origin, Station destination) {
        Boolean exists = Universal.db().queryOne(
            """
            SELECT 1 AS exist FROM train_routes
            WHERE origin_station_id = ? AND destination_station_id = ? AND route_id != ?
            """,
            rs -> rs.getBoolean("exist"),
            origin.getStationId(),
            destination.getStationId(),
            excludeRouteId
        );
        if(exists != null) return true;
        return false;
    }
}

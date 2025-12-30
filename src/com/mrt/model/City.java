package com.mrt.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mrt.Universal;

public class City {
    private int cityId;
    private String cityName;
    public int getCityId() {
        return cityId;
    }
    public String getCityName() {
        return cityName;
    }
    public City(int cityId, String cityName) {
        this.cityId = cityId;
        this.cityName = cityName;
    }

    @Override
    public boolean equals(Object other) {
        if(this == other) return true;
        if(other instanceof City) {
            return cityId == ((City) other).getCityId();
        }
        return false;
    }

    public static City getCityFromId(int cityId) {
        return Universal.db().queryOne(
            "SELECT * FROM cities WHERE city_id = ?", 
            rs -> parseResultSet(rs), 
            cityId
        );
    }

    @Override
    public String toString() {
        return cityName;
    }

    public static City parseResultSet(ResultSet rs) throws SQLException {
        return new City(
            rs.getInt("city_id"),
            rs.getString("city_name")
        );
    }
}

package com.mrt;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetHandler<T> {
    T map(ResultSet rs) throws SQLException;
}

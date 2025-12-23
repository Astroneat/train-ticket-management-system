package com.mrt.dbobject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private int userId;
    private String email;
    private String fullName;
    private String role;

    public User(int userId, String email, String fullName, String role) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }

    public static User parseResultSet(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("email"), 
            rs.getString("full_name"), 
            rs.getString("role")
        );
    }
}

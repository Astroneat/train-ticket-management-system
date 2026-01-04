package com.mrt.services;

import com.mrt.Universal;
import com.mrt.model.User;

public class UserService {
    
    public static User getUserById(int userId) {
        return Universal.db().queryOne(
            """
                SELECT * FROM users WHERE user_id = ?
            """,
            rs -> User.parseResultSet(rs),
            userId
        );
    }
}

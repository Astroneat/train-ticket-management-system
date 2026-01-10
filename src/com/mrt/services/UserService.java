package com.mrt.services;

import java.util.Random;

import com.mrt.Universal;
import com.mrt.models.User;

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

    public static String generateResetPasswordCode(String email) {
        int min = 1;
        int max = 999999;
        Random rand = new Random();
        int code = rand.nextInt(max - min + 1) + min;
        String formattedCode = String.format("%06d", code);

        Boolean exist = Universal.db().queryOne(
            """
                SELECT 1 AS exist FROM reset_password_tokens r
                INNER JOIN users u ON r.user_id = u.user_id
                WHERE u.email = ?;
            """,
            rs -> rs.getBoolean("exist"),
            email
        );

        if(exist == null) {
            Universal.db().execute(
                """
                    INSERT INTO reset_password_tokens(user_id, token)
                    SELECT u.user_id, ?
                    FROM users u
                    WHERE u.email = ?; 
                """,
                formattedCode,
                email
            );
        }
        else {
            Universal.db().execute(
                """
                    UPDATE reset_password_tokens r
                    INNER JOIN users u ON u.user_id = r.user_id AND u.email = ?
                    SET r.token = ?
                """,
                email,
                formattedCode
            );
        }

        return formattedCode;
    }

    public static void changePassword(String email, String password) {
        Universal.db().execute(
            """
                UPDATE users SET password = ? WHERE email = ?
            """,
            password,
            email
        );
    }
}

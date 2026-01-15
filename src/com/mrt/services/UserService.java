package com.mrt.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mrt.Universal;
import com.mrt.models.Schedule;
import com.mrt.models.Seat;
import com.mrt.models.User;

public class UserService {

    public static final int OK = 0;
    public static final int BLANK_INPUT = 1;
    public static final int EMAIL_IN_USE = 2;
    
    public static User getUserById(int userId) {
        return Universal.db().queryOne(
            """
                SELECT * FROM users WHERE user_id = ?
            """,
            rs -> User.parseResultSet(rs),
            userId
        );
    }

    public static User getUserBySeat(Schedule schedule, Seat seat) {
        return Universal.db().queryOne(
            """
                SELECT * FROM tickets tk
                INNER JOIN train_schedules ts ON ts.schedule_id = tk.schedule_id
                INNER JOIN users u ON tk.user_id = u.user_id
                WHERE ts.schedule_id = ? AND tk.car_no = ? AND tk.seat_index = ?
            """,
            rs -> User.parseResultSet(rs),
            schedule.getScheduleId(),
            seat.getCarNo(),
            seat.getSeatIndex()
        );
    }

    public static int updateUser(User user, String email, String fullName, String password) {
        if(email.isBlank() || fullName.isBlank()) return BLANK_INPUT;
        if(!user.getEmail().equals(email) && inUse(email)) return EMAIL_IN_USE;

        List<Object> args = new ArrayList<>();
        String sql = 
        """
            UPDATE users SET email = ?, full_name = ?
        """;
        args.add(email);
        args.add(fullName);
        if(!password.isBlank()) {
            sql += ", password = ?";
            args.add(password);
        }
        sql += " WHERE user_id = ?";
        args.add(user.getUserId());

        Universal.db().execute(
            sql,
            args.toArray()
        );
        return OK;
    }

    public static boolean inUse(String email) {
        Boolean exist = Universal.db().queryOne(
            """
                SELECT 1 AS exist FROM users
                WHERE email = ?        
            """,
            rs -> rs.getBoolean("exist"),
            email
        );
        if(exist == null) return false;
        return true;
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

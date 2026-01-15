package com.mrt.admin.reports;

import java.time.ZoneId;
import java.util.List;

import com.mrt.Universal;
import com.mrt.admin.reports.models.BarData;
import com.mrt.admin.reports.models.LinePoint;
import com.mrt.admin.reports.models.TicketStatus;
import com.mrt.services.SeatService;

public class Analytics {
    
    public static int totalTicketsSold() {
        return Universal.db().queryOne(
            """
                SELECT COUNT(tickets.ticket_id) AS cnt FROM tickets
                WHERE status != 'cancelled';
            """,
            rs -> rs.getInt("cnt")
        );
    }

    public static int totalRevenue() {
        return totalTicketsSold() * SeatService.getPricePerTicket();
    }

    public static int activeSchedules() {
        return Universal.db().queryOne(
            """
                SELECT COUNT(ts.schedule_id) AS cnt
                FROM train_schedules ts
                WHERE ts.status = 'scheduled';        
            """,
            rs -> rs.getInt("cnt")
        );
    }

    public static int cancelledSchedules() {
        return Universal.db().queryOne(
            """
                SELECT COUNT(ts.schedule_id) AS cnt
                FROM train_schedules ts
                WHERE ts.status = 'cancelled';        
            """,
            rs -> rs.getInt("cnt")
        );
    }

    public static List<TicketStatus> ticketStatusStat() {
        return Universal.db().query(
            """
                SELECT status, COUNT(*) AS cnt
                FROM tickets
                GROUP BY status  
            """,
            rs -> new TicketStatus(rs.getString("status"), rs.getInt("cnt"))
        );
    } 

    public static List<LinePoint> ticketsSoldPerDayLastNDays(int N) {
        return Universal.db().query(
            """
                WITH RECURSIVE dates AS (
                    SELECT DATE(DATE_SUB(UTC_TIMESTAMP(), INTERVAL ? DAY)) AS day
                    UNION ALL
                    SELECT DATE_ADD(day, INTERVAL 1 DAY)
                    FROM dates
                    WHERE day < DATE(UTC_TIMESTAMP())
                )
                SELECT day, COUNT(ticket_id) AS cnt FROM dates
                LEFT JOIN tickets ON DATE(tickets.booked_at) = dates.day
                GROUP BY dates.day;
            """,
            rs -> new LinePoint(
                rs.getTimestamp("day").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(Universal.getDateFormatter()),
                rs.getInt("cnt")
            ),
            N
        );
    }

    public static List<LinePoint> revenueSoldPerDayLastNDays(int N) {
        return Universal.db().query(
            """
                WITH RECURSIVE dates AS (
                    SELECT DATE(DATE_SUB(UTC_TIMESTAMP(), INTERVAL ? DAY)) AS day
                    UNION ALL
                    SELECT DATE_ADD(day, INTERVAL 1 DAY)
                    FROM dates
                    WHERE day < DATE(UTC_TIMESTAMP())
                )
                SELECT day, COUNT(ticket_id) AS cnt FROM dates
                LEFT JOIN tickets ON DATE(tickets.booked_at) = dates.day
                GROUP BY dates.day;
            """,
            rs -> new LinePoint(
                rs.getTimestamp("day").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(Universal.getDateFormatter()),
                rs.getInt("cnt") *  SeatService.getPricePerTicket()
            ),
            N
        );
    }

    public static List<BarData> routePopularity(int topKRoutes) {
        return Universal.db().query(
            """
                SELECT tr.route_id, tr.route_code, COUNT(tk.ticket_id) sold FROM tickets tk
                INNER JOIN train_schedules ts ON tk.schedule_id = ts.schedule_id
                RIGHT JOIN train_routes tr ON ts.route_id = tr.route_id
                GROUP BY tr.route_id
                ORDER BY sold DESC
                LIMIT ?;
            """,
            rs -> new BarData(
                rs.getString("tr.route_code"),
                rs.getInt("sold")
            ),
            topKRoutes
        );
    }
}

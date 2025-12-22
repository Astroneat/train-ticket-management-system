import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

interface ResultSetHandler<T> {
    T map(ResultSet rs) throws SQLException;
}

public class EasyDB {
    private int port;
    private String dbName;
    private String user;
    private String password;

    private Connection conn;
    private Statement stmt;

    public EasyDB(int port, String dbName, String user, String password) {
        this.port = port;
        this.dbName = dbName;
        this.user = user;
        this.password = password;
    }

    public void establishConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(String.format(
                "jdbc:mysql://localhost:%d/%s?serverTimezone=UTC",
                port,
                dbName
            ), user, password);
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("Database connected successfully!");
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }
    public void closeConnection() {
        try {
            if(stmt != null && !stmt.isClosed()) stmt.close();
        } catch(Exception ignored) {}
        try {
            if(conn != null && !conn.isClosed()) conn.close();
        } catch(Exception ignored) {}
    }

    public <T> List<T> query(String sql, ResultSetHandler<T> handler, String... args) {
        List<T> results = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            for(int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            // System.out.println(ps.toString());
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    results.add(handler.map(rs));
                }
            }
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    public <T> T queryOne(String sql, ResultSetHandler<T> handler, String... args) {
        List<T> results = query(sql, handler, args);
        if(results.isEmpty()) return null;
        return results.get(0);
    }

    public int execute(String sql, String... args) {
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            for(int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            return ps.executeUpdate();
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

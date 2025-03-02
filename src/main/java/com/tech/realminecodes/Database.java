package com.tech.realminecodes;

import java.sql.*;

public class Database {

    private final Main plugin;
    private Connection connection;

    public Database(Main plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        try {
            String host = plugin.getConfig().getString("database.host");
            int port = plugin.getConfig().getInt("database.port");
            String database = plugin.getConfig().getString("database.database");
            String username = plugin.getConfig().getString("database.username");
            String password = plugin.getConfig().getString("database.password");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            connection = DriverManager.getConnection(url, username, password);
            createTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        try (Statement stmt = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS codes_history (" +
                    "username VARCHAR(16), " +
                    "activation_date DATETIME, " +
                    "code_type VARCHAR(10), " +
                    "code VARCHAR(50), " +
                    "ip_address VARCHAR(45))";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasActivatedWithinTimeout(String username, String code, int timeout) {
        String sql = "SELECT * FROM codes_history WHERE username = ? AND code = ? AND activation_date >= DATE_SUB(NOW(), INTERVAL ? SECOND)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, code);
            pstmt.setInt(3, timeout);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasActivatedByIPWithinTimeout(String ipAddress, String code, int timeout) {
        String sql = "SELECT * FROM codes_history WHERE ip_address = ? AND code = ? AND activation_date >= DATE_SUB(NOW(), INTERVAL ? SECOND)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ipAddress);
            pstmt.setString(2, code);
            pstmt.setInt(3, timeout);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasActivatedByUsername(String username, String code) {
        String sql = "SELECT * FROM codes_history WHERE username = ? AND code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, code);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getCodeActivations(String code) {
        String sql = "SELECT COUNT(*) AS count FROM codes_history WHERE code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getCodeActivationsByIP(String code, String ipAddress) {
        String sql = "SELECT COUNT(*) AS count FROM codes_history WHERE code = ? AND ip_address = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setString(2, ipAddress);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean hasActivatedByIP(String ipAddress, String code) {
        String sql = "SELECT * FROM codes_history WHERE ip_address = ? AND code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ipAddress);
            pstmt.setString(2, code);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void logActivation(String username, String code, String codeType, String ipAddress) {
        String sql = "INSERT INTO codes_history (username, activation_date, code_type, code, ip_address) VALUES (?, NOW(), ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, codeType);
            pstmt.setString(3, code);
            pstmt.setString(4, ipAddress);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
package com.medipredict.database;

import com.medipredict.util.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    static {
        try {
            if (DatabaseConfig.isSQLite()) {
                Class.forName("org.sqlite.JDBC");
            } else if (DatabaseConfig.isMySQL()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
            }
            Logger.info("JDBC Driver loaded successfully for: " + DatabaseConfig.getDbType());
        } catch (ClassNotFoundException e) {
            Logger.error("JDBC Driver class not found for: " + DatabaseConfig.getDbType(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (DatabaseConfig.isSQLite()) {
            Connection conn = DriverManager.getConnection(DatabaseConfig.getJdbcUrl());
            // Enable foreign keys for SQLite
            try (var stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            } catch (SQLException ignored) {}
            return conn;
        } else {
            return DriverManager.getConnection(
                    DatabaseConfig.getJdbcUrl(),
                    DatabaseConfig.getUsername(),
                    DatabaseConfig.getPassword()
            );
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            Logger.error("Database connection test failed", e);
            return false;
        }
    }
}

package com.medipredict.database;

import com.medipredict.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();
    private static String dbType = "sqlite";
    private static String jdbcUrl;
    private static String username;
    private static String password;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try {
            File propFile = new File("resources/db.properties");
            if (propFile.exists()) {
                try (InputStream is = new FileInputStream(propFile)) {
                    properties.load(is);
                }
            } else {
                try (InputStream is = DatabaseConfig.class.getResourceAsStream("/db.properties")) {
                    if (is != null) {
                        properties.load(is);
                    }
                }
            }

            dbType = properties.getProperty("db.type", "sqlite").trim().toLowerCase();
            if ("mysql".equalsIgnoreCase(dbType)) {
                jdbcUrl = properties.getProperty("mysql.url", "jdbc:mysql://localhost:3306/medipredict?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
                username = properties.getProperty("mysql.username", "root");
                password = properties.getProperty("mysql.password", "root");
            } else {
                dbType = "sqlite";
                jdbcUrl = properties.getProperty("sqlite.url", "jdbc:sqlite:medipredict.db");
                username = "";
                password = "";
            }
            Logger.info("Database configured for type: " + dbType.toUpperCase() + " with URL: " + jdbcUrl);
        } catch (Exception e) {
            Logger.error("Failed to load db.properties, falling back to default SQLite configuration", e);
            dbType = "sqlite";
            jdbcUrl = "jdbc:sqlite:medipredict.db";
            username = "";
            password = "";
        }
    }

    public static String getDbType() {
        return dbType;
    }

    public static String getJdbcUrl() {
        return jdbcUrl;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static boolean isSQLite() {
        return "sqlite".equalsIgnoreCase(dbType);
    }

    public static boolean isMySQL() {
        return "mysql".equalsIgnoreCase(dbType);
    }
}

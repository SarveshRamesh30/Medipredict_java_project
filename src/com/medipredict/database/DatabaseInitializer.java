package com.medipredict.database;

import com.medipredict.util.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        Logger.info("Initializing database schema for: " + DatabaseConfig.getDbType().toUpperCase());

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            boolean isSQLite = DatabaseConfig.isSQLite();

            String autoInc = isSQLite ? "INTEGER PRIMARY KEY AUTOINCREMENT" : "INT PRIMARY KEY AUTO_INCREMENT";
            String currentTs = isSQLite ? "CURRENT_TIMESTAMP" : "CURRENT_TIMESTAMP";
            String textType = "TEXT";

            // 1. users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "user_id " + autoInc + ", " +
                    "email VARCHAR(150) NOT NULL UNIQUE, " +
                    "password_hash VARCHAR(255) NOT NULL, " +
                    "salt VARCHAR(255) NOT NULL, " +
                    "role VARCHAR(20) NOT NULL, " +
                    "full_name VARCHAR(100) NOT NULL, " +
                    "phone VARCHAR(25), " +
                    "created_at TIMESTAMP DEFAULT " + currentTs + ", " +
                    "updated_at TIMESTAMP DEFAULT " + currentTs + ");");

            // 2. patients table
            stmt.execute("CREATE TABLE IF NOT EXISTS patients (" +
                    "patient_id " + autoInc + ", " +
                    "user_id INT NOT NULL UNIQUE, " +
                    "dob DATE, " +
                    "gender VARCHAR(20), " +
                    "blood_group VARCHAR(10), " +
                    "medical_history " + textType + ", " +
                    "emergency_contact VARCHAR(100), " +
                    "created_at TIMESTAMP DEFAULT " + currentTs + ", " +
                    "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE);");

            // 3. doctors table
            stmt.execute("CREATE TABLE IF NOT EXISTS doctors (" +
                    "doctor_id " + autoInc + ", " +
                    "user_id INT NOT NULL UNIQUE, " +
                    "specialization VARCHAR(100) NOT NULL, " +
                    "hospital_clinic VARCHAR(150) NOT NULL, " +
                    "experience_years INT DEFAULT 0, " +
                    "consultation_fee DECIMAL(10, 2) DEFAULT 50.00, " +
                    "bio " + textType + ", " +
                    "availability_hours VARCHAR(100) DEFAULT '9:00 AM - 5:00 PM', " +
                    "created_at TIMESTAMP DEFAULT " + currentTs + ", " +
                    "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE);");

            // 4. symptoms table
            stmt.execute("CREATE TABLE IF NOT EXISTS symptoms (" +
                    "symptom_id " + autoInc + ", " +
                    "name VARCHAR(100) NOT NULL UNIQUE, " +
                    "category VARCHAR(50) NOT NULL, " +
                    "description " + textType + ", " +
                    "severity_weight INT DEFAULT 1);");

            // 5. conditions table
            stmt.execute("CREATE TABLE IF NOT EXISTS conditions (" +
                    "condition_id " + autoInc + ", " +
                    "name VARCHAR(100) NOT NULL UNIQUE, " +
                    "category VARCHAR(50) NOT NULL, " +
                    "description " + textType + " NOT NULL, " +
                    "precautions " + textType + " NOT NULL, " +
                    "severity_level VARCHAR(20) DEFAULT 'Moderate');");

            // 6. condition_symptoms table (many-to-many relationship with weights)
            stmt.execute("CREATE TABLE IF NOT EXISTS condition_symptoms (" +
                    "condition_id INT NOT NULL, " +
                    "symptom_id INT NOT NULL, " +
                    "weight INT DEFAULT 1, " +
                    "PRIMARY KEY (condition_id, symptom_id), " +
                    "FOREIGN KEY (condition_id) REFERENCES conditions(condition_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (symptom_id) REFERENCES symptoms(symptom_id) ON DELETE CASCADE);");

            // 7. predictions table
            stmt.execute("CREATE TABLE IF NOT EXISTS predictions (" +
                    "prediction_id " + autoInc + ", " +
                    "patient_id INT NOT NULL, " +
                    "primary_condition_id INT NOT NULL, " +
                    "match_score DOUBLE DEFAULT 0.0, " +
                    "confidence_level VARCHAR(20) DEFAULT 'Medium', " +
                    "risk_level VARCHAR(30) DEFAULT 'Moderate', " +
                    "notes " + textType + ", " +
                    "created_at TIMESTAMP DEFAULT " + currentTs + ", " +
                    "FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (primary_condition_id) REFERENCES conditions(condition_id) ON DELETE CASCADE);");

            // 8. prediction_symptoms table
            stmt.execute("CREATE TABLE IF NOT EXISTS prediction_symptoms (" +
                    "prediction_id INT NOT NULL, " +
                    "symptom_id INT NOT NULL, " +
                    "PRIMARY KEY (prediction_id, symptom_id), " +
                    "FOREIGN KEY (prediction_id) REFERENCES predictions(prediction_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (symptom_id) REFERENCES symptoms(symptom_id) ON DELETE CASCADE);");

            // 9. appointments table
            stmt.execute("CREATE TABLE IF NOT EXISTS appointments (" +
                    "appointment_id " + autoInc + ", " +
                    "patient_id INT NOT NULL, " +
                    "doctor_id INT NOT NULL, " +
                    "appointment_date DATE NOT NULL, " +
                    "appointment_time VARCHAR(20) NOT NULL, " +
                    "reason " + textType + " NOT NULL, " +
                    "status VARCHAR(20) DEFAULT 'PENDING', " +
                    "notes " + textType + ", " +
                    "created_at TIMESTAMP DEFAULT " + currentTs + ", " +
                    "updated_at TIMESTAMP DEFAULT " + currentTs + ", " +
                    "FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE);");

            // 10. recommendations table
            stmt.execute("CREATE TABLE IF NOT EXISTS recommendations (" +
                    "recommendation_id " + autoInc + ", " +
                    "patient_id INT NOT NULL, " +
                    "doctor_id INT NOT NULL, " +
                    "prediction_id INT, " +
                    "recommendation_text " + textType + " NOT NULL, " +
                    "lifestyle_advice " + textType + ", " +
                    "follow_up_date DATE, " +
                    "notes " + textType + ", " +
                    "created_at TIMESTAMP DEFAULT " + currentTs + ", " +
                    "FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (prediction_id) REFERENCES predictions(prediction_id) ON DELETE SET NULL);");

            // 11. messages table
            stmt.execute("CREATE TABLE IF NOT EXISTS messages (" +
                    "message_id " + autoInc + ", " +
                    "sender_id INT NOT NULL, " +
                    "receiver_id INT NOT NULL, " +
                    "message_text " + textType + " NOT NULL, " +
                    "is_read INT DEFAULT 0, " +
                    "sent_at TIMESTAMP DEFAULT " + currentTs + ", " +
                    "FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (receiver_id) REFERENCES users(user_id) ON DELETE CASCADE);");

            Logger.info("Database schema verified and initialized.");

            // Check if sample data needs to be populated
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM symptoms;");
            if (rs.next() && rs.getInt("cnt") == 0) {
                Logger.info("Empty database detected. Seeding sample symptoms, conditions, users, and appointments...");
                SampleDataLoader.seedSampleData(conn);
            }

        } catch (SQLException e) {
            Logger.error("Error initializing database schema", e);
            throw new RuntimeException("Database initialization failure", e);
        }
    }
}

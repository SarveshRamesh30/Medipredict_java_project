package com.medipredict.dao;

import com.medipredict.database.DatabaseConnection;
import com.medipredict.model.Patient;
import com.medipredict.model.Role;
import com.medipredict.util.DateTimeUtil;
import com.medipredict.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public Patient findByUserId(int userId) {
        String sql = "SELECT u.*, p.patient_id, p.dob, p.gender, p.blood_group, p.medical_history, p.emergency_contact " +
                "FROM patients p JOIN users u ON p.user_id = u.user_id WHERE p.user_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPatient(rs);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding patient by user ID: " + userId, e);
        }
        return null;
    }

    public Patient findById(int patientId) {
        String sql = "SELECT u.*, p.patient_id, p.dob, p.gender, p.blood_group, p.medical_history, p.emergency_contact " +
                "FROM patients p JOIN users u ON p.user_id = u.user_id WHERE p.patient_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPatient(rs);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding patient by patient ID: " + patientId, e);
        }
        return null;
    }

    public int create(Patient patient) {
        String sql = "INSERT INTO patients (user_id, dob, gender, blood_group, medical_history, emergency_contact) VALUES (?, ?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, patient.getUserId());
            ps.setDate(2, patient.getDob());
            ps.setString(3, patient.getGender());
            ps.setString(4, patient.getBloodGroup());
            ps.setString(5, patient.getMedicalHistory());
            ps.setString(6, patient.getEmergencyContact());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    patient.setPatientId(generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            Logger.error("Error creating patient for user ID: " + patient.getUserId(), e);
        }
        return -1;
    }

    public boolean update(Patient patient) {
        String sql = "UPDATE patients SET dob = ?, gender = ?, blood_group = ?, medical_history = ?, emergency_contact = ? WHERE patient_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, patient.getDob());
            ps.setString(2, patient.getGender());
            ps.setString(3, patient.getBloodGroup());
            ps.setString(4, patient.getMedicalHistory());
            ps.setString(5, patient.getEmergencyContact());
            ps.setInt(6, patient.getPatientId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("Error updating patient ID: " + patient.getPatientId(), e);
            return false;
        }
    }

    public List<Patient> findAll() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT u.*, p.patient_id, p.dob, p.gender, p.blood_group, p.medical_history, p.emergency_contact " +
                "FROM patients p JOIN users u ON p.user_id = u.user_id ORDER BY u.full_name ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapPatient(rs));
            }
        } catch (SQLException e) {
            Logger.error("Error finding all patients", e);
        }
        return list;
    }

    public List<Patient> searchPatients(String keyword) {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT u.*, p.patient_id, p.dob, p.gender, p.blood_group, p.medical_history, p.emergency_contact " +
                "FROM patients p JOIN users u ON p.user_id = u.user_id " +
                "WHERE LOWER(u.full_name) LIKE ? OR LOWER(u.email) LIKE ? OR CAST(p.patient_id AS TEXT) LIKE ? " +
                "ORDER BY u.full_name ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPatient(rs));
                }
            }
        } catch (SQLException e) {
            Logger.error("Error searching patients with keyword: " + keyword, e);
        }
        return list;
    }

    private Patient mapPatient(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setUserId(rs.getInt("user_id"));
        p.setEmail(rs.getString("email"));
        p.setPasswordHash(rs.getString("password_hash"));
        p.setSalt(rs.getString("salt"));
        p.setRole(Role.PATIENT);
        p.setFullName(rs.getString("full_name"));
        p.setPhone(rs.getString("phone"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));

        p.setPatientId(rs.getInt("patient_id"));
        String dobStr = rs.getString("dob");
        p.setDob(DateTimeUtil.parseDate(dobStr));
        p.setGender(rs.getString("gender"));
        p.setBloodGroup(rs.getString("blood_group"));
        p.setMedicalHistory(rs.getString("medical_history"));
        p.setEmergencyContact(rs.getString("emergency_contact"));
        return p;
    }
}

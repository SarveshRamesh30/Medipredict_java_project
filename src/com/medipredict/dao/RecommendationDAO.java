package com.medipredict.dao;

import com.medipredict.database.DatabaseConnection;
import com.medipredict.model.Recommendation;
import com.medipredict.util.DateTimeUtil;
import com.medipredict.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class RecommendationDAO {

    public int create(Recommendation r) {
        String sql = "INSERT INTO recommendations (patient_id, doctor_id, prediction_id, recommendation_text, lifestyle_advice, follow_up_date, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getPatientId());
            ps.setInt(2, r.getDoctorId());
            if (r.getPredictionId() != null) {
                ps.setInt(3, r.getPredictionId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setString(4, r.getRecommendationText());
            ps.setString(5, r.getLifestyleAdvice());
            ps.setDate(6, r.getFollowUpDate());
            ps.setString(7, r.getNotes());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    r.setRecommendationId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            Logger.error("Error creating recommendation", e);
        }
        return -1;
    }

    public Recommendation findById(int id) {
        String sql = "SELECT r.*, " +
                "up.full_name AS patient_name, " +
                "ud.full_name AS doctor_name, d.specialization AS doctor_specialization, " +
                "c.name AS condition_name " +
                "FROM recommendations r " +
                "JOIN patients p ON r.patient_id = p.patient_id " +
                "JOIN users up ON p.user_id = up.user_id " +
                "JOIN doctors d ON r.doctor_id = d.doctor_id " +
                "JOIN users ud ON d.user_id = ud.user_id " +
                "LEFT JOIN predictions pr ON r.prediction_id = pr.prediction_id " +
                "LEFT JOIN conditions c ON pr.primary_condition_id = c.condition_id " +
                "WHERE r.recommendation_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRecommendation(rs);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding recommendation by ID: " + id, e);
        }
        return null;
    }

    public List<Recommendation> findByPatientId(int patientId) {
        List<Recommendation> list = new ArrayList<>();
        String sql = "SELECT r.*, " +
                "up.full_name AS patient_name, " +
                "ud.full_name AS doctor_name, d.specialization AS doctor_specialization, " +
                "c.name AS condition_name " +
                "FROM recommendations r " +
                "JOIN patients p ON r.patient_id = p.patient_id " +
                "JOIN users up ON p.user_id = up.user_id " +
                "JOIN doctors d ON r.doctor_id = d.doctor_id " +
                "JOIN users ud ON d.user_id = ud.user_id " +
                "LEFT JOIN predictions pr ON r.prediction_id = pr.prediction_id " +
                "LEFT JOIN conditions c ON pr.primary_condition_id = c.condition_id " +
                "WHERE r.patient_id = ? ORDER BY r.created_at DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRecommendation(rs));
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding recommendations for patient ID: " + patientId, e);
        }
        return list;
    }

    public List<Recommendation> findByDoctorId(int doctorId) {
        List<Recommendation> list = new ArrayList<>();
        String sql = "SELECT r.*, " +
                "up.full_name AS patient_name, " +
                "ud.full_name AS doctor_name, d.specialization AS doctor_specialization, " +
                "c.name AS condition_name " +
                "FROM recommendations r " +
                "JOIN patients p ON r.patient_id = p.patient_id " +
                "JOIN users up ON p.user_id = up.user_id " +
                "JOIN doctors d ON r.doctor_id = d.doctor_id " +
                "JOIN users ud ON d.user_id = ud.user_id " +
                "LEFT JOIN predictions pr ON r.prediction_id = pr.prediction_id " +
                "LEFT JOIN conditions c ON pr.primary_condition_id = c.condition_id " +
                "WHERE r.doctor_id = ? ORDER BY r.created_at DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRecommendation(rs));
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding recommendations for doctor ID: " + doctorId, e);
        }
        return list;
    }

    public Recommendation getLatestByPatientId(int patientId) {
        String sql = "SELECT r.*, " +
                "up.full_name AS patient_name, " +
                "ud.full_name AS doctor_name, d.specialization AS doctor_specialization, " +
                "c.name AS condition_name " +
                "FROM recommendations r " +
                "JOIN patients p ON r.patient_id = p.patient_id " +
                "JOIN users up ON p.user_id = up.user_id " +
                "JOIN doctors d ON r.doctor_id = d.doctor_id " +
                "JOIN users ud ON d.user_id = ud.user_id " +
                "LEFT JOIN predictions pr ON r.prediction_id = pr.prediction_id " +
                "LEFT JOIN conditions c ON pr.primary_condition_id = c.condition_id " +
                "WHERE r.patient_id = ? ORDER BY r.created_at DESC LIMIT 1;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRecommendation(rs);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding latest recommendation for patient ID: " + patientId, e);
        }
        return null;
    }

    private Recommendation mapRecommendation(ResultSet rs) throws SQLException {
        Recommendation r = new Recommendation();
        r.setRecommendationId(rs.getInt("recommendation_id"));
        r.setPatientId(rs.getInt("patient_id"));
        r.setDoctorId(rs.getInt("doctor_id"));
        int pId = rs.getInt("prediction_id");
        r.setPredictionId(rs.wasNull() ? null : pId);
        r.setRecommendationText(rs.getString("recommendation_text"));
        r.setLifestyleAdvice(rs.getString("lifestyle_advice"));
        String fDateStr = rs.getString("follow_up_date");
        r.setFollowUpDate(DateTimeUtil.parseDate(fDateStr));
        r.setNotes(rs.getString("notes"));
        r.setCreatedAt(rs.getTimestamp("created_at"));

        r.setPatientName(rs.getString("patient_name"));
        r.setDoctorName(rs.getString("doctor_name"));
        r.setDoctorSpecialization(rs.getString("doctor_specialization"));
        r.setConditionName(rs.getString("condition_name"));
        return r;
    }
}

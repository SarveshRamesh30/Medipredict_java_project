package com.medipredict.dao;

import com.medipredict.database.DatabaseConnection;
import com.medipredict.model.Prediction;
import com.medipredict.model.Symptom;
import com.medipredict.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PredictionDAO {

    public int savePrediction(Prediction prediction, List<Integer> symptomIds) {
        String sqlPred = "INSERT INTO predictions (patient_id, primary_condition_id, match_score, confidence_level, risk_level, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?);";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int predictionId = -1;
            try (PreparedStatement ps = conn.prepareStatement(sqlPred, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, prediction.getPatientId());
                ps.setInt(2, prediction.getPrimaryConditionId());
                ps.setDouble(3, prediction.getMatchScore());
                ps.setString(4, prediction.getConfidenceLevel());
                ps.setString(5, prediction.getRiskLevel());
                ps.setString(6, prediction.getNotes());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        predictionId = rs.getInt(1);
                        prediction.setPredictionId(predictionId);
                    }
                }
            }

            if (predictionId != -1 && symptomIds != null && !symptomIds.isEmpty()) {
                String sqlSym = "INSERT INTO prediction_symptoms (prediction_id, symptom_id) VALUES (?, ?);";
                try (PreparedStatement psSym = conn.prepareStatement(sqlSym)) {
                    for (int symId : symptomIds) {
                        psSym.setInt(1, predictionId);
                        psSym.setInt(2, symId);
                        psSym.addBatch();
                    }
                    psSym.executeBatch();
                }
            }

            conn.commit();
            return predictionId;
        } catch (SQLException e) {
            Logger.error("Error saving prediction for patient ID: " + prediction.getPatientId(), e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {}
            }
            return -1;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public Prediction findById(int predictionId) {
        String sql = "SELECT p.*, c.name AS condition_name, c.description AS condition_desc, c.precautions AS condition_precautions " +
                "FROM predictions p " +
                "JOIN conditions c ON p.primary_condition_id = c.condition_id " +
                "WHERE p.prediction_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, predictionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Prediction pred = mapPrediction(rs);
                    pred.setSymptoms(findSymptomsForPrediction(conn, predictionId));
                    return pred;
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding prediction by ID: " + predictionId, e);
        }
        return null;
    }

    public List<Prediction> findByPatientId(int patientId) {
        List<Prediction> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name AS condition_name, c.description AS condition_desc, c.precautions AS condition_precautions " +
                "FROM predictions p " +
                "JOIN conditions c ON p.primary_condition_id = c.condition_id " +
                "WHERE p.patient_id = ? ORDER BY p.created_at DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Prediction pred = mapPrediction(rs);
                    pred.setSymptoms(findSymptomsForPrediction(conn, pred.getPredictionId()));
                    list.add(pred);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding predictions for patient ID: " + patientId, e);
        }
        return list;
    }

    public Prediction getLatestPredictionByPatientId(int patientId) {
        String sql = "SELECT p.*, c.name AS condition_name, c.description AS condition_desc, c.precautions AS condition_precautions " +
                "FROM predictions p " +
                "JOIN conditions c ON p.primary_condition_id = c.condition_id " +
                "WHERE p.patient_id = ? ORDER BY p.created_at DESC LIMIT 1;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Prediction pred = mapPrediction(rs);
                    pred.setSymptoms(findSymptomsForPrediction(conn, pred.getPredictionId()));
                    return pred;
                }
            }
        } catch (SQLException e) {
            Logger.error("Error getting latest prediction for patient ID: " + patientId, e);
        }
        return null;
    }

    public int countByPatientId(int patientId) {
        String sql = "SELECT COUNT(*) AS cnt FROM predictions WHERE patient_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (SQLException e) {
            Logger.error("Error counting predictions for patient ID: " + patientId, e);
        }
        return 0;
    }

    public List<Prediction> findRecentPredictions(int limit) {
        List<Prediction> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name AS condition_name, c.description AS condition_desc, c.precautions AS condition_precautions, u.full_name AS patient_name " +
                "FROM predictions p " +
                "JOIN conditions c ON p.primary_condition_id = c.condition_id " +
                "JOIN patients pt ON p.patient_id = pt.patient_id " +
                "JOIN users u ON pt.user_id = u.user_id " +
                "ORDER BY p.created_at DESC LIMIT ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Prediction pred = mapPrediction(rs);
                    pred.setNotes("Patient: " + rs.getString("patient_name") + " | " + (pred.getNotes() != null ? pred.getNotes() : ""));
                    pred.setSymptoms(findSymptomsForPrediction(conn, pred.getPredictionId()));
                    list.add(pred);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding recent predictions", e);
        }
        return list;
    }

    private List<Symptom> findSymptomsForPrediction(Connection conn, int predictionId) {
        List<Symptom> list = new ArrayList<>();
        String sql = "SELECT s.* FROM symptoms s " +
                "JOIN prediction_symptoms ps ON s.symptom_id = ps.symptom_id " +
                "WHERE ps.prediction_id = ? ORDER BY s.name ASC;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, predictionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Symptom(
                            rs.getInt("symptom_id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getString("description"),
                            rs.getInt("severity_weight")
                    ));
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding symptoms for prediction: " + predictionId, e);
        }
        return list;
    }

    private Prediction mapPrediction(ResultSet rs) throws SQLException {
        Prediction p = new Prediction();
        p.setPredictionId(rs.getInt("prediction_id"));
        p.setPatientId(rs.getInt("patient_id"));
        p.setPrimaryConditionId(rs.getInt("primary_condition_id"));
        p.setMatchScore(rs.getDouble("match_score"));
        p.setConfidenceLevel(rs.getString("confidence_level"));
        p.setRiskLevel(rs.getString("risk_level"));
        p.setNotes(rs.getString("notes"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setConditionName(rs.getString("condition_name"));
        p.setConditionDescription(rs.getString("condition_desc"));
        p.setPrecautions(rs.getString("condition_precautions"));
        return p;
    }
}

package com.medipredict.dao;

import com.medipredict.database.DatabaseConnection;
import com.medipredict.model.MedicalCondition;
import com.medipredict.model.Symptom;
import com.medipredict.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConditionDAO {

    public List<MedicalCondition> findAll() {
        List<MedicalCondition> list = new ArrayList<>();
        String sql = "SELECT * FROM conditions ORDER BY name ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapCondition(rs));
            }
        } catch (SQLException e) {
            Logger.error("Error finding all conditions", e);
        }
        return list;
    }

    public MedicalCondition findById(int conditionId) {
        String sql = "SELECT * FROM conditions WHERE condition_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, conditionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MedicalCondition cond = mapCondition(rs);
                    cond.setAssociatedSymptoms(findSymptomsForCondition(conditionId));
                    return cond;
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding condition by ID: " + conditionId, e);
        }
        return null;
    }

    public List<Symptom> findSymptomsForCondition(int conditionId) {
        List<Symptom> list = new ArrayList<>();
        String sql = "SELECT s.* FROM symptoms s " +
                "JOIN condition_symptoms cs ON s.symptom_id = cs.symptom_id " +
                "WHERE cs.condition_id = ? ORDER BY cs.weight DESC, s.name ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, conditionId);
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
            Logger.error("Error finding symptoms for condition: " + conditionId, e);
        }
        return list;
    }

    public List<MedicalCondition> findAllWithSymptoms() {
        Map<Integer, MedicalCondition> condMap = new HashMap<>();
        String sqlCond = "SELECT * FROM conditions;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlCond)) {
            while (rs.next()) {
                MedicalCondition c = mapCondition(rs);
                condMap.put(c.getConditionId(), c);
            }
        } catch (SQLException e) {
            Logger.error("Error loading conditions", e);
        }

        String sqlCS = "SELECT cs.condition_id, cs.weight, s.* " +
                "FROM condition_symptoms cs " +
                "JOIN symptoms s ON cs.symptom_id = s.symptom_id;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlCS)) {
            while (rs.next()) {
                int cId = rs.getInt("condition_id");
                MedicalCondition cond = condMap.get(cId);
                if (cond != null) {
                    Symptom sym = new Symptom(
                            rs.getInt("symptom_id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getString("description"),
                            rs.getInt("severity_weight")
                    );
                    cond.getAssociatedSymptoms().add(sym);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error loading condition-symptom mappings", e);
        }

        return new ArrayList<>(condMap.values());
    }

    private MedicalCondition mapCondition(ResultSet rs) throws SQLException {
        return new MedicalCondition(
                rs.getInt("condition_id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getString("precautions"),
                rs.getString("severity_level")
        );
    }
}

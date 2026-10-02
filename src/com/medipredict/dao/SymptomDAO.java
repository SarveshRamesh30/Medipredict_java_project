package com.medipredict.dao;

import com.medipredict.database.DatabaseConnection;
import com.medipredict.model.Symptom;
import com.medipredict.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SymptomDAO {

    public List<Symptom> findAll() {
        List<Symptom> list = new ArrayList<>();
        String sql = "SELECT * FROM symptoms ORDER BY name ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapSymptom(rs));
            }
        } catch (SQLException e) {
            Logger.error("Error finding all symptoms", e);
        }
        return list;
    }

    public Symptom findById(int symptomId) {
        String sql = "SELECT * FROM symptoms WHERE symptom_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, symptomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSymptom(rs);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding symptom by ID: " + symptomId, e);
        }
        return null;
    }

    public List<Symptom> findByCategory(String category) {
        List<Symptom> list = new ArrayList<>();
        String sql = "SELECT * FROM symptoms WHERE LOWER(category) = LOWER(?) ORDER BY name ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSymptom(rs));
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding symptoms by category: " + category, e);
        }
        return list;
    }

    public List<Symptom> search(String keyword) {
        List<Symptom> list = new ArrayList<>();
        String sql = "SELECT * FROM symptoms WHERE LOWER(name) LIKE ? OR LOWER(category) LIKE ? ORDER BY name ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSymptom(rs));
                }
            }
        } catch (SQLException e) {
            Logger.error("Error searching symptoms: " + keyword, e);
        }
        return list;
    }

    public List<String> getAllCategories() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM symptoms ORDER BY category ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            Logger.error("Error fetching symptom categories", e);
        }
        return list;
    }

    private Symptom mapSymptom(ResultSet rs) throws SQLException {
        return new Symptom(
                rs.getInt("symptom_id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getInt("severity_weight")
        );
    }
}

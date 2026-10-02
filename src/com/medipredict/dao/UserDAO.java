package com.medipredict.dao;

import com.medipredict.database.DatabaseConnection;
import com.medipredict.model.Role;
import com.medipredict.model.User;
import com.medipredict.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding user by email: " + email, e);
        }
        return null;
    }

    public User findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding user by ID: " + userId, e);
        }
        return null;
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) AS cnt FROM users WHERE LOWER(email) = LOWER(?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt") > 0;
                }
            }
        } catch (SQLException e) {
            Logger.error("Error checking email existence: " + email, e);
        }
        return false;
    }

    public int create(User user) {
        String sql = "INSERT INTO users (email, password_hash, salt, role, full_name, phone) VALUES (?, ?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getEmail().trim().toLowerCase());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getSalt());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getFullName().trim());
            ps.setString(6, user.getPhone());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    user.setUserId(generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            Logger.error("Error creating user: " + user.getEmail(), e);
        }
        return -1;
    }

    public boolean update(User user) {
        String sql = "UPDATE users SET full_name = ?, phone = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName().trim());
            ps.setString(2, user.getPhone());
            ps.setInt(3, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("Error updating user ID: " + user.getUserId(), e);
            return false;
        }
    }

    public boolean updatePassword(int userId, String newHash, String newSalt) {
        String sql = "UPDATE users SET password_hash = ?, salt = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setString(2, newSalt);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("Error updating password for user ID: " + userId, e);
            return false;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setSalt(rs.getString("salt"));
        u.setRole(Role.fromString(rs.getString("role")));
        u.setFullName(rs.getString("full_name"));
        u.setPhone(rs.getString("phone"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        u.setUpdatedAt(rs.getTimestamp("updated_at"));
        return u;
    }
}

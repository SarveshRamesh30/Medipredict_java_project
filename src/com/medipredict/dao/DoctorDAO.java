package com.medipredict.dao;

import com.medipredict.database.DatabaseConnection;
import com.medipredict.model.Doctor;
import com.medipredict.model.Role;
import com.medipredict.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    public Doctor findByUserId(int userId) {
        String sql = "SELECT u.*, d.doctor_id, d.specialization, d.hospital_clinic, d.experience_years, d.consultation_fee, d.bio, d.availability_hours " +
                "FROM doctors d JOIN users u ON d.user_id = u.user_id WHERE d.user_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapDoctor(rs);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding doctor by user ID: " + userId, e);
        }
        return null;
    }

    public Doctor findById(int doctorId) {
        String sql = "SELECT u.*, d.doctor_id, d.specialization, d.hospital_clinic, d.experience_years, d.consultation_fee, d.bio, d.availability_hours " +
                "FROM doctors d JOIN users u ON d.user_id = u.user_id WHERE d.doctor_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapDoctor(rs);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding doctor by doctor ID: " + doctorId, e);
        }
        return null;
    }

    public int create(Doctor doctor) {
        String sql = "INSERT INTO doctors (user_id, specialization, hospital_clinic, experience_years, consultation_fee, bio, availability_hours) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, doctor.getUserId());
            ps.setString(2, doctor.getSpecialization());
            ps.setString(3, doctor.getHospitalClinic());
            ps.setInt(4, doctor.getExperienceYears());
            ps.setBigDecimal(5, doctor.getConsultationFee());
            ps.setString(6, doctor.getBio());
            ps.setString(7, doctor.getAvailabilityHours());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    doctor.setDoctorId(generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            Logger.error("Error creating doctor profile for user ID: " + doctor.getUserId(), e);
        }
        return -1;
    }

    public boolean update(Doctor doctor) {
        String sql = "UPDATE doctors SET specialization = ?, hospital_clinic = ?, experience_years = ?, consultation_fee = ?, bio = ?, availability_hours = ? WHERE doctor_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, doctor.getSpecialization());
            ps.setString(2, doctor.getHospitalClinic());
            ps.setInt(3, doctor.getExperienceYears());
            ps.setBigDecimal(4, doctor.getConsultationFee());
            ps.setString(5, doctor.getBio());
            ps.setString(6, doctor.getAvailabilityHours());
            ps.setInt(7, doctor.getDoctorId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("Error updating doctor ID: " + doctor.getDoctorId(), e);
            return false;
        }
    }

    public List<Doctor> findAll() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT u.*, d.doctor_id, d.specialization, d.hospital_clinic, d.experience_years, d.consultation_fee, d.bio, d.availability_hours " +
                "FROM doctors d JOIN users u ON d.user_id = u.user_id ORDER BY u.full_name ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapDoctor(rs));
            }
        } catch (SQLException e) {
            Logger.error("Error finding all doctors", e);
        }
        return list;
    }

    public List<Doctor> searchDoctors(String keyword, String specialization) {
        List<Doctor> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT u.*, d.doctor_id, d.specialization, d.hospital_clinic, d.experience_years, d.consultation_fee, d.bio, d.availability_hours " +
                "FROM doctors d JOIN users u ON d.user_id = u.user_id WHERE 1=1 ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasSpec = specialization != null && !specialization.trim().isEmpty() && !"All Specializations".equalsIgnoreCase(specialization.trim());

        if (hasKeyword) {
            sql.append("AND (LOWER(u.full_name) LIKE ? OR LOWER(d.hospital_clinic) LIKE ? OR LOWER(d.specialization) LIKE ?) ");
        }
        if (hasSpec) {
            sql.append("AND LOWER(d.specialization) = LOWER(?) ");
        }
        sql.append("ORDER BY d.experience_years DESC, u.full_name ASC;");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (hasKeyword) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                ps.setString(idx++, pattern);
                ps.setString(idx++, pattern);
                ps.setString(idx++, pattern);
            }
            if (hasSpec) {
                ps.setString(idx++, specialization.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDoctor(rs));
                }
            }
        } catch (SQLException e) {
            Logger.error("Error searching doctors", e);
        }
        return list;
    }

    public List<String> getAllSpecializations() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT specialization FROM doctors ORDER BY specialization ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("specialization"));
            }
        } catch (SQLException e) {
            Logger.error("Error getting specializations", e);
        }
        return list;
    }

    private Doctor mapDoctor(ResultSet rs) throws SQLException {
        Doctor d = new Doctor();
        d.setUserId(rs.getInt("user_id"));
        d.setEmail(rs.getString("email"));
        d.setPasswordHash(rs.getString("password_hash"));
        d.setSalt(rs.getString("salt"));
        d.setRole(Role.DOCTOR);
        d.setFullName(rs.getString("full_name"));
        d.setPhone(rs.getString("phone"));
        d.setCreatedAt(rs.getTimestamp("created_at"));
        d.setUpdatedAt(rs.getTimestamp("updated_at"));

        d.setDoctorId(rs.getInt("doctor_id"));
        d.setSpecialization(rs.getString("specialization"));
        d.setHospitalClinic(rs.getString("hospital_clinic"));
        d.setExperienceYears(rs.getInt("experience_years"));
        d.setConsultationFee(rs.getBigDecimal("consultation_fee"));
        d.setBio(rs.getString("bio"));
        d.setAvailabilityHours(rs.getString("availability_hours"));
        return d;
    }
}

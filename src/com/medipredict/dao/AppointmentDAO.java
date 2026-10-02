package com.medipredict.dao;

import com.medipredict.database.DatabaseConnection;
import com.medipredict.model.Appointment;
import com.medipredict.util.DateTimeUtil;
import com.medipredict.util.Logger;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    public int create(Appointment a) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, reason, status, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getPatientId());
            ps.setInt(2, a.getDoctorId());
            ps.setDate(3, a.getAppointmentDate());
            ps.setString(4, a.getAppointmentTime());
            ps.setString(5, a.getReason());
            ps.setString(6, a.getStatus() != null ? a.getStatus() : "PENDING");
            ps.setString(7, a.getNotes());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    a.setAppointmentId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            Logger.error("Error creating appointment", e);
        }
        return -1;
    }

    public boolean isSlotBooked(int doctorId, Date date, String time) {
        String sql = "SELECT COUNT(*) AS cnt FROM appointments " +
                "WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? AND status != 'CANCELLED';";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, date);
            ps.setString(3, time);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt") > 0;
                }
            }
        } catch (SQLException e) {
            Logger.error("Error checking appointment slot availability", e);
        }
        return false;
    }

    public boolean updateStatus(int appointmentId, String status, String notes) {
        String sql = "UPDATE appointments SET status = ?, notes = COALESCE(?, notes), updated_at = CURRENT_TIMESTAMP WHERE appointment_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, notes);
            ps.setInt(3, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("Error updating appointment status for ID: " + appointmentId, e);
            return false;
        }
    }

    public Appointment findById(int appointmentId) {
        String sql = "SELECT a.*, " +
                "up.full_name AS patient_name, up.email AS patient_email, up.phone AS patient_phone, " +
                "ud.full_name AS doctor_name, d.specialization AS doctor_specialization, d.hospital_clinic AS doctor_hospital " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "JOIN users up ON p.user_id = up.user_id " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "JOIN users ud ON d.user_id = ud.user_id " +
                "WHERE a.appointment_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAppointment(rs);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding appointment by ID: " + appointmentId, e);
        }
        return null;
    }

    public List<Appointment> findByPatientId(int patientId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, " +
                "up.full_name AS patient_name, up.email AS patient_email, up.phone AS patient_phone, " +
                "ud.full_name AS doctor_name, d.specialization AS doctor_specialization, d.hospital_clinic AS doctor_hospital " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "JOIN users up ON p.user_id = up.user_id " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "JOIN users ud ON d.user_id = ud.user_id " +
                "WHERE a.patient_id = ? ORDER BY a.appointment_date DESC, a.appointment_time DESC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAppointment(rs));
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding appointments for patient ID: " + patientId, e);
        }
        return list;
    }

    public List<Appointment> findByDoctorId(int doctorId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, " +
                "up.full_name AS patient_name, up.email AS patient_email, up.phone AS patient_phone, " +
                "ud.full_name AS doctor_name, d.specialization AS doctor_specialization, d.hospital_clinic AS doctor_hospital " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "JOIN users up ON p.user_id = up.user_id " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "JOIN users ud ON d.user_id = ud.user_id " +
                "WHERE a.doctor_id = ? ORDER BY a.appointment_date ASC, a.appointment_time ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAppointment(rs));
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding appointments for doctor ID: " + doctorId, e);
        }
        return list;
    }

    public Appointment getUpcomingByPatientId(int patientId) {
        String sql = "SELECT a.*, " +
                "up.full_name AS patient_name, up.email AS patient_email, up.phone AS patient_phone, " +
                "ud.full_name AS doctor_name, d.specialization AS doctor_specialization, d.hospital_clinic AS doctor_hospital " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "JOIN users up ON p.user_id = up.user_id " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "JOIN users ud ON d.user_id = ud.user_id " +
                "WHERE a.patient_id = ? AND a.status IN ('PENDING', 'CONFIRMED') AND a.appointment_date >= CURRENT_DATE " +
                "ORDER BY a.appointment_date ASC, a.appointment_time ASC LIMIT 1;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAppointment(rs);
                }
            }
        } catch (SQLException e) {
            Logger.error("Error finding upcoming appointment for patient ID: " + patientId, e);
        }
        return null;
    }

    public int countTodayAppointmentsByDoctor(int doctorId) {
        String sql = "SELECT COUNT(*) AS cnt FROM appointments " +
                "WHERE doctor_id = ? AND appointment_date = CURRENT_DATE AND status != 'CANCELLED';";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (SQLException e) {
            Logger.error("Error counting today's appointments for doctor ID: " + doctorId, e);
        }
        return 0;
    }

    public int countPendingByDoctor(int doctorId) {
        String sql = "SELECT COUNT(*) AS cnt FROM appointments " +
                "WHERE doctor_id = ? AND status = 'PENDING';";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (SQLException e) {
            Logger.error("Error counting pending appointments for doctor ID: " + doctorId, e);
        }
        return 0;
    }

    private Appointment mapAppointment(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setDoctorId(rs.getInt("doctor_id"));
        String aDateStr = rs.getString("appointment_date");
        a.setAppointmentDate(DateTimeUtil.parseDate(aDateStr));
        a.setAppointmentTime(rs.getString("appointment_time"));
        a.setReason(rs.getString("reason"));
        a.setStatus(rs.getString("status"));
        a.setNotes(rs.getString("notes"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        a.setUpdatedAt(rs.getTimestamp("updated_at"));

        a.setPatientName(rs.getString("patient_name"));
        a.setPatientEmail(rs.getString("patient_email"));
        a.setPatientPhone(rs.getString("patient_phone"));
        a.setDoctorName(rs.getString("doctor_name"));
        a.setDoctorSpecialization(rs.getString("doctor_specialization"));
        a.setDoctorHospital(rs.getString("doctor_hospital"));
        return a;
    }
}

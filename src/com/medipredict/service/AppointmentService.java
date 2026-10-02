package com.medipredict.service;

import com.medipredict.dao.AppointmentDAO;
import com.medipredict.model.Appointment;
import com.medipredict.util.ValidationUtil;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class AppointmentService {
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public static class AppointmentResult {
        private final boolean success;
        private final String message;
        private final Appointment appointment;

        public AppointmentResult(boolean success, String message, Appointment appointment) {
            this.success = success;
            this.message = message;
            this.appointment = appointment;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Appointment getAppointment() {
            return appointment;
        }
    }

    public AppointmentResult bookAppointment(int patientId, int doctorId, Date date, String time, String reason) {
        if (patientId <= 0 || doctorId <= 0) {
            return new AppointmentResult(false, "Invalid patient or doctor identifier.", null);
        }
        if (date == null) {
            return new AppointmentResult(false, "Please select an appointment date.", null);
        }
        if (date.toLocalDate().isBefore(LocalDate.now())) {
            return new AppointmentResult(false, "Appointment date cannot be in the past.", null);
        }
        if (!ValidationUtil.isNotEmpty(time)) {
            return new AppointmentResult(false, "Please select an appointment time slot.", null);
        }
        if (!ValidationUtil.isNotEmpty(reason)) {
            return new AppointmentResult(false, "Please provide the reason for consultation.", null);
        }

        // Prevent duplicate booking for the same doctor, date, and time
        if (appointmentDAO.isSlotBooked(doctorId, date, time.trim())) {
            return new AppointmentResult(false, "This appointment slot (" + time + " on " + date + ") is already booked with this doctor. Please choose a different time slot.", null);
        }

        Appointment a = new Appointment();
        a.setPatientId(patientId);
        a.setDoctorId(doctorId);
        a.setAppointmentDate(date);
        a.setAppointmentTime(time.trim());
        a.setReason(reason.trim());
        a.setStatus("PENDING");

        int id = appointmentDAO.create(a);
        if (id != -1) {
            a.setAppointmentId(id);
            return new AppointmentResult(true, "Appointment successfully booked! Status: PENDING confirmation by doctor.", a);
        } else {
            return new AppointmentResult(false, "Failed to book appointment due to a database error. Please try again.", null);
        }
    }

    public List<Appointment> getPatientAppointments(int patientId) {
        return appointmentDAO.findByPatientId(patientId);
    }

    public List<Appointment> getDoctorAppointments(int doctorId) {
        return appointmentDAO.findByDoctorId(doctorId);
    }

    public boolean updateStatus(int appointmentId, String status, String notes) {
        return appointmentDAO.updateStatus(appointmentId, status, notes);
    }

    public boolean cancelAppointment(int appointmentId, String reason) {
        return appointmentDAO.updateStatus(appointmentId, "CANCELLED", reason);
    }
}

package com.medipredict.service;

import com.medipredict.dao.AppointmentDAO;
import com.medipredict.dao.DoctorDAO;
import com.medipredict.dao.PatientDAO;
import com.medipredict.dao.PredictionDAO;
import com.medipredict.dao.UserDAO;
import com.medipredict.model.Doctor;
import com.medipredict.model.Patient;
import com.medipredict.model.Prediction;

import java.util.List;

public class DoctorService {
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final UserDAO userDAO = new UserDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final PredictionDAO predictionDAO = new PredictionDAO();

    public static class DoctorDashboardSummary {
        public Doctor doctor;
        public int todayAppointmentsCount;
        public int pendingAppointmentsCount;
        public int totalConsultingPatients;
        public int unreadMessagesCount;
        public List<Prediction> recentPatientPredictions;
    }

    public Doctor getDoctorByUserId(int userId) {
        return doctorDAO.findByUserId(userId);
    }

    public Doctor getDoctorById(int doctorId) {
        return doctorDAO.findById(doctorId);
    }

    public boolean updateProfile(Doctor doctor) {
        if (doctor == null) return false;
        boolean userOk = userDAO.update(doctor);
        boolean doctorOk = doctorDAO.update(doctor);
        if (userOk && doctorOk) {
            SessionManager.getInstance().setCurrentDoctor(doctor);
            SessionManager.getInstance().getCurrentUser().setFullName(doctor.getFullName());
            SessionManager.getInstance().getCurrentUser().setPhone(doctor.getPhone());
            return true;
        }
        return false;
    }

    public List<Doctor> getAllDoctors() {
        return doctorDAO.findAll();
    }

    public List<Doctor> searchDoctors(String keyword, String specialization) {
        return doctorDAO.searchDoctors(keyword, specialization);
    }

    public List<String> getAllSpecializations() {
        return doctorDAO.getAllSpecializations();
    }

    public List<Patient> getAllPatients() {
        return patientDAO.findAll();
    }

    public List<Patient> searchPatients(String keyword) {
        return patientDAO.searchPatients(keyword);
    }

    public DoctorDashboardSummary getDashboardSummary(int doctorId) {
        DoctorDashboardSummary s = new DoctorDashboardSummary();
        s.doctor = doctorDAO.findById(doctorId);
        s.todayAppointmentsCount = appointmentDAO.countTodayAppointmentsByDoctor(doctorId);
        s.pendingAppointmentsCount = appointmentDAO.countPendingByDoctor(doctorId);
        s.totalConsultingPatients = patientDAO.findAll().size();
        s.recentPatientPredictions = predictionDAO.findRecentPredictions(5);
        return s;
    }
}

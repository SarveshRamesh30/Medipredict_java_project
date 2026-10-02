package com.medipredict.service;

import com.medipredict.dao.AppointmentDAO;
import com.medipredict.dao.PatientDAO;
import com.medipredict.dao.PredictionDAO;
import com.medipredict.dao.RecommendationDAO;
import com.medipredict.dao.UserDAO;
import com.medipredict.model.Appointment;
import com.medipredict.model.Patient;
import com.medipredict.model.Prediction;
import com.medipredict.model.Recommendation;
import com.medipredict.model.User;
import com.medipredict.util.Logger;
import com.medipredict.util.PasswordUtil;
import com.medipredict.util.ValidationUtil;

public class PatientService {
    private final PatientDAO patientDAO = new PatientDAO();
    private final UserDAO userDAO = new UserDAO();
    private final PredictionDAO predictionDAO = new PredictionDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final RecommendationDAO recommendationDAO = new RecommendationDAO();

    public static class PatientDashboardSummary {
        public Patient patient;
        public int totalPredictions;
        public Prediction latestPrediction;
        public Appointment upcomingAppointment;
        public Recommendation latestRecommendation;
        public int unreadMessagesCount;
    }

    public Patient getPatientByUserId(int userId) {
        return patientDAO.findByUserId(userId);
    }

    public Patient getPatientById(int patientId) {
        return patientDAO.findById(patientId);
    }

    public boolean updateProfile(Patient patient) {
        if (patient == null) return false;
        boolean userOk = userDAO.update(patient);
        boolean patientOk = patientDAO.update(patient);
        if (userOk && patientOk) {
            SessionManager.getInstance().setCurrentPatient(patient);
            SessionManager.getInstance().getCurrentUser().setFullName(patient.getFullName());
            SessionManager.getInstance().getCurrentUser().setPhone(patient.getPhone());
            return true;
        }
        return false;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        if (!ValidationUtil.isStrongPassword(newPassword)) {
            return false;
        }
        User user = userDAO.findById(userId);
        if (user == null) return false;

        if (!PasswordUtil.verifyPassword(oldPassword, user.getPasswordHash(), user.getSalt())) {
            return false;
        }

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(newPassword, salt);
        return userDAO.updatePassword(userId, hash, salt);
    }

    public PatientDashboardSummary getDashboardSummary(int patientId) {
        PatientDashboardSummary summary = new PatientDashboardSummary();
        summary.patient = patientDAO.findById(patientId);
        summary.totalPredictions = predictionDAO.countByPatientId(patientId);
        summary.latestPrediction = predictionDAO.getLatestPredictionByPatientId(patientId);
        summary.upcomingAppointment = appointmentDAO.getUpcomingByPatientId(patientId);
        summary.latestRecommendation = recommendationDAO.getLatestByPatientId(patientId);
        return summary;
    }
}

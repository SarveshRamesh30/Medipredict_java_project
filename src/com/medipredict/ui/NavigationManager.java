package com.medipredict.ui;

import com.medipredict.model.Doctor;
import com.medipredict.model.PredictionResultData;
import com.medipredict.service.SessionManager;
import com.medipredict.ui.screens.AppointmentBookingScreen;
import com.medipredict.ui.screens.DoctorAppointmentsScreen;
import com.medipredict.ui.screens.DoctorDashboardScreen;
import com.medipredict.ui.screens.DoctorDirectoryScreen;
import com.medipredict.ui.screens.DoctorPatientsScreen;
import com.medipredict.ui.screens.DoctorProfileScreen;
import com.medipredict.ui.screens.DoctorRecommendationsScreen;
import com.medipredict.ui.screens.ForgotPasswordScreen;
import com.medipredict.ui.screens.LandingScreen;
import com.medipredict.ui.screens.LoginScreen;
import com.medipredict.ui.screens.MessagingScreen;
import com.medipredict.ui.screens.PatientAppointmentsScreen;
import com.medipredict.ui.screens.PatientDashboardScreen;
import com.medipredict.ui.screens.PatientProfileScreen;
import com.medipredict.ui.screens.PatientRecommendationsScreen;
import com.medipredict.ui.screens.PredictionHistoryScreen;
import com.medipredict.ui.screens.PredictionResultScreen;
import com.medipredict.ui.screens.RegisterScreen;
import com.medipredict.ui.screens.SymptomPredictionScreen;
import com.medipredict.util.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class NavigationManager {
    public static final String SCREEN_LANDING = "LANDING";
    public static final String SCREEN_LOGIN = "LOGIN";
    public static final String SCREEN_REGISTER = "REGISTER";
    public static final String SCREEN_FORGOT_PASSWORD = "FORGOT_PASSWORD";
    public static final String SCREEN_PATIENT_DASHBOARD = "PATIENT_DASHBOARD";
    public static final String SCREEN_DOCTOR_DASHBOARD = "DOCTOR_DASHBOARD";
    public static final String SCREEN_SYMPTOM_PREDICTION = "SYMPTOM_PREDICTION";
    public static final String SCREEN_PREDICTION_RESULT = "PREDICTION_RESULT";
    public static final String SCREEN_PREDICTION_HISTORY = "PREDICTION_HISTORY";
    public static final String SCREEN_DOCTOR_DIRECTORY = "DOCTOR_DIRECTORY";
    public static final String SCREEN_APPOINTMENT_BOOKING = "APPOINTMENT_BOOKING";
    public static final String SCREEN_PATIENT_APPOINTMENTS = "PATIENT_APPOINTMENTS";
    public static final String SCREEN_DOCTOR_APPOINTMENTS = "DOCTOR_APPOINTMENTS";
    public static final String SCREEN_DOCTOR_PATIENTS = "DOCTOR_PATIENTS";
    public static final String SCREEN_DOCTOR_RECOMMENDATIONS = "DOCTOR_RECOMMENDATIONS";
    public static final String SCREEN_PATIENT_RECOMMENDATIONS = "PATIENT_RECOMMENDATIONS";
    public static final String SCREEN_PATIENT_PROFILE = "PATIENT_PROFILE";
    public static final String SCREEN_DOCTOR_PROFILE = "DOCTOR_PROFILE";
    public static final String SCREEN_MESSAGING = "MESSAGING";

    private final MainFrame mainFrame;

    public NavigationManager(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public void setView(JPanel panel) {
        mainFrame.getContentPane().removeAll();
        mainFrame.getContentPane().add(panel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public void navigateTo(String screenKey) {
        Logger.info("Navigating to screen: " + screenKey);
        switch (screenKey) {
            case SCREEN_LANDING:
                showLanding();
                break;
            case SCREEN_LOGIN:
                showLogin();
                break;
            case SCREEN_REGISTER:
                showRegister();
                break;
            case SCREEN_FORGOT_PASSWORD:
                showForgotPassword();
                break;
            case SCREEN_PATIENT_DASHBOARD:
                showPatientDashboard();
                break;
            case SCREEN_DOCTOR_DASHBOARD:
                showDoctorDashboard();
                break;
            case SCREEN_SYMPTOM_PREDICTION:
                showSymptomPrediction();
                break;
            case SCREEN_PREDICTION_HISTORY:
                showPredictionHistory();
                break;
            case SCREEN_DOCTOR_DIRECTORY:
                showDoctorDirectory();
                break;
            case SCREEN_PATIENT_APPOINTMENTS:
                showPatientAppointments();
                break;
            case SCREEN_DOCTOR_APPOINTMENTS:
                showDoctorAppointments();
                break;
            case SCREEN_DOCTOR_PATIENTS:
                showDoctorPatients();
                break;
            case SCREEN_DOCTOR_RECOMMENDATIONS:
                showDoctorRecommendations();
                break;
            case SCREEN_PATIENT_RECOMMENDATIONS:
                showPatientRecommendations();
                break;
            case SCREEN_PATIENT_PROFILE:
                showPatientProfile();
                break;
            case SCREEN_DOCTOR_PROFILE:
                showDoctorProfile();
                break;
            case SCREEN_MESSAGING:
                showMessaging(null);
                break;
            default:
                showLanding();
        }
    }

    public void showLanding() {
        setView(new LandingScreen(this));
    }

    public void showLogin() {
        setView(new LoginScreen(this));
    }

    public void showRegister() {
        setView(new RegisterScreen(this));
    }

    public void showForgotPassword() {
        setView(new ForgotPasswordScreen(this));
    }

    public void showPatientDashboard() {
        setView(new PatientDashboardScreen(this));
    }

    public void showDoctorDashboard() {
        setView(new DoctorDashboardScreen(this));
    }

    public void showSymptomPrediction() {
        setView(new SymptomPredictionScreen(this));
    }

    public void showPredictionResult(PredictionResultData data) {
        setView(new PredictionResultScreen(this, data));
    }

    public void showPredictionHistory() {
        setView(new PredictionHistoryScreen(this));
    }

    public void showDoctorDirectory() {
        setView(new DoctorDirectoryScreen(this));
    }

    public void showAppointmentBooking(Doctor doctor) {
        setView(new AppointmentBookingScreen(this, doctor));
    }

    public void showPatientAppointments() {
        setView(new PatientAppointmentsScreen(this));
    }

    public void showDoctorAppointments() {
        setView(new DoctorAppointmentsScreen(this));
    }

    public void showDoctorPatients() {
        setView(new DoctorPatientsScreen(this));
    }

    public void showDoctorRecommendations() {
        setView(new DoctorRecommendationsScreen(this));
    }

    public void showPatientRecommendations() {
        setView(new PatientRecommendationsScreen(this));
    }

    public void showPatientProfile() {
        setView(new PatientProfileScreen(this));
    }

    public void showDoctorProfile() {
        setView(new DoctorProfileScreen(this));
    }

    public void showMessaging(Integer targetUserId) {
        setView(new MessagingScreen(this, targetUserId));
    }

    public void logout() {
        SessionManager.getInstance().clearSession();
        showLanding();
    }
}

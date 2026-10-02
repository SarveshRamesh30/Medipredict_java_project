package com.medipredict;

import com.medipredict.dao.AppointmentDAO;
import com.medipredict.dao.ConditionDAO;
import com.medipredict.dao.DoctorDAO;
import com.medipredict.dao.MessageDAO;
import com.medipredict.dao.PatientDAO;
import com.medipredict.dao.PredictionDAO;
import com.medipredict.dao.RecommendationDAO;
import com.medipredict.dao.SymptomDAO;
import com.medipredict.dao.UserDAO;
import com.medipredict.database.DatabaseInitializer;
import com.medipredict.model.Doctor;
import com.medipredict.model.Patient;
import com.medipredict.model.PredictionResultData;
import com.medipredict.model.Role;
import com.medipredict.model.Symptom;
import com.medipredict.model.User;
import com.medipredict.prediction.ConditionKnowledgeBase;
import com.medipredict.service.AppointmentService;
import com.medipredict.service.AuthService;
import com.medipredict.service.DoctorService;
import com.medipredict.service.MessageService;
import com.medipredict.service.PatientService;
import com.medipredict.service.PredictionService;
import com.medipredict.service.RecommendationService;
import com.medipredict.util.Logger;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IntegrationTest {
    private static int passedTests = 0;
    private static int totalTests = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   MediPredict Headless Automated Test Suite     ");
        System.out.println("=================================================");

        try {
            // 1. Database Initialization
            DatabaseInitializer.initializeDatabase();
            ConditionKnowledgeBase.reload();

            // 2. Run Test Cases
            testDatabaseSeeding();
            testAuthenticationAndRoles();
            testDuplicateEmailPrevention();
            testPredictionEngineLogic();
            testAppointmentBookingAndConflictPrevention();
            testDoctorRecommendations();
            testPatientDoctorMessaging();
            testProfileUpdates();

            System.out.println("\n=================================================");
            System.out.printf("Test Execution Complete: %d / %d Passed (%.1f%%)\n",
                    passedTests, totalTests, (passedTests * 100.0 / totalTests));
            System.out.println("=================================================");

            if (passedTests == totalTests) {
                System.out.println(">>> ALL TESTS PASSED SUCCESSFULLY! <<<");
                System.exit(0);
            } else {
                System.err.println(">>> SOME TESTS FAILED <<<");
                System.exit(1);
            }

        } catch (Exception e) {
            Logger.error("Test Suite encountered an unexpected exception", e);
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition, String details) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.println("  [PASS] " + testName + (details.isEmpty() ? "" : " - " + details));
        } else {
            System.err.println("  [FAIL] " + testName + " - " + details);
        }
    }

    private static void testDatabaseSeeding() {
        System.out.println("\n[1] Testing Database Schema & Sample Data Seeding...");
        SymptomDAO symptomDAO = new SymptomDAO();
        ConditionDAO conditionDAO = new ConditionDAO();
        PatientDAO patientDAO = new PatientDAO();
        DoctorDAO doctorDAO = new DoctorDAO();

        List<Symptom> symptoms = symptomDAO.findAll();
        assertTrue("Symptoms Seeding", symptoms.size() >= 20, "Found " + symptoms.size() + " symptoms");

        var conditions = conditionDAO.findAll();
        assertTrue("Conditions Seeding", conditions.size() >= 10, "Found " + conditions.size() + " conditions");

        var patients = patientDAO.findAll();
        assertTrue("Patients Seeding", !patients.isEmpty(), "Found " + patients.size() + " patients");

        var doctors = doctorDAO.findAll();
        assertTrue("Doctors Seeding", doctors.size() >= 5, "Found " + doctors.size() + " specialist doctors");
    }

    private static void testAuthenticationAndRoles() {
        System.out.println("\n[2] Testing Authentication & Role-Based Access...");
        AuthService authService = new AuthService();

        // Test Patient Login
        AuthService.AuthResult pLogin = authService.login("patient@medipredict.com", "patient123", Role.PATIENT);
        assertTrue("Patient Login Success", pLogin.isSuccess(), pLogin.getMessage());

        // Test Doctor Login
        AuthService.AuthResult dLogin = authService.login("doctor@medipredict.com", "doctor123", Role.DOCTOR);
        assertTrue("Doctor Login Success", dLogin.isSuccess(), dLogin.getMessage());

        // Test Role Mismatch Restriction
        AuthService.AuthResult mismatch = authService.login("patient@medipredict.com", "patient123", Role.DOCTOR);
        assertTrue("Role Mismatch Guard", !mismatch.isSuccess(), "Prevented patient from logging into doctor role");

        // Test Invalid Password
        AuthService.AuthResult badPass = authService.login("patient@medipredict.com", "wrongpass", Role.PATIENT);
        assertTrue("Bad Password Rejection", !badPass.isSuccess(), badPass.getMessage());
    }

    private static void testDuplicateEmailPrevention() {
        System.out.println("\n[3] Testing User Registration & Duplicate Email Guard...");
        AuthService authService = new AuthService();

        // Duplicate Patient Registration
        AuthService.AuthResult dup = authService.registerPatient(
                "Duplicate User", "patient@medipredict.com", "1234567890", "pass123",
                Date.valueOf("2000-01-01"), "Male", "O+", "None", "None"
        );
        assertTrue("Duplicate Email Rejection", !dup.isSuccess(), dup.getMessage());

        // Unique New Patient Registration
        String testEmail = "newtest" + System.currentTimeMillis() + "@medipredict.com";
        AuthService.AuthResult newReg = authService.registerPatient(
                "New Test Patient", testEmail, "+1 (555) 777-8888", "testpass123",
                Date.valueOf("1995-04-10"), "Female", "B+", "None", "+1 555 9999"
        );
        assertTrue("New Patient Registration", newReg.isSuccess(), newReg.getMessage());
    }

    private static void testPredictionEngineLogic() {
        System.out.println("\n[4] Testing Symptom Prediction Engine Logic...");
        PredictionService predictionService = new PredictionService();
        SymptomDAO symptomDAO = new SymptomDAO();

        List<Symptom> migraineSymptoms = new ArrayList<>();
        migraineSymptoms.addAll(symptomDAO.search("Headache"));
        migraineSymptoms.addAll(symptomDAO.search("Sensitivity to Light"));
        migraineSymptoms.addAll(symptomDAO.search("Nausea"));

        PredictionResultData result = predictionService.runPrediction(1, migraineSymptoms, "Test migraine prediction");

        assertTrue("Prediction Result Generated", result != null, "Result object created");
        assertTrue("Primary Condition Match", "Migraine".equalsIgnoreCase(result.getPrimaryCondition().getName()),
                "Identified: " + result.getPrimaryCondition().getName() + " with score: " + result.getMatchScore() + "%");
        assertTrue("Confidence Score", "High".equalsIgnoreCase(result.getConfidenceLevel()),
                "Confidence level: " + result.getConfidenceLevel());
        assertTrue("Medical Disclaimer Included", result.getDisclaimer() != null && result.getDisclaimer().toLowerCase().contains("medical diagnosis"),
                "Disclaimer verified");
    }

    private static void testAppointmentBookingAndConflictPrevention() {
        System.out.println("\n[5] Testing Appointment Booking & Slot Conflict Prevention...");
        AppointmentService appointmentService = new AppointmentService();

        Date futureDate = Date.valueOf(LocalDate.now().plusDays(10 + (int)((System.currentTimeMillis() / 1000) % 500)));
        String timeSlot = "11:30 AM";

        // Initial Booking
        var res1 = appointmentService.bookAppointment(1, 1, futureDate, timeSlot, "Integration test booking session");
        assertTrue("Appointment Booking 1", res1.isSuccess(), res1.getMessage());

        // Duplicate Booking attempt on same doctor, date, and time
        var res2 = appointmentService.bookAppointment(2, 1, futureDate, timeSlot, "Integration test conflicting booking attempt");
        assertTrue("Duplicate Slot Collision Prevention", !res2.isSuccess(), res2.getMessage());
    }

    private static void testDoctorRecommendations() {
        System.out.println("\n[6] Testing Doctor Recommendations Workflow...");
        RecommendationService recService = new RecommendationService();

        var res = recService.createRecommendation(
                1, 2, 1,
                "Follow up in 2 weeks for migraine symptom evaluation.",
                "Avoid late night screen time and drink minimum 2.5L water daily.",
                Date.valueOf(LocalDate.now().plusDays(14)),
                "Test recommendation note"
        );
        assertTrue("Doctor Recommendation Created", res.isSuccess(), res.getMessage());

        var list = recService.getPatientRecommendations(1);
        assertTrue("Patient Retrieved Recommendations", !list.isEmpty(), "Found " + list.size() + " recommendations");
    }

    private static void testPatientDoctorMessaging() {
        System.out.println("\n[7] Testing Database-backed Messaging System...");
        MessageService messageService = new MessageService();

        boolean sent1 = messageService.sendMessage(1, 3, "Hello Doctor, this is an automated integration message test.");
        assertTrue("Patient Sent Message", sent1, "Message 1 delivered");

        boolean sent2 = messageService.sendMessage(3, 1, "Hello Patient, I have received your symptom query.");
        assertTrue("Doctor Replied Message", sent2, "Message 2 delivered");

        var conversation = messageService.getConversation(1, 3);
        assertTrue("Conversation History", conversation.size() >= 2, "Conversation has " + conversation.size() + " messages");
    }

    private static void testProfileUpdates() {
        System.out.println("\n[8] Testing Profile CRUD & Password Updates...");
        PatientService patientService = new PatientService();
        PatientDAO patientDAO = new PatientDAO();

        Patient p = patientDAO.findById(1);
        p.setEmergencyContact("+1 (555) 000-1111 (Updated Contact)");
        boolean updated = patientService.updateProfile(p);
        assertTrue("Patient Profile Updated", updated, "Emergency contact updated");

        Patient refreshed = patientDAO.findById(1);
        assertTrue("Profile Persistence", "+1 (555) 000-1111 (Updated Contact)".equals(refreshed.getEmergencyContact()),
                "Persisted value: " + refreshed.getEmergencyContact());
    }
}

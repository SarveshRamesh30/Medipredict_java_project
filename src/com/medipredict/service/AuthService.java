package com.medipredict.service;

import com.medipredict.dao.DoctorDAO;
import com.medipredict.dao.PatientDAO;
import com.medipredict.dao.UserDAO;
import com.medipredict.model.Doctor;
import com.medipredict.model.Patient;
import com.medipredict.model.Role;
import com.medipredict.model.User;
import com.medipredict.util.Logger;
import com.medipredict.util.PasswordUtil;
import com.medipredict.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.Date;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();

    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final User user;

        public AuthResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public User getUser() {
            return user;
        }
    }

    public AuthResult login(String email, String password, Role expectedRole) {
        if (!ValidationUtil.isNotEmpty(email) || !ValidationUtil.isNotEmpty(password)) {
            return new AuthResult(false, "Please enter both email and password.", null);
        }

        if (!ValidationUtil.isValidEmail(email)) {
            return new AuthResult(false, "Please enter a valid email address format.", null);
        }

        User user = userDAO.findByEmail(email.trim());
        if (user == null) {
            return new AuthResult(false, "Invalid email or password.", null);
        }

        if (expectedRole != null && user.getRole() != expectedRole) {
            return new AuthResult(false, "Access restricted: This account is registered as a " +
                    user.getRole().name().toLowerCase() + ". Please select the correct login role tab.", null);
        }

        boolean passwordMatch = PasswordUtil.verifyPassword(password, user.getPasswordHash(), user.getSalt());
        if (!passwordMatch) {
            return new AuthResult(false, "Invalid email or password.", null);
        }

        // Initialize session
        Patient patient = null;
        Doctor doctor = null;
        if (user.getRole() == Role.PATIENT) {
            patient = patientDAO.findByUserId(user.getUserId());
        } else if (user.getRole() == Role.DOCTOR) {
            doctor = doctorDAO.findByUserId(user.getUserId());
        }

        SessionManager.getInstance().setSession(user, patient, doctor);
        return new AuthResult(true, "Login successful. Welcome, " + user.getFullName() + "!", user);
    }

    public AuthResult registerPatient(String fullName, String email, String phone, String password,
                                      Date dob, String gender, String bloodGroup, String medicalHistory, String emergencyContact) {
        if (!ValidationUtil.isNotEmpty(fullName)) {
            return new AuthResult(false, "Full Name is required.", null);
        }
        if (!ValidationUtil.isValidEmail(email)) {
            return new AuthResult(false, "Please enter a valid email address.", null);
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            return new AuthResult(false, "Please enter a valid contact phone number.", null);
        }
        if (!ValidationUtil.isStrongPassword(password)) {
            return new AuthResult(false, "Password must be at least 6 characters long.", null);
        }
        if (userDAO.emailExists(email)) {
            return new AuthResult(false, "An account with this email address already exists. Please log in.", null);
        }

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);

        User user = new User(0, email.trim().toLowerCase(), hash, salt, Role.PATIENT, fullName.trim(), phone.trim());
        int userId = userDAO.create(user);
        if (userId == -1) {
            return new AuthResult(false, "Unable to create user account. Please try again.", null);
        }
        user.setUserId(userId);

        Patient patient = new Patient();
        patient.setUserId(userId);
        patient.setDob(dob);
        patient.setGender(gender != null ? gender : "Not Specified");
        patient.setBloodGroup(bloodGroup != null ? bloodGroup : "Unknown");
        patient.setMedicalHistory(medicalHistory);
        patient.setEmergencyContact(emergencyContact);

        int patientId = patientDAO.create(patient);
        if (patientId == -1) {
            return new AuthResult(false, "Account created, but patient profile setup encountered an issue.", user);
        }
        patient.setPatientId(patientId);

        Logger.info("Registered new patient: " + user.getEmail());
        return new AuthResult(true, "Patient registration successful! You may now log in.", user);
    }

    public AuthResult registerDoctor(String fullName, String email, String phone, String password,
                                     String specialization, String hospitalClinic, int experienceYears,
                                     BigDecimal consultationFee, String bio, String availabilityHours) {
        if (!ValidationUtil.isNotEmpty(fullName)) {
            return new AuthResult(false, "Doctor Full Name is required.", null);
        }
        if (!ValidationUtil.isValidEmail(email)) {
            return new AuthResult(false, "Please enter a valid email address.", null);
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            return new AuthResult(false, "Please enter a valid contact phone number.", null);
        }
        if (!ValidationUtil.isStrongPassword(password)) {
            return new AuthResult(false, "Password must be at least 6 characters long.", null);
        }
        if (!ValidationUtil.isNotEmpty(specialization)) {
            return new AuthResult(false, "Specialization is required.", null);
        }
        if (!ValidationUtil.isNotEmpty(hospitalClinic)) {
            return new AuthResult(false, "Hospital/Clinic name is required.", null);
        }
        if (userDAO.emailExists(email)) {
            return new AuthResult(false, "An account with this email address already exists. Please log in.", null);
        }

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);

        User user = new User(0, email.trim().toLowerCase(), hash, salt, Role.DOCTOR, fullName.trim(), phone.trim());
        int userId = userDAO.create(user);
        if (userId == -1) {
            return new AuthResult(false, "Unable to create doctor account. Please try again.", null);
        }
        user.setUserId(userId);

        Doctor doctor = new Doctor();
        doctor.setUserId(userId);
        doctor.setSpecialization(specialization.trim());
        doctor.setHospitalClinic(hospitalClinic.trim());
        doctor.setExperienceYears(Math.max(0, experienceYears));
        doctor.setConsultationFee(consultationFee != null ? consultationFee : BigDecimal.valueOf(50.00));
        doctor.setBio(bio);
        doctor.setAvailabilityHours(availabilityHours != null && !availabilityHours.isEmpty() ? availabilityHours : "9:00 AM - 5:00 PM");

        int doctorId = doctorDAO.create(doctor);
        if (doctorId == -1) {
            return new AuthResult(false, "Account created, but doctor profile setup encountered an issue.", user);
        }
        doctor.setDoctorId(doctorId);

        Logger.info("Registered new doctor: " + user.getEmail());
        return new AuthResult(true, "Doctor registration successful! You may now log in.", user);
    }

    public AuthResult resetPassword(String email, String newPassword) {
        if (!ValidationUtil.isValidEmail(email)) {
            return new AuthResult(false, "Please enter a valid email address.", null);
        }
        if (!ValidationUtil.isStrongPassword(newPassword)) {
            return new AuthResult(false, "New password must be at least 6 characters long.", null);
        }

        User user = userDAO.findByEmail(email.trim());
        if (user == null) {
            return new AuthResult(false, "No account found registered with email: " + email, null);
        }

        String newSalt = PasswordUtil.generateSalt();
        String newHash = PasswordUtil.hashPassword(newPassword, newSalt);

        boolean updated = userDAO.updatePassword(user.getUserId(), newHash, newSalt);
        if (updated) {
            return new AuthResult(true, "Password has been successfully updated. Please log in with your new password.", user);
        } else {
            return new AuthResult(false, "Failed to update password. Please try again.", null);
        }
    }

    public void logout() {
        SessionManager.getInstance().clearSession();
    }
}

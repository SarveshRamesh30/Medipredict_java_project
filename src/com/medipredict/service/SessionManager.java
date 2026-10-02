package com.medipredict.service;

import com.medipredict.model.Doctor;
import com.medipredict.model.Patient;
import com.medipredict.model.Role;
import com.medipredict.model.User;
import com.medipredict.util.Logger;

public class SessionManager {
    private static SessionManager instance;

    private User currentUser;
    private Patient currentPatient;
    private Doctor currentDoctor;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public synchronized void setSession(User user, Patient patient, Doctor doctor) {
        this.currentUser = user;
        this.currentPatient = patient;
        this.currentDoctor = doctor;
        Logger.info("Session established for: " + (user != null ? user.getEmail() : "None") + " (" + (user != null ? user.getRole() : "None") + ")");
    }

    public synchronized void clearSession() {
        Logger.info("Session terminated for: " + (currentUser != null ? currentUser.getEmail() : "Anonymous"));
        this.currentUser = null;
        this.currentPatient = null;
        this.currentDoctor = null;
    }

    public synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    public synchronized boolean isPatient() {
        return currentUser != null && currentUser.getRole() == Role.PATIENT;
    }

    public synchronized boolean isDoctor() {
        return currentUser != null && currentUser.getRole() == Role.DOCTOR;
    }

    public synchronized User getCurrentUser() {
        return currentUser;
    }

    public synchronized Patient getCurrentPatient() {
        return currentPatient;
    }

    public synchronized void setCurrentPatient(Patient currentPatient) {
        this.currentPatient = currentPatient;
    }

    public synchronized Doctor getCurrentDoctor() {
        return currentDoctor;
    }

    public synchronized void setCurrentDoctor(Doctor currentDoctor) {
        this.currentDoctor = currentDoctor;
    }
}

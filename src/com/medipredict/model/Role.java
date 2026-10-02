package com.medipredict.model;

public enum Role {
    PATIENT,
    DOCTOR;

    public static Role fromString(String roleStr) {
        if (roleStr == null) return PATIENT;
        try {
            return Role.valueOf(roleStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return PATIENT;
        }
    }
}

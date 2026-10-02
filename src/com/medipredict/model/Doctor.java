package com.medipredict.model;

import java.math.BigDecimal;

public class Doctor extends User {
    private int doctorId;
    private String specialization;
    private String hospitalClinic;
    private int experienceYears;
    private BigDecimal consultationFee;
    private String bio;
    private String availabilityHours;

    public Doctor() {
        super();
        setRole(Role.DOCTOR);
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getHospitalClinic() {
        return hospitalClinic;
    }

    public void setHospitalClinic(String hospitalClinic) {
        this.hospitalClinic = hospitalClinic;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvailabilityHours() {
        return availabilityHours;
    }

    public void setAvailabilityHours(String availabilityHours) {
        this.availabilityHours = availabilityHours;
    }
}

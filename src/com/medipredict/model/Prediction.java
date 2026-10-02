package com.medipredict.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Prediction {
    private int predictionId;
    private int patientId;
    private int primaryConditionId;
    private String conditionName;
    private double matchScore; // e.g. 85.5%
    private String confidenceLevel; // High, Medium, Low
    private String riskLevel; // Mild, Moderate, High/Urgent
    private String notes;
    private Timestamp createdAt;
    private List<Symptom> symptoms = new ArrayList<>();
    private String symptomNames; // Formatted comma-separated string for easy table display
    private String precautions;
    private String conditionDescription;

    public Prediction() {
    }

    public Prediction(int predictionId, int patientId, int primaryConditionId, String conditionName, double matchScore, String confidenceLevel, String riskLevel, String notes, Timestamp createdAt) {
        this.predictionId = predictionId;
        this.patientId = patientId;
        this.primaryConditionId = primaryConditionId;
        this.conditionName = conditionName;
        this.matchScore = matchScore;
        this.confidenceLevel = confidenceLevel;
        this.riskLevel = riskLevel;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public int getPredictionId() {
        return predictionId;
    }

    public void setPredictionId(int predictionId) {
        this.predictionId = predictionId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getPrimaryConditionId() {
        return primaryConditionId;
    }

    public void setPrimaryConditionId(int primaryConditionId) {
        this.primaryConditionId = primaryConditionId;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

    public String getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(String confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<Symptom> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<Symptom> symptoms) {
        this.symptoms = symptoms;
    }

    public String getSymptomNames() {
        if (symptomNames != null && !symptomNames.isEmpty()) {
            return symptomNames;
        }
        if (symptoms != null && !symptoms.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < symptoms.size(); i++) {
                sb.append(symptoms.get(i).getName());
                if (i < symptoms.size() - 1) sb.append(", ");
            }
            return sb.toString();
        }
        return "";
    }

    public void setSymptomNames(String symptomNames) {
        this.symptomNames = symptomNames;
    }

    public String getPrecautions() {
        return precautions;
    }

    public void setPrecautions(String precautions) {
        this.precautions = precautions;
    }

    public String getConditionDescription() {
        return conditionDescription;
    }

    public void setConditionDescription(String conditionDescription) {
        this.conditionDescription = conditionDescription;
    }
}

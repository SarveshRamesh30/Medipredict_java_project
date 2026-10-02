package com.medipredict.model;

import java.util.ArrayList;
import java.util.List;

public class MedicalCondition {
    private int conditionId;
    private String name;
    private String category;
    private String description;
    private String precautions;
    private String severityLevel; // Mild, Moderate, Severe
    private List<Symptom> associatedSymptoms = new ArrayList<>();

    public MedicalCondition() {
    }

    public MedicalCondition(int conditionId, String name, String category, String description, String precautions, String severityLevel) {
        this.conditionId = conditionId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.precautions = precautions;
        this.severityLevel = severityLevel;
    }

    public int getConditionId() {
        return conditionId;
    }

    public void setConditionId(int conditionId) {
        this.conditionId = conditionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrecautions() {
        return precautions;
    }

    public void setPrecautions(String precautions) {
        this.precautions = precautions;
    }

    public String getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(String severityLevel) {
        this.severityLevel = severityLevel;
    }

    public List<Symptom> getAssociatedSymptoms() {
        return associatedSymptoms;
    }

    public void setAssociatedSymptoms(List<Symptom> associatedSymptoms) {
        this.associatedSymptoms = associatedSymptoms;
    }

    @Override
    public String toString() {
        return name;
    }
}

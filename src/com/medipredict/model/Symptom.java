package com.medipredict.model;

public class Symptom {
    private int symptomId;
    private String name;
    private String category;
    private String description;
    private int severityWeight;

    public Symptom() {
    }

    public Symptom(int symptomId, String name, String category, String description, int severityWeight) {
        this.symptomId = symptomId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.severityWeight = severityWeight;
    }

    public int getSymptomId() {
        return symptomId;
    }

    public void setSymptomId(int symptomId) {
        this.symptomId = symptomId;
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

    public int getSeverityWeight() {
        return severityWeight;
    }

    public void setSeverityWeight(int severityWeight) {
        this.severityWeight = severityWeight;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symptom symptom = (Symptom) o;
        return symptomId == symptom.symptomId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(symptomId);
    }
}

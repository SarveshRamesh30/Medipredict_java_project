package com.medipredict.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PredictionResultData {
    private MedicalCondition primaryCondition;
    private double matchScore; // e.g., 85.0%
    private String confidenceLevel; // "High", "Medium", "Low"
    private String riskLevel; // "Mild", "Moderate", "Severe / Urgent"
    private List<Symptom> inputSymptoms = new ArrayList<>();
    private List<Symptom> matchedSymptoms = new ArrayList<>();
    private List<Symptom> missingConditionSymptoms = new ArrayList<>();
    private List<DifferentialMatch> differentialDiagnoses = new ArrayList<>();
    private String explanation;
    private String generalPrecautions;
    private String disclaimer;
    private Timestamp predictionTimestamp;
    private int savedPredictionId;

    public static class DifferentialMatch {
        private final MedicalCondition condition;
        private final double score;
        private final String confidence;

        public DifferentialMatch(MedicalCondition condition, double score, String confidence) {
            this.condition = condition;
            this.score = score;
            this.confidence = confidence;
        }

        public MedicalCondition getCondition() {
            return condition;
        }

        public double getScore() {
            return score;
        }

        public String getConfidence() {
            return confidence;
        }
    }

    public PredictionResultData() {
        this.disclaimer = "MediPredict provides preliminary symptom-based information for educational and informational purposes only. It does not provide a medical diagnosis or replace professional medical advice. Please consult a qualified healthcare professional for proper evaluation and treatment.";
        this.predictionTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public MedicalCondition getPrimaryCondition() {
        return primaryCondition;
    }

    public void setPrimaryCondition(MedicalCondition primaryCondition) {
        this.primaryCondition = primaryCondition;
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

    public List<Symptom> getInputSymptoms() {
        return inputSymptoms;
    }

    public void setInputSymptoms(List<Symptom> inputSymptoms) {
        this.inputSymptoms = inputSymptoms;
    }

    public List<Symptom> getMatchedSymptoms() {
        return matchedSymptoms;
    }

    public void setMatchedSymptoms(List<Symptom> matchedSymptoms) {
        this.matchedSymptoms = matchedSymptoms;
    }

    public List<Symptom> getMissingConditionSymptoms() {
        return missingConditionSymptoms;
    }

    public void setMissingConditionSymptoms(List<Symptom> missingConditionSymptoms) {
        this.missingConditionSymptoms = missingConditionSymptoms;
    }

    public List<DifferentialMatch> getDifferentialDiagnoses() {
        return differentialDiagnoses;
    }

    public void setDifferentialDiagnoses(List<DifferentialMatch> differentialDiagnoses) {
        this.differentialDiagnoses = differentialDiagnoses;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getGeneralPrecautions() {
        return generalPrecautions;
    }

    public void setGeneralPrecautions(String generalPrecautions) {
        this.generalPrecautions = generalPrecautions;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public Timestamp getPredictionTimestamp() {
        return predictionTimestamp;
    }

    public void setPredictionTimestamp(Timestamp predictionTimestamp) {
        this.predictionTimestamp = predictionTimestamp;
    }

    public int getSavedPredictionId() {
        return savedPredictionId;
    }

    public void setSavedPredictionId(int savedPredictionId) {
        this.savedPredictionId = savedPredictionId;
    }
}

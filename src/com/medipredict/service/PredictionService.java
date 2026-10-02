package com.medipredict.service;

import com.medipredict.dao.PredictionDAO;
import com.medipredict.dao.SymptomDAO;
import com.medipredict.model.Prediction;
import com.medipredict.model.PredictionResultData;
import com.medipredict.model.Symptom;
import com.medipredict.prediction.PredictionEngine;
import com.medipredict.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class PredictionService {
    private final PredictionEngine engine = new PredictionEngine();
    private final PredictionDAO predictionDAO = new PredictionDAO();
    private final SymptomDAO symptomDAO = new SymptomDAO();

    public PredictionResultData runPrediction(int patientId, List<Symptom> symptoms, String optionalNotes) {
        if (symptoms == null || symptoms.isEmpty()) {
            throw new IllegalArgumentException("Please select at least one symptom before requesting a condition prediction.");
        }

        PredictionResultData result = engine.predict(symptoms);

        // Save to Database
        if (patientId > 0 && result.getPrimaryCondition() != null) {
            Prediction pred = new Prediction();
            pred.setPatientId(patientId);
            pred.setPrimaryConditionId(result.getPrimaryCondition().getConditionId());
            pred.setMatchScore(result.getMatchScore());
            pred.setConfidenceLevel(result.getConfidenceLevel());
            pred.setRiskLevel(result.getRiskLevel());
            pred.setNotes(optionalNotes);

            List<Integer> symIds = new ArrayList<>();
            for (Symptom s : symptoms) {
                symIds.add(s.getSymptomId());
            }

            int savedId = predictionDAO.savePrediction(pred, symIds);
            result.setSavedPredictionId(savedId);
            Logger.info("Prediction persisted to DB with ID: " + savedId);
        }

        return result;
    }

    public List<Prediction> getPredictionHistory(int patientId) {
        return predictionDAO.findByPatientId(patientId);
    }

    public Prediction getPredictionDetails(int predictionId) {
        return predictionDAO.findById(predictionId);
    }

    public List<Symptom> getAllSymptoms() {
        return symptomDAO.findAll();
    }

    public List<Symptom> searchSymptoms(String keyword) {
        return symptomDAO.search(keyword);
    }

    public List<Symptom> getSymptomsByCategory(String category) {
        return symptomDAO.findByCategory(category);
    }

    public List<String> getSymptomCategories() {
        return symptomDAO.getAllCategories();
    }
}

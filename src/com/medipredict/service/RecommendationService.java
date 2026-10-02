package com.medipredict.service;

import com.medipredict.dao.RecommendationDAO;
import com.medipredict.model.Recommendation;
import com.medipredict.util.ValidationUtil;

import java.sql.Date;
import java.util.List;

public class RecommendationService {
    private final RecommendationDAO recommendationDAO = new RecommendationDAO();

    public static class RecommendationResult {
        private final boolean success;
        private final String message;
        private final Recommendation recommendation;

        public RecommendationResult(boolean success, String message, Recommendation recommendation) {
            this.success = success;
            this.message = message;
            this.recommendation = recommendation;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Recommendation getRecommendation() {
            return recommendation;
        }
    }

    public RecommendationResult createRecommendation(int patientId, int doctorId, Integer predictionId,
                                                      String recommendationText, String lifestyleAdvice,
                                                      Date followUpDate, String notes) {
        if (patientId <= 0 || doctorId <= 0) {
            return new RecommendationResult(false, "Invalid patient or doctor identifier.", null);
        }
        if (!ValidationUtil.isNotEmpty(recommendationText)) {
            return new RecommendationResult(false, "Recommendation text is required.", null);
        }

        Recommendation r = new Recommendation();
        r.setPatientId(patientId);
        r.setDoctorId(doctorId);
        r.setPredictionId(predictionId);
        r.setRecommendationText(recommendationText.trim());
        r.setLifestyleAdvice(lifestyleAdvice != null ? lifestyleAdvice.trim() : "");
        r.setFollowUpDate(followUpDate);
        r.setNotes(notes != null ? notes.trim() : "");

        int id = recommendationDAO.create(r);
        if (id != -1) {
            r.setRecommendationId(id);
            return new RecommendationResult(true, "Doctor recommendation submitted successfully!", r);
        } else {
            return new RecommendationResult(false, "Failed to submit recommendation due to a database error.", null);
        }
    }

    public List<Recommendation> getPatientRecommendations(int patientId) {
        return recommendationDAO.findByPatientId(patientId);
    }

    public List<Recommendation> getDoctorRecommendations(int doctorId) {
        return recommendationDAO.findByDoctorId(doctorId);
    }

    public Recommendation getRecommendationById(int recommendationId) {
        return recommendationDAO.findById(recommendationId);
    }
}

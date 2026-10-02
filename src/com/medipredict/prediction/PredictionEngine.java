package com.medipredict.prediction;

import com.medipredict.model.MedicalCondition;
import com.medipredict.model.PredictionResultData;
import com.medipredict.model.Symptom;
import com.medipredict.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class PredictionEngine {

    private final SymptomMatcher matcher = new SymptomMatcher();

    public PredictionResultData predict(List<Symptom> inputSymptoms) {
        if (inputSymptoms == null || inputSymptoms.isEmpty()) {
            throw new IllegalArgumentException("At least one symptom must be selected for condition prediction.");
        }

        List<MedicalCondition> conditions = ConditionKnowledgeBase.getAllConditions();
        List<MatchResult> rankedMatches = matcher.matchAll(inputSymptoms, conditions);

        PredictionResultData result = new PredictionResultData();
        result.setInputSymptoms(inputSymptoms);

        if (rankedMatches.isEmpty()) {
            // General fallback if no direct matches in DB
            MedicalCondition generalCond = new MedicalCondition(
                    0,
                    "General Non-Specific Symptoms",
                    "General Health",
                    "The selected symptoms do not form a distinct pattern for a specific common illness in our dataset.",
                    "• Monitor your symptoms over the next 24-48 hours.\n• Maintain adequate hydration and rest.\n• Consult a general physician if symptoms persist or new symptoms appear.",
                    "Mild"
            );
            result.setPrimaryCondition(generalCond);
            result.setMatchScore(25.0);
            result.setConfidenceLevel("Low");
            result.setRiskLevel("Mild");
            result.setExplanation("The reported combination of symptoms did not strongly match a single specific acute profile. A general medical evaluation is recommended.");
            result.setGeneralPrecautions(generalCond.getPrecautions());
            return result;
        }

        // Primary match is the highest scoring condition
        MatchResult topMatch = rankedMatches.get(0);
        MedicalCondition primaryCond = topMatch.getCondition();

        result.setPrimaryCondition(primaryCond);
        result.setMatchScore(topMatch.getScore());
        result.setConfidenceLevel(topMatch.getConfidenceLevel());
        result.setMatchedSymptoms(topMatch.getMatchedSymptoms());
        result.setMissingConditionSymptoms(topMatch.getMissingSymptoms());
        result.setGeneralPrecautions(primaryCond.getPrecautions());

        // Determine Risk Level
        int maxSymptomWeight = 1;
        for (Symptom s : inputSymptoms) {
            if (s.getSeverityWeight() > maxSymptomWeight) {
                maxSymptomWeight = s.getSeverityWeight();
            }
        }
        if ("Severe".equalsIgnoreCase(primaryCond.getSeverityLevel()) || maxSymptomWeight >= 3) {
            result.setRiskLevel("Severe / Urgent Attention");
        } else if ("Moderate".equalsIgnoreCase(primaryCond.getSeverityLevel()) || maxSymptomWeight >= 2) {
            result.setRiskLevel("Moderate");
        } else {
            result.setRiskLevel("Mild");
        }

        // Collect top differential diagnoses (up to 3 other possibilities)
        List<PredictionResultData.DifferentialMatch> diffs = new ArrayList<>();
        for (int i = 1; i < Math.min(4, rankedMatches.size()); i++) {
            MatchResult mr = rankedMatches.get(i);
            diffs.add(new PredictionResultData.DifferentialMatch(
                    mr.getCondition(),
                    mr.getScore(),
                    mr.getConfidenceLevel()
            ));
        }
        result.setDifferentialDiagnoses(diffs);

        // Build human-friendly explainable breakdown
        StringBuilder exp = new StringBuilder();
        exp.append("Based on the ").append(inputSymptoms.size()).append(" symptom(s) you reported, ")
                .append("the prediction engine identified a strong correlation with ")
                .append(primaryCond.getName()).append(" (")
                .append(topMatch.getScore()).append("% match index).\n\n")
                .append("Key matching indicators included: ");

        List<Symptom> ms = topMatch.getMatchedSymptoms();
        for (int i = 0; i < ms.size(); i++) {
            exp.append(ms.get(i).getName());
            if (i < ms.size() - 1) exp.append(", ");
        }
        exp.append(".\n\n");
        exp.append("Medical Overview: ").append(primaryCond.getDescription());

        result.setExplanation(exp.toString());
        Logger.info("Prediction generated: " + primaryCond.getName() + " (" + topMatch.getScore() + "% match)");

        return result;
    }
}

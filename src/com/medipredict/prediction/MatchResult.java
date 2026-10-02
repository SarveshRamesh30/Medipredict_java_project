package com.medipredict.prediction;

import com.medipredict.model.MedicalCondition;
import com.medipredict.model.Symptom;

import java.util.ArrayList;
import java.util.List;

public class MatchResult implements Comparable<MatchResult> {
    private final MedicalCondition condition;
    private final double score;
    private final String confidenceLevel;
    private final List<Symptom> matchedSymptoms;
    private final List<Symptom> missingSymptoms;

    public MatchResult(MedicalCondition condition, double score, String confidenceLevel,
                       List<Symptom> matchedSymptoms, List<Symptom> missingSymptoms) {
        this.condition = condition;
        this.score = score;
        this.confidenceLevel = confidenceLevel;
        this.matchedSymptoms = matchedSymptoms != null ? matchedSymptoms : new ArrayList<>();
        this.missingSymptoms = missingSymptoms != null ? missingSymptoms : new ArrayList<>();
    }

    public MedicalCondition getCondition() {
        return condition;
    }

    public double getScore() {
        return score;
    }

    public String getConfidenceLevel() {
        return confidenceLevel;
    }

    public List<Symptom> getMatchedSymptoms() {
        return matchedSymptoms;
    }

    public List<Symptom> getMissingSymptoms() {
        return missingSymptoms;
    }

    @Override
    public int compareTo(MatchResult other) {
        return Double.compare(other.score, this.score); // descending order
    }
}

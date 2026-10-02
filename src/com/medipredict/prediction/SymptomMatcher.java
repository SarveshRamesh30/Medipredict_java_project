package com.medipredict.prediction;

import com.medipredict.model.MedicalCondition;
import com.medipredict.model.Symptom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SymptomMatcher {

    public List<MatchResult> matchAll(List<Symptom> inputSymptoms, List<MedicalCondition> conditions) {
        if (inputSymptoms == null || inputSymptoms.isEmpty() || conditions == null || conditions.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Integer> inputSymIds = new HashSet<>();
        for (Symptom s : inputSymptoms) {
            inputSymIds.add(s.getSymptomId());
        }

        List<MatchResult> results = new ArrayList<>();

        for (MedicalCondition cond : conditions) {
            List<Symptom> condSymptoms = cond.getAssociatedSymptoms();
            if (condSymptoms == null || condSymptoms.isEmpty()) {
                continue;
            }

            List<Symptom> matched = new ArrayList<>();
            List<Symptom> missing = new ArrayList<>();

            int totalConditionWeight = 0;
            int matchedWeight = 0;

            for (Symptom cs : condSymptoms) {
                int w = Math.max(cs.getSeverityWeight(), 1);
                totalConditionWeight += w;
                if (inputSymIds.contains(cs.getSymptomId())) {
                    matched.add(cs);
                    matchedWeight += w;
                } else {
                    missing.add(cs);
                }
            }

            if (matched.isEmpty()) {
                continue; // No correlation
            }

            // Calculate weighted match percentages
            double recall = (double) matchedWeight / totalConditionWeight;
            double precision = (double) matched.size() / inputSymptoms.size();

            // Combined harmonic-weighted score
            double composite = (recall * 0.70) + (precision * 0.30);
            double percentage = composite * 100.0;

            // Bonus for high direct match count
            if (matched.size() >= 3) {
                percentage = Math.min(98.5, percentage + 5.0);
            }

            percentage = Math.round(percentage * 10.0) / 10.0; // round to 1 decimal
            percentage = Math.max(10.0, Math.min(98.5, percentage));

            String confidence;
            if (percentage >= 70.0) {
                confidence = "High";
            } else if (percentage >= 40.0) {
                confidence = "Medium";
            } else {
                confidence = "Low";
            }

            results.add(new MatchResult(cond, percentage, confidence, matched, missing));
        }

        Collections.sort(results);
        return results;
    }
}

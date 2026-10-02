package com.medipredict.prediction;

import com.medipredict.dao.ConditionDAO;
import com.medipredict.model.MedicalCondition;
import com.medipredict.util.Logger;

import java.util.Collections;
import java.util.List;

public class ConditionKnowledgeBase {
    private static List<MedicalCondition> cachedConditions = null;
    private static final ConditionDAO conditionDAO = new ConditionDAO();

    public static synchronized List<MedicalCondition> getAllConditions() {
        if (cachedConditions == null || cachedConditions.isEmpty()) {
            reload();
        }
        return cachedConditions != null ? cachedConditions : Collections.emptyList();
    }

    public static synchronized void reload() {
        try {
            cachedConditions = conditionDAO.findAllWithSymptoms();
            Logger.info("ConditionKnowledgeBase loaded " + cachedConditions.size() + " conditions.");
        } catch (Exception e) {
            Logger.error("Failed to load ConditionKnowledgeBase", e);
            cachedConditions = Collections.emptyList();
        }
    }
}

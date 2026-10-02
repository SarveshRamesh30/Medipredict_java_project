/**
 * MediPredict Condition Matching & AI Heuristics Engine
 * 100% Faithful Port of the Java PredictionEngine and SymptomMatcher.
 */

const PredictionEngine = {
  predict(selectedSymptomIds) {
    if (!selectedSymptomIds || selectedSymptomIds.length === 0) {
      throw new Error("At least one symptom must be selected for condition prediction.");
    }

    const allSymptoms = DB.getSymptoms();
    const allConditions = DB.getConditions();

    // Resolve input symptom objects
    const inputSymptoms = selectedSymptomIds
      .map(id => allSymptoms.find(s => s.id === parseInt(id)))
      .filter(Boolean);

    const inputSymIdSet = new Set(inputSymptoms.map(s => s.id));
    const rankedMatches = [];

    // Evaluate each condition
    for (const cond of allConditions) {
      const condSymptomIds = cond.symptomIds || [];
      const condWeights = cond.symptomWeights || [];

      const matchedSymptoms = [];
      const missingSymptoms = [];

      let totalConditionWeight = 0;
      let matchedWeight = 0;

      for (let i = 0; i < condSymptomIds.length; i++) {
        const symId = condSymptomIds[i];
        const weight = Math.max(condWeights[i] || 1, 1);
        const symptomObj = allSymptoms.find(s => s.id === symId);

        if (!symptomObj) continue;

        totalConditionWeight += weight;

        if (inputSymIdSet.has(symId)) {
          matchedSymptoms.push({ ...symptomObj, weight });
          matchedWeight += weight;
        } else {
          missingSymptoms.push({ ...symptomObj, weight });
        }
      }

      if (matchedSymptoms.length === 0) {
        continue; // No correlation
      }

      // Calculate weighted match percentages
      const recall = matchedWeight / totalConditionWeight;
      const precision = matchedSymptoms.length / inputSymptoms.length;

      // Combined harmonic-weighted score
      const composite = (recall * 0.70) + (precision * 0.30);
      let percentage = composite * 100.0;

      // Bonus for high direct match count (>= 3)
      if (matchedSymptoms.length >= 3) {
        percentage = Math.min(98.5, percentage + 5.0);
      }

      // Round to 1 decimal place and clamp
      percentage = Math.round(percentage * 10) / 10;
      percentage = Math.max(10.0, Math.min(98.5, percentage));

      let confidence = "Low";
      if (percentage >= 70.0) {
        confidence = "High";
      } else if (percentage >= 40.0) {
        confidence = "Medium";
      }

      rankedMatches.push({
        condition: cond,
        percentage,
        confidence,
        matchedSymptoms,
        missingSymptoms
      });
    }

    // Sort descending by percentage match
    rankedMatches.sort((a, b) => b.percentage - a.percentage);

    if (rankedMatches.length === 0) {
      const fallback = {
        name: "General Non-Specific Symptoms",
        category: "General Health",
        description: "The selected symptoms do not form a distinct pattern for a specific common illness in our clinical knowledge base.",
        precautions: "• Monitor your symptoms over the next 24-48 hours.\n• Maintain adequate hydration and rest.\n• Consult a general physician if symptoms persist or new symptoms appear.",
        severityLevel: "Mild"
      };

      return {
        primaryCondition: fallback,
        matchScore: 25.0,
        confidenceLevel: "Low",
        riskLevel: "Mild",
        inputSymptoms,
        matchedSymptoms: [],
        missingSymptoms: [],
        differentialDiagnoses: [],
        explanation: "The reported combination of symptoms did not strongly match a single specific acute profile. A general medical evaluation is recommended.",
        precautions: fallback.precautions.split("\n").filter(p => p.trim())
      };
    }

    // Top match
    const topMatch = rankedMatches[0];
    const primaryCond = topMatch.condition;

    // Assess overall risk level
    let maxSymptomWeight = 1;
    for (const s of inputSymptoms) {
      if (s.severityWeight > maxSymptomWeight) {
        maxSymptomWeight = s.severityWeight;
      }
    }

    let riskLevel = "Mild";
    if (primaryCond.severityLevel === "Severe" || maxSymptomWeight >= 3) {
      riskLevel = "Severe / Urgent Attention";
    } else if (primaryCond.severityLevel === "Moderate" || maxSymptomWeight >= 2) {
      riskLevel = "Moderate";
    }

    // Differential diagnoses (up to 3 other possibilities)
    const differentials = rankedMatches.slice(1, 4).map(m => ({
      condition: m.condition,
      score: m.percentage,
      confidence: m.confidence
    }));

    // Explainable clinical breakdown text
    const matchedNames = topMatch.matchedSymptoms.map(s => s.name).join(", ");
    const explanation = `Based on the ${inputSymptoms.length} symptom(s) you reported, the prediction engine identified a strong correlation with ${primaryCond.name} (${topMatch.percentage}% match index).\n\nKey matching clinical indicators included: ${matchedNames}.\n\nMedical Overview: ${primaryCond.description}`;

    return {
      primaryCondition: primaryCond,
      matchScore: topMatch.percentage,
      confidenceLevel: topMatch.confidence,
      riskLevel: riskLevel,
      inputSymptoms,
      matchedSymptoms: topMatch.matchedSymptoms,
      missingSymptoms: topMatch.missingSymptoms,
      differentialDiagnoses: differentials,
      explanation: explanation,
      precautions: (primaryCond.precautions || "").split("\n").filter(p => p.trim())
    };
  }
};

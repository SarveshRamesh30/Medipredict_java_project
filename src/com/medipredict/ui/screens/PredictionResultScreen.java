package com.medipredict.ui.screens;

import com.medipredict.model.PredictionResultData;
import com.medipredict.model.Symptom;
import com.medipredict.ui.NavigationManager;
import com.medipredict.ui.components.HeaderPanel;
import com.medipredict.ui.components.ModernButton;
import com.medipredict.ui.components.ModernCard;
import com.medipredict.ui.components.ResponsiveScrollPanel;
import com.medipredict.ui.components.SidebarPanel;
import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;
import com.medipredict.ui.theme.UIUtils;
import com.medipredict.util.DateTimeUtil;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class PredictionResultScreen extends JPanel {
    private final NavigationManager navManager;
    private final PredictionResultData resultData;

    public PredictionResultScreen(NavigationManager navManager, PredictionResultData resultData) {
        this.navManager = navManager;
        this.resultData = resultData;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        add(new HeaderPanel(navManager), BorderLayout.NORTH);
        add(new SidebarPanel(navManager, NavigationManager.SCREEN_PREDICTION_RESULT), BorderLayout.WEST);

        ResponsiveScrollPanel mainContent = new ResponsiveScrollPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(UIUtils.createPadding(24, 28, 28, 28));

        // 1. TOP RESULT HERO CARD
        ModernCard heroCard = new ModernCard(new BorderLayout(), 22);
        heroCard.setAlignmentX(0.0f);

        JPanel heroTop = new JPanel(new BorderLayout());
        heroTop.setOpaque(false);

        JLabel resTag = new JLabel("PRELIMINARY SYMPTOM ASSESSMENT");
        resTag.setFont(ThemeFonts.BADGE);
        resTag.setForeground(ThemeColors.TEXT_MUTED);

        String conditionName = resultData != null && resultData.getPrimaryCondition() != null ?
                resultData.getPrimaryCondition().getName() : "General Symptoms";
        JLabel conditionTitle = new JLabel("Possible Condition: " + conditionName);
        conditionTitle.setFont(ThemeFonts.HERO);
        conditionTitle.setForeground(ThemeColors.PRIMARY);

        String conf = resultData != null ? resultData.getConfidenceLevel() : "Medium";
        double score = resultData != null ? resultData.getMatchScore() : 50.0;
        Color confBg = "High".equalsIgnoreCase(conf) ? ThemeColors.SUCCESS_LIGHT :
                ("Low".equalsIgnoreCase(conf) ? ThemeColors.DANGER_LIGHT : ThemeColors.WARNING_LIGHT);
        Color confFg = "High".equalsIgnoreCase(conf) ? ThemeColors.SUCCESS :
                ("Low".equalsIgnoreCase(conf) ? ThemeColors.DANGER : ThemeColors.WARNING);

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        badgePanel.setOpaque(false);
        badgePanel.add(UIUtils.createBadge("Match Score: " + score + "%", ThemeColors.PRIMARY_LIGHT, ThemeColors.PRIMARY));
        badgePanel.add(UIUtils.createBadge(conf + " Confidence", confBg, confFg));

        if (resultData != null && resultData.getRiskLevel() != null) {
            String risk = resultData.getRiskLevel();
            Color rBg = risk.contains("Severe") ? ThemeColors.DANGER_LIGHT :
                    (risk.contains("Moderate") ? ThemeColors.WARNING_LIGHT : ThemeColors.SUCCESS_LIGHT);
            Color rFg = risk.contains("Severe") ? ThemeColors.DANGER :
                    (risk.contains("Moderate") ? ThemeColors.WARNING : ThemeColors.SUCCESS);
            badgePanel.add(UIUtils.createBadge("Risk: " + risk, rBg, rFg));
        }

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);
        titleBlock.add(resTag);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(conditionTitle);

        // Visual match progress meter
        JPanel meterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        meterPanel.setOpaque(false);

        JProgressBar scoreBar = new JProgressBar(0, 100);
        scoreBar.setValue((int) Math.round(score));
        scoreBar.setPreferredSize(new Dimension(280, 8));
        scoreBar.setForeground(ThemeColors.PRIMARY);
        scoreBar.setBackground(new Color(226, 232, 240));
        scoreBar.setBorderPainted(false);

        meterPanel.add(scoreBar);
        titleBlock.add(meterPanel);

        heroTop.add(titleBlock, BorderLayout.WEST);
        heroTop.add(badgePanel, BorderLayout.EAST);

        JLabel timestampLabel = new JLabel("Assessment Date: " +
                (resultData != null ? DateTimeUtil.formatTimestamp(resultData.getPredictionTimestamp()) : "Today"));
        timestampLabel.setFont(ThemeFonts.BODY_SMALL);
        timestampLabel.setForeground(ThemeColors.TEXT_MUTED);

        heroCard.add(heroTop, BorderLayout.CENTER);
        heroCard.add(timestampLabel, BorderLayout.SOUTH);

        mainContent.add(heroCard);
        mainContent.add(Box.createVerticalStrut(18));

        // 2. TWO COLUMNS (Details & Precautions)
        JPanel grid = new JPanel(new GridLayout(1, 2, 20, 0));
        grid.setOpaque(false);
        grid.setAlignmentX(0.0f);

        // Column 1: Match Explanation & Symptoms Analysis
        ModernCard expCard = new ModernCard(new BorderLayout(), 18);
        JLabel expTitle = new JLabel("Analysis & Reported Symptoms");
        expTitle.setFont(ThemeFonts.TITLE_SMALL);
        expTitle.setForeground(ThemeColors.PRIMARY);

        JPanel expBody = new JPanel();
        expBody.setLayout(new BoxLayout(expBody, BoxLayout.Y_AXIS));
        expBody.setOpaque(false);
        expBody.setBorder(UIUtils.createPadding(10, 0, 0, 0));

        JTextArea expArea = new JTextArea(resultData != null ? resultData.getExplanation() : "No details available.");
        expArea.setFont(ThemeFonts.BODY);
        expArea.setForeground(ThemeColors.TEXT_PRIMARY);
        expArea.setLineWrap(true);
        expArea.setWrapStyleWord(true);
        expArea.setEditable(false);
        expArea.setOpaque(false);

        // Differential diagnoses section
        JPanel diffPanel = new JPanel();
        diffPanel.setLayout(new BoxLayout(diffPanel, BoxLayout.Y_AXIS));
        diffPanel.setOpaque(false);
        diffPanel.setBorder(UIUtils.createPadding(10, 0, 0, 0));

        if (resultData != null && !resultData.getDifferentialDiagnoses().isEmpty()) {
            JLabel diffTitle = new JLabel("Differential Possibilities Considered:");
            diffTitle.setFont(ThemeFonts.BODY_SMALL_BOLD);
            diffTitle.setForeground(ThemeColors.TEXT_SECONDARY);
            diffPanel.add(diffTitle);
            diffPanel.add(Box.createVerticalStrut(4));

            for (PredictionResultData.DifferentialMatch dm : resultData.getDifferentialDiagnoses()) {
                JLabel dItem = new JLabel("• " + dm.getCondition().getName() + " (" + dm.getScore() + "% match)");
                dItem.setFont(ThemeFonts.BODY_SMALL);
                dItem.setForeground(ThemeColors.TEXT_SECONDARY);
                diffPanel.add(dItem);
            }
        }

        expBody.add(expArea);
        expBody.add(diffPanel);

        expCard.add(expTitle, BorderLayout.NORTH);
        expCard.add(expBody, BorderLayout.CENTER);

        // Column 2: General Information & Self-Care Precautions
        ModernCard precCard = new ModernCard(new BorderLayout(), 18);
        JLabel precTitle = new JLabel("General Information & Precautions");
        precTitle.setFont(ThemeFonts.TITLE_SMALL);
        precTitle.setForeground(ThemeColors.ACCENT_BLUE);

        JTextArea precArea = new JTextArea(resultData != null && resultData.getGeneralPrecautions() != null ?
                resultData.getGeneralPrecautions() : "• Stay hydrated\n• Rest adequately\n• Consult a physician if symptoms persist.");
        precArea.setFont(ThemeFonts.BODY);
        precArea.setForeground(ThemeColors.TEXT_PRIMARY);
        precArea.setLineWrap(true);
        precArea.setWrapStyleWord(true);
        precArea.setEditable(false);
        precArea.setOpaque(false);
        precArea.setBorder(UIUtils.createPadding(10, 0, 0, 0));

        precCard.add(precTitle, BorderLayout.NORTH);
        precCard.add(precArea, BorderLayout.CENTER);

        grid.add(expCard);
        grid.add(precCard);

        mainContent.add(grid);
        mainContent.add(Box.createVerticalStrut(18));

        // 3. PROMINENT MEDICAL DISCLAIMER BANNER
        ModernCard discCard = new ModernCard(new BorderLayout(), 16);
        discCard.setBackground(ThemeColors.WARNING_LIGHT);
        discCard.setAlignmentX(0.0f);

        JLabel discTitle = new JLabel("⚠ IMPORTANT NON-DIAGNOSTIC MEDICAL DISCLAIMER");
        discTitle.setFont(ThemeFonts.BODY_SMALL_BOLD);
        discTitle.setForeground(ThemeColors.WARNING);

        JLabel discText = new JLabel("<html><body style='color: #92400E; font-size: 11px;'>" +
                (resultData != null ? resultData.getDisclaimer() :
                        "This result is a preliminary symptom-based prediction and is not a medical diagnosis. Please consult a qualified healthcare professional for proper evaluation.") +
                "</body></html>");

        discCard.add(discTitle, BorderLayout.NORTH);
        discCard.add(discText, BorderLayout.CENTER);

        mainContent.add(discCard);
        mainContent.add(Box.createVerticalStrut(20));

        // 4. ACTION BUTTONS BAR
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        actionRow.setOpaque(false);
        actionRow.setAlignmentX(0.0f);

        ModernButton consultBtn = new ModernButton("👨‍⚕️ Consult / Book Doctor", ModernButton.ButtonStyle.PRIMARY);
        consultBtn.setPreferredSize(new Dimension(210, 42));
        consultBtn.setFont(ThemeFonts.BUTTON_LARGE);
        consultBtn.addActionListener(e -> navManager.showDoctorDirectory());

        ModernButton historyBtn = new ModernButton("📜 View History", ModernButton.ButtonStyle.OUTLINE);
        historyBtn.setPreferredSize(new Dimension(160, 42));
        historyBtn.addActionListener(e -> navManager.showPredictionHistory());

        ModernButton newPredBtn = new ModernButton("🩺 Start New Prediction", ModernButton.ButtonStyle.SECONDARY);
        newPredBtn.setPreferredSize(new Dimension(200, 42));
        newPredBtn.addActionListener(e -> navManager.showSymptomPrediction());

        actionRow.add(consultBtn);
        actionRow.add(historyBtn);
        actionRow.add(newPredBtn);

        mainContent.add(actionRow);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }
}

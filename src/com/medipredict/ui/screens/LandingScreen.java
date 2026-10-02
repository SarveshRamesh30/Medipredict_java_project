package com.medipredict.ui.screens;

import com.medipredict.ui.NavigationManager;
import com.medipredict.ui.components.HeaderPanel;
import com.medipredict.ui.components.ModernButton;
import com.medipredict.ui.components.ModernCard;
import com.medipredict.ui.components.ResponsiveScrollPanel;
import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;
import com.medipredict.ui.theme.UIUtils;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;

public class LandingScreen extends JPanel {

    public LandingScreen(NavigationManager navManager) {
        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        // Header
        add(new HeaderPanel(navManager), BorderLayout.NORTH);

        // Scrollable content
        ResponsiveScrollPanel mainContent = new ResponsiveScrollPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(UIUtils.createPadding(24, 36, 40, 36));

        // 1. GORGEOUS HERO BANNER (Full Width, Rich Gradient)
        JPanel heroCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntiAliasing(g2);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(15, 118, 110),
                        getWidth(), getHeight(), new Color(14, 116, 144)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        heroCard.setOpaque(false);
        heroCard.setLayout(new BorderLayout());
        heroCard.setBorder(UIUtils.createPadding(36, 40, 36, 40));
        heroCard.setAlignmentX(0.0f);

        JPanel heroTextPanel = new JPanel();
        heroTextPanel.setLayout(new BoxLayout(heroTextPanel, BoxLayout.Y_AXIS));
        heroTextPanel.setOpaque(false);

        JLabel heroBadge = new JLabel("SMART HEALTHCARE PREDICTION & CONSULTATION");
        heroBadge.setFont(ThemeFonts.BADGE);
        heroBadge.setForeground(new Color(204, 251, 241));

        JLabel heroTitle = new JLabel("Understand Your Symptoms. Take the Right Next Step.");
        heroTitle.setFont(ThemeFonts.HERO);
        heroTitle.setForeground(Color.WHITE);

        JLabel heroSubtitle = new JLabel("<html><body style='color: #E6FFFA; font-size: 13px; line-height: 1.5;'>" +
                "MediPredict is an intelligent clinical assistance platform that helps you assess preliminary medical conditions " +
                "based on reported symptoms, securely manage your clinical history, and schedule verified consultations with specialist doctors." +
                "</body></html>");
        heroSubtitle.setFont(ThemeFonts.BODY_LARGE);

        JPanel heroActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 16));
        heroActionPanel.setOpaque(false);

        ModernButton getStartedBtn = new ModernButton("Get Started Free ➢", ModernButton.ButtonStyle.SECONDARY);
        getStartedBtn.setPreferredSize(new Dimension(180, 42));
        getStartedBtn.setFont(ThemeFonts.BUTTON_LARGE);
        getStartedBtn.addActionListener(e -> navManager.showRegister());

        ModernButton loginBtn = new ModernButton("Sign In to Portal", ModernButton.ButtonStyle.OUTLINE);
        loginBtn.setPreferredSize(new Dimension(170, 42));
        loginBtn.setFont(ThemeFonts.BUTTON_LARGE);
        loginBtn.addActionListener(e -> navManager.showLogin());

        heroActionPanel.add(getStartedBtn);
        heroActionPanel.add(loginBtn);

        // Stats highlight bar inside hero
        JPanel statsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 8));
        statsBar.setOpaque(false);
        statsBar.setBorder(UIUtils.createPadding(8, 0, 0, 0));

        statsBar.add(createHeroStatPill("🩺 27+ Symptoms"));
        statsBar.add(createHeroStatPill("📋 12+ Conditions"));
        statsBar.add(createHeroStatPill("👨‍⚕️ Verified Doctors"));
        statsBar.add(createHeroStatPill("🔒 Salted SHA-256"));

        heroTextPanel.add(heroBadge);
        heroTextPanel.add(Box.createVerticalStrut(8));
        heroTextPanel.add(heroTitle);
        heroTextPanel.add(Box.createVerticalStrut(10));
        heroTextPanel.add(heroSubtitle);
        heroTextPanel.add(Box.createVerticalStrut(6));
        heroTextPanel.add(heroActionPanel);
        heroTextPanel.add(Box.createVerticalStrut(4));
        heroTextPanel.add(statsBar);

        heroCard.add(heroTextPanel, BorderLayout.CENTER);
        mainContent.add(heroCard);
        mainContent.add(Box.createVerticalStrut(28));

        // 2. FEATURES GRID SECTION (Responsive, No restrictive heights)
        JLabel sectionHeading = new JLabel("Core Healthcare Capabilities");
        sectionHeading.setFont(ThemeFonts.TITLE_LARGE);
        sectionHeading.setForeground(ThemeColors.TEXT_PRIMARY);
        sectionHeading.setAlignmentX(0.0f);
        mainContent.add(sectionHeading);
        mainContent.add(Box.createVerticalStrut(14));

        JPanel featuresGrid = new JPanel(new GridLayout(2, 3, 20, 20));
        featuresGrid.setOpaque(false);
        featuresGrid.setAlignmentX(0.0f);

        featuresGrid.add(createFeatureCard("🩺 Symptom-Based Prediction",
                "Select from categorized symptoms to receive an instant, explainable match score, confidence rating, and differential insights."));

        featuresGrid.add(createFeatureCard("👨‍⚕️ Specialist Consultations",
                "Browse accredited physicians across Neurology, Cardiology, Pulmonology, and General Practice with experience and fees upfront."));

        featuresGrid.add(createFeatureCard("📅 Appointment Management",
                "Book consultations with automated duplicate collision prevention, status tracking, and doctor schedule confirmations."));

        featuresGrid.add(createFeatureCard("📜 Clinical Prediction History",
                "Access a complete chronological timeline of your previous symptom assessments with immutable record keeping."));

        featuresGrid.add(createFeatureCard("📋 Doctor Care Advice",
                "Receive personalized clinical guidance, lifestyle recommendations, and scheduled follow-up targets directly on your dashboard."));

        featuresGrid.add(createFeatureCard("💬 Secure Doctor Messaging",
                "Consult securely with your attending physician through real-time database messaging with unread status indicators."));

        mainContent.add(featuresGrid);
        mainContent.add(Box.createVerticalStrut(24));

        // 3. DISCLAIMER BANNER (Full width, generous padding)
        ModernCard disclaimerCard = new ModernCard(new BorderLayout(), 16);
        disclaimerCard.setBackground(ThemeColors.WARNING_LIGHT);
        disclaimerCard.setDrawBorder(true);
        disclaimerCard.setAlignmentX(0.0f);

        JLabel disTitle = new JLabel("⚠️ Important Medical Disclaimer");
        disTitle.setFont(ThemeFonts.BODY_BOLD);
        disTitle.setForeground(ThemeColors.WARNING);

        JLabel disBody = new JLabel("<html><body style='color: #92400E; font-size: 11px; line-height: 1.4;'>" +
                "MediPredict provides preliminary symptom-based information for educational and informational purposes only. " +
                "It does not provide a medical diagnosis or replace professional medical advice. Always consult a qualified healthcare professional." +
                "</body></html>");
        disclaimerCard.add(disTitle, BorderLayout.NORTH);
        disclaimerCard.add(Box.createVerticalStrut(4), BorderLayout.CENTER);
        disclaimerCard.add(disBody, BorderLayout.SOUTH);

        mainContent.add(disclaimerCard);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createHeroStatPill(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(ThemeFonts.BADGE);
        lbl.setForeground(new Color(204, 251, 241));
        lbl.setBackground(new Color(255, 255, 255, 30));
        lbl.setOpaque(true);
        lbl.setBorder(UIUtils.createPadding(4, 10, 4, 10));
        return lbl;
    }

    private ModernCard createFeatureCard(String title, String desc) {
        ModernCard card = new ModernCard(new BorderLayout(), 20);
        card.setPreferredSize(new Dimension(320, 150));
        card.setMinimumSize(new Dimension(260, 140));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(ThemeFonts.TITLE_SMALL);
        titleLbl.setForeground(ThemeColors.PRIMARY);

        JLabel descLbl = new JLabel("<html><body style='color: #475569; font-size: 12px; line-height: 1.4;'>" + desc + "</body></html>");
        descLbl.setFont(ThemeFonts.BODY);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(Box.createVerticalStrut(8), BorderLayout.CENTER);
        card.add(descLbl, BorderLayout.SOUTH);
        return card;
    }
}

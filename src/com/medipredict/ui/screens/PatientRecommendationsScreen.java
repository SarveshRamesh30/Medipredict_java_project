package com.medipredict.ui.screens;

import com.medipredict.model.Patient;
import com.medipredict.model.Recommendation;
import com.medipredict.service.RecommendationService;
import com.medipredict.service.SessionManager;
import com.medipredict.ui.NavigationManager;
import com.medipredict.ui.components.HeaderPanel;
import com.medipredict.ui.components.ModernButton;
import com.medipredict.ui.components.ModernCard;
import com.medipredict.ui.components.ModernTable;
import com.medipredict.ui.components.SidebarPanel;
import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;
import com.medipredict.ui.theme.UIUtils;
import com.medipredict.util.DateTimeUtil;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

public class PatientRecommendationsScreen extends JPanel {
    private final NavigationManager navManager;
    private final RecommendationService recommendationService = new RecommendationService();

    private List<Recommendation> recommendationList;
    private final DefaultTableModel tableModel;
    private final ModernTable table;

    public PatientRecommendationsScreen(NavigationManager navManager) {
        this.navManager = navManager;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        add(new HeaderPanel(navManager), BorderLayout.NORTH);
        add(new SidebarPanel(navManager, NavigationManager.SCREEN_PATIENT_RECOMMENDATIONS), BorderLayout.WEST);

        JPanel mainContent = new JPanel(new BorderLayout(0, 16));
        mainContent.setOpaque(false);
        mainContent.setBorder(UIUtils.createPadding(24, 28, 28, 28));

        // 1. Header
        JPanel topRow = new JPanel();
        topRow.setLayout(new BoxLayout(topRow, BoxLayout.Y_AXIS));
        topRow.setOpaque(false);

        JLabel title = new JLabel("Doctor Recommendations & Care Plans");
        title.setFont(ThemeFonts.TITLE_LARGE);
        title.setForeground(ThemeColors.PRIMARY);
        title.setAlignmentX(0.0f);

        JLabel sub = new JLabel("Review clinical suggestions, lifestyle modifications, and scheduled follow-ups provided by attending physicians.");
        sub.setFont(ThemeFonts.BODY);
        sub.setForeground(ThemeColors.TEXT_SECONDARY);
        sub.setAlignmentX(0.0f);

        topRow.add(title);
        topRow.add(Box.createVerticalStrut(4));
        topRow.add(sub);

        mainContent.add(topRow, BorderLayout.NORTH);

        // 2. Table Card (Full expansion)
        ModernCard tableCard = new ModernCard(new BorderLayout(), 16);

        String[] columns = {"ID", "Attending Doctor", "Specialization", "Clinical Recommendation", "Follow-up Date", "Date Received"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new ModernTable(tableModel);
        loadRecommendations();

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bottomBar.setOpaque(false);

        ModernButton detailsBtn = new ModernButton("🔍 View Full Recommendation", ModernButton.ButtonStyle.PRIMARY);
        detailsBtn.addActionListener(e -> showSelectedDetails());

        ModernButton consultBtn = new ModernButton("👨‍⚕️ Book Follow-up Consultation", ModernButton.ButtonStyle.OUTLINE);
        consultBtn.addActionListener(e -> navManager.showDoctorDirectory());

        bottomBar.add(detailsBtn);
        bottomBar.add(consultBtn);

        tableCard.add(tableScroll, BorderLayout.CENTER);
        tableCard.add(bottomBar, BorderLayout.SOUTH);

        mainContent.add(tableCard, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }

    private void loadRecommendations() {
        Patient p = SessionManager.getInstance().getCurrentPatient();
        if (p == null) return;
        recommendationList = recommendationService.getPatientRecommendations(p.getPatientId());

        tableModel.setRowCount(0);
        for (Recommendation r : recommendationList) {
            String shortText = r.getRecommendationText();
            if (shortText.length() > 40) shortText = shortText.substring(0, 37) + "...";

            tableModel.addRow(new Object[]{
                    "#" + r.getRecommendationId(),
                    r.getDoctorName() != null ? r.getDoctorName() : "Doctor",
                    r.getDoctorSpecialization() != null ? r.getDoctorSpecialization() : "General",
                    shortText,
                    r.getFollowUpDate() != null ? DateTimeUtil.formatDate(r.getFollowUpDate()) : "As needed",
                    DateTimeUtil.formatTimestamp(r.getCreatedAt())
            });
        }
    }

    private void showSelectedDetails() {
        int row = table.getSelectedRow();
        if (row < 0) {
            if (!recommendationList.isEmpty()) {
                row = 0;
            } else {
                return;
            }
        }

        Recommendation r = recommendationList.get(row);

        JDialog dialog = new JDialog(navManager.getMainFrame(), "Doctor Recommendation Details", true);
        dialog.setSize(520, 500);
        dialog.setLocationRelativeTo(navManager.getMainFrame());

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(UIUtils.createPadding(22, 26, 22, 26));

        JLabel docLbl = new JLabel("From: " + r.getDoctorName() + " (" + r.getDoctorSpecialization() + ")");
        docLbl.setFont(ThemeFonts.TITLE_MEDIUM);
        docLbl.setForeground(ThemeColors.PRIMARY);

        JLabel dateLbl = new JLabel("Issued on: " + DateTimeUtil.formatTimestamp(r.getCreatedAt()) +
                (r.getFollowUpDate() != null ? " | Follow-up: " + DateTimeUtil.formatDate(r.getFollowUpDate()) : ""));
        dateLbl.setFont(ThemeFonts.BODY_SMALL);
        dateLbl.setForeground(ThemeColors.TEXT_MUTED);

        JLabel recHead = new JLabel("Clinical Medical Advice:");
        recHead.setFont(ThemeFonts.BODY_SMALL_BOLD);
        recHead.setForeground(ThemeColors.TEXT_PRIMARY);

        JTextArea recArea = new JTextArea(r.getRecommendationText());
        recArea.setFont(ThemeFonts.BODY);
        recArea.setEditable(false);
        recArea.setLineWrap(true);
        recArea.setWrapStyleWord(true);
        recArea.setOpaque(false);

        JLabel lifeHead = new JLabel("Dietary & Lifestyle Advice:");
        lifeHead.setFont(ThemeFonts.BODY_SMALL_BOLD);
        lifeHead.setForeground(ThemeColors.TEXT_PRIMARY);

        JTextArea lifeArea = new JTextArea(r.getLifestyleAdvice() != null && !r.getLifestyleAdvice().isEmpty() ?
                r.getLifestyleAdvice() : "Follow standard hydration and sleep routines.");
        lifeArea.setFont(ThemeFonts.BODY);
        lifeArea.setEditable(false);
        lifeArea.setLineWrap(true);
        lifeArea.setWrapStyleWord(true);
        lifeArea.setOpaque(false);

        ModernButton closeBtn = new ModernButton("Close", ModernButton.ButtonStyle.PRIMARY);
        closeBtn.addActionListener(e -> dialog.dispose());

        p.add(docLbl);
        p.add(Box.createVerticalStrut(4));
        p.add(dateLbl);
        p.add(Box.createVerticalStrut(14));
        p.add(recHead);
        p.add(Box.createVerticalStrut(4));
        p.add(recArea);
        p.add(Box.createVerticalStrut(14));
        p.add(lifeHead);
        p.add(Box.createVerticalStrut(4));
        p.add(lifeArea);
        p.add(Box.createVerticalStrut(18));
        p.add(closeBtn);

        dialog.getContentPane().add(p);
        dialog.setVisible(true);
    }
}

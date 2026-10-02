package com.medipredict.ui.screens;

import com.medipredict.model.Doctor;
import com.medipredict.model.Patient;
import com.medipredict.model.Recommendation;
import com.medipredict.service.DoctorService;
import com.medipredict.service.RecommendationService;
import com.medipredict.service.SessionManager;
import com.medipredict.ui.NavigationManager;
import com.medipredict.ui.components.HeaderPanel;
import com.medipredict.ui.components.ModernButton;
import com.medipredict.ui.components.ModernCard;
import com.medipredict.ui.components.ModernComboBox;
import com.medipredict.ui.components.ModernTable;
import com.medipredict.ui.components.ModernTextField;
import com.medipredict.ui.components.ResponsiveScrollPanel;
import com.medipredict.ui.components.SidebarPanel;
import com.medipredict.ui.components.ToastNotification;
import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;
import com.medipredict.ui.theme.UIUtils;
import com.medipredict.util.DateTimeUtil;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class DoctorRecommendationsScreen extends JPanel {
    private final NavigationManager navManager;
    private final DoctorService doctorService = new DoctorService();
    private final RecommendationService recommendationService = new RecommendationService();

    private final ModernComboBox<PatientItem> patientCombo;
    private final JTextArea recommendationArea;
    private final JTextArea lifestyleArea;
    private final ModernTextField followUpField;
    private final ModernTextField notesField;

    private final DefaultTableModel tableModel;
    private final ModernTable table;
    private List<Recommendation> pastRecommendations;

    private static class PatientItem {
        final Patient patient;
        PatientItem(Patient patient) { this.patient = patient; }
        @Override
        public String toString() {
            return patient.getFullName() + " (ID: #" + patient.getPatientId() + ")";
        }
    }

    public DoctorRecommendationsScreen(NavigationManager navManager) {
        this.navManager = navManager;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        add(new HeaderPanel(navManager), BorderLayout.NORTH);
        add(new SidebarPanel(navManager, NavigationManager.SCREEN_DOCTOR_RECOMMENDATIONS), BorderLayout.WEST);

        ResponsiveScrollPanel mainContent = new ResponsiveScrollPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(UIUtils.createPadding(24, 28, 28, 28));

        // 1. Header
        JLabel title = new JLabel("Doctor Clinical Recommendations & Advice");
        title.setFont(ThemeFonts.TITLE_LARGE);
        title.setForeground(ThemeColors.PRIMARY);
        title.setAlignmentX(0.0f);

        JLabel sub = new JLabel("Issue customized clinical recommendations, dietary & lifestyle guidance, and schedule follow-ups.");
        sub.setFont(ThemeFonts.BODY);
        sub.setForeground(ThemeColors.TEXT_SECONDARY);
        sub.setAlignmentX(0.0f);

        mainContent.add(title);
        mainContent.add(Box.createVerticalStrut(4));
        mainContent.add(sub);
        mainContent.add(Box.createVerticalStrut(18));

        // 2. Main Two Column Grid (Create Recommendation Form & Past Recommendations Table)
        JPanel grid = new JPanel(new GridLayout(1, 2, 20, 0));
        grid.setOpaque(false);
        grid.setAlignmentX(0.0f);

        // Column 1: Issue Recommendation Form Card
        ModernCard formCard = new ModernCard(new BorderLayout(), 18);
        JLabel formTitle = new JLabel("📝 Create Clinical Recommendation");
        formTitle.setFont(ThemeFonts.TITLE_SMALL);
        formTitle.setForeground(ThemeColors.PRIMARY);

        JPanel formBody = new JPanel();
        formBody.setLayout(new BoxLayout(formBody, BoxLayout.Y_AXIS));
        formBody.setOpaque(false);
        formBody.setBorder(UIUtils.createPadding(10, 0, 0, 0));

        List<Patient> patients = doctorService.getAllPatients();
        PatientItem[] pItems = new PatientItem[patients.size()];
        for (int i = 0; i < patients.size(); i++) {
            pItems[i] = new PatientItem(patients.get(i));
        }
        patientCombo = new ModernComboBox<>(pItems);
        patientCombo.setPreferredSize(new Dimension(480, 38));
        patientCombo.setAlignmentX(0.0f);

        recommendationArea = new JTextArea(3, 20);
        recommendationArea.setFont(ThemeFonts.BODY);
        recommendationArea.setLineWrap(true);
        recommendationArea.setWrapStyleWord(true);
        recommendationArea.setText("Maintain adequate rest and monitor temperature every 6 hours.");
        JScrollPane recScroll = new JScrollPane(recommendationArea);
        recScroll.setPreferredSize(new Dimension(480, 80));
        recScroll.setAlignmentX(0.0f);

        lifestyleArea = new JTextArea(3, 20);
        lifestyleArea.setFont(ThemeFonts.BODY);
        lifestyleArea.setLineWrap(true);
        lifestyleArea.setWrapStyleWord(true);
        lifestyleArea.setText("Drink minimum 2.5L water daily. Avoid heavy meals before sleep.");
        JScrollPane lifeScroll = new JScrollPane(lifestyleArea);
        lifeScroll.setPreferredSize(new Dimension(480, 80));
        lifeScroll.setAlignmentX(0.0f);

        followUpField = new ModernTextField(LocalDate.now().plusDays(7).toString());
        followUpField.setText(LocalDate.now().plusDays(7).toString());
        followUpField.setPreferredSize(new Dimension(480, 38));
        followUpField.setAlignmentX(0.0f);

        notesField = new ModernTextField("Optional clinical notes");
        notesField.setPreferredSize(new Dimension(480, 38));
        notesField.setAlignmentX(0.0f);

        ModernButton submitBtn = new ModernButton("✓ Submit Recommendation", ModernButton.ButtonStyle.PRIMARY);
        submitBtn.setPreferredSize(new Dimension(480, 40));
        submitBtn.setAlignmentX(0.0f);
        submitBtn.addActionListener(e -> handleSubmit());

        formBody.add(createLabel("Select Patient:"));
        formBody.add(patientCombo);
        formBody.add(Box.createVerticalStrut(8));
        formBody.add(createLabel("Clinical Recommendation / Medical Advice:"));
        formBody.add(recScroll);
        formBody.add(Box.createVerticalStrut(8));
        formBody.add(createLabel("Dietary & Lifestyle Guidance:"));
        formBody.add(lifeScroll);
        formBody.add(Box.createVerticalStrut(8));
        formBody.add(createLabel("Recommended Follow-up Date (YYYY-MM-DD):"));
        formBody.add(followUpField);
        formBody.add(Box.createVerticalStrut(8));
        formBody.add(createLabel("Additional Notes:"));
        formBody.add(notesField);
        formBody.add(Box.createVerticalStrut(14));
        formBody.add(submitBtn);

        formCard.add(formTitle, BorderLayout.NORTH);
        formCard.add(formBody, BorderLayout.CENTER);

        // Column 2: Past Recommendations Table Card
        ModernCard tableCard = new ModernCard(new BorderLayout(), 18);
        JLabel tableTitle = new JLabel("📋 Issued Recommendations History");
        tableTitle.setFont(ThemeFonts.TITLE_SMALL);
        tableTitle.setForeground(ThemeColors.ACCENT_BLUE);

        String[] columns = {"ID", "Patient Name", "Recommendation", "Follow-up", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new ModernTable(tableModel);
        loadTableData();

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);

        tableCard.add(tableTitle, BorderLayout.NORTH);
        tableCard.add(tableScroll, BorderLayout.CENTER);

        grid.add(formCard);
        grid.add(tableCard);

        mainContent.add(grid);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(ThemeFonts.BODY_SMALL_BOLD);
        lbl.setForeground(ThemeColors.TEXT_PRIMARY);
        lbl.setAlignmentX(0.0f);
        return lbl;
    }

    private void loadTableData() {
        Doctor d = SessionManager.getInstance().getCurrentDoctor();
        if (d == null) return;
        pastRecommendations = recommendationService.getDoctorRecommendations(d.getDoctorId());

        tableModel.setRowCount(0);
        for (Recommendation r : pastRecommendations) {
            String shortText = r.getRecommendationText();
            if (shortText.length() > 35) shortText = shortText.substring(0, 32) + "...";
            tableModel.addRow(new Object[]{
                    "#" + r.getRecommendationId(),
                    r.getPatientName() != null ? r.getPatientName() : "Patient #" + r.getPatientId(),
                    shortText,
                    r.getFollowUpDate() != null ? DateTimeUtil.formatDate(r.getFollowUpDate()) : "None",
                    DateTimeUtil.formatTimestamp(r.getCreatedAt())
            });
        }
    }

    private void handleSubmit() {
        Doctor d = SessionManager.getInstance().getCurrentDoctor();
        if (d == null) {
            ToastNotification.show(navManager.getMainFrame(), "Doctor session expired.", ToastNotification.ToastType.ERROR);
            return;
        }

        PatientItem pItem = (PatientItem) patientCombo.getSelectedItem();
        if (pItem == null) {
            ToastNotification.show(navManager.getMainFrame(), "Please select a patient.", ToastNotification.ToastType.WARNING);
            return;
        }

        String recText = recommendationArea.getText().trim();
        String lifestyle = lifestyleArea.getText().trim();
        Date followUp = DateTimeUtil.parseDate(followUpField.getText().trim());
        String notes = notesField.getText().trim();

        RecommendationService.RecommendationResult res = recommendationService.createRecommendation(
                pItem.patient.getPatientId(), d.getDoctorId(), null, recText, lifestyle, followUp, notes
        );

        if (res.isSuccess()) {
            ToastNotification.show(navManager.getMainFrame(), res.getMessage(), ToastNotification.ToastType.SUCCESS);
            loadTableData();
        } else {
            ToastNotification.show(navManager.getMainFrame(), res.getMessage(), ToastNotification.ToastType.ERROR);
        }
    }
}

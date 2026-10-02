package com.medipredict.ui.screens;

import com.medipredict.model.Patient;
import com.medipredict.model.PredictionResultData;
import com.medipredict.model.Symptom;
import com.medipredict.service.PredictionService;
import com.medipredict.service.SessionManager;
import com.medipredict.ui.NavigationManager;
import com.medipredict.ui.components.HeaderPanel;
import com.medipredict.ui.components.ModernButton;
import com.medipredict.ui.components.ModernCard;
import com.medipredict.ui.components.ModernComboBox;
import com.medipredict.ui.components.ModernTextField;
import com.medipredict.ui.components.ResponsiveScrollPanel;
import com.medipredict.ui.components.SidebarPanel;
import com.medipredict.ui.components.SymptomTagChip;
import com.medipredict.ui.components.ToastNotification;
import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;
import com.medipredict.ui.theme.UIUtils;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SymptomPredictionScreen extends JPanel {
    private final NavigationManager navManager;
    private final PredictionService predictionService = new PredictionService();

    private final List<Symptom> allSymptoms;
    private final Set<Symptom> selectedSymptoms = new HashSet<>();
    private final List<SymptomTagChip> chipList = new ArrayList<>();

    private final JPanel chipsContainer;
    private final JLabel selectedCountLabel;
    private final ModernTextField searchField;
    private final ModernComboBox<String> categoryCombo;
    private final ModernTextField notesField;

    public SymptomPredictionScreen(NavigationManager navManager) {
        this.navManager = navManager;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        add(new HeaderPanel(navManager), BorderLayout.NORTH);
        add(new SidebarPanel(navManager, NavigationManager.SCREEN_SYMPTOM_PREDICTION), BorderLayout.WEST);

        allSymptoms = predictionService.getAllSymptoms();

        ResponsiveScrollPanel mainContent = new ResponsiveScrollPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(UIUtils.createPadding(24, 28, 28, 28));

        // 1. Title Banner
        JLabel title = new JLabel("Symptom-Based Medical Condition Prediction");
        title.setFont(ThemeFonts.TITLE_LARGE);
        title.setForeground(ThemeColors.PRIMARY);
        title.setAlignmentX(0.0f);

        JLabel sub = new JLabel("Select all symptoms you are currently experiencing to evaluate preliminary condition correlations.");
        sub.setFont(ThemeFonts.BODY);
        sub.setForeground(ThemeColors.TEXT_SECONDARY);
        sub.setAlignmentX(0.0f);

        mainContent.add(title);
        mainContent.add(Box.createVerticalStrut(4));
        mainContent.add(sub);
        mainContent.add(Box.createVerticalStrut(18));

        // 2. Filters & Search Bar Row
        ModernCard filterCard = new ModernCard(new BorderLayout(), 14);
        filterCard.setAlignmentX(0.0f);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterPanel.setOpaque(false);

        JLabel searchLbl = new JLabel("🔍 Search:");
        searchLbl.setFont(ThemeFonts.BODY_BOLD);
        searchLbl.setForeground(ThemeColors.TEXT_PRIMARY);

        searchField = new ModernTextField("Type symptom (e.g. Headache, Cough, Fever)...");
        searchField.setPreferredSize(new Dimension(300, 38));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterChips(); }
            @Override public void removeUpdate(DocumentEvent e) { filterChips(); }
            @Override public void changedUpdate(DocumentEvent e) { filterChips(); }
        });

        JLabel catLbl = new JLabel("Category:");
        catLbl.setFont(ThemeFonts.BODY_BOLD);
        catLbl.setForeground(ThemeColors.TEXT_PRIMARY);

        List<String> categories = new ArrayList<>();
        categories.add("All Categories");
        categories.addAll(predictionService.getSymptomCategories());

        categoryCombo = new ModernComboBox<>(categories.toArray(new String[0]));
        categoryCombo.setPreferredSize(new Dimension(200, 38));
        categoryCombo.addActionListener(e -> filterChips());

        filterPanel.add(searchLbl);
        filterPanel.add(searchField);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(catLbl);
        filterPanel.add(categoryCombo);

        filterCard.add(filterPanel, BorderLayout.CENTER);
        mainContent.add(filterCard);
        mainContent.add(Box.createVerticalStrut(16));

        // 3. Symptoms Chips Catalog Card
        ModernCard catalogCard = new ModernCard(new BorderLayout(), 18);
        catalogCard.setPreferredSize(new Dimension(900, 360));
        catalogCard.setMinimumSize(new Dimension(400, 240));
        catalogCard.setAlignmentX(0.0f);

        JPanel catHeader = new JPanel(new BorderLayout());
        catHeader.setOpaque(false);
        JLabel catTitle = new JLabel("Click Symptoms to Select / Deselect (Multi-Select Enabled)");
        catTitle.setFont(ThemeFonts.TITLE_SMALL);
        catTitle.setForeground(ThemeColors.TEXT_PRIMARY);

        selectedCountLabel = new JLabel("0 symptoms selected");
        selectedCountLabel.setFont(ThemeFonts.BODY_BOLD);
        selectedCountLabel.setForeground(ThemeColors.PRIMARY);

        catHeader.add(catTitle, BorderLayout.WEST);
        catHeader.add(selectedCountLabel, BorderLayout.EAST);

        chipsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        chipsContainer.setOpaque(false);

        for (Symptom s : allSymptoms) {
            SymptomTagChip chip = new SymptomTagChip(s, false);
            chip.setOnToggleListener(isSelected -> {
                if (isSelected) {
                    selectedSymptoms.add(s);
                } else {
                    selectedSymptoms.remove(s);
                }
                updateSelectedCount();
            });
            chipList.add(chip);
            chipsContainer.add(chip);
        }

        JScrollPane chipsScroll = new JScrollPane(chipsContainer);
        chipsScroll.setBorder(null);
        chipsScroll.setOpaque(false);
        chipsScroll.getViewport().setOpaque(false);

        catalogCard.add(catHeader, BorderLayout.NORTH);
        catalogCard.add(chipsScroll, BorderLayout.CENTER);

        mainContent.add(catalogCard);
        mainContent.add(Box.createVerticalStrut(16));

        // 4. Submission & Notes Card
        ModernCard actionCard = new ModernCard(new BorderLayout(), 18);
        actionCard.setAlignmentX(0.0f);

        JPanel notesPanel = new JPanel();
        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.Y_AXIS));
        notesPanel.setOpaque(false);

        JLabel notesLbl = new JLabel("Optional Observations / Duration Notes:");
        notesLbl.setFont(ThemeFonts.BODY_SMALL_BOLD);
        notesLbl.setForeground(ThemeColors.TEXT_PRIMARY);

        notesField = new ModernTextField("e.g. Symptoms started 2 days ago after exposure to cold weather");
        notesField.setPreferredSize(new Dimension(600, 38));

        notesPanel.add(notesLbl);
        notesPanel.add(Box.createVerticalStrut(4));
        notesPanel.add(notesField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        btnPanel.setOpaque(false);

        ModernButton clearBtn = new ModernButton("Clear All", ModernButton.ButtonStyle.SECONDARY);
        clearBtn.setPreferredSize(new Dimension(110, 42));
        clearBtn.addActionListener(e -> clearSelection());

        ModernButton analyzeBtn = new ModernButton("🩺 Analyze & Predict Condition", ModernButton.ButtonStyle.PRIMARY);
        analyzeBtn.setPreferredSize(new Dimension(260, 42));
        analyzeBtn.setFont(ThemeFonts.BUTTON_LARGE);
        analyzeBtn.addActionListener(e -> handlePrediction());

        btnPanel.add(clearBtn);
        btnPanel.add(analyzeBtn);

        actionCard.add(notesPanel, BorderLayout.CENTER);
        actionCard.add(btnPanel, BorderLayout.EAST);

        mainContent.add(actionCard);
        mainContent.add(Box.createVerticalStrut(16));

        // 5. Medical Notice
        ModernCard noticeCard = new ModernCard(new BorderLayout(), 14);
        noticeCard.setBackground(ThemeColors.PRIMARY_LIGHT);
        noticeCard.setAlignmentX(0.0f);
        JLabel noticeLbl = new JLabel("<html><body style='color: #0F766E; font-size: 11px;'>" +
                "<b>Preliminary Scoring Engine:</b> Selecting multiple symptoms produces a higher-confidence match score and helps detect potential differential conditions.</body></html>");
        noticeCard.add(noticeLbl, BorderLayout.CENTER);
        mainContent.add(noticeCard);

        JScrollPane mainScroll = new JScrollPane(mainContent);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
    }

    private void filterChips() {
        String keyword = searchField.getText().trim().toLowerCase();
        String cat = (String) categoryCombo.getSelectedItem();
        boolean allCats = cat == null || "All Categories".equalsIgnoreCase(cat);

        for (SymptomTagChip chip : chipList) {
            Symptom s = chip.getSymptom();
            boolean matchName = s.getName().toLowerCase().contains(keyword);
            boolean matchCat = allCats || s.getCategory().equalsIgnoreCase(cat);

            chip.setVisible(matchName && matchCat);
        }
        chipsContainer.revalidate();
        chipsContainer.repaint();
    }

    private void updateSelectedCount() {
        int count = selectedSymptoms.size();
        selectedCountLabel.setText(count + " symptom" + (count == 1 ? "" : "s") + " selected");
    }

    private void clearSelection() {
        selectedSymptoms.clear();
        for (SymptomTagChip chip : chipList) {
            chip.setSelected(false);
        }
        updateSelectedCount();
    }

    private void handlePrediction() {
        if (selectedSymptoms.isEmpty()) {
            ToastNotification.show(navManager.getMainFrame(), "Please select at least one symptom to proceed.", ToastNotification.ToastType.WARNING);
            return;
        }

        Patient currentPatient = SessionManager.getInstance().getCurrentPatient();
        int patientId = currentPatient != null ? currentPatient.getPatientId() : 0;
        String notes = notesField.getText().trim();

        List<Symptom> inputList = new ArrayList<>(selectedSymptoms);
        PredictionResultData result = predictionService.runPrediction(patientId, inputList, notes);

        ToastNotification.show(navManager.getMainFrame(), "Condition prediction generated successfully!", ToastNotification.ToastType.SUCCESS);
        navManager.showPredictionResult(result);
    }
}

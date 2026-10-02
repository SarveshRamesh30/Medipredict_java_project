package com.medipredict.ui.screens;

import com.medipredict.model.Patient;
import com.medipredict.model.Prediction;
import com.medipredict.service.PredictionService;
import com.medipredict.service.SessionManager;
import com.medipredict.ui.NavigationManager;
import com.medipredict.ui.components.HeaderPanel;
import com.medipredict.ui.components.ModernButton;
import com.medipredict.ui.components.ModernCard;
import com.medipredict.ui.components.ModernTable;
import com.medipredict.ui.components.ModernTextField;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

public class PredictionHistoryScreen extends JPanel {
    private final NavigationManager navManager;
    private final PredictionService predictionService = new PredictionService();

    private List<Prediction> allPredictions = new ArrayList<>();
    private final DefaultTableModel tableModel;
    private final ModernTable historyTable;
    private final ModernTextField searchField;

    public PredictionHistoryScreen(NavigationManager navManager) {
        this.navManager = navManager;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        add(new HeaderPanel(navManager), BorderLayout.NORTH);
        add(new SidebarPanel(navManager, NavigationManager.SCREEN_PREDICTION_HISTORY), BorderLayout.WEST);

        Patient patient = SessionManager.getInstance().getCurrentPatient();
        int patientId = patient != null ? patient.getPatientId() : 0;
        allPredictions = predictionService.getPredictionHistory(patientId);

        JPanel mainContent = new JPanel(new BorderLayout(0, 16));
        mainContent.setOpaque(false);
        mainContent.setBorder(UIUtils.createPadding(24, 28, 28, 28));

        // Top Section: Title & Search
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setOpaque(false);

        // 1. Header Row
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setAlignmentX(0.0f);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("Prediction History & Clinical Timeline");
        title.setFont(ThemeFonts.TITLE_LARGE);
        title.setForeground(ThemeColors.PRIMARY);

        JLabel sub = new JLabel("Review your previous preliminary assessments and symptom submissions.");
        sub.setFont(ThemeFonts.BODY);
        sub.setForeground(ThemeColors.TEXT_SECONDARY);

        titleBlock.add(title);
        titleBlock.add(sub);

        ModernButton newPredBtn = new ModernButton("+ New Prediction", ModernButton.ButtonStyle.PRIMARY);
        newPredBtn.setPreferredSize(new Dimension(170, 38));
        newPredBtn.addActionListener(e -> navManager.showSymptomPrediction());

        topRow.add(titleBlock, BorderLayout.WEST);
        topRow.add(newPredBtn, BorderLayout.EAST);

        northPanel.add(topRow);
        northPanel.add(Box.createVerticalStrut(16));

        // 2. Search Card
        ModernCard searchCard = new ModernCard(new BorderLayout(), 12);
        searchCard.setAlignmentX(0.0f);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        searchPanel.setOpaque(false);

        JLabel sLbl = new JLabel("🔍 Filter History:");
        sLbl.setFont(ThemeFonts.BODY_BOLD);

        searchField = new ModernTextField("Filter by condition or symptom...");
        searchField.setPreferredSize(new Dimension(360, 36));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterTable(); }
            @Override public void removeUpdate(DocumentEvent e) { filterTable(); }
            @Override public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        searchPanel.add(sLbl);
        searchPanel.add(searchField);
        searchCard.add(searchPanel, BorderLayout.CENTER);

        northPanel.add(searchCard);
        mainContent.add(northPanel, BorderLayout.NORTH);

        // 3. Table Card (Full height and width)
        ModernCard tableCard = new ModernCard(new BorderLayout(), 16);

        String[] columns = {"ID", "Assessment Date", "Reported Symptoms", "Possible Condition", "Match Score", "Confidence", "Risk Level"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new ModernTable(tableModel);
        populateTable(allPredictions);

        JScrollPane tableScroll = new JScrollPane(historyTable);
        tableScroll.setBorder(null);

        // Bottom action bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bottomBar.setOpaque(false);

        ModernButton detailsBtn = new ModernButton("🔍 View Complete Details", ModernButton.ButtonStyle.PRIMARY);
        detailsBtn.addActionListener(e -> showSelectedDetails());

        ModernButton consultBtn = new ModernButton("👨‍⚕️ Consult Doctor", ModernButton.ButtonStyle.OUTLINE);
        consultBtn.addActionListener(e -> navManager.showDoctorDirectory());

        bottomBar.add(detailsBtn);
        bottomBar.add(consultBtn);

        tableCard.add(tableScroll, BorderLayout.CENTER);
        tableCard.add(bottomBar, BorderLayout.SOUTH);

        mainContent.add(tableCard, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }

    private void populateTable(List<Prediction> list) {
        tableModel.setRowCount(0);
        for (Prediction p : list) {
            tableModel.addRow(new Object[]{
                    "#" + p.getPredictionId(),
                    DateTimeUtil.formatTimestamp(p.getCreatedAt()),
                    p.getSymptomNames(),
                    p.getConditionName(),
                    p.getMatchScore() + "%",
                    p.getConfidenceLevel(),
                    p.getRiskLevel()
            });
        }
    }

    private void filterTable() {
        String kw = searchField.getText().trim().toLowerCase();
        List<Prediction> filtered = new ArrayList<>();
        for (Prediction p : allPredictions) {
            boolean matchCond = p.getConditionName() != null && p.getConditionName().toLowerCase().contains(kw);
            boolean matchSym = p.getSymptomNames() != null && p.getSymptomNames().toLowerCase().contains(kw);
            if (matchCond || matchSym) {
                filtered.add(p);
            }
        }
        populateTable(filtered);
    }

    private void showSelectedDetails() {
        int row = historyTable.getSelectedRow();
        if (row < 0) {
            if (!allPredictions.isEmpty()) {
                row = 0;
            } else {
                return;
            }
        }

        String idStr = (String) tableModel.getValueAt(row, 0);
        int predId = Integer.parseInt(idStr.replace("#", ""));
        Prediction fullPred = predictionService.getPredictionDetails(predId);
        if (fullPred == null) return;

        JDialog dialog = new JDialog(navManager.getMainFrame(), "Prediction Details - #" + predId, true);
        dialog.setSize(520, 540);
        dialog.setLocationRelativeTo(navManager.getMainFrame());

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(UIUtils.createPadding(20, 24, 20, 24));

        JLabel title = new JLabel("Possible Condition: " + fullPred.getConditionName());
        title.setFont(ThemeFonts.TITLE_MEDIUM);
        title.setForeground(ThemeColors.PRIMARY);

        JLabel dateLbl = new JLabel("Date: " + DateTimeUtil.formatTimestamp(fullPred.getCreatedAt()) +
                " | Match: " + fullPred.getMatchScore() + "% (" + fullPred.getConfidenceLevel() + " Confidence)");
        dateLbl.setFont(ThemeFonts.BODY_SMALL);
        dateLbl.setForeground(ThemeColors.TEXT_MUTED);

        JLabel symHead = new JLabel("Reported Symptoms:");
        symHead.setFont(ThemeFonts.BODY_SMALL_BOLD);
        symHead.setForeground(ThemeColors.TEXT_PRIMARY);

        JTextArea symArea = new JTextArea(fullPred.getSymptomNames());
        symArea.setFont(ThemeFonts.BODY);
        symArea.setEditable(false);
        symArea.setLineWrap(true);
        symArea.setWrapStyleWord(true);
        symArea.setOpaque(false);

        JLabel precHead = new JLabel("General Precautions & Self-Care:");
        precHead.setFont(ThemeFonts.BODY_SMALL_BOLD);
        precHead.setForeground(ThemeColors.TEXT_PRIMARY);

        JTextArea precArea = new JTextArea(fullPred.getPrecautions() != null ? fullPred.getPrecautions() : "Follow standard wellness guidance.");
        precArea.setFont(ThemeFonts.BODY);
        precArea.setEditable(false);
        precArea.setLineWrap(true);
        precArea.setWrapStyleWord(true);
        precArea.setOpaque(false);

        ModernButton closeBtn = new ModernButton("Close", ModernButton.ButtonStyle.PRIMARY);
        closeBtn.addActionListener(e -> dialog.dispose());

        p.add(title);
        p.add(Box.createVerticalStrut(4));
        p.add(dateLbl);
        p.add(Box.createVerticalStrut(14));
        p.add(symHead);
        p.add(Box.createVerticalStrut(4));
        p.add(symArea);
        p.add(Box.createVerticalStrut(14));
        p.add(precHead);
        p.add(Box.createVerticalStrut(4));
        p.add(precArea);
        p.add(Box.createVerticalStrut(20));
        p.add(closeBtn);

        dialog.getContentPane().add(p);
        dialog.setVisible(true);
    }
}

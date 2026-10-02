package com.medipredict.ui.screens;

import com.medipredict.model.Patient;
import com.medipredict.model.Prediction;
import com.medipredict.service.DoctorService;
import com.medipredict.service.PredictionService;
import com.medipredict.ui.NavigationManager;
import com.medipredict.ui.components.HeaderPanel;
import com.medipredict.ui.components.ModernButton;
import com.medipredict.ui.components.ModernCard;
import com.medipredict.ui.components.ModernTable;
import com.medipredict.ui.components.ModernTextField;
import com.medipredict.ui.components.SidebarPanel;
import com.medipredict.ui.components.ToastNotification;
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
import java.awt.GridLayout;
import java.util.List;

public class DoctorPatientsScreen extends JPanel {
    private final NavigationManager navManager;
    private final DoctorService doctorService = new DoctorService();
    private final PredictionService predictionService = new PredictionService();

    private List<Patient> patientList;
    private final DefaultTableModel tableModel;
    private final ModernTable table;
    private final ModernTextField searchField;

    public DoctorPatientsScreen(NavigationManager navManager) {
        this.navManager = navManager;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        add(new HeaderPanel(navManager), BorderLayout.NORTH);
        add(new SidebarPanel(navManager, NavigationManager.SCREEN_DOCTOR_PATIENTS), BorderLayout.WEST);

        JPanel mainContent = new JPanel(new BorderLayout(0, 16));
        mainContent.setOpaque(false);
        mainContent.setBorder(UIUtils.createPadding(24, 28, 28, 28));

        // Top Section: Title & Search
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setOpaque(false);

        // 1. Header
        JLabel title = new JLabel("Patient Records & Clinical History");
        title.setFont(ThemeFonts.TITLE_LARGE);
        title.setForeground(ThemeColors.PRIMARY);
        title.setAlignmentX(0.0f);

        JLabel sub = new JLabel("Browse patient profiles, review historic symptom assessments, and inspect clinical backgrounds.");
        sub.setFont(ThemeFonts.BODY);
        sub.setForeground(ThemeColors.TEXT_SECONDARY);
        sub.setAlignmentX(0.0f);

        northPanel.add(title);
        northPanel.add(Box.createVerticalStrut(4));
        northPanel.add(sub);
        northPanel.add(Box.createVerticalStrut(16));

        // 2. Search Card
        ModernCard searchCard = new ModernCard(new BorderLayout(), 12);
        searchCard.setAlignmentX(0.0f);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        searchPanel.setOpaque(false);

        JLabel sLbl = new JLabel("🔍 Search Patient:");
        sLbl.setFont(ThemeFonts.BODY_BOLD);

        searchField = new ModernTextField("Search by patient name, email, or ID...");
        searchField.setPreferredSize(new Dimension(360, 36));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { refreshTable(); }
            @Override public void removeUpdate(DocumentEvent e) { refreshTable(); }
            @Override public void changedUpdate(DocumentEvent e) { refreshTable(); }
        });

        searchPanel.add(sLbl);
        searchPanel.add(searchField);
        filterCardAdd(searchCard, searchPanel);
        northPanel.add(searchCard);

        mainContent.add(northPanel, BorderLayout.NORTH);

        // 3. Table Card (Full height and width)
        ModernCard tableCard = new ModernCard(new BorderLayout(), 16);

        String[] columns = {"Patient ID", "Full Name", "Email Address", "Phone", "Age / DOB", "Gender", "Blood Group"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new ModernTable(tableModel);
        loadPatients();

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        actions.setOpaque(false);

        ModernButton viewDetailsBtn = new ModernButton("🔍 View Full Patient Clinical File", ModernButton.ButtonStyle.PRIMARY);
        viewDetailsBtn.addActionListener(e -> showPatientDetails());

        ModernButton msgBtn = new ModernButton("💬 Message Patient", ModernButton.ButtonStyle.SECONDARY);
        msgBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && row < patientList.size()) {
                navManager.showMessaging(patientList.get(row).getUserId());
            } else {
                ToastNotification.show(navManager.getMainFrame(), "Select a patient to message.", ToastNotification.ToastType.WARNING);
            }
        });

        actions.add(msgBtn);
        actions.add(viewDetailsBtn);

        tableCard.add(tableScroll, BorderLayout.CENTER);
        tableCard.add(actions, BorderLayout.SOUTH);

        mainContent.add(tableCard, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }

    private void filterCardAdd(ModernCard card, JPanel p) {
        card.add(p, BorderLayout.CENTER);
    }

    private void loadPatients() {
        patientList = doctorService.getAllPatients();
        refreshTable();
    }

    private void refreshTable() {
        String kw = searchField.getText().trim();
        List<Patient> filtered = kw.isEmpty() ? patientList : doctorService.searchPatients(kw);

        tableModel.setRowCount(0);
        for (Patient p : filtered) {
            int age = p.getDob() != null ? DateTimeUtil.calculateAge(p.getDob()) : 0;
            String dobStr = p.getDob() != null ? (age > 0 ? age + " yrs (" + DateTimeUtil.formatDate(p.getDob()) + ")" : DateTimeUtil.formatDate(p.getDob())) : "N/A";
            tableModel.addRow(new Object[]{
                    "#" + p.getPatientId(),
                    p.getFullName(),
                    p.getEmail(),
                    p.getPhone(),
                    dobStr,
                    p.getGender(),
                    p.getBloodGroup()
            });
        }
    }

    private void showPatientDetails() {
        int row = table.getSelectedRow();
        if (row < 0) {
            ToastNotification.show(navManager.getMainFrame(), "Please select a patient from the table.", ToastNotification.ToastType.WARNING);
            return;
        }

        String idStr = (String) tableModel.getValueAt(row, 0);
        int patientId = Integer.parseInt(idStr.replace("#", ""));

        Patient selected = null;
        for (Patient p : patientList) {
            if (p.getPatientId() == patientId) {
                selected = p;
                break;
            }
        }
        if (selected == null) return;

        List<Prediction> preds = predictionService.getPredictionHistory(patientId);

        JDialog dialog = new JDialog(navManager.getMainFrame(), "Clinical Patient File - " + selected.getFullName(), true);
        dialog.setSize(620, 600);
        dialog.setLocationRelativeTo(navManager.getMainFrame());

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(UIUtils.createPadding(22, 26, 22, 26));

        JLabel nameLbl = new JLabel(selected.getFullName() + " (Patient ID: #" + selected.getPatientId() + ")");
        nameLbl.setFont(ThemeFonts.TITLE_MEDIUM);
        nameLbl.setForeground(ThemeColors.PRIMARY);

        JLabel bio = new JLabel("Email: " + selected.getEmail() + " | Phone: " + selected.getPhone() + " | Blood: " + selected.getBloodGroup());
        bio.setFont(ThemeFonts.BODY);
        bio.setForeground(ThemeColors.TEXT_SECONDARY);

        JLabel historyHead = new JLabel("Known Medical History / Allergies:");
        historyHead.setFont(ThemeFonts.BODY_SMALL_BOLD);
        historyHead.setForeground(ThemeColors.TEXT_PRIMARY);

        JTextArea histArea = new JTextArea(selected.getMedicalHistory() != null && !selected.getMedicalHistory().isEmpty() ?
                selected.getMedicalHistory() : "None reported.");
        histArea.setFont(ThemeFonts.BODY);
        histArea.setEditable(false);
        histArea.setLineWrap(true);
        histArea.setWrapStyleWord(true);
        histArea.setOpaque(false);

        JLabel predHead = new JLabel("Symptom Prediction History (" + preds.size() + " records):");
        predHead.setFont(ThemeFonts.BODY_SMALL_BOLD);
        predHead.setForeground(ThemeColors.PRIMARY);

        JPanel predList = new JPanel();
        predList.setLayout(new BoxLayout(predList, BoxLayout.Y_AXIS));
        predList.setOpaque(false);

        if (preds.isEmpty()) {
            JLabel noPred = new JLabel("No preliminary symptom reports submitted yet.");
            noPred.setFont(ThemeFonts.BODY_SMALL);
            noPred.setForeground(ThemeColors.TEXT_MUTED);
            predList.add(noPred);
        } else {
            for (Prediction pred : preds) {
                JPanel item = new JPanel(new BorderLayout());
                item.setOpaque(false);
                item.setBorder(UIUtils.createPadding(4, 0, 4, 0));

                JLabel cName = new JLabel("🩺 " + pred.getConditionName() + " (" + pred.getMatchScore() + "% Match - " + pred.getConfidenceLevel() + ")");
                cName.setFont(ThemeFonts.BODY_BOLD);
                JLabel sNames = new JLabel("Symptoms: " + pred.getSymptomNames() + " [" + DateTimeUtil.formatTimestamp(pred.getCreatedAt()) + "]");
                sNames.setFont(ThemeFonts.BODY_SMALL);
                sNames.setForeground(ThemeColors.TEXT_MUTED);

                item.add(cName, BorderLayout.NORTH);
                item.add(sNames, BorderLayout.CENTER);
                predList.add(item);
            }
        }

        JScrollPane predScroll = new JScrollPane(predList);
        predScroll.setPreferredSize(new Dimension(550, 180));
        predScroll.setBorder(UIUtils.createPadding(4, 4, 4, 4));

        ModernButton closeBtn = new ModernButton("Close Record", ModernButton.ButtonStyle.PRIMARY);
        closeBtn.addActionListener(e -> dialog.dispose());

        p.add(nameLbl);
        p.add(Box.createVerticalStrut(4));
        p.add(bio);
        p.add(Box.createVerticalStrut(12));
        p.add(historyHead);
        p.add(Box.createVerticalStrut(4));
        p.add(histArea);
        p.add(Box.createVerticalStrut(14));
        p.add(predHead);
        p.add(Box.createVerticalStrut(6));
        p.add(predScroll);
        p.add(Box.createVerticalStrut(16));
        p.add(closeBtn);

        dialog.getContentPane().add(p);
        dialog.setVisible(true);
    }
}

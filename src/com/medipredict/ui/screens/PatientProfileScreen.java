package com.medipredict.ui.screens;

import com.medipredict.model.Patient;
import com.medipredict.service.PatientService;
import com.medipredict.service.SessionManager;
import com.medipredict.ui.NavigationManager;
import com.medipredict.ui.components.HeaderPanel;
import com.medipredict.ui.components.ModernButton;
import com.medipredict.ui.components.ModernCard;
import com.medipredict.ui.components.ModernComboBox;
import com.medipredict.ui.components.ModernPasswordField;
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
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.Date;

public class PatientProfileScreen extends JPanel {
    private final NavigationManager navManager;
    private final PatientService patientService = new PatientService();

    private final ModernTextField fullNameField;
    private final ModernTextField emailField;
    private final ModernTextField phoneField;
    private final ModernTextField dobField;
    private final ModernComboBox<String> genderCombo;
    private final ModernComboBox<String> bloodGroupCombo;
    private final ModernTextField medicalHistoryField;
    private final ModernTextField emergencyContactField;

    private final ModernPasswordField oldPassField;
    private final ModernPasswordField newPassField;
    private final ModernPasswordField confirmPassField;

    public PatientProfileScreen(NavigationManager navManager) {
        this.navManager = navManager;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        add(new HeaderPanel(navManager), BorderLayout.NORTH);
        add(new SidebarPanel(navManager, NavigationManager.SCREEN_PATIENT_PROFILE), BorderLayout.WEST);

        Patient patient = SessionManager.getInstance().getCurrentPatient();

        ResponsiveScrollPanel mainContent = new ResponsiveScrollPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(UIUtils.createPadding(24, 28, 28, 28));

        // 1. Header
        JLabel title = new JLabel("Patient Profile & Health Information");
        title.setFont(ThemeFonts.TITLE_LARGE);
        title.setForeground(ThemeColors.PRIMARY);
        title.setAlignmentX(0.0f);

        JLabel sub = new JLabel("Update your contact details, emergency contacts, medical background, and account security.");
        sub.setFont(ThemeFonts.BODY);
        sub.setForeground(ThemeColors.TEXT_SECONDARY);
        sub.setAlignmentX(0.0f);

        mainContent.add(title);
        mainContent.add(Box.createVerticalStrut(4));
        mainContent.add(sub);
        mainContent.add(Box.createVerticalStrut(18));

        // 2. Two Columns (Profile Form & Change Password Form)
        JPanel grid = new JPanel(new GridLayout(1, 2, 20, 0));
        grid.setOpaque(false);
        grid.setAlignmentX(0.0f);

        // Column 1: Personal & Health Info
        ModernCard profileCard = new ModernCard(new BorderLayout(), 18);
        JLabel profTitle = new JLabel("👤 Personal & Medical Information");
        profTitle.setFont(ThemeFonts.TITLE_SMALL);
        profTitle.setForeground(ThemeColors.PRIMARY);

        JPanel profBody = new JPanel();
        profBody.setLayout(new BoxLayout(profBody, BoxLayout.Y_AXIS));
        profBody.setOpaque(false);
        profBody.setBorder(UIUtils.createPadding(10, 0, 0, 0));

        fullNameField = new ModernTextField();
        fullNameField.setText(patient != null ? patient.getFullName() : "");
        fullNameField.setMaximumSize(new Dimension(500, 36));
        fullNameField.setAlignmentX(0.0f);

        emailField = new ModernTextField();
        emailField.setText(patient != null ? patient.getEmail() : "");
        emailField.setEditable(false);
        emailField.setMaximumSize(new Dimension(500, 36));
        emailField.setAlignmentX(0.0f);

        phoneField = new ModernTextField();
        phoneField.setText(patient != null ? patient.getPhone() : "");
        phoneField.setMaximumSize(new Dimension(500, 36));
        phoneField.setAlignmentX(0.0f);

        dobField = new ModernTextField();
        dobField.setText(patient != null && patient.getDob() != null ? DateTimeUtil.formatIsoDate(patient.getDob()) : "");
        dobField.setMaximumSize(new Dimension(500, 36));
        dobField.setAlignmentX(0.0f);

        genderCombo = new ModernComboBox<>(new String[]{"Male", "Female", "Other", "Prefer not to say"});
        if (patient != null && patient.getGender() != null) {
            genderCombo.setSelectedItem(patient.getGender());
        }
        genderCombo.setMaximumSize(new Dimension(500, 36));
        genderCombo.setAlignmentX(0.0f);

        bloodGroupCombo = new ModernComboBox<>(new String[]{"Unknown", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"});
        if (patient != null && patient.getBloodGroup() != null) {
            bloodGroupCombo.setSelectedItem(patient.getBloodGroup());
        }
        bloodGroupCombo.setMaximumSize(new Dimension(500, 36));
        bloodGroupCombo.setAlignmentX(0.0f);

        medicalHistoryField = new ModernTextField();
        medicalHistoryField.setText(patient != null && patient.getMedicalHistory() != null ? patient.getMedicalHistory() : "");
        medicalHistoryField.setMaximumSize(new Dimension(500, 36));
        medicalHistoryField.setAlignmentX(0.0f);

        emergencyContactField = new ModernTextField();
        emergencyContactField.setText(patient != null && patient.getEmergencyContact() != null ? patient.getEmergencyContact() : "");
        emergencyContactField.setMaximumSize(new Dimension(500, 36));
        emergencyContactField.setAlignmentX(0.0f);

        ModernButton saveProfileBtn = new ModernButton("💾 Save Profile Changes", ModernButton.ButtonStyle.PRIMARY);
        saveProfileBtn.setMaximumSize(new Dimension(500, 38));
        saveProfileBtn.setAlignmentX(0.0f);
        saveProfileBtn.addActionListener(e -> handleSaveProfile());

        profBody.add(createLabel("Full Name:"));
        profBody.add(fullNameField);
        profBody.add(Box.createVerticalStrut(6));
        profBody.add(createLabel("Registered Email (Fixed):"));
        profBody.add(emailField);
        profBody.add(Box.createVerticalStrut(6));
        profBody.add(createLabel("Contact Phone:"));
        profBody.add(phoneField);
        profBody.add(Box.createVerticalStrut(6));
        profBody.add(createLabel("Date of Birth (YYYY-MM-DD):"));
        profBody.add(dobField);
        profBody.add(Box.createVerticalStrut(6));
        profBody.add(createLabel("Blood Group:"));
        profBody.add(bloodGroupCombo);
        profBody.add(Box.createVerticalStrut(6));
        profBody.add(createLabel("Medical History / Allergies:"));
        profBody.add(medicalHistoryField);
        profBody.add(Box.createVerticalStrut(6));
        profBody.add(createLabel("Emergency Contact:"));
        profBody.add(emergencyContactField);
        profBody.add(Box.createVerticalStrut(12));
        profBody.add(saveProfileBtn);

        profileCard.add(profTitle, BorderLayout.NORTH);
        profileCard.add(profBody, BorderLayout.CENTER);

        // Column 2: Change Password
        ModernCard securityCard = new ModernCard(new BorderLayout(), 18);
        JLabel secTitle = new JLabel("🔒 Account Password & Security");
        secTitle.setFont(ThemeFonts.TITLE_SMALL);
        secTitle.setForeground(ThemeColors.ACCENT_BLUE);

        JPanel secBody = new JPanel();
        secBody.setLayout(new BoxLayout(secBody, BoxLayout.Y_AXIS));
        secBody.setOpaque(false);
        secBody.setBorder(UIUtils.createPadding(10, 0, 0, 0));

        oldPassField = new ModernPasswordField("Enter current password");
        oldPassField.setMaximumSize(new Dimension(500, 36));
        oldPassField.setAlignmentX(0.0f);

        newPassField = new ModernPasswordField("Enter new password (min 6 chars)");
        newPassField.setMaximumSize(new Dimension(500, 36));
        newPassField.setAlignmentX(0.0f);

        confirmPassField = new ModernPasswordField("Confirm new password");
        confirmPassField.setMaximumSize(new Dimension(500, 36));
        confirmPassField.setAlignmentX(0.0f);

        ModernButton passBtn = new ModernButton("Update Password", ModernButton.ButtonStyle.OUTLINE);
        passBtn.setMaximumSize(new Dimension(500, 38));
        passBtn.setAlignmentX(0.0f);
        passBtn.addActionListener(e -> handleChangePassword());

        secBody.add(createLabel("Current Password:"));
        secBody.add(oldPassField);
        secBody.add(Box.createVerticalStrut(10));
        secBody.add(createLabel("New Password:"));
        secBody.add(newPassField);
        secBody.add(Box.createVerticalStrut(10));
        secBody.add(createLabel("Confirm New Password:"));
        secBody.add(confirmPassField);
        secBody.add(Box.createVerticalStrut(16));
        secBody.add(passBtn);

        securityCard.add(secTitle, BorderLayout.NORTH);
        securityCard.add(secBody, BorderLayout.CENTER);

        grid.add(profileCard);
        grid.add(securityCard);

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

    private void handleSaveProfile() {
        Patient p = SessionManager.getInstance().getCurrentPatient();
        if (p == null) return;

        p.setFullName(fullNameField.getText().trim());
        p.setPhone(phoneField.getText().trim());
        p.setDob(DateTimeUtil.parseDate(dobField.getText().trim()));
        p.setGender((String) genderCombo.getSelectedItem());
        p.setBloodGroup((String) bloodGroupCombo.getSelectedItem());
        p.setMedicalHistory(medicalHistoryField.getText().trim());
        p.setEmergencyContact(emergencyContactField.getText().trim());

        boolean ok = patientService.updateProfile(p);
        if (ok) {
            ToastNotification.show(navManager.getMainFrame(), "Profile information updated successfully!", ToastNotification.ToastType.SUCCESS);
        } else {
            ToastNotification.show(navManager.getMainFrame(), "Failed to update profile.", ToastNotification.ToastType.ERROR);
        }
    }

    private void handleChangePassword() {
        Patient p = SessionManager.getInstance().getCurrentPatient();
        if (p == null) return;

        String oldP = new String(oldPassField.getPassword());
        String newP = new String(newPassField.getPassword());
        String confP = new String(confirmPassField.getPassword());

        if (!newP.equals(confP)) {
            ToastNotification.show(navManager.getMainFrame(), "New passwords do not match.", ToastNotification.ToastType.WARNING);
            return;
        }

        boolean ok = patientService.changePassword(p.getUserId(), oldP, newP);
        if (ok) {
            ToastNotification.show(navManager.getMainFrame(), "Password updated successfully!", ToastNotification.ToastType.SUCCESS);
            oldPassField.setText("");
            newPassField.setText("");
            confirmPassField.setText("");
        } else {
            ToastNotification.show(navManager.getMainFrame(), "Incorrect current password or invalid new password.", ToastNotification.ToastType.ERROR);
        }
    }
}

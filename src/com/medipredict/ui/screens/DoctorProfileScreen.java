package com.medipredict.ui.screens;

import com.medipredict.model.Doctor;
import com.medipredict.service.DoctorService;
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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.math.BigDecimal;

public class DoctorProfileScreen extends JPanel {
    private final NavigationManager navManager;
    private final DoctorService doctorService = new DoctorService();
    private final PatientService patientService = new PatientService(); // for changePassword

    private final ModernTextField fullNameField;
    private final ModernTextField emailField;
    private final ModernTextField phoneField;
    private final ModernComboBox<String> specializationCombo;
    private final ModernTextField hospitalField;
    private final ModernTextField experienceField;
    private final ModernTextField feeField;
    private final ModernTextField hoursField;
    private final ModernTextField bioField;

    private final ModernPasswordField oldPassField;
    private final ModernPasswordField newPassField;
    private final ModernPasswordField confirmPassField;

    public DoctorProfileScreen(NavigationManager navManager) {
        this.navManager = navManager;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        add(new HeaderPanel(navManager), BorderLayout.NORTH);
        add(new SidebarPanel(navManager, NavigationManager.SCREEN_DOCTOR_PROFILE), BorderLayout.WEST);

        Doctor doctor = SessionManager.getInstance().getCurrentDoctor();

        ResponsiveScrollPanel mainContent = new ResponsiveScrollPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(UIUtils.createPadding(24, 28, 28, 28));

        // 1. Header
        JLabel title = new JLabel("Doctor Profile & Clinical Settings");
        title.setFont(ThemeFonts.TITLE_LARGE);
        title.setForeground(ThemeColors.PRIMARY);
        title.setAlignmentX(0.0f);

        JLabel sub = new JLabel("Manage clinical credentials, consulting fees, working hours, and account security.");
        sub.setFont(ThemeFonts.BODY);
        sub.setForeground(ThemeColors.TEXT_SECONDARY);
        sub.setAlignmentX(0.0f);

        mainContent.add(title);
        mainContent.add(Box.createVerticalStrut(4));
        mainContent.add(sub);
        mainContent.add(Box.createVerticalStrut(18));

        // 2. Two Columns (Clinical Profile & Security)
        JPanel grid = new JPanel(new GridLayout(1, 2, 20, 0));
        grid.setOpaque(false);
        grid.setAlignmentX(0.0f);

        // Column 1: Profile
        ModernCard profileCard = new ModernCard(new BorderLayout(), 18);
        JLabel profTitle = new JLabel("👨‍⚕️ Clinical Practice Details");
        profTitle.setFont(ThemeFonts.TITLE_SMALL);
        profTitle.setForeground(ThemeColors.PRIMARY);

        JPanel profBody = new JPanel();
        profBody.setLayout(new BoxLayout(profBody, BoxLayout.Y_AXIS));
        profBody.setOpaque(false);
        profBody.setBorder(UIUtils.createPadding(10, 0, 0, 0));

        fullNameField = new ModernTextField();
        fullNameField.setText(doctor != null ? doctor.getFullName() : "");
        fullNameField.setMaximumSize(new Dimension(500, 36));
        fullNameField.setAlignmentX(0.0f);

        emailField = new ModernTextField();
        emailField.setText(doctor != null ? doctor.getEmail() : "");
        emailField.setEditable(false);
        emailField.setMaximumSize(new Dimension(500, 36));
        emailField.setAlignmentX(0.0f);

        phoneField = new ModernTextField();
        phoneField.setText(doctor != null ? doctor.getPhone() : "");
        phoneField.setMaximumSize(new Dimension(500, 36));
        phoneField.setAlignmentX(0.0f);

        specializationCombo = new ModernComboBox<>(new String[]{
                "General Physician", "Neurologist", "Pulmonologist",
                "Gastroenterologist", "Cardiologist", "ENT Specialist", "Dermatologist"
        });
        if (doctor != null && doctor.getSpecialization() != null) {
            specializationCombo.setSelectedItem(doctor.getSpecialization());
        }
        specializationCombo.setMaximumSize(new Dimension(500, 36));
        specializationCombo.setAlignmentX(0.0f);

        hospitalField = new ModernTextField();
        hospitalField.setText(doctor != null ? doctor.getHospitalClinic() : "");
        hospitalField.setMaximumSize(new Dimension(500, 36));
        hospitalField.setAlignmentX(0.0f);

        experienceField = new ModernTextField();
        experienceField.setText(doctor != null ? String.valueOf(doctor.getExperienceYears()) : "0");
        experienceField.setMaximumSize(new Dimension(500, 36));
        experienceField.setAlignmentX(0.0f);

        feeField = new ModernTextField();
        feeField.setText(doctor != null && doctor.getConsultationFee() != null ? doctor.getConsultationFee().toString() : "50.00");
        feeField.setMaximumSize(new Dimension(500, 36));
        feeField.setAlignmentX(0.0f);

        hoursField = new ModernTextField();
        hoursField.setText(doctor != null && doctor.getAvailabilityHours() != null ? doctor.getAvailabilityHours() : "9:00 AM - 5:00 PM");
        hoursField.setMaximumSize(new Dimension(500, 36));
        hoursField.setAlignmentX(0.0f);

        bioField = new ModernTextField();
        bioField.setText(doctor != null && doctor.getBio() != null ? doctor.getBio() : "");
        bioField.setMaximumSize(new Dimension(500, 36));
        bioField.setAlignmentX(0.0f);

        ModernButton saveProfileBtn = new ModernButton("💾 Save Practice Details", ModernButton.ButtonStyle.PRIMARY);
        saveProfileBtn.setMaximumSize(new Dimension(500, 38));
        saveProfileBtn.setAlignmentX(0.0f);
        saveProfileBtn.addActionListener(e -> handleSaveProfile());

        profBody.add(createLabel("Doctor Full Name:"));
        profBody.add(fullNameField);
        profBody.add(Box.createVerticalStrut(6));
        profBody.add(createLabel("Registered Email (Fixed):"));
        profBody.add(emailField);
        profBody.add(Box.createVerticalStrut(6));
        profBody.add(createLabel("Contact Phone:"));
        profBody.add(phoneField);
        profBody.add(Box.createVerticalStrut(6));
        profBody.add(createLabel("Medical Specialization:"));
        profBody.add(specializationCombo);
        profBody.add(Box.createVerticalStrut(6));
        profBody.add(createLabel("Hospital / Clinic Affiliation:"));
        profBody.add(hospitalField);
        profBody.add(Box.createVerticalStrut(6));
        profBody.add(createLabel("Years of Experience:"));
        profBody.add(experienceField);
        profBody.add(Box.createVerticalStrut(6));
        profBody.add(createLabel("Consultation Fee ($):"));
        profBody.add(feeField);
        profBody.add(Box.createVerticalStrut(6));
        profBody.add(createLabel("Availability Hours:"));
        profBody.add(hoursField);
        profBody.add(Box.createVerticalStrut(10));
        profBody.add(saveProfileBtn);

        profileCard.add(profTitle, BorderLayout.NORTH);
        profileCard.add(profBody, BorderLayout.CENTER);

        // Column 2: Security & Bio
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

        secBody.add(createLabel("Professional Bio / Credentials:"));
        secBody.add(bioField);
        secBody.add(Box.createVerticalStrut(14));
        secBody.add(createLabel("Current Password:"));
        secBody.add(oldPassField);
        secBody.add(Box.createVerticalStrut(8));
        secBody.add(createLabel("New Password:"));
        secBody.add(newPassField);
        secBody.add(Box.createVerticalStrut(8));
        secBody.add(createLabel("Confirm New Password:"));
        secBody.add(confirmPassField);
        secBody.add(Box.createVerticalStrut(14));
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
        Doctor d = SessionManager.getInstance().getCurrentDoctor();
        if (d == null) return;

        d.setFullName(fullNameField.getText().trim());
        d.setPhone(phoneField.getText().trim());
        d.setSpecialization((String) specializationCombo.getSelectedItem());
        d.setHospitalClinic(hospitalField.getText().trim());

        try {
            d.setExperienceYears(Integer.parseInt(experienceField.getText().trim()));
        } catch (Exception ignored) {}

        try {
            d.setConsultationFee(new BigDecimal(feeField.getText().trim()));
        } catch (Exception ignored) {}

        d.setAvailabilityHours(hoursField.getText().trim());
        d.setBio(bioField.getText().trim());

        boolean ok = doctorService.updateProfile(d);
        if (ok) {
            ToastNotification.show(navManager.getMainFrame(), "Practice details updated successfully!", ToastNotification.ToastType.SUCCESS);
        } else {
            ToastNotification.show(navManager.getMainFrame(), "Failed to update doctor profile.", ToastNotification.ToastType.ERROR);
        }
    }

    private void handleChangePassword() {
        Doctor d = SessionManager.getInstance().getCurrentDoctor();
        if (d == null) return;

        String oldP = new String(oldPassField.getPassword());
        String newP = new String(newPassField.getPassword());
        String confP = new String(confirmPassField.getPassword());

        if (!newP.equals(confP)) {
            ToastNotification.show(navManager.getMainFrame(), "New passwords do not match.", ToastNotification.ToastType.WARNING);
            return;
        }

        boolean ok = patientService.changePassword(d.getUserId(), oldP, newP);
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

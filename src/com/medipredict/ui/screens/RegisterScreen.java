package com.medipredict.ui.screens;

import com.medipredict.model.Role;
import com.medipredict.service.AuthService;
import com.medipredict.ui.NavigationManager;
import com.medipredict.ui.components.HeaderPanel;
import com.medipredict.ui.components.ModernButton;
import com.medipredict.ui.components.ModernCard;
import com.medipredict.ui.components.ModernComboBox;
import com.medipredict.ui.components.ModernPasswordField;
import com.medipredict.ui.components.ModernTextField;
import com.medipredict.ui.components.ToastNotification;
import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;
import com.medipredict.ui.theme.UIUtils;
import com.medipredict.util.DateTimeUtil;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.math.BigDecimal;
import java.sql.Date;

public class RegisterScreen extends JPanel {
    private final NavigationManager navManager;
    private final AuthService authService = new AuthService();

    private Role selectedRole = Role.PATIENT;

    // Common Fields
    private final ModernTextField fullNameField;
    private final ModernTextField emailField;
    private final ModernTextField phoneField;
    private final ModernPasswordField passwordField;

    // Patient Fields
    private final ModernTextField dobField;
    private final ModernComboBox<String> genderCombo;
    private final ModernComboBox<String> bloodGroupCombo;
    private final ModernTextField medicalHistoryField;
    private final ModernTextField emergencyContactField;

    // Doctor Fields
    private final ModernComboBox<String> specializationCombo;
    private final ModernTextField hospitalField;
    private final ModernTextField experienceField;
    private final ModernTextField feeField;
    private final ModernTextField bioField;
    private final ModernTextField availabilityField;

    private final CardLayout roleCardLayout = new CardLayout();
    private final JPanel roleSpecificPanel = new JPanel(roleCardLayout);
    private final JLabel errorLabel;

    public RegisterScreen(NavigationManager navManager) {
        this.navManager = navManager;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        add(new HeaderPanel(navManager), BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(UIUtils.createPadding(20, 20, 30, 20));

        ModernCard regCard = new ModernCard(new BorderLayout(), 26);
        regCard.setPreferredSize(new Dimension(580, 940));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Create Your MediPredict Account");
        titleLabel.setFont(ThemeFonts.TITLE_LARGE);
        titleLabel.setForeground(ThemeColors.PRIMARY);
        titleLabel.setAlignmentX(0.0f);

        JLabel subLabel = new JLabel("Join our smart healthcare platform as a Patient or Doctor");
        subLabel.setFont(ThemeFonts.BODY);
        subLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        subLabel.setAlignmentX(0.0f);

        // Role Selector
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        rolePanel.setOpaque(false);
        rolePanel.setAlignmentX(0.0f);

        JRadioButton patientRadio = new JRadioButton("I am a Patient", true);
        patientRadio.setFont(ThemeFonts.BODY_BOLD);
        patientRadio.setOpaque(false);
        patientRadio.setForeground(ThemeColors.TEXT_PRIMARY);

        JRadioButton doctorRadio = new JRadioButton("I am a Doctor", false);
        doctorRadio.setFont(ThemeFonts.BODY_BOLD);
        doctorRadio.setOpaque(false);
        doctorRadio.setForeground(ThemeColors.TEXT_PRIMARY);

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(patientRadio);
        roleGroup.add(doctorRadio);

        patientRadio.addActionListener(e -> {
            selectedRole = Role.PATIENT;
            roleCardLayout.show(roleSpecificPanel, "PATIENT");
        });
        doctorRadio.addActionListener(e -> {
            selectedRole = Role.DOCTOR;
            roleCardLayout.show(roleSpecificPanel, "DOCTOR");
        });

        rolePanel.add(patientRadio);
        rolePanel.add(doctorRadio);

        // Common Fields
        fullNameField = new ModernTextField("e.g. Sarvesh Kumar");
        fullNameField.setMaximumSize(new Dimension(500, 38));
        fullNameField.setAlignmentX(0.0f);

        emailField = new ModernTextField("e.g. sarvesh@example.com");
        emailField.setMaximumSize(new Dimension(500, 38));
        emailField.setAlignmentX(0.0f);

        phoneField = new ModernTextField("e.g. +1 (555) 234-5678");
        phoneField.setMaximumSize(new Dimension(500, 38));
        phoneField.setAlignmentX(0.0f);

        passwordField = new ModernPasswordField("Create a secure password (min 6 characters)");
        passwordField.setMaximumSize(new Dimension(500, 38));
        passwordField.setAlignmentX(0.0f);

        // Build Patient Panel
        JPanel patientForm = new JPanel();
        patientForm.setLayout(new BoxLayout(patientForm, BoxLayout.Y_AXIS));
        patientForm.setOpaque(false);

        dobField = new ModernTextField("YYYY-MM-DD (e.g. 2002-05-15)");
        dobField.setMaximumSize(new Dimension(500, 38));
        dobField.setAlignmentX(0.0f);

        genderCombo = new ModernComboBox<>(new String[]{"Male", "Female", "Other", "Prefer not to say"});
        genderCombo.setMaximumSize(new Dimension(500, 38));
        genderCombo.setAlignmentX(0.0f);

        bloodGroupCombo = new ModernComboBox<>(new String[]{"Unknown", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"});
        bloodGroupCombo.setMaximumSize(new Dimension(500, 38));
        bloodGroupCombo.setAlignmentX(0.0f);

        medicalHistoryField = new ModernTextField("Optional: e.g. Seasonal allergies, asthma, none");
        medicalHistoryField.setMaximumSize(new Dimension(500, 38));
        medicalHistoryField.setAlignmentX(0.0f);

        emergencyContactField = new ModernTextField("Optional: e.g. +1 (555) 999-0000 (Father)");
        emergencyContactField.setMaximumSize(new Dimension(500, 38));
        emergencyContactField.setAlignmentX(0.0f);

        patientForm.add(createFieldLabel("Date of Birth (YYYY-MM-DD)"));
        patientForm.add(dobField);
        patientForm.add(Box.createVerticalStrut(8));
        patientForm.add(createFieldLabel("Gender"));
        patientForm.add(genderCombo);
        patientForm.add(Box.createVerticalStrut(8));
        patientForm.add(createFieldLabel("Blood Group"));
        patientForm.add(bloodGroupCombo);
        patientForm.add(Box.createVerticalStrut(8));
        patientForm.add(createFieldLabel("Medical History / Known Allergies"));
        patientForm.add(medicalHistoryField);
        patientForm.add(Box.createVerticalStrut(8));
        patientForm.add(createFieldLabel("Emergency Contact"));
        patientForm.add(emergencyContactField);

        // Build Doctor Panel
        JPanel doctorForm = new JPanel();
        doctorForm.setLayout(new BoxLayout(doctorForm, BoxLayout.Y_AXIS));
        doctorForm.setOpaque(false);

        specializationCombo = new ModernComboBox<>(new String[]{
                "General Physician", "Neurologist", "Pulmonologist",
                "Gastroenterologist", "Cardiologist", "ENT Specialist", "Dermatologist"
        });
        specializationCombo.setMaximumSize(new Dimension(500, 38));
        specializationCombo.setAlignmentX(0.0f);

        hospitalField = new ModernTextField("e.g. City Care Memorial Hospital");
        hospitalField.setMaximumSize(new Dimension(500, 38));
        hospitalField.setAlignmentX(0.0f);

        experienceField = new ModernTextField("e.g. 10 (years)");
        experienceField.setMaximumSize(new Dimension(500, 38));
        experienceField.setAlignmentX(0.0f);

        feeField = new ModernTextField("e.g. 75.00");
        feeField.setMaximumSize(new Dimension(500, 38));
        feeField.setAlignmentX(0.0f);

        availabilityField = new ModernTextField("e.g. 9:00 AM - 5:00 PM");
        availabilityField.setMaximumSize(new Dimension(500, 38));
        availabilityField.setAlignmentX(0.0f);

        bioField = new ModernTextField("Brief clinical summary / credentials");
        bioField.setMaximumSize(new Dimension(500, 38));
        bioField.setAlignmentX(0.0f);

        doctorForm.add(createFieldLabel("Medical Specialization"));
        doctorForm.add(specializationCombo);
        doctorForm.add(Box.createVerticalStrut(8));
        doctorForm.add(createFieldLabel("Hospital / Clinic Affiliation"));
        doctorForm.add(hospitalField);
        doctorForm.add(Box.createVerticalStrut(8));
        doctorForm.add(createFieldLabel("Years of Clinical Experience"));
        doctorForm.add(experienceField);
        doctorForm.add(Box.createVerticalStrut(8));
        doctorForm.add(createFieldLabel("Consultation Fee ($)"));
        doctorForm.add(feeField);
        doctorForm.add(Box.createVerticalStrut(8));
        doctorForm.add(createFieldLabel("Availability Hours"));
        doctorForm.add(availabilityField);
        doctorForm.add(Box.createVerticalStrut(8));
        doctorForm.add(createFieldLabel("Professional Bio"));
        doctorForm.add(bioField);

        roleSpecificPanel.setOpaque(false);
        roleSpecificPanel.setAlignmentX(0.0f);
        roleSpecificPanel.add(patientForm, "PATIENT");
        roleSpecificPanel.add(doctorForm, "DOCTOR");

        errorLabel = new JLabel(" ");
        errorLabel.setFont(ThemeFonts.BODY_SMALL);
        errorLabel.setForeground(ThemeColors.DANGER);
        errorLabel.setAlignmentX(0.0f);

        ModernButton submitBtn = new ModernButton("Create Account", ModernButton.ButtonStyle.PRIMARY);
        submitBtn.setMaximumSize(new Dimension(500, 42));
        submitBtn.setAlignmentX(0.0f);
        submitBtn.addActionListener(e -> handleRegister());

        JPanel bottomLink = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomLink.setOpaque(false);
        bottomLink.setAlignmentX(0.0f);
        JLabel existLbl = new JLabel("Already have an account?");
        existLbl.setFont(ThemeFonts.BODY);
        ModernButton loginLink = new ModernButton("Sign In", ModernButton.ButtonStyle.LINK);
        loginLink.addActionListener(e -> navManager.showLogin());
        bottomLink.add(existLbl);
        bottomLink.add(loginLink);

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(subLabel);
        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(rolePanel);
        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(createFieldLabel("Full Name"));
        formPanel.add(fullNameField);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createFieldLabel("Email Address"));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createFieldLabel("Phone Number"));
        formPanel.add(phoneField);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createFieldLabel("Password"));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(roleSpecificPanel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(submitBtn);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(bottomLink);

        regCard.add(formPanel, BorderLayout.CENTER);
        centerWrapper.add(regCard);

        JScrollPane scrollPane = new JScrollPane(centerWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(ThemeFonts.BODY_SMALL_BOLD);
        lbl.setForeground(ThemeColors.TEXT_PRIMARY);
        lbl.setAlignmentX(0.0f);
        return lbl;
    }

    private void handleRegister() {
        errorLabel.setText(" ");
        String name = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String pass = new String(passwordField.getPassword());

        AuthService.AuthResult res;
        if (selectedRole == Role.PATIENT) {
            Date dob = DateTimeUtil.parseDate(dobField.getText().trim());
            String gender = (String) genderCombo.getSelectedItem();
            String blood = (String) bloodGroupCombo.getSelectedItem();
            String history = medicalHistoryField.getText().trim();
            String emergency = emergencyContactField.getText().trim();

            res = authService.registerPatient(name, email, phone, pass, dob, gender, blood, history, emergency);
        } else {
            String spec = (String) specializationCombo.getSelectedItem();
            String hosp = hospitalField.getText().trim();
            int exp = 0;
            try {
                exp = Integer.parseInt(experienceField.getText().trim());
            } catch (Exception ignored) {}

            BigDecimal fee = BigDecimal.valueOf(50.00);
            try {
                fee = new BigDecimal(feeField.getText().trim());
            } catch (Exception ignored) {}

            String bio = bioField.getText().trim();
            String hours = availabilityField.getText().trim();

            res = authService.registerDoctor(name, email, phone, pass, spec, hosp, exp, fee, bio, hours);
        }

        if (res.isSuccess()) {
            ToastNotification.show(navManager.getMainFrame(), res.getMessage(), ToastNotification.ToastType.SUCCESS);
            navManager.showLogin();
        } else {
            errorLabel.setText(res.getMessage());
            ToastNotification.show(navManager.getMainFrame(), res.getMessage(), ToastNotification.ToastType.ERROR);
        }
    }
}

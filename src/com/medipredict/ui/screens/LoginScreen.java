package com.medipredict.ui.screens;

import com.medipredict.model.Role;
import com.medipredict.service.AuthService;
import com.medipredict.ui.NavigationManager;
import com.medipredict.ui.components.HeaderPanel;
import com.medipredict.ui.components.ModernButton;
import com.medipredict.ui.components.ModernCard;
import com.medipredict.ui.components.ModernPasswordField;
import com.medipredict.ui.components.ModernTextField;
import com.medipredict.ui.components.ToastNotification;
import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;
import com.medipredict.ui.theme.UIUtils;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;

public class LoginScreen extends JPanel {
    private final NavigationManager navManager;
    private final AuthService authService = new AuthService();

    private Role selectedRole = Role.PATIENT;
    private final ModernTextField emailField;
    private final ModernPasswordField passwordField;
    private final JLabel errorLabel;

    public LoginScreen(NavigationManager navManager) {
        this.navManager = navManager;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        // Header
        add(new HeaderPanel(navManager), BorderLayout.NORTH);

        // Center Container
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(UIUtils.createPadding(30, 20, 30, 20));

        ModernCard loginCard = new ModernCard(new BorderLayout(), 28);
        loginCard.setPreferredSize(new Dimension(480, 560));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("Sign In to MediPredict");
        titleLabel.setFont(ThemeFonts.TITLE_LARGE);
        titleLabel.setForeground(ThemeColors.PRIMARY);
        titleLabel.setAlignmentX(0.0f);

        JLabel subLabel = new JLabel("Select your role and enter your credentials");
        subLabel.setFont(ThemeFonts.BODY);
        subLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        subLabel.setAlignmentX(0.0f);

        // Role Toggle Panel
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        rolePanel.setOpaque(false);
        rolePanel.setAlignmentX(0.0f);

        JRadioButton patientRadio = new JRadioButton("Patient Portal", true);
        patientRadio.setFont(ThemeFonts.BODY_BOLD);
        patientRadio.setOpaque(false);
        patientRadio.setForeground(ThemeColors.TEXT_PRIMARY);

        JRadioButton doctorRadio = new JRadioButton("Doctor Portal", false);
        doctorRadio.setFont(ThemeFonts.BODY_BOLD);
        doctorRadio.setOpaque(false);
        doctorRadio.setForeground(ThemeColors.TEXT_PRIMARY);

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(patientRadio);
        roleGroup.add(doctorRadio);

        patientRadio.addActionListener(e -> selectedRole = Role.PATIENT);
        doctorRadio.addActionListener(e -> selectedRole = Role.DOCTOR);

        rolePanel.add(patientRadio);
        rolePanel.add(doctorRadio);

        // Inputs
        JLabel emailLbl = new JLabel("Email Address");
        emailLbl.setFont(ThemeFonts.BODY_SMALL_BOLD);
        emailLbl.setForeground(ThemeColors.TEXT_PRIMARY);
        emailLbl.setAlignmentX(0.0f);

        emailField = new ModernTextField("e.g. patient@medipredict.com");
        emailField.setAlignmentX(0.0f);
        emailField.setMaximumSize(new Dimension(400, 40));

        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(ThemeFonts.BODY_SMALL_BOLD);
        passLbl.setForeground(ThemeColors.TEXT_PRIMARY);
        passLbl.setAlignmentX(0.0f);

        passwordField = new ModernPasswordField("Enter your password");
        passwordField.setAlignmentX(0.0f);
        passwordField.setMaximumSize(new Dimension(400, 40));

        // Error message label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(ThemeFonts.BODY_SMALL);
        errorLabel.setForeground(ThemeColors.DANGER);
        errorLabel.setAlignmentX(0.0f);

        // Submit button
        ModernButton submitBtn = new ModernButton("Sign In", ModernButton.ButtonStyle.PRIMARY);
        submitBtn.setAlignmentX(0.0f);
        submitBtn.setMaximumSize(new Dimension(400, 42));
        submitBtn.addActionListener(e -> handleLogin());

        // Quick Demo Buttons Panel
        JPanel demoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        demoPanel.setOpaque(false);
        demoPanel.setAlignmentX(0.0f);

        JLabel demoLabel = new JLabel("Quick Demo: ");
        demoLabel.setFont(ThemeFonts.BODY_SMALL);
        demoLabel.setForeground(ThemeColors.TEXT_MUTED);

        ModernButton demoPatientBtn = new ModernButton("Patient", ModernButton.ButtonStyle.SECONDARY);
        demoPatientBtn.setPreferredSize(new Dimension(75, 26));
        demoPatientBtn.setFont(ThemeFonts.BADGE);
        demoPatientBtn.addActionListener(e -> {
            patientRadio.setSelected(true);
            selectedRole = Role.PATIENT;
            emailField.setText("patient@medipredict.com");
            passwordField.setText("patient123");
        });

        ModernButton demoDoctorBtn = new ModernButton("Doctor", ModernButton.ButtonStyle.SECONDARY);
        demoDoctorBtn.setPreferredSize(new Dimension(75, 26));
        demoDoctorBtn.setFont(ThemeFonts.BADGE);
        demoDoctorBtn.addActionListener(e -> {
            doctorRadio.setSelected(true);
            selectedRole = Role.DOCTOR;
            emailField.setText("doctor@medipredict.com");
            passwordField.setText("doctor123");
        });

        demoPanel.add(demoLabel);
        demoPanel.add(demoPatientBtn);
        demoPanel.add(demoDoctorBtn);

        // Footer links
        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        linksPanel.setOpaque(false);
        linksPanel.setAlignmentX(0.0f);

        ModernButton forgotBtn = new ModernButton("Forgot Password?", ModernButton.ButtonStyle.LINK);
        forgotBtn.addActionListener(e -> navManager.showForgotPassword());

        ModernButton registerLink = new ModernButton("Create New Account", ModernButton.ButtonStyle.LINK);
        registerLink.addActionListener(e -> navManager.showRegister());

        linksPanel.add(forgotBtn);
        linksPanel.add(new JLabel("•"));
        linksPanel.add(registerLink);

        // Assemble
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(subLabel);
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(rolePanel);
        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(emailLbl);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(passLbl);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(submitBtn);
        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(demoPanel);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(linksPanel);

        loginCard.add(formPanel, BorderLayout.CENTER);
        centerWrapper.add(loginCard);

        JScrollPane scrollPane = new JScrollPane(centerWrapper);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void handleLogin() {
        errorLabel.setText(" ");
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword());

        AuthService.AuthResult res = authService.login(email, pass, selectedRole);
        if (res.isSuccess()) {
            ToastNotification.show(navManager.getMainFrame(), res.getMessage(), ToastNotification.ToastType.SUCCESS);
            if (selectedRole == Role.PATIENT) {
                navManager.showPatientDashboard();
            } else {
                navManager.showDoctorDashboard();
            }
        } else {
            errorLabel.setText(res.getMessage());
            ToastNotification.show(navManager.getMainFrame(), res.getMessage(), ToastNotification.ToastType.ERROR);
        }
    }
}

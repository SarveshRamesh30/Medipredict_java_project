package com.medipredict.ui.screens;

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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;

public class ForgotPasswordScreen extends JPanel {
    private final NavigationManager navManager;
    private final AuthService authService = new AuthService();

    private final ModernTextField emailField;
    private final ModernPasswordField newPasswordField;
    private final ModernPasswordField confirmPasswordField;
    private final JLabel statusLabel;

    public ForgotPasswordScreen(NavigationManager navManager) {
        this.navManager = navManager;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        add(new HeaderPanel(navManager), BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(UIUtils.createPadding(30, 20, 30, 20));

        ModernCard card = new ModernCard(new BorderLayout(), 26);
        card.setPreferredSize(new Dimension(460, 480));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Reset Your Password");
        titleLabel.setFont(ThemeFonts.TITLE_LARGE);
        titleLabel.setForeground(ThemeColors.PRIMARY);
        titleLabel.setAlignmentX(0.0f);

        JLabel subLabel = new JLabel("Enter your registered email and new secure password");
        subLabel.setFont(ThemeFonts.BODY);
        subLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        subLabel.setAlignmentX(0.0f);

        emailField = new ModernTextField("Enter your registered email address");
        emailField.setAlignmentX(0.0f);
        emailField.setMaximumSize(new Dimension(400, 38));

        newPasswordField = new ModernPasswordField("Enter new password (min 6 chars)");
        newPasswordField.setAlignmentX(0.0f);
        newPasswordField.setMaximumSize(new Dimension(400, 38));

        confirmPasswordField = new ModernPasswordField("Confirm new password");
        confirmPasswordField.setAlignmentX(0.0f);
        confirmPasswordField.setMaximumSize(new Dimension(400, 38));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(ThemeFonts.BODY_SMALL);
        statusLabel.setForeground(ThemeColors.DANGER);
        statusLabel.setAlignmentX(0.0f);

        ModernButton resetBtn = new ModernButton("Update Password", ModernButton.ButtonStyle.PRIMARY);
        resetBtn.setAlignmentX(0.0f);
        resetBtn.setMaximumSize(new Dimension(400, 42));
        resetBtn.addActionListener(e -> handleReset());

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backPanel.setOpaque(false);
        backPanel.setAlignmentX(0.0f);
        ModernButton backBtn = new ModernButton("← Back to Sign In", ModernButton.ButtonStyle.LINK);
        backBtn.addActionListener(e -> navManager.showLogin());
        backPanel.add(backBtn);

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(subLabel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(createFieldLabel("Registered Email"));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(createFieldLabel("New Password"));
        formPanel.add(newPasswordField);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(createFieldLabel("Confirm Password"));
        formPanel.add(confirmPasswordField);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(statusLabel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(resetBtn);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(backPanel);

        card.add(formPanel, BorderLayout.CENTER);
        centerWrapper.add(card);

        JScrollPane scrollPane = new JScrollPane(centerWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(ThemeFonts.BODY_SMALL_BOLD);
        lbl.setForeground(ThemeColors.TEXT_PRIMARY);
        lbl.setAlignmentX(0.0f);
        return lbl;
    }

    private void handleReset() {
        statusLabel.setText(" ");
        String email = emailField.getText().trim();
        String pass1 = new String(newPasswordField.getPassword());
        String pass2 = new String(confirmPasswordField.getPassword());

        if (!pass1.equals(pass2)) {
            statusLabel.setText("Passwords do not match. Please re-enter.");
            return;
        }

        AuthService.AuthResult res = authService.resetPassword(email, pass1);
        if (res.isSuccess()) {
            ToastNotification.show(navManager.getMainFrame(), res.getMessage(), ToastNotification.ToastType.SUCCESS);
            navManager.showLogin();
        } else {
            statusLabel.setText(res.getMessage());
            ToastNotification.show(navManager.getMainFrame(), res.getMessage(), ToastNotification.ToastType.ERROR);
        }
    }
}

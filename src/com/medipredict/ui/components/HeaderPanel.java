package com.medipredict.ui.components;

import com.medipredict.model.User;
import com.medipredict.service.SessionManager;
import com.medipredict.ui.NavigationManager;
import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;
import com.medipredict.ui.theme.UIUtils;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class HeaderPanel extends JPanel {

    public HeaderPanel(NavigationManager navManager) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColors.BORDER));
        setPreferredSize(new Dimension(1000, 64));

        // Left Brand
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        brandPanel.setOpaque(false);

        // Logo icon badge
        JPanel logoBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntiAliasing(g2);
                g2.setColor(ThemeColors.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(ThemeFonts.TITLE_MEDIUM);
                g2.drawString("+", 12, 24);
                g2.dispose();
            }
        };
        logoBadge.setPreferredSize(new Dimension(36, 36));
        logoBadge.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel brandName = new JLabel("MediPredict");
        brandName.setFont(ThemeFonts.TITLE_MEDIUM);
        brandName.setForeground(ThemeColors.PRIMARY);

        JLabel tagline = new JLabel("Understand Your Symptoms. Take the Right Next Step.");
        tagline.setFont(ThemeFonts.BODY_SMALL);
        tagline.setForeground(ThemeColors.TEXT_MUTED);

        titleBlock.add(brandName);
        titleBlock.add(tagline);

        brandPanel.add(logoBadge);
        brandPanel.add(titleBlock);

        // Right User Context & Logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 14));
        userPanel.setOpaque(false);

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            JLabel userLabel = new JLabel(currentUser.getFullName());
            userLabel.setFont(ThemeFonts.BODY_BOLD);
            userLabel.setForeground(ThemeColors.TEXT_PRIMARY);

            String roleStr = currentUser.getRole().name();
            Color roleBg = "DOCTOR".equals(roleStr) ? ThemeColors.ACCENT_BLUE_LIGHT : ThemeColors.PRIMARY_LIGHT;
            Color roleFg = "DOCTOR".equals(roleStr) ? ThemeColors.ACCENT_BLUE : ThemeColors.PRIMARY;
            JPanel roleBadge = UIUtils.createBadge(roleStr, roleBg, roleFg);

            ModernButton logoutBtn = new ModernButton("Logout", ModernButton.ButtonStyle.SECONDARY);
            logoutBtn.setPreferredSize(new Dimension(84, 32));
            logoutBtn.addActionListener(e -> navManager.logout());

            userPanel.add(userLabel);
            userPanel.add(roleBadge);
            userPanel.add(Box.createHorizontalStrut(8));
            userPanel.add(logoutBtn);
        } else {
            ModernButton loginBtn = new ModernButton("Login", ModernButton.ButtonStyle.OUTLINE);
            loginBtn.setPreferredSize(new Dimension(80, 32));
            loginBtn.addActionListener(e -> navManager.showLogin());

            ModernButton registerBtn = new ModernButton("Register", ModernButton.ButtonStyle.PRIMARY);
            registerBtn.setPreferredSize(new Dimension(90, 32));
            registerBtn.addActionListener(e -> navManager.showRegister());

            userPanel.add(loginBtn);
            userPanel.add(registerBtn);
        }

        add(brandPanel, BorderLayout.WEST);
        add(userPanel, BorderLayout.EAST);
    }
}

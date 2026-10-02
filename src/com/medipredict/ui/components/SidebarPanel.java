package com.medipredict.ui.components;

import com.medipredict.model.Role;
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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SidebarPanel extends JPanel {
    private final NavigationManager navManager;
    private final String activeScreenKey;
    private final List<NavItem> navItems = new ArrayList<>();

    public SidebarPanel(NavigationManager navManager, String activeScreenKey) {
        this.navManager = navManager;
        this.activeScreenKey = activeScreenKey;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_SIDEBAR);
        setPreferredSize(new Dimension(230, 700));

        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false);
        menuContainer.setBorder(UIUtils.createPadding(18, 12, 18, 12));

        Role role = SessionManager.getInstance().isLoggedIn() ?
                SessionManager.getInstance().getCurrentUser().getRole() : Role.PATIENT;

        JLabel sectionTitle = new JLabel(role == Role.DOCTOR ? "DOCTOR PORTAL" : "PATIENT PORTAL");
        sectionTitle.setFont(ThemeFonts.BADGE);
        sectionTitle.setForeground(ThemeColors.TEXT_MUTED);
        sectionTitle.setBorder(UIUtils.createPadding(6, 12, 12, 12));
        menuContainer.add(sectionTitle);

        if (role == Role.PATIENT) {
            addNav(menuContainer, "📊  Dashboard", NavigationManager.SCREEN_PATIENT_DASHBOARD);
            addNav(menuContainer, "🩺  Symptom Predictor", NavigationManager.SCREEN_SYMPTOM_PREDICTION);
            addNav(menuContainer, "📜  Prediction History", NavigationManager.SCREEN_PREDICTION_HISTORY);
            addNav(menuContainer, "👨‍⚕️  Find Doctors", NavigationManager.SCREEN_DOCTOR_DIRECTORY);
            addNav(menuContainer, "📅  My Appointments", NavigationManager.SCREEN_PATIENT_APPOINTMENTS);
            addNav(menuContainer, "📋  Doctor Advice", NavigationManager.SCREEN_PATIENT_RECOMMENDATIONS);
            addNav(menuContainer, "💬  Messages", NavigationManager.SCREEN_MESSAGING);
            addNav(menuContainer, "👤  My Profile", NavigationManager.SCREEN_PATIENT_PROFILE);
        } else {
            addNav(menuContainer, "📊  Doctor Dashboard", NavigationManager.SCREEN_DOCTOR_DASHBOARD);
            addNav(menuContainer, "📅  Appointments", NavigationManager.SCREEN_DOCTOR_APPOINTMENTS);
            addNav(menuContainer, "👥  Patient Directory", NavigationManager.SCREEN_DOCTOR_PATIENTS);
            addNav(menuContainer, "📋  Recommendations", NavigationManager.SCREEN_DOCTOR_RECOMMENDATIONS);
            addNav(menuContainer, "💬  Messages", NavigationManager.SCREEN_MESSAGING);
            addNav(menuContainer, "👨‍⚕️  Doctor Profile", NavigationManager.SCREEN_DOCTOR_PROFILE);
        }

        menuContainer.add(Box.createVerticalGlue());

        // Educational info card at bottom of sidebar
        ModernCard tipCard = new ModernCard(new BorderLayout(), 10);
        tipCard.setBackground(ThemeColors.BG_SIDEBAR_ACTIVE);
        tipCard.setDrawBorder(false);
        JLabel tipHeader = new JLabel("ⓘ Medical Disclaimer");
        tipHeader.setFont(ThemeFonts.BODY_SMALL_BOLD);
        tipHeader.setForeground(ThemeColors.PRIMARY_LIGHT);
        JLabel tipBody = new JLabel("<html><body style='width: 140px; color: #94A3B8; font-size: 10px;'>Predictions are for informational purposes only. Consult a doctor for medical evaluation.</body></html>");
        tipCard.add(tipHeader, BorderLayout.NORTH);
        tipCard.add(tipBody, BorderLayout.CENTER);
        menuContainer.add(tipCard);

        add(menuContainer, BorderLayout.CENTER);
    }

    private void addNav(JPanel container, String title, String screenKey) {
        NavItem item = new NavItem(title, screenKey, screenKey.equals(activeScreenKey));
        navItems.add(item);
        container.add(item);
        container.add(Box.createVerticalStrut(4));
    }

    private class NavItem extends JPanel {
        private final String screenKey;
        private final boolean active;
        private boolean hovered = false;

        public NavItem(String title, String screenKey, boolean active) {
            this.screenKey = screenKey;
            this.active = active;

            setLayout(new BorderLayout());
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(206, 40));
            setMaximumSize(new Dimension(206, 40));
            setBorder(UIUtils.createPadding(9, 14, 9, 14));

            JLabel label = new JLabel(title);
            label.setFont(active ? ThemeFonts.BODY_BOLD : ThemeFonts.BODY);
            label.setForeground(active ? Color.WHITE : (hovered ? ThemeColors.TEXT_ON_DARK : new Color(203, 213, 225)));
            add(label, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    navManager.navigateTo(screenKey);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            UIUtils.enableAntiAliasing(g2);

            int w = getWidth();
            int h = getHeight();

            if (active) {
                g2.setColor(ThemeColors.PRIMARY);
                g2.fillRoundRect(0, 0, w, h, 10, 10);
            } else if (hovered) {
                g2.setColor(ThemeColors.BG_SIDEBAR_ACTIVE);
                g2.fillRoundRect(0, 0, w, h, 10, 10);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}

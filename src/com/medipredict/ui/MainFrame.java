package com.medipredict.ui;

import com.formdev.flatlaf.FlatLightLaf;
import com.medipredict.util.Logger;

import javax.swing.JFrame;
import javax.swing.UIManager;
import java.awt.Dimension;

public class MainFrame extends JFrame {
    private final NavigationManager navigationManager;

    public MainFrame() {
        initLookAndFeel();

        setTitle("MediPredict – Smart Medical Condition Prediction & Doctor Consultation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1020, 680));
        setPreferredSize(new Dimension(1280, 820));

        navigationManager = new NavigationManager(this);
        navigationManager.showLanding();

        pack();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Auto-maximize for full-screen immersive view
    }

    private void initLookAndFeel() {
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("CheckBox.arc", 6);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.thumbArc", 8);
            UIManager.put("ScrollBar.width", 10);
        } catch (Exception e) {
            Logger.warn("FlatLaf not loaded, using system look and feel: " + e.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }
}

package com.medipredict.ui.components;

import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;
import com.medipredict.ui.theme.UIUtils;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class ToastNotification {

    public enum ToastType {
        SUCCESS,
        ERROR,
        INFO,
        WARNING
    }

    public static void show(JFrame parent, String message, ToastType type) {
        if (parent == null) return;

        JDialog dialog = new JDialog(parent);
        dialog.setUndecorated(true);
        dialog.setAlwaysOnTop(true);

        Color bg;
        Color border;
        Color fg = Color.WHITE;

        switch (type) {
            case SUCCESS:
                bg = ThemeColors.SUCCESS;
                border = new Color(5, 150, 105);
                break;
            case ERROR:
                bg = ThemeColors.DANGER;
                border = new Color(220, 38, 38);
                break;
            case WARNING:
                bg = ThemeColors.WARNING;
                border = new Color(217, 119, 6);
                break;
            default:
                bg = ThemeColors.PRIMARY;
                border = ThemeColors.PRIMARY_HOVER;
        }

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntiAliasing(g2);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel label = new JLabel(message);
        label.setFont(ThemeFonts.BODY_BOLD);
        label.setForeground(fg);
        panel.add(label, BorderLayout.CENTER);

        dialog.getContentPane().add(panel);
        dialog.pack();

        int x = parent.getX() + (parent.getWidth() - dialog.getWidth()) / 2;
        int y = parent.getY() + parent.getHeight() - dialog.getHeight() - 40;
        dialog.setLocation(x, y);

        dialog.setVisible(true);

        Timer timer = new Timer(3000, e -> {
            dialog.setVisible(false);
            dialog.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }
}

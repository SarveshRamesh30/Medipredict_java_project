package com.medipredict.ui.components;

import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;
import com.medipredict.ui.theme.UIUtils;

import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ModernTextField extends JTextField {
    private String placeholder = "";
    private boolean isFocused = false;
    private int cornerRadius = 8;

    public ModernTextField() {
        this("");
    }

    public ModernTextField(String placeholder) {
        this.placeholder = placeholder;
        setFont(ThemeFonts.BODY);
        setForeground(ThemeColors.TEXT_PRIMARY);
        setOpaque(false);
        setBorder(UIUtils.createPadding(8, 12, 8, 12));
        setPreferredSize(new Dimension(220, 38));

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UIUtils.enableAntiAliasing(g2);

        int w = getWidth();
        int h = getHeight();

        // Background
        g2.setColor(ThemeColors.BG_CARD);
        g2.fillRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        // Border
        g2.setColor(isFocused ? ThemeColors.BORDER_FOCUS : ThemeColors.BORDER);
        g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        super.paintComponent(g);

        // Draw Placeholder if empty
        if (getText().isEmpty() && !placeholder.isEmpty() && !isFocused) {
            g2.setColor(ThemeColors.TEXT_MUTED);
            g2.setFont(ThemeFonts.BODY);
            int textY = (h - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
            g2.drawString(placeholder, 14, textY);
        }

        g2.dispose();
    }
}

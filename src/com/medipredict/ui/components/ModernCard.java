package com.medipredict.ui.components;

import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.UIUtils;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

public class ModernCard extends JPanel {
    private int cornerRadius = 14;
    private boolean drawBorder = true;

    public ModernCard() {
        this(new BorderLayout());
    }

    public ModernCard(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBorder(UIUtils.createPadding(16, 18, 16, 18));
    }

    public ModernCard(LayoutManager layout, int padding) {
        super(layout);
        setOpaque(false);
        setBorder(UIUtils.createPadding(padding, padding, padding, padding));
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public void setDrawBorder(boolean draw) {
        this.drawBorder = draw;
        repaint();
    }

    private java.awt.Color gradientStart = null;
    private java.awt.Color gradientEnd = null;

    public void setGradient(java.awt.Color start, java.awt.Color end) {
        this.gradientStart = start;
        this.gradientEnd = end;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UIUtils.enableAntiAliasing(g2);

        int w = getWidth();
        int h = getHeight();

        // Card background fill (Gradient or solid background)
        if (gradientStart != null && gradientEnd != null) {
            java.awt.GradientPaint gp = new java.awt.GradientPaint(0, 0, gradientStart, w, h, gradientEnd);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
        } else {
            java.awt.Color bg = getBackground();
            if (bg == null || bg.getAlpha() == 0) {
                bg = ThemeColors.BG_CARD;
            }
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
        }

        // Subtle Card border
        if (drawBorder) {
            g2.setColor(ThemeColors.BORDER);
            g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}

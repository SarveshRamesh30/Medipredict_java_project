package com.medipredict.ui.components;

import com.medipredict.model.Symptom;
import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;
import com.medipredict.ui.theme.UIUtils;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class SymptomTagChip extends JPanel {
    private final Symptom symptom;
    private boolean selected = false;
    private boolean hovered = false;
    private Consumer<Boolean> onToggleListener;

    public SymptomTagChip(Symptom symptom, boolean initialSelected) {
        this.symptom = symptom;
        this.selected = initialSelected;

        setLayout(new BorderLayout());
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(UIUtils.createPadding(6, 12, 6, 12));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggle();
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

    public void setOnToggleListener(Consumer<Boolean> listener) {
        this.onToggleListener = listener;
    }

    public void toggle() {
        this.selected = !this.selected;
        repaint();
        if (onToggleListener != null) {
            onToggleListener.accept(this.selected);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean sel) {
        this.selected = sel;
        repaint();
    }

    public Symptom getSymptom() {
        return symptom;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UIUtils.enableAntiAliasing(g2);

        int w = getWidth();
        int h = getHeight();

        Color bgColor;
        Color borderColor;
        Color textColor;

        if (selected) {
            bgColor = ThemeColors.PRIMARY;
            borderColor = ThemeColors.PRIMARY_HOVER;
            textColor = Color.WHITE;
        } else if (hovered) {
            bgColor = ThemeColors.BG_HOVER;
            borderColor = ThemeColors.PRIMARY;
            textColor = ThemeColors.PRIMARY;
        } else {
            bgColor = Color.WHITE;
            borderColor = ThemeColors.BORDER;
            textColor = ThemeColors.TEXT_PRIMARY;
        }

        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, w - 1, h - 1, 16, 16);

        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

        // Draw checkmark or dot if selected
        g2.setFont(ThemeFonts.BODY_BOLD);
        g2.setColor(textColor);

        String text = (selected ? "✓  " : "+  ") + symptom.getName();
        int textY = (h - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
        g2.drawString(text, 10, textY);

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Graphics g = getGraphics();
        int textW = 80;
        if (g != null) {
            g.setFont(ThemeFonts.BODY_BOLD);
            textW = g.getFontMetrics().stringWidth("✓  " + symptom.getName());
        } else {
            textW = symptom.getName().length() * 9 + 30;
        }
        return new Dimension(textW + 24, 32);
    }
}

package com.medipredict.ui.components;

import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;
import com.medipredict.ui.theme.UIUtils;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernButton extends JButton {

    public enum ButtonStyle {
        PRIMARY,
        SECONDARY,
        SUCCESS,
        DANGER,
        OUTLINE,
        LINK
    }

    private ButtonStyle style;
    private boolean isHovered = false;
    private int cornerRadius = 10;

    public ModernButton(String text) {
        this(text, ButtonStyle.PRIMARY);
    }

    public ModernButton(String text, ButtonStyle style) {
        super(text);
        this.style = style;
        setFont(ThemeFonts.BUTTON);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(UIUtils.createPadding(9, 18, 9, 18));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    public void setButtonStyle(ButtonStyle style) {
        this.style = style;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UIUtils.enableAntiAliasing(g2);

        int w = getWidth();
        int h = getHeight();

        Color bgColor;
        Color fgColor;
        Color borderColor = null;

        switch (style) {
            case PRIMARY:
                bgColor = isHovered ? ThemeColors.PRIMARY_HOVER : ThemeColors.PRIMARY;
                fgColor = ThemeColors.TEXT_ON_PRIMARY;
                break;
            case SECONDARY:
                bgColor = isHovered ? new Color(226, 232, 240) : new Color(241, 245, 249);
                fgColor = ThemeColors.TEXT_PRIMARY;
                break;
            case SUCCESS:
                bgColor = isHovered ? new Color(5, 150, 105) : ThemeColors.SUCCESS;
                fgColor = Color.WHITE;
                break;
            case DANGER:
                bgColor = isHovered ? new Color(220, 38, 38) : ThemeColors.DANGER;
                fgColor = Color.WHITE;
                break;
            case OUTLINE:
                bgColor = isHovered ? ThemeColors.BG_HOVER : Color.WHITE;
                fgColor = ThemeColors.PRIMARY;
                borderColor = ThemeColors.PRIMARY;
                break;
            case LINK:
                bgColor = isHovered ? ThemeColors.BG_HOVER : new Color(0, 0, 0, 0);
                fgColor = ThemeColors.PRIMARY;
                break;
            default:
                bgColor = ThemeColors.PRIMARY;
                fgColor = Color.WHITE;
        }

        if (style != ButtonStyle.LINK || isHovered) {
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
        }

        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
        }

        setForeground(fgColor);
        g2.dispose();
        super.paintComponent(g);
    }
}

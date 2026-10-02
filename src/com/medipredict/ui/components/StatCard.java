package com.medipredict.ui.components;

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

public class StatCard extends ModernCard {
    private final JLabel valueLabel;
    private final JLabel titleLabel;
    private final JLabel subtitleLabel;

    public StatCard(String title, String value, String subtitle, Color accentColor) {
        super(new BorderLayout(), 16);
        setPreferredSize(new Dimension(240, 130));
        setMinimumSize(new Dimension(180, 120));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeFonts.BADGE);
        titleLabel.setForeground(ThemeColors.TEXT_SECONDARY);

        valueLabel = new JLabel(value);
        valueLabel.setFont(ThemeFonts.HERO);
        valueLabel.setForeground(accentColor != null ? accentColor : ThemeColors.PRIMARY);

        subtitleLabel = new JLabel(subtitle != null ? subtitle : "");
        subtitleLabel.setFont(ThemeFonts.BODY_SMALL);
        subtitleLabel.setForeground(ThemeColors.TEXT_MUTED);

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(6));
        content.add(valueLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(subtitleLabel);

        add(content, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }
}

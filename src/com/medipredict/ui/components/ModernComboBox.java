package com.medipredict.ui.components;

import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;

import javax.swing.JComboBox;
import java.awt.Dimension;

public class ModernComboBox<E> extends JComboBox<E> {

    public ModernComboBox(E[] items) {
        super(items);
        init();
    }

    public ModernComboBox() {
        super();
        init();
    }

    private void init() {
        setFont(ThemeFonts.BODY);
        setBackground(ThemeColors.BG_CARD);
        setForeground(ThemeColors.TEXT_PRIMARY);
        setPreferredSize(new Dimension(220, 38));
    }
}

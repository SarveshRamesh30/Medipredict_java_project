package com.medipredict.ui.components;

import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

public class ModernTable extends JTable {

    public ModernTable(TableModel model) {
        super(model);
        initStyle();
    }

    public ModernTable() {
        super();
        initStyle();
    }

    private void initStyle() {
        setRowHeight(38);
        setFont(ThemeFonts.BODY);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setSelectionBackground(ThemeColors.PRIMARY_LIGHT);
        setSelectionForeground(ThemeColors.PRIMARY_HOVER);

        JTableHeader header = getTableHeader();
        header.setFont(ThemeFonts.BODY_SMALL_BOLD);
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(ThemeColors.TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        header.setReorderingAllowed(false);

        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(ThemeColors.TEXT_PRIMARY);
                }
                setBorder(noFocusBorder);
                return c;
            }
        });
    }
}

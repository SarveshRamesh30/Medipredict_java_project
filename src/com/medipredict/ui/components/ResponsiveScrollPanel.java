package com.medipredict.ui.components;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

/**
 * A responsive JPanel implementing Scrollable that automatically tracks
 * the full width of its parent JScrollPane viewport, preventing content
 * from being truncated to an arbitrary fixed width or leaving screens half-empty.
 */
public class ResponsiveScrollPanel extends JPanel implements Scrollable {

    public ResponsiveScrollPanel() {
        super();
    }

    public ResponsiveScrollPanel(LayoutManager layout) {
        super(layout);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 24;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 72;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        // Forces this container's width to match the viewport width at all times
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        // False allows natural vertical scrolling without squishing components
        return false;
    }
}

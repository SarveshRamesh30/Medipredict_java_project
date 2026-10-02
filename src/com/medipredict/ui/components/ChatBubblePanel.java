package com.medipredict.ui.components;

import com.medipredict.model.Message;
import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;
import com.medipredict.ui.theme.UIUtils;
import com.medipredict.util.DateTimeUtil;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class ChatBubblePanel extends JPanel {

    public ChatBubblePanel(Message message, boolean isCurrentUser) {
        setLayout(new FlowLayout(isCurrentUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 10, 4));
        setOpaque(false);

        JPanel bubbleContainer = new JPanel();
        bubbleContainer.setLayout(new BoxLayout(bubbleContainer, BoxLayout.Y_AXIS));
        bubbleContainer.setOpaque(false);

        // Header label: Sender Name + Role
        String senderInfo = (isCurrentUser ? "You" : (message.getSenderName() != null ? message.getSenderName() : "User"));
        JLabel headerLabel = new JLabel(senderInfo);
        headerLabel.setFont(ThemeFonts.BADGE);
        headerLabel.setForeground(isCurrentUser ? ThemeColors.PRIMARY : ThemeColors.TEXT_SECONDARY);
        headerLabel.setAlignmentX(isCurrentUser ? 1.0f : 0.0f);

        // Bubble body
        JPanel bubble = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntiAliasing(g2);
                int w = getWidth();
                int h = getHeight();

                g2.setColor(isCurrentUser ? ThemeColors.PRIMARY : Color.WHITE);
                g2.fillRoundRect(0, 0, w - 1, h - 1, 14, 14);

                g2.setColor(isCurrentUser ? ThemeColors.PRIMARY_HOVER : ThemeColors.BORDER);
                g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        bubble.setLayout(new BorderLayout());
        bubble.setOpaque(false);
        bubble.setBorder(UIUtils.createPadding(8, 12, 8, 12));

        JTextArea msgArea = new JTextArea(message.getMessageText());
        msgArea.setFont(ThemeFonts.BODY);
        msgArea.setForeground(isCurrentUser ? Color.WHITE : ThemeColors.TEXT_PRIMARY);
        msgArea.setOpaque(false);
        msgArea.setEditable(false);
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        msgArea.setColumns(28);

        bubble.add(msgArea, BorderLayout.CENTER);

        // Timestamp
        String timeStr = message.getSentAt() != null ? DateTimeUtil.formatTime(message.getSentAt()) : "";
        JLabel timeLabel = new JLabel(timeStr);
        timeLabel.setFont(ThemeFonts.BODY_SMALL);
        timeLabel.setForeground(ThemeColors.TEXT_MUTED);
        timeLabel.setAlignmentX(isCurrentUser ? 1.0f : 0.0f);

        bubbleContainer.add(headerLabel);
        bubbleContainer.add(bubble);
        bubbleContainer.add(timeLabel);

        add(bubbleContainer);
    }
}

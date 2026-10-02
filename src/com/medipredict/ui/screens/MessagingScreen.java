package com.medipredict.ui.screens;

import com.medipredict.model.Doctor;
import com.medipredict.model.Message;
import com.medipredict.model.Patient;
import com.medipredict.model.Role;
import com.medipredict.model.User;
import com.medipredict.service.DoctorService;
import com.medipredict.service.MessageService;
import com.medipredict.service.PatientService;
import com.medipredict.service.SessionManager;
import com.medipredict.ui.NavigationManager;
import com.medipredict.ui.components.ChatBubblePanel;
import com.medipredict.ui.components.HeaderPanel;
import com.medipredict.ui.components.ModernButton;
import com.medipredict.ui.components.ModernCard;
import com.medipredict.ui.components.ModernTextField;
import com.medipredict.ui.components.SidebarPanel;
import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;
import com.medipredict.ui.theme.UIUtils;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class MessagingScreen extends JPanel {
    private final NavigationManager navManager;
    private final MessageService messageService = new MessageService();
    private final DoctorService doctorService = new DoctorService();
    private final PatientService patientService = new PatientService();

    private final JList<ContactItem> contactList;
    private final DefaultListModel<ContactItem> contactListModel;

    private final JPanel chatHeaderPanel;
    private final JLabel chatPartnerNameLabel;
    private final JLabel chatPartnerSubLabel;

    private final JPanel messagesContainer;
    private final JScrollPane messagesScrollPane;
    private final ModernTextField messageInputField;

    private ContactItem currentContact = null;

    private static class ContactItem {
        final int userId;
        final String name;
        final String subtitle;

        ContactItem(int userId, String name, String subtitle) {
            this.userId = userId;
            this.name = name;
            this.subtitle = subtitle;
        }

        @Override
        public String toString() {
            return name + " (" + subtitle + ")";
        }
    }

    public MessagingScreen(NavigationManager navManager, Integer targetUserId) {
        this.navManager = navManager;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        add(new HeaderPanel(navManager), BorderLayout.NORTH);
        add(new SidebarPanel(navManager, NavigationManager.SCREEN_MESSAGING), BorderLayout.WEST);

        JPanel mainLayout = new JPanel(new BorderLayout(16, 0));
        mainLayout.setOpaque(false);
        mainLayout.setBorder(UIUtils.createPadding(20, 24, 20, 24));

        // 1. LEFT CONTACTS LIST CARD
        ModernCard contactsCard = new ModernCard(new BorderLayout(), 14);
        contactsCard.setPreferredSize(new Dimension(280, 600));

        JLabel contactsTitle = new JLabel("💬 Conversations");
        contactsTitle.setFont(ThemeFonts.TITLE_SMALL);
        contactsTitle.setForeground(ThemeColors.PRIMARY);
        contactsTitle.setBorder(UIUtils.createPadding(0, 0, 10, 0));

        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);
        contactList.setFont(ThemeFonts.BODY);
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactList.setSelectionBackground(ThemeColors.PRIMARY_LIGHT);
        contactList.setSelectionForeground(ThemeColors.PRIMARY_HOVER);
        contactList.setFixedCellHeight(42);

        contactList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ContactItem selected = contactList.getSelectedValue();
                if (selected != null) {
                    selectContact(selected);
                }
            }
        });

        JScrollPane contactsScroll = new JScrollPane(contactList);
        contactsScroll.setBorder(null);

        contactsCard.add(contactsTitle, BorderLayout.NORTH);
        contactsCard.add(contactsScroll, BorderLayout.CENTER);

        // 2. RIGHT CHAT CONVERSATION CARD
        ModernCard chatCard = new ModernCard(new BorderLayout(), 0);

        // Chat Header
        chatHeaderPanel = new JPanel(new BorderLayout());
        chatHeaderPanel.setBackground(new Color(248, 250, 252));
        chatHeaderPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColors.BORDER));
        chatHeaderPanel.setPreferredSize(new Dimension(600, 56));
        chatHeaderPanel.setBorder(UIUtils.createPadding(10, 16, 10, 16));

        JPanel chatInfo = new JPanel();
        chatInfo.setLayout(new BoxLayout(chatInfo, BoxLayout.Y_AXIS));
        chatInfo.setOpaque(false);

        chatPartnerNameLabel = new JLabel("Select a contact to view conversation");
        chatPartnerNameLabel.setFont(ThemeFonts.TITLE_SMALL);
        chatPartnerNameLabel.setForeground(ThemeColors.TEXT_PRIMARY);

        chatPartnerSubLabel = new JLabel("Encrypted medical consultation channel");
        chatPartnerSubLabel.setFont(ThemeFonts.BODY_SMALL);
        chatPartnerSubLabel.setForeground(ThemeColors.TEXT_MUTED);

        chatInfo.add(chatPartnerNameLabel);
        chatInfo.add(chatPartnerSubLabel);
        chatHeaderPanel.add(chatInfo, BorderLayout.CENTER);

        // Chat Messages Area
        messagesContainer = new JPanel();
        messagesContainer.setLayout(new BoxLayout(messagesContainer, BoxLayout.Y_AXIS));
        messagesContainer.setOpaque(false);
        messagesContainer.setBorder(UIUtils.createPadding(12, 12, 12, 12));

        messagesScrollPane = new JScrollPane(messagesContainer);
        messagesScrollPane.setBorder(null);
        messagesScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Chat Input Bar
        JPanel inputBar = new JPanel(new BorderLayout(10, 0));
        inputBar.setOpaque(false);
        inputBar.setBorder(UIUtils.createPadding(12, 16, 14, 16));

        messageInputField = new ModernTextField("Type your medical query or response...");
        messageInputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleSendMessage();
                }
            }
        });

        ModernButton sendBtn = new ModernButton("Send ➢", ModernButton.ButtonStyle.PRIMARY);
        sendBtn.setPreferredSize(new Dimension(90, 38));
        sendBtn.addActionListener(e -> handleSendMessage());

        inputBar.add(messageInputField, BorderLayout.CENTER);
        inputBar.add(sendBtn, BorderLayout.EAST);

        chatCard.add(chatHeaderPanel, BorderLayout.NORTH);
        chatCard.add(messagesScrollPane, BorderLayout.CENTER);
        chatCard.add(inputBar, BorderLayout.SOUTH);

        mainLayout.add(contactsCard, BorderLayout.WEST);
        mainLayout.add(chatCard, BorderLayout.CENTER);

        add(mainLayout, BorderLayout.CENTER);

        loadContacts(targetUserId);
    }

    private void loadContacts(Integer targetUserId) {
        contactListModel.clear();
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) return;

        ContactItem targetItem = null;

        if (currentUser.getRole() == Role.PATIENT) {
            // Patient chats with Doctors
            List<Doctor> doctors = doctorService.getAllDoctors();
            for (Doctor d : doctors) {
                ContactItem item = new ContactItem(d.getUserId(), d.getFullName(), d.getSpecialization());
                contactListModel.addElement(item);
                if (targetUserId != null && d.getUserId() == targetUserId) {
                    targetItem = item;
                }
            }
        } else {
            // Doctor chats with Patients
            List<Patient> patients = doctorService.getAllPatients();
            for (Patient p : patients) {
                ContactItem item = new ContactItem(p.getUserId(), p.getFullName(), "Patient #" + p.getPatientId());
                contactListModel.addElement(item);
                if (targetUserId != null && p.getUserId() == targetUserId) {
                    targetItem = item;
                }
            }
        }

        if (targetItem != null) {
            contactList.setSelectedValue(targetItem, true);
        } else if (!contactListModel.isEmpty()) {
            contactList.setSelectedIndex(0);
        }
    }

    private void selectContact(ContactItem item) {
        this.currentContact = item;
        chatPartnerNameLabel.setText(item.name);
        chatPartnerSubLabel.setText(item.subtitle);

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            messageService.markAsRead(item.userId, currentUser.getUserId());
        }

        refreshMessages();
    }

    private void refreshMessages() {
        messagesContainer.removeAll();
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || currentContact == null) {
            messagesContainer.revalidate();
            messagesContainer.repaint();
            return;
        }

        List<Message> conversation = messageService.getConversation(currentUser.getUserId(), currentContact.userId);
        if (conversation.isEmpty()) {
            JLabel empty = new JLabel("No messages yet. Send a message to begin consultation.");
            empty.setFont(ThemeFonts.BODY);
            empty.setForeground(ThemeColors.TEXT_MUTED);
            empty.setAlignmentX(0.5f);
            messagesContainer.add(Box.createVerticalGlue());
            messagesContainer.add(empty);
            messagesContainer.add(Box.createVerticalGlue());
        } else {
            for (Message m : conversation) {
                boolean isMe = (m.getSenderId() == currentUser.getUserId());
                ChatBubblePanel bubble = new ChatBubblePanel(m, isMe);
                bubble.setAlignmentX(isMe ? 1.0f : 0.0f);
                messagesContainer.add(bubble);
                messagesContainer.add(Box.createVerticalStrut(4));
            }
        }

        messagesContainer.revalidate();
        messagesContainer.repaint();

        // Scroll to bottom
        javax.swing.SwingUtilities.invokeLater(() -> {
            var vBar = messagesScrollPane.getVerticalScrollBar();
            vBar.setValue(vBar.getMaximum());
        });
    }

    private void handleSendMessage() {
        String text = messageInputField.getText().trim();
        if (text.isEmpty() || currentContact == null) return;

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) return;

        boolean sent = messageService.sendMessage(currentUser.getUserId(), currentContact.userId, text);
        if (sent) {
            messageInputField.setText("");
            refreshMessages();
        }
    }
}

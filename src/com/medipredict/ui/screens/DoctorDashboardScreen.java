package com.medipredict.ui.screens;

import com.medipredict.model.Appointment;
import com.medipredict.model.Doctor;
import com.medipredict.model.Prediction;
import com.medipredict.service.AppointmentService;
import com.medipredict.service.DoctorService;
import com.medipredict.service.MessageService;
import com.medipredict.service.SessionManager;
import com.medipredict.ui.NavigationManager;
import com.medipredict.ui.components.HeaderPanel;
import com.medipredict.ui.components.ModernButton;
import com.medipredict.ui.components.ModernCard;
import com.medipredict.ui.components.ResponsiveScrollPanel;
import com.medipredict.ui.components.SidebarPanel;
import com.medipredict.ui.components.StatCard;
import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;
import com.medipredict.ui.theme.UIUtils;
import com.medipredict.util.DateTimeUtil;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

public class DoctorDashboardScreen extends JPanel {
    private final NavigationManager navManager;
    private final DoctorService doctorService = new DoctorService();
    private final AppointmentService appointmentService = new AppointmentService();
    private final MessageService messageService = new MessageService();

    public DoctorDashboardScreen(NavigationManager navManager) {
        this.navManager = navManager;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        // Header & Sidebar
        add(new HeaderPanel(navManager), BorderLayout.NORTH);
        add(new SidebarPanel(navManager, NavigationManager.SCREEN_DOCTOR_DASHBOARD), BorderLayout.WEST);

        Doctor doctor = SessionManager.getInstance().getCurrentDoctor();
        int doctorId = doctor != null ? doctor.getDoctorId() : 0;
        int userId = SessionManager.getInstance().getCurrentUser() != null ? SessionManager.getInstance().getCurrentUser().getUserId() : 0;

        DoctorService.DoctorDashboardSummary summary = doctorService.getDashboardSummary(doctorId);
        int unreadCount = messageService.getUnreadCount(userId);

        ResponsiveScrollPanel mainContent = new ResponsiveScrollPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(UIUtils.createPadding(24, 28, 28, 28));

        // 1. WELCOME HERO SECTION
        ModernCard welcomeCard = new ModernCard(new BorderLayout(), 20);
        welcomeCard.setAlignmentX(0.0f);

        JPanel welcomeText = new JPanel();
        welcomeText.setLayout(new BoxLayout(welcomeText, BoxLayout.Y_AXIS));
        welcomeText.setOpaque(false);

        String docName = doctor != null && doctor.getFullName() != null ? doctor.getFullName() : "Doctor";
        JLabel welcomeTitle = new JLabel("Welcome, " + docName);
        welcomeTitle.setFont(ThemeFonts.TITLE_LARGE);
        welcomeTitle.setForeground(ThemeColors.PRIMARY);

        String subInfo = (doctor != null ? doctor.getSpecialization() + " • " + doctor.getHospitalClinic() : "Clinical Portal");
        JLabel welcomeSub = new JLabel(subInfo + " | Manage your patient appointments, clinical recommendations, and patient histories.");
        welcomeSub.setFont(ThemeFonts.BODY);
        welcomeSub.setForeground(ThemeColors.TEXT_SECONDARY);

        welcomeText.add(welcomeTitle);
        welcomeText.add(Box.createVerticalStrut(4));
        welcomeText.add(welcomeSub);

        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        quickActions.setOpaque(false);

        ModernButton apptsBtn = new ModernButton("📅 Manage Appointments", ModernButton.ButtonStyle.PRIMARY);
        apptsBtn.setPreferredSize(new Dimension(190, 40));
        apptsBtn.addActionListener(e -> navManager.showDoctorAppointments());

        ModernButton recBtn = new ModernButton("📋 Issue Recommendation", ModernButton.ButtonStyle.OUTLINE);
        recBtn.setPreferredSize(new Dimension(190, 40));
        recBtn.addActionListener(e -> navManager.showDoctorRecommendations());

        quickActions.add(apptsBtn);
        quickActions.add(recBtn);

        welcomeCard.add(welcomeText, BorderLayout.CENTER);
        welcomeCard.add(quickActions, BorderLayout.EAST);

        mainContent.add(welcomeCard);
        mainContent.add(Box.createVerticalStrut(20));

        // 2. 4 STAT CARDS ROW
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(0.0f);

        statsRow.add(new StatCard("TODAY'S APPOINTMENTS", String.valueOf(summary.todayAppointmentsCount), "Scheduled sessions", ThemeColors.PRIMARY));
        statsRow.add(new StatCard("PENDING REQUESTS", String.valueOf(summary.pendingAppointmentsCount), "Awaiting confirmation", ThemeColors.WARNING));
        statsRow.add(new StatCard("CONSULTING PATIENTS", String.valueOf(summary.totalConsultingPatients), "Registered health records", ThemeColors.ACCENT_BLUE));
        statsRow.add(new StatCard("UNREAD MESSAGES", String.valueOf(unreadCount), "Patient inquiries", ThemeColors.INFO));

        mainContent.add(statsRow);
        mainContent.add(Box.createVerticalStrut(22));

        // 3. TWO COLUMNS SECTION (Today's Schedule & Recent Predictions)
        JPanel columnsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        columnsPanel.setOpaque(false);
        columnsPanel.setAlignmentX(0.0f);

        // Column 1: Today's Appointments List
        columnsPanel.add(createAppointmentsOverviewCard(doctorId));

        // Column 2: Recent Patient Predictions Stream
        columnsPanel.add(createRecentPredictionsCard(summary.recentPatientPredictions));

        mainContent.add(columnsPanel);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private ModernCard createAppointmentsOverviewCard(int doctorId) {
        ModernCard card = new ModernCard(new BorderLayout(), 18);
        card.setPreferredSize(new Dimension(540, 320));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Appointment Requests & Schedule");
        title.setFont(ThemeFonts.TITLE_SMALL);
        title.setForeground(ThemeColors.PRIMARY);
        header.add(title, BorderLayout.WEST);

        ModernButton viewAllBtn = new ModernButton("View All →", ModernButton.ButtonStyle.LINK);
        viewAllBtn.addActionListener(e -> navManager.showDoctorAppointments());
        header.add(viewAllBtn, BorderLayout.EAST);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(UIUtils.createPadding(10, 0, 0, 0));

        List<Appointment> appts = appointmentService.getDoctorAppointments(doctorId);
        if (appts.isEmpty()) {
            JLabel empty = new JLabel("No pending or scheduled appointments at the moment.");
            empty.setFont(ThemeFonts.BODY);
            empty.setForeground(ThemeColors.TEXT_MUTED);
            listPanel.add(empty);
        } else {
            for (int i = 0; i < Math.min(3, appts.size()); i++) {
                Appointment a = appts.get(i);
                JPanel item = new JPanel(new BorderLayout());
                item.setOpaque(false);
                item.setBorder(UIUtils.createPadding(6, 0, 6, 0));

                JPanel left = new JPanel();
                left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
                left.setOpaque(false);

                JLabel pName = new JLabel("👤 " + (a.getPatientName() != null ? a.getPatientName() : "Patient #" + a.getPatientId()));
                pName.setFont(ThemeFonts.BODY_BOLD);
                JLabel details = new JLabel("📅 " + DateTimeUtil.formatDate(a.getAppointmentDate()) + " at " + a.getAppointmentTime() + " • " + a.getReason());
                details.setFont(ThemeFonts.BODY_SMALL);
                details.setForeground(ThemeColors.TEXT_SECONDARY);

                left.add(pName);
                left.add(details);

                String st = a.getStatus();
                Color bg = "CONFIRMED".equalsIgnoreCase(st) ? ThemeColors.SUCCESS_LIGHT :
                        ("COMPLETED".equalsIgnoreCase(st) ? ThemeColors.INFO_LIGHT : ThemeColors.WARNING_LIGHT);
                Color fg = "CONFIRMED".equalsIgnoreCase(st) ? ThemeColors.SUCCESS :
                        ("COMPLETED".equalsIgnoreCase(st) ? ThemeColors.INFO : ThemeColors.WARNING);

                item.add(left, BorderLayout.CENTER);
                item.add(UIUtils.createBadge(st, bg, fg), BorderLayout.EAST);
                listPanel.add(item);
                if (i < Math.min(3, appts.size()) - 1) {
                    listPanel.add(Box.createVerticalStrut(6));
                }
            }
        }

        card.add(header, BorderLayout.NORTH);
        card.add(listPanel, BorderLayout.CENTER);
        return card;
    }

    private ModernCard createRecentPredictionsCard(List<Prediction> predictions) {
        ModernCard card = new ModernCard(new BorderLayout(), 18);
        card.setPreferredSize(new Dimension(540, 320));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Recent Patient Condition Reports");
        title.setFont(ThemeFonts.TITLE_SMALL);
        title.setForeground(ThemeColors.ACCENT_BLUE);
        header.add(title, BorderLayout.WEST);

        ModernButton viewPatientsBtn = new ModernButton("Patient Directory →", ModernButton.ButtonStyle.LINK);
        viewPatientsBtn.addActionListener(e -> navManager.showDoctorPatients());
        header.add(viewPatientsBtn, BorderLayout.EAST);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(UIUtils.createPadding(10, 0, 0, 0));

        if (predictions == null || predictions.isEmpty()) {
            JLabel empty = new JLabel("No recent symptom prediction reports submitted yet.");
            empty.setFont(ThemeFonts.BODY);
            empty.setForeground(ThemeColors.TEXT_MUTED);
            listPanel.add(empty);
        } else {
            for (int i = 0; i < Math.min(3, predictions.size()); i++) {
                Prediction p = predictions.get(i);
                JPanel item = new JPanel(new BorderLayout());
                item.setOpaque(false);
                item.setBorder(UIUtils.createPadding(6, 0, 6, 0));

                JPanel left = new JPanel();
                left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
                left.setOpaque(false);

                JLabel condName = new JLabel("🩺 " + p.getConditionName());
                condName.setFont(ThemeFonts.BODY_BOLD);

                String symStr = p.getSymptomNames().isEmpty() ? "Reported" : p.getSymptomNames();
                if (symStr.length() > 40) symStr = symStr.substring(0, 37) + "...";
                JLabel symLbl = new JLabel(symStr + " (" + DateTimeUtil.formatTimestamp(p.getCreatedAt()) + ")");
                symLbl.setFont(ThemeFonts.BODY_SMALL);
                symLbl.setForeground(ThemeColors.TEXT_SECONDARY);

                left.add(condName);
                left.add(symLbl);

                Color bg = "High".equalsIgnoreCase(p.getConfidenceLevel()) ? ThemeColors.SUCCESS_LIGHT : ThemeColors.WARNING_LIGHT;
                Color fg = "High".equalsIgnoreCase(p.getConfidenceLevel()) ? ThemeColors.SUCCESS : ThemeColors.WARNING;
                item.add(left, BorderLayout.CENTER);
                item.add(UIUtils.createBadge(p.getConfidenceLevel() + " Match (" + p.getMatchScore() + "%)", bg, fg), BorderLayout.EAST);

                listPanel.add(item);
                if (i < Math.min(3, predictions.size()) - 1) {
                    listPanel.add(Box.createVerticalStrut(6));
                }
            }
        }

        card.add(header, BorderLayout.NORTH);
        card.add(listPanel, BorderLayout.CENTER);
        return card;
    }
}

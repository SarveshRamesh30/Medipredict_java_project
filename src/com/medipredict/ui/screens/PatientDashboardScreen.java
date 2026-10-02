package com.medipredict.ui.screens;

import com.medipredict.model.Appointment;
import com.medipredict.model.Patient;
import com.medipredict.model.Prediction;
import com.medipredict.model.Recommendation;
import com.medipredict.service.MessageService;
import com.medipredict.service.PatientService;
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

public class PatientDashboardScreen extends JPanel {
    private final NavigationManager navManager;
    private final PatientService patientService = new PatientService();
    private final MessageService messageService = new MessageService();

    public PatientDashboardScreen(NavigationManager navManager) {
        this.navManager = navManager;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        // Header & Sidebar
        add(new HeaderPanel(navManager), BorderLayout.NORTH);
        add(new SidebarPanel(navManager, NavigationManager.SCREEN_PATIENT_DASHBOARD), BorderLayout.WEST);

        Patient patient = SessionManager.getInstance().getCurrentPatient();
        int patientId = patient != null ? patient.getPatientId() : 0;
        int userId = SessionManager.getInstance().getCurrentUser() != null ? SessionManager.getInstance().getCurrentUser().getUserId() : 0;

        PatientService.PatientDashboardSummary summary = patientService.getDashboardSummary(patientId);
        int unreadCount = messageService.getUnreadCount(userId);

        ResponsiveScrollPanel mainContent = new ResponsiveScrollPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(UIUtils.createPadding(24, 28, 28, 28));

        // 1. WELCOME HERO SECTION
        ModernCard welcomeCard = new ModernCard(new BorderLayout(), 22);
        welcomeCard.setAlignmentX(0.0f);

        JPanel welcomeText = new JPanel();
        welcomeText.setLayout(new BoxLayout(welcomeText, BoxLayout.Y_AXIS));
        welcomeText.setOpaque(false);

        String patientName = patient != null && patient.getFullName() != null ? patient.getFullName() : "Patient";
        JLabel welcomeTitle = new JLabel("Welcome back, " + patientName);
        welcomeTitle.setFont(ThemeFonts.TITLE_LARGE);
        welcomeTitle.setForeground(ThemeColors.PRIMARY);

        JLabel welcomeSub = new JLabel("Track your preliminary symptom insights and manage doctor consultations effortlessly.");
        welcomeSub.setFont(ThemeFonts.BODY);
        welcomeSub.setForeground(ThemeColors.TEXT_SECONDARY);

        welcomeText.add(welcomeTitle);
        welcomeText.add(Box.createVerticalStrut(4));
        welcomeText.add(welcomeSub);

        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        quickActions.setOpaque(false);

        ModernButton predictBtn = new ModernButton("🩺 Start New Prediction", ModernButton.ButtonStyle.PRIMARY);
        predictBtn.setPreferredSize(new Dimension(200, 40));
        predictBtn.setFont(ThemeFonts.BUTTON);
        predictBtn.addActionListener(e -> navManager.showSymptomPrediction());

        ModernButton consultBtn = new ModernButton("👨‍⚕️ Find Doctors", ModernButton.ButtonStyle.OUTLINE);
        consultBtn.setPreferredSize(new Dimension(140, 40));
        consultBtn.setFont(ThemeFonts.BUTTON);
        consultBtn.addActionListener(e -> navManager.showDoctorDirectory());

        quickActions.add(predictBtn);
        quickActions.add(consultBtn);

        welcomeCard.add(welcomeText, BorderLayout.CENTER);
        welcomeCard.add(quickActions, BorderLayout.EAST);

        mainContent.add(welcomeCard);
        mainContent.add(Box.createVerticalStrut(20));

        // 2. 4 STAT CARDS ROW
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(0.0f);

        statsRow.add(new StatCard("TOTAL PREDICTIONS", String.valueOf(summary.totalPredictions), "Symptom assessments", ThemeColors.PRIMARY));
        statsRow.add(new StatCard("UPCOMING APPOINTMENTS", summary.upcomingAppointment != null ? "1" : "0", summary.upcomingAppointment != null ? summary.upcomingAppointment.getAppointmentTime() : "No active bookings", ThemeColors.ACCENT_BLUE));
        statsRow.add(new StatCard("DOCTOR ADVICE", summary.latestRecommendation != null ? "Available" : "None", "Clinical recommendations", ThemeColors.SUCCESS));
        statsRow.add(new StatCard("UNREAD MESSAGES", String.valueOf(unreadCount), "Doctor communications", ThemeColors.INFO));

        mainContent.add(statsRow);
        mainContent.add(Box.createVerticalStrut(22));

        // 3. 3 SUMMARY CARDS IN ROW / GRID
        JPanel cardsGrid = new JPanel(new GridLayout(1, 3, 16, 0));
        cardsGrid.setOpaque(false);
        cardsGrid.setAlignmentX(0.0f);

        // Card A: Latest Prediction
        cardsGrid.add(createLatestPredictionCard(summary.latestPrediction));

        // Card B: Upcoming Appointment
        cardsGrid.add(createUpcomingAppointmentCard(summary.upcomingAppointment));

        // Card C: Doctor Recommendation
        cardsGrid.add(createRecommendationCard(summary.latestRecommendation));

        mainContent.add(cardsGrid);
        mainContent.add(Box.createVerticalStrut(22));

        // 4. MEDICAL DISCLAIMER CARD
        ModernCard disclaimerCard = new ModernCard(new BorderLayout(), 16);
        disclaimerCard.setBackground(ThemeColors.WARNING_LIGHT);
        disclaimerCard.setDrawBorder(true);
        disclaimerCard.setAlignmentX(0.0f);
        JLabel discLabel = new JLabel("<html><body style='color: #92400E; font-size: 11px;'>" +
                "<b>Medical Notice:</b> MediPredict is an educational tool and does not provide formal medical diagnoses. " +
                "Always consult a licensed medical physician for accurate assessment and treatment prescriptions.</body></html>");
        disclaimerCard.add(discLabel, BorderLayout.CENTER);
        mainContent.add(disclaimerCard);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private ModernCard createLatestPredictionCard(Prediction pred) {
        ModernCard card = new ModernCard(new BorderLayout(), 18);
        card.setPreferredSize(new Dimension(360, 270));
        card.setMinimumSize(new Dimension(260, 240));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Latest Prediction");
        title.setFont(ThemeFonts.TITLE_SMALL);
        title.setForeground(ThemeColors.PRIMARY);
        header.add(title, BorderLayout.WEST);

        if (pred != null) {
            String conf = pred.getConfidenceLevel() != null ? pred.getConfidenceLevel() : "Medium";
            Color bg = "High".equalsIgnoreCase(conf) ? ThemeColors.SUCCESS_LIGHT : ThemeColors.WARNING_LIGHT;
            Color fg = "High".equalsIgnoreCase(conf) ? ThemeColors.SUCCESS : ThemeColors.WARNING;
            header.add(UIUtils.createBadge(conf + " Match", bg, fg), BorderLayout.EAST);
        }

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(UIUtils.createPadding(10, 0, 10, 0));

        if (pred != null) {
            JLabel condLabel = new JLabel("Possible Condition: " + pred.getConditionName());
            condLabel.setFont(ThemeFonts.BODY_BOLD);
            condLabel.setForeground(ThemeColors.TEXT_PRIMARY);

            JLabel dateLabel = new JLabel("Date: " + DateTimeUtil.formatTimestamp(pred.getCreatedAt()));
            dateLabel.setFont(ThemeFonts.BODY_SMALL);
            dateLabel.setForeground(ThemeColors.TEXT_MUTED);

            JLabel symLabel = new JLabel("<html><body style='width: 280px; color: #475569; font-size: 11px;'>" +
                    "<b>Symptoms:</b> " + (pred.getSymptomNames().isEmpty() ? "Recorded" : pred.getSymptomNames()) + "</body></html>");

            body.add(condLabel);
            body.add(Box.createVerticalStrut(4));
            body.add(dateLabel);
            body.add(Box.createVerticalStrut(8));
            body.add(symLabel);
        } else {
            JLabel emptyLabel = new JLabel("<html><body style='color: #64748B;'>No condition assessments recorded yet.<br>Start your first symptom prediction to see insights.</body></html>");
            emptyLabel.setFont(ThemeFonts.BODY);
            body.add(emptyLabel);
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        footer.setOpaque(false);
        ModernButton viewHistoryBtn = new ModernButton(pred != null ? "View Full History →" : "Start Prediction →", ModernButton.ButtonStyle.LINK);
        viewHistoryBtn.addActionListener(e -> {
            if (pred != null) navManager.showPredictionHistory();
            else navManager.showSymptomPrediction();
        });
        footer.add(viewHistoryBtn);

        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    private ModernCard createUpcomingAppointmentCard(Appointment appt) {
        ModernCard card = new ModernCard(new BorderLayout(), 18);
        card.setPreferredSize(new Dimension(360, 270));
        card.setMinimumSize(new Dimension(260, 240));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Upcoming Appointment");
        title.setFont(ThemeFonts.TITLE_SMALL);
        title.setForeground(ThemeColors.ACCENT_BLUE);
        header.add(title, BorderLayout.WEST);

        if (appt != null) {
            String status = appt.getStatus();
            Color bg = "CONFIRMED".equalsIgnoreCase(status) ? ThemeColors.SUCCESS_LIGHT : ThemeColors.WARNING_LIGHT;
            Color fg = "CONFIRMED".equalsIgnoreCase(status) ? ThemeColors.SUCCESS : ThemeColors.WARNING;
            header.add(UIUtils.createBadge(status, bg, fg), BorderLayout.EAST);
        }

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(UIUtils.createPadding(10, 0, 10, 0));

        if (appt != null) {
            JLabel docName = new JLabel(appt.getDoctorName() != null ? appt.getDoctorName() : "Doctor");
            docName.setFont(ThemeFonts.BODY_BOLD);
            docName.setForeground(ThemeColors.TEXT_PRIMARY);

            JLabel docSpec = new JLabel(appt.getDoctorSpecialization() != null ? appt.getDoctorSpecialization() : "Specialist");
            docSpec.setFont(ThemeFonts.BODY_SMALL);
            docSpec.setForeground(ThemeColors.TEXT_SECONDARY);

            JLabel schedule = new JLabel("📅 " + DateTimeUtil.formatDate(appt.getAppointmentDate()) + "  ⏰ " + appt.getAppointmentTime());
            schedule.setFont(ThemeFonts.BODY_SMALL_BOLD);
            schedule.setForeground(ThemeColors.PRIMARY);

            body.add(docName);
            body.add(Box.createVerticalStrut(2));
            body.add(docSpec);
            body.add(Box.createVerticalStrut(8));
            body.add(schedule);
        } else {
            JLabel emptyLabel = new JLabel("<html><body style='color: #64748B;'>No upcoming appointments scheduled.<br>Connect with our expert physicians.</body></html>");
            emptyLabel.setFont(ThemeFonts.BODY);
            body.add(emptyLabel);
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        footer.setOpaque(false);
        ModernButton bookBtn = new ModernButton(appt != null ? "Manage Bookings →" : "Book Appointment →", ModernButton.ButtonStyle.LINK);
        bookBtn.addActionListener(e -> {
            if (appt != null) navManager.showPatientAppointments();
            else navManager.showDoctorDirectory();
        });
        footer.add(bookBtn);

        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    private ModernCard createRecommendationCard(Recommendation rec) {
        ModernCard card = new ModernCard(new BorderLayout(), 18);
        card.setPreferredSize(new Dimension(360, 270));
        card.setMinimumSize(new Dimension(260, 240));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Doctor Recommendation");
        title.setFont(ThemeFonts.TITLE_SMALL);
        title.setForeground(ThemeColors.SUCCESS);
        header.add(title, BorderLayout.WEST);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(UIUtils.createPadding(10, 0, 10, 0));

        if (rec != null) {
            JLabel docName = new JLabel("From: " + (rec.getDoctorName() != null ? rec.getDoctorName() : "Attending Doctor"));
            docName.setFont(ThemeFonts.BODY_BOLD);
            docName.setForeground(ThemeColors.TEXT_PRIMARY);

            String text = rec.getLifestyleAdvice() != null && !rec.getLifestyleAdvice().isEmpty() ?
                    rec.getLifestyleAdvice() : rec.getRecommendationText();
            if (text.length() > 90) text = text.substring(0, 87) + "...";

            JLabel advice = new JLabel("<html><body style='width: 280px; color: #475569; font-size: 11px;'>" + text + "</body></html>");

            body.add(docName);
            body.add(Box.createVerticalStrut(6));
            body.add(advice);
        } else {
            JLabel emptyLabel = new JLabel("<html><body style='color: #64748B;'>No personalized doctor recommendations received yet.</body></html>");
            emptyLabel.setFont(ThemeFonts.BODY);
            body.add(emptyLabel);
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        footer.setOpaque(false);
        ModernButton viewAdviceBtn = new ModernButton("View All Advice →", ModernButton.ButtonStyle.LINK);
        viewAdviceBtn.addActionListener(e -> navManager.showPatientRecommendations());
        footer.add(viewAdviceBtn);

        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }
}

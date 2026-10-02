package com.medipredict.ui.screens;

import com.medipredict.model.Doctor;
import com.medipredict.model.Patient;
import com.medipredict.service.AppointmentService;
import com.medipredict.service.DoctorService;
import com.medipredict.service.SessionManager;
import com.medipredict.ui.NavigationManager;
import com.medipredict.ui.components.HeaderPanel;
import com.medipredict.ui.components.ModernButton;
import com.medipredict.ui.components.ModernCard;
import com.medipredict.ui.components.ModernComboBox;
import com.medipredict.ui.components.ModernTextField;
import com.medipredict.ui.components.SidebarPanel;
import com.medipredict.ui.components.ToastNotification;
import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;
import com.medipredict.ui.theme.UIUtils;
import com.medipredict.util.DateTimeUtil;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class AppointmentBookingScreen extends JPanel {
    private final NavigationManager navManager;
    private final AppointmentService appointmentService = new AppointmentService();
    private final DoctorService doctorService = new DoctorService();

    private final ModernComboBox<DoctorItem> doctorCombo;
    private final ModernTextField dateField;
    private final ModernComboBox<String> timeSlotCombo;
    private final JTextArea reasonArea;
    private final JLabel statusLabel;

    private static class DoctorItem {
        final Doctor doctor;
        DoctorItem(Doctor doctor) { this.doctor = doctor; }
        @Override
        public String toString() {
            return doctor.getFullName() + " (" + doctor.getSpecialization() + " - " + doctor.getHospitalClinic() + ")";
        }
    }

    public AppointmentBookingScreen(NavigationManager navManager, Doctor preselectedDoctor) {
        this.navManager = navManager;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        add(new HeaderPanel(navManager), BorderLayout.NORTH);
        add(new SidebarPanel(navManager, NavigationManager.SCREEN_PATIENT_APPOINTMENTS), BorderLayout.WEST);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(UIUtils.createPadding(20, 20, 30, 20));

        ModernCard bookingCard = new ModernCard(new BorderLayout(), 24);
        bookingCard.setPreferredSize(new Dimension(560, 600));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        JLabel title = new JLabel("Schedule Doctor Consultation");
        title.setFont(ThemeFonts.TITLE_LARGE);
        title.setForeground(ThemeColors.PRIMARY);
        title.setAlignmentX(0.0f);

        JLabel sub = new JLabel("Select your doctor, date, and preferred time slot");
        sub.setFont(ThemeFonts.BODY);
        sub.setForeground(ThemeColors.TEXT_SECONDARY);
        sub.setAlignmentX(0.0f);

        // Doctor dropdown
        List<Doctor> allDoctors = doctorService.getAllDoctors();
        DoctorItem[] docItems = new DoctorItem[allDoctors.size()];
        DoctorItem selectedItem = null;
        for (int i = 0; i < allDoctors.size(); i++) {
            docItems[i] = new DoctorItem(allDoctors.get(i));
            if (preselectedDoctor != null && allDoctors.get(i).getDoctorId() == preselectedDoctor.getDoctorId()) {
                selectedItem = docItems[i];
            }
        }
        doctorCombo = new ModernComboBox<>(docItems);
        if (selectedItem != null) {
            doctorCombo.setSelectedItem(selectedItem);
        }
        doctorCombo.setAlignmentX(0.0f);
        doctorCombo.setMaximumSize(new Dimension(480, 38));

        // Date field (defaults to tomorrow)
        String defaultDate = LocalDate.now().plusDays(1).toString();
        dateField = new ModernTextField(defaultDate);
        dateField.setText(defaultDate);
        dateField.setAlignmentX(0.0f);
        dateField.setMaximumSize(new Dimension(480, 38));

        // Time slots
        String[] slots = {"09:00 AM", "10:00 AM", "10:30 AM", "11:30 AM", "02:00 PM", "02:30 PM", "03:30 PM", "04:30 PM"};
        timeSlotCombo = new ModernComboBox<>(slots);
        timeSlotCombo.setAlignmentX(0.0f);
        timeSlotCombo.setMaximumSize(new Dimension(480, 38));

        // Reason Area
        reasonArea = new JTextArea(4, 30);
        reasonArea.setFont(ThemeFonts.BODY);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setBorder(UIUtils.createPadding(8, 10, 8, 10));

        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        reasonScroll.setAlignmentX(0.0f);
        reasonScroll.setMaximumSize(new Dimension(480, 90));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(ThemeFonts.BODY_SMALL);
        statusLabel.setForeground(ThemeColors.DANGER);
        statusLabel.setAlignmentX(0.0f);

        // Actions
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(0.0f);

        ModernButton cancelBtn = new ModernButton("Cancel", ModernButton.ButtonStyle.SECONDARY);
        cancelBtn.addActionListener(e -> navManager.showDoctorDirectory());

        ModernButton submitBtn = new ModernButton("Confirm Booking", ModernButton.ButtonStyle.PRIMARY);
        submitBtn.addActionListener(e -> handleBooking());

        btnPanel.add(cancelBtn);
        btnPanel.add(submitBtn);

        // Assemble
        formPanel.add(title);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(sub);
        formPanel.add(Box.createVerticalStrut(18));
        formPanel.add(createLabel("Select Consulting Doctor:"));
        formPanel.add(doctorCombo);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(createLabel("Appointment Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(createLabel("Preferred Time Slot:"));
        formPanel.add(timeSlotCombo);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(createLabel("Reason for Consultation / Primary Symptoms:"));
        formPanel.add(reasonScroll);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(statusLabel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(btnPanel);

        bookingCard.add(formPanel, BorderLayout.CENTER);
        centerWrapper.add(bookingCard);

        JScrollPane scrollPane = new JScrollPane(centerWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(ThemeFonts.BODY_SMALL_BOLD);
        lbl.setForeground(ThemeColors.TEXT_PRIMARY);
        lbl.setAlignmentX(0.0f);
        return lbl;
    }

    private void handleBooking() {
        statusLabel.setText(" ");
        Patient patient = SessionManager.getInstance().getCurrentPatient();
        if (patient == null) {
            statusLabel.setText("Patient session expired. Please re-login.");
            return;
        }

        DoctorItem docItem = (DoctorItem) doctorCombo.getSelectedItem();
        if (docItem == null) {
            statusLabel.setText("Please select a doctor.");
            return;
        }

        Date date = DateTimeUtil.parseDate(dateField.getText().trim());
        String time = (String) timeSlotCombo.getSelectedItem();
        String reason = reasonArea.getText().trim();

        AppointmentService.AppointmentResult res = appointmentService.bookAppointment(
                patient.getPatientId(), docItem.doctor.getDoctorId(), date, time, reason
        );

        if (res.isSuccess()) {
            ToastNotification.show(navManager.getMainFrame(), res.getMessage(), ToastNotification.ToastType.SUCCESS);
            navManager.showPatientAppointments();
        } else {
            statusLabel.setText(res.getMessage());
            ToastNotification.show(navManager.getMainFrame(), res.getMessage(), ToastNotification.ToastType.ERROR);
        }
    }
}

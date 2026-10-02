package com.medipredict.ui.screens;

import com.medipredict.model.Appointment;
import com.medipredict.model.Patient;
import com.medipredict.service.AppointmentService;
import com.medipredict.service.SessionManager;
import com.medipredict.ui.NavigationManager;
import com.medipredict.ui.components.HeaderPanel;
import com.medipredict.ui.components.ModernButton;
import com.medipredict.ui.components.ModernCard;
import com.medipredict.ui.components.ModernTable;
import com.medipredict.ui.components.SidebarPanel;
import com.medipredict.ui.components.ToastNotification;
import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;
import com.medipredict.ui.theme.UIUtils;
import com.medipredict.util.DateTimeUtil;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

public class PatientAppointmentsScreen extends JPanel {
    private final NavigationManager navManager;
    private final AppointmentService appointmentService = new AppointmentService();

    private List<Appointment> appointmentList;
    private final DefaultTableModel tableModel;
    private final ModernTable table;

    public PatientAppointmentsScreen(NavigationManager navManager) {
        this.navManager = navManager;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        add(new HeaderPanel(navManager), BorderLayout.NORTH);
        add(new SidebarPanel(navManager, NavigationManager.SCREEN_PATIENT_APPOINTMENTS), BorderLayout.WEST);

        JPanel mainContent = new JPanel(new BorderLayout(0, 16));
        mainContent.setOpaque(false);
        mainContent.setBorder(UIUtils.createPadding(24, 28, 28, 28));

        // 1. Header Row
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("My Consultation Appointments");
        title.setFont(ThemeFonts.TITLE_LARGE);
        title.setForeground(ThemeColors.PRIMARY);

        JLabel sub = new JLabel("View appointment confirmations, times, and status tracking.");
        sub.setFont(ThemeFonts.BODY);
        sub.setForeground(ThemeColors.TEXT_SECONDARY);

        titleBlock.add(title);
        titleBlock.add(sub);

        ModernButton bookBtn = new ModernButton("+ Book New Appointment", ModernButton.ButtonStyle.PRIMARY);
        bookBtn.setPreferredSize(new Dimension(200, 38));
        bookBtn.addActionListener(e -> navManager.showDoctorDirectory());

        topRow.add(titleBlock, BorderLayout.WEST);
        topRow.add(bookBtn, BorderLayout.EAST);

        mainContent.add(topRow, BorderLayout.NORTH);

        // 2. Table Card (Expands to fill 100% of available height and width)
        ModernCard tableCard = new ModernCard(new BorderLayout(), 16);

        String[] columns = {"ID", "Doctor", "Specialization", "Hospital", "Date", "Time", "Reason", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new ModernTable(tableModel);
        loadAppointments();

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        actions.setOpaque(false);

        ModernButton cancelBtn = new ModernButton("Cancel Selected Appointment", ModernButton.ButtonStyle.DANGER);
        cancelBtn.addActionListener(e -> handleCancel());

        actions.add(cancelBtn);

        tableCard.add(tableScroll, BorderLayout.CENTER);
        tableCard.add(actions, BorderLayout.SOUTH);

        mainContent.add(tableCard, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }

    private void loadAppointments() {
        Patient p = SessionManager.getInstance().getCurrentPatient();
        if (p == null) return;
        appointmentList = appointmentService.getPatientAppointments(p.getPatientId());

        tableModel.setRowCount(0);
        for (Appointment a : appointmentList) {
            tableModel.addRow(new Object[]{
                    "#" + a.getAppointmentId(),
                    a.getDoctorName() != null ? a.getDoctorName() : "Doctor",
                    a.getDoctorSpecialization() != null ? a.getDoctorSpecialization() : "General",
                    a.getDoctorHospital() != null ? a.getDoctorHospital() : "Hospital",
                    DateTimeUtil.formatDate(a.getAppointmentDate()),
                    a.getAppointmentTime(),
                    a.getReason(),
                    a.getStatus()
            });
        }
    }

    private void handleCancel() {
        int row = table.getSelectedRow();
        if (row < 0) {
            ToastNotification.show(navManager.getMainFrame(), "Please select an appointment from the table to cancel.", ToastNotification.ToastType.WARNING);
            return;
        }

        Appointment selected = appointmentList.get(row);
        if ("CANCELLED".equalsIgnoreCase(selected.getStatus()) || "COMPLETED".equalsIgnoreCase(selected.getStatus())) {
            ToastNotification.show(navManager.getMainFrame(), "This appointment is already marked as " + selected.getStatus() + ".", ToastNotification.ToastType.INFO);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(navManager.getMainFrame(),
                "Are you sure you want to cancel appointment #" + selected.getAppointmentId() + " with " + selected.getDoctorName() + "?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = appointmentService.cancelAppointment(selected.getAppointmentId(), "Cancelled by patient");
            if (success) {
                ToastNotification.show(navManager.getMainFrame(), "Appointment cancelled successfully.", ToastNotification.ToastType.SUCCESS);
                loadAppointments();
            } else {
                ToastNotification.show(navManager.getMainFrame(), "Failed to cancel appointment.", ToastNotification.ToastType.ERROR);
            }
        }
    }
}

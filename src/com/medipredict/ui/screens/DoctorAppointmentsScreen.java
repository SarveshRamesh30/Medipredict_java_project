package com.medipredict.ui.screens;

import com.medipredict.model.Appointment;
import com.medipredict.model.Doctor;
import com.medipredict.service.AppointmentService;
import com.medipredict.service.SessionManager;
import com.medipredict.ui.NavigationManager;
import com.medipredict.ui.components.HeaderPanel;
import com.medipredict.ui.components.ModernButton;
import com.medipredict.ui.components.ModernCard;
import com.medipredict.ui.components.ModernComboBox;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

public class DoctorAppointmentsScreen extends JPanel {
    private final NavigationManager navManager;
    private final AppointmentService appointmentService = new AppointmentService();

    private List<Appointment> allAppointments = new ArrayList<>();
    private final DefaultTableModel tableModel;
    private final ModernTable table;
    private final ModernComboBox<String> statusFilter;

    public DoctorAppointmentsScreen(NavigationManager navManager) {
        this.navManager = navManager;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        add(new HeaderPanel(navManager), BorderLayout.NORTH);
        add(new SidebarPanel(navManager, NavigationManager.SCREEN_DOCTOR_APPOINTMENTS), BorderLayout.WEST);

        JPanel mainContent = new JPanel(new BorderLayout(0, 16));
        mainContent.setOpaque(false);
        mainContent.setBorder(UIUtils.createPadding(24, 28, 28, 28));

        // Top Section: Title & Filters
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setOpaque(false);

        // 1. Header Row
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setAlignmentX(0.0f);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("Appointment Requests & Patient Consultations");
        title.setFont(ThemeFonts.TITLE_LARGE);
        title.setForeground(ThemeColors.PRIMARY);

        JLabel sub = new JLabel("Review consultation requests, confirm slots, and mark sessions as completed.");
        sub.setFont(ThemeFonts.BODY);
        sub.setForeground(ThemeColors.TEXT_SECONDARY);

        titleBlock.add(title);
        titleBlock.add(sub);

        topRow.add(titleBlock, BorderLayout.WEST);
        northPanel.add(topRow);
        northPanel.add(Box.createVerticalStrut(16));

        // 2. Filter Bar
        ModernCard filterCard = new ModernCard(new BorderLayout(), 12);
        filterCard.setAlignmentX(0.0f);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterPanel.setOpaque(false);

        JLabel fLbl = new JLabel("Status Filter:");
        fLbl.setFont(ThemeFonts.BODY_BOLD);

        statusFilter = new ModernComboBox<>(new String[]{"All Statuses", "PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"});
        statusFilter.setPreferredSize(new Dimension(180, 36));
        statusFilter.addActionListener(e -> filterAppointments());

        filterPanel.add(fLbl);
        filterPanel.add(statusFilter);
        filterCard.add(filterPanel, BorderLayout.CENTER);

        northPanel.add(filterCard);
        mainContent.add(northPanel, BorderLayout.NORTH);

        // 3. Table Card (Full expansion)
        ModernCard tableCard = new ModernCard(new BorderLayout(), 16);

        String[] columns = {"ID", "Patient Name", "Contact", "Date", "Time Slot", "Reason", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new ModernTable(tableModel);
        loadAppointments();

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);

        // Action Toolbar
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        actions.setOpaque(false);

        ModernButton confirmBtn = new ModernButton("✓ Confirm", ModernButton.ButtonStyle.SUCCESS);
        confirmBtn.addActionListener(e -> updateSelectedStatus("CONFIRMED"));

        ModernButton completeBtn = new ModernButton("✓ Mark Completed", ModernButton.ButtonStyle.PRIMARY);
        completeBtn.addActionListener(e -> updateSelectedStatus("COMPLETED"));

        ModernButton cancelBtn = new ModernButton("✗ Cancel", ModernButton.ButtonStyle.DANGER);
        cancelBtn.addActionListener(e -> updateSelectedStatus("CANCELLED"));

        ModernButton recBtn = new ModernButton("📋 Issue Advice", ModernButton.ButtonStyle.OUTLINE);
        recBtn.addActionListener(e -> navManager.showDoctorRecommendations());

        actions.add(confirmBtn);
        actions.add(completeBtn);
        actions.add(cancelBtn);
        actions.add(recBtn);

        tableCard.add(tableScroll, BorderLayout.CENTER);
        tableCard.add(actions, BorderLayout.SOUTH);

        mainContent.add(tableCard, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }

    private void loadAppointments() {
        Doctor d = SessionManager.getInstance().getCurrentDoctor();
        if (d == null) return;
        allAppointments = appointmentService.getDoctorAppointments(d.getDoctorId());
        filterAppointments();
    }

    private void filterAppointments() {
        String filter = (String) statusFilter.getSelectedItem();
        boolean all = filter == null || "All Statuses".equalsIgnoreCase(filter);

        tableModel.setRowCount(0);
        for (Appointment a : allAppointments) {
            if (all || a.getStatus().equalsIgnoreCase(filter)) {
                tableModel.addRow(new Object[]{
                        "#" + a.getAppointmentId(),
                        a.getPatientName() != null ? a.getPatientName() : "Patient #" + a.getPatientId(),
                        a.getPatientPhone() != null ? a.getPatientPhone() : a.getPatientEmail(),
                        DateTimeUtil.formatDate(a.getAppointmentDate()),
                        a.getAppointmentTime(),
                        a.getReason(),
                        a.getStatus()
                });
            }
        }
    }

    private void updateSelectedStatus(String newStatus) {
        int row = table.getSelectedRow();
        if (row < 0) {
            ToastNotification.show(navManager.getMainFrame(), "Please select an appointment from the table.", ToastNotification.ToastType.WARNING);
            return;
        }

        String idStr = (String) tableModel.getValueAt(row, 0);
        int apptId = Integer.parseInt(idStr.replace("#", ""));

        boolean success = appointmentService.updateStatus(apptId, newStatus, null);
        if (success) {
            ToastNotification.show(navManager.getMainFrame(), "Appointment #" + apptId + " marked as " + newStatus, ToastNotification.ToastType.SUCCESS);
            loadAppointments();
        } else {
            ToastNotification.show(navManager.getMainFrame(), "Failed to update appointment status.", ToastNotification.ToastType.ERROR);
        }
    }
}

package com.medipredict.ui.screens;

import com.medipredict.model.Doctor;
import com.medipredict.service.DoctorService;
import com.medipredict.ui.NavigationManager;
import com.medipredict.ui.components.HeaderPanel;
import com.medipredict.ui.components.ModernButton;
import com.medipredict.ui.components.ModernCard;
import com.medipredict.ui.components.ModernComboBox;
import com.medipredict.ui.components.ModernTextField;
import com.medipredict.ui.components.ResponsiveScrollPanel;
import com.medipredict.ui.components.SidebarPanel;
import com.medipredict.ui.theme.ThemeColors;
import com.medipredict.ui.theme.ThemeFonts;
import com.medipredict.ui.theme.UIUtils;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

public class DoctorDirectoryScreen extends JPanel {
    private final NavigationManager navManager;
    private final DoctorService doctorService = new DoctorService();

    private final ModernTextField searchField;
    private final ModernComboBox<String> specCombo;
    private final JPanel cardsGrid;

    public DoctorDirectoryScreen(NavigationManager navManager) {
        this.navManager = navManager;

        setLayout(new BorderLayout());
        setBackground(ThemeColors.BG_MAIN);

        add(new HeaderPanel(navManager), BorderLayout.NORTH);
        add(new SidebarPanel(navManager, NavigationManager.SCREEN_DOCTOR_DIRECTORY), BorderLayout.WEST);

        ResponsiveScrollPanel mainContent = new ResponsiveScrollPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(UIUtils.createPadding(24, 28, 28, 28));

        // 1. Title Banner
        JLabel title = new JLabel("Doctor Directory & Consultation Specialists");
        title.setFont(ThemeFonts.TITLE_LARGE);
        title.setForeground(ThemeColors.PRIMARY);
        title.setAlignmentX(0.0f);

        JLabel sub = new JLabel("Browse accredited medical physicians and schedule in-person or virtual consultations.");
        sub.setFont(ThemeFonts.BODY);
        sub.setForeground(ThemeColors.TEXT_SECONDARY);
        sub.setAlignmentX(0.0f);

        mainContent.add(title);
        mainContent.add(Box.createVerticalStrut(4));
        mainContent.add(sub);
        mainContent.add(Box.createVerticalStrut(18));

        // 2. Search & Filter Bar
        ModernCard filterCard = new ModernCard(new BorderLayout(), 14);
        filterCard.setAlignmentX(0.0f);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        filterPanel.setOpaque(false);

        JLabel searchLbl = new JLabel("🔍 Search Doctors:");
        searchLbl.setFont(ThemeFonts.BODY_BOLD);

        searchField = new ModernTextField("Search by name, hospital, or specialty...");
        searchField.setPreferredSize(new Dimension(320, 38));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { refreshDoctors(); }
            @Override public void removeUpdate(DocumentEvent e) { refreshDoctors(); }
            @Override public void changedUpdate(DocumentEvent e) { refreshDoctors(); }
        });

        JLabel specLbl = new JLabel("Specialty:");
        specLbl.setFont(ThemeFonts.BODY_BOLD);

        List<String> specs = new ArrayList<>();
        specs.add("All Specializations");
        specs.addAll(doctorService.getAllSpecializations());

        specCombo = new ModernComboBox<>(specs.toArray(new String[0]));
        specCombo.setPreferredSize(new Dimension(220, 38));
        specCombo.addActionListener(e -> refreshDoctors());

        filterPanel.add(searchLbl);
        filterPanel.add(searchField);
        filterPanel.add(Box.createHorizontalStrut(12));
        filterPanel.add(specLbl);
        filterPanel.add(specCombo);

        filterCard.add(filterPanel, BorderLayout.CENTER);
        mainContent.add(filterCard);
        mainContent.add(Box.createVerticalStrut(18));

        // 3. Doctor Cards Grid Container
        cardsGrid = new JPanel(new GridLayout(0, 2, 20, 20));
        cardsGrid.setOpaque(false);
        cardsGrid.setAlignmentX(0.0f);

        refreshDoctors();

        mainContent.add(cardsGrid);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void refreshDoctors() {
        cardsGrid.removeAll();
        String keyword = searchField.getText().trim();
        String spec = (String) specCombo.getSelectedItem();

        List<Doctor> doctors = doctorService.searchDoctors(keyword, spec);
        if (doctors.isEmpty()) {
            JLabel empty = new JLabel("No doctors found matching your filter criteria.");
            empty.setFont(ThemeFonts.BODY);
            empty.setForeground(ThemeColors.TEXT_MUTED);
            cardsGrid.add(empty);
        } else {
            for (Doctor d : doctors) {
                cardsGrid.add(createDoctorCard(d));
            }
        }
        cardsGrid.revalidate();
        cardsGrid.repaint();
    }

    private ModernCard createDoctorCard(Doctor doc) {
        ModernCard card = new ModernCard(new BorderLayout(), 18);
        card.setPreferredSize(new Dimension(500, 220));
        card.setMinimumSize(new Dimension(360, 200));

        // Header: Name + Specialization Badge
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel nameLabel = new JLabel("👨‍⚕️ " + doc.getFullName());
        nameLabel.setFont(ThemeFonts.TITLE_SMALL);
        nameLabel.setForeground(ThemeColors.PRIMARY);

        JPanel specBadge = UIUtils.createBadge(doc.getSpecialization(), ThemeColors.ACCENT_BLUE_LIGHT, ThemeColors.ACCENT_BLUE);

        header.add(nameLabel, BorderLayout.WEST);
        header.add(specBadge, BorderLayout.EAST);

        // Body: Hospital, Experience, Fee, Hours
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(UIUtils.createPadding(10, 0, 10, 0));

        JLabel hospLabel = new JLabel("🏥 " + doc.getHospitalClinic());
        hospLabel.setFont(ThemeFonts.BODY);
        hospLabel.setForeground(ThemeColors.TEXT_PRIMARY);

        JLabel detailsLabel = new JLabel("⭐ " + doc.getExperienceYears() + " yrs experience  |  💵 Fee: $" +
                (doc.getConsultationFee() != null ? doc.getConsultationFee() : "50.00") +
                "  |  ⏰ " + doc.getAvailabilityHours());
        detailsLabel.setFont(ThemeFonts.BODY_SMALL);
        detailsLabel.setForeground(ThemeColors.TEXT_SECONDARY);

        String bio = doc.getBio() != null && !doc.getBio().isEmpty() ? doc.getBio() : "Experienced healthcare provider dedicated to clinical excellence.";
        if (bio.length() > 95) bio = bio.substring(0, 92) + "...";
        JLabel bioLabel = new JLabel("<html><body style='color: #64748B; font-size: 11px;'>" + bio + "</body></html>");

        body.add(hospLabel);
        body.add(Box.createVerticalStrut(4));
        body.add(detailsLabel);
        body.add(Box.createVerticalStrut(6));
        body.add(bioLabel);

        // Footer Actions: Book Appointment & Message
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        ModernButton msgBtn = new ModernButton("💬 Message", ModernButton.ButtonStyle.SECONDARY);
        msgBtn.setPreferredSize(new Dimension(105, 36));
        msgBtn.addActionListener(e -> navManager.showMessaging(doc.getUserId()));

        ModernButton bookBtn = new ModernButton("📅 Book Appointment", ModernButton.ButtonStyle.PRIMARY);
        bookBtn.setPreferredSize(new Dimension(165, 36));
        bookBtn.addActionListener(e -> navManager.showAppointmentBooking(doc));

        actions.add(msgBtn);
        actions.add(bookBtn);

        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);
        return card;
    }
}

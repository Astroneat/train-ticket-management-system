package com.mrt.user.schedules;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JPanel;

import com.mrt.model.Schedule;

public class SchedulesPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel contentPanel;

    public static final String SEARCH  = "SEARCH";
    public static final String SEATS   = "SEATS";
    public static final String SUMMARY = "SUMMARY";
    public static final String SUCCESS = "SUCCESS";

    private Schedule selectedSchedule;


    public SchedulesPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(SEARCH, new ScheduleSearchPanel(this));
        contentPanel.add(SEATS, new SeatsPanel());

        showPage(SEARCH);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void showPage(String page) {
        cardLayout.show(contentPanel, page);
    }

    public Schedule getSelectedSchedule() {
        return selectedSchedule;
    }
    public void setSelectedSchedule(Schedule schedule) {
        selectedSchedule = schedule;
    }
}

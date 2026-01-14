package com.mrt.admin.schedules;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JPanel;

import com.mrt.Universal;
import com.mrt.admin.schedules.search.ScheduleSearchPanel;
import com.mrt.admin.schedules.seats.SeatViewPanel;
import com.mrt.frames.AdminFrame;
import com.mrt.frames.Page;
import com.mrt.models.Schedule;

public class SchedulesPanel extends JPanel implements Page {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private Schedule selectedSchedule;

    public static final String SEARCH = "SEARCH";
    public static final String SEATS = "SEATS";

    private ScheduleSearchPanel search = new ScheduleSearchPanel(this);
    private SeatViewPanel seats = new SeatViewPanel(this);

    public SchedulesPanel(AdminFrame adminFrame) {
        setLayout(new BorderLayout());
        setBackground(Universal.BACKGROUND_WHITE);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        contentPanel.add(SEARCH, search);
        contentPanel.add(SEATS, seats);

        // contentPanel.add(BookingStep.SEATS.getLabel(), new SeatSelectionPanel(this));

        showPage(SEARCH);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void showPage(String page) {
        if(page.equals(SEATS)) seats.onShow();
        cardLayout.show(contentPanel, page);
    }

    public Schedule getSelectedSchedule() {
        return selectedSchedule;
    }
    public void setSelectedSchedule(Schedule schedule) {
        selectedSchedule = schedule;
    }

    public void refreshPage() {
        showPage(SEARCH);
    }
}

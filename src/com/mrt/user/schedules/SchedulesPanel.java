package com.mrt.user.schedules;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.mrt.Universal;
import com.mrt.models.Schedule;
import com.mrt.models.Seat;
import com.mrt.models.User;
import com.mrt.services.TicketService;
import com.mrt.user.UserFrame;
import com.mrt.user.schedules.search.ScheduleSearchPanel;
import com.mrt.user.schedules.seats.SeatSelectionPanel;
import com.mrt.user.schedules.success.SuccessPanel;
import com.mrt.user.schedules.summary.SummaryPanel;

public class SchedulesPanel extends JPanel {

    private User user;

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private Schedule selectedSchedule;
    private List<Seat> selectedSeats;
    private BreadcrumbPanel breadcrumb;

    private Map<BookingStep, BookingPage> pages;

    public SchedulesPanel(UserFrame userFrame, User user) {
        setLayout(new BorderLayout());
        setBackground(Universal.BACKGROUND_WHITE);

        this.user = user;

        breadcrumb = new BreadcrumbPanel(this);
        add(breadcrumb, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        contentPanel.add(BookingStep.SEARCH.getLabel(), new ScheduleSearchPanel(this));

        pages = new HashMap<>();
        pages.put(BookingStep.SEATS, new SeatSelectionPanel(this));
        pages.put(BookingStep.SUMMARY, new SummaryPanel(this));
        pages.put(BookingStep.SUCCESS, new SuccessPanel(userFrame, this));

        for(Map.Entry<BookingStep, BookingPage> entry: pages.entrySet()) {
            contentPanel.add(entry.getKey().getLabel(), (JPanel) entry.getValue());
        }
        // contentPanel.add(BookingStep.SEATS.getLabel(), new SeatSelectionPanel(this));

        showStep(BookingStep.SEARCH);
        // showStep(BookingStep.SUCCESS);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void showStep(BookingStep step) {
        BookingPage page = pages.get(step);
        if(page != null) page.onShow();

        cardLayout.show(contentPanel, step.getLabel());
        breadcrumb.setCurrentStep(step);
    }

    public void confirmBooking() {
        showStep(BookingStep.SUCCESS);
        TicketService.bookTickets(user, selectedSchedule, selectedSeats);
    }

    public Schedule getSelectedSchedule() {
        return selectedSchedule;
    }
    public void setSelectedSchedule(Schedule schedule) {
        selectedSchedule = schedule;
    }

    public List<Seat> getSelectedSeats() {
        return selectedSeats;
    }
    public void setSelectedSeats(List<Seat> seats) {
        selectedSeats = seats;
    }

    public User getUser() {
        return user;
    }
}

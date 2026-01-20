package com.mrt.user.tickets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.models.Route;
import com.mrt.models.Schedule;
import com.mrt.models.Ticket;
import com.mrt.models.Train;
import com.mrt.models.User;
import com.mrt.services.CurrencyService;
import com.mrt.services.RouteService;
import com.mrt.services.ScheduleService;
import com.mrt.services.SeatService;
import com.mrt.services.TicketService;
import com.mrt.services.TrainService;

public class TicketListScrollPane extends JScrollPane {

    private User user;
    private int numOfTickets;
    private TicketStatus status;
    private String searchTerm;
    
    public TicketListScrollPane(User user, TicketStatus status) {
        this.user = user;
        this.status = status;
        showTickets(status, "");

        setBorder(null);
        setOpaque(false);
        getViewport().setOpaque(false);
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        setPreferredSize(getPreferredSize());
    }

    public void showTickets(TicketStatus status, String searchTerm) {
        this.searchTerm = searchTerm;

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        List<Ticket> tickets = TicketService.getTicketsByUser(user, searchTerm);
        numOfTickets = 0;
        for (Ticket ticket: tickets) {
            Schedule schedule = ScheduleService.getScheduleById(ticket.getScheduleId());
            Route route = RouteService.getRouteById(schedule.getRouteId());
            Train train = TrainService.getTrainById(schedule.getTrainId());
            if (ticket.getStatus().equals(status.getStatus())) {
                panel.add(createTicketItemPanel(ticket, schedule, route, train));
                numOfTickets++;
            }
        }
        if(numOfTickets == 0) {
            panel.add(createEmptyMessagePanel());
        }
        panel.add(Box.createVerticalStrut(10));


        setViewportView(panel);
    }

    private JPanel createEmptyMessagePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        panel.setOpaque(false);

        JLabel label = UIFactory.createPlainLabel("No tickets available.", 20);
        label.setForeground(Color.GRAY);
        panel.add(label);

        return panel;
    }

    private JPanel createTicketItemPanel(Ticket ticket, Schedule schedule, Route route, Train train) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Universal.PASTEL_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(createTicketInfoPanel(ticket, schedule, route, train));
        panel.add(createTicketViewDetailsPanel(ticket, schedule, route, train));

        wrapper.add(panel, BorderLayout.CENTER);
        wrapper.setMaximumSize(new Dimension(1000, wrapper.getPreferredSize().height));
        return wrapper;
    }

    private JPanel createTicketInfoPanel(Ticket ticket, Schedule schedule, Route route, Train train) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);

        panel.add(createTicketDetailPanel(ticket, schedule, route, train), BorderLayout.CENTER);
        panel.add(createTicketStatusPanel(ticket), BorderLayout.EAST);

        return panel;
    }

    private JPanel createTicketDetailPanel(Ticket ticket, Schedule schedule, Route route, Train train) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        int verticalStrut = 10;
        int fontSize = 18;

        JPanel routePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        routePanel.setOpaque(false);
        routePanel.add(UIFactory.createBoldLabel(route.getRouteSummary(), fontSize + 2));
        panel.add(routePanel);
        panel.add(Box.createVerticalStrut(verticalStrut));

        JPanel trainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        trainPanel.setOpaque(false);
        trainPanel.add(UIFactory.createBoldLabel("Train: ", fontSize));
        trainPanel.add(UIFactory.createPlainLabel(train.getTrainSummary(), fontSize));
        trainPanel.add(UIFactory.createBoldLabel(" • ", fontSize));
        trainPanel.add(UIFactory.createPlainLabel(schedule.getFormattedDepartureTime(), fontSize));
        panel.add(trainPanel);
        panel.add(Box.createVerticalStrut(verticalStrut));

        JPanel seatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        seatPanel.setOpaque(false);
        seatPanel.add(UIFactory.createBoldLabel("Seat: ", fontSize));
        seatPanel.add(UIFactory.createPlainLabel("Car " + ticket.getCarNo() + " - " + SeatService.toSeatCode(ticket.getSeatIndex()), fontSize));
        panel.add(seatPanel);
        panel.add(Box.createVerticalStrut(verticalStrut));

        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pricePanel.setOpaque(false);
        pricePanel.add(UIFactory.createBoldLabel("Price: ", fontSize));
        pricePanel.add(UIFactory.createPlainLabel(CurrencyService.formatVnd(ticket.getPrice()), fontSize));
        panel.add(pricePanel);
        panel.add(Box.createVerticalStrut(verticalStrut));

        JPanel bookingIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bookingIdPanel.setOpaque(false);
        bookingIdPanel.add(UIFactory.createBoldLabel("Booking ID: ", fontSize));
        bookingIdPanel.add(UIFactory.createPlainLabel(ticket.getTicketId() + "", fontSize));
        panel.add(bookingIdPanel);
        panel.add(Box.createVerticalStrut(verticalStrut));

        return panel;
    }

    private JPanel createTicketStatusPanel(Ticket ticket) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.setOpaque(false);

        String status = ticket.getStatus();
        JLabel label = UIFactory.createBoldLabel(status, 16);
        switch (status) {
            case "booked":
                label.setForeground(Universal.PASTEL_BLUE);
                break;
            case "boarded":
                label.setForeground(Universal.PASTEL_PURPLE);
                break;
            case "cancelled":
                label.setForeground(Universal.PASTEL_RED);
                break;
            case "expired":
                label.setForeground(Color.GRAY);
                break;
            default:
                label.setForeground(Color.BLACK);
                break;
        }
        panel.add(label);

        return panel;
    }

    private JPanel createTicketViewDetailsPanel(Ticket ticket, Schedule schedule, Route route, Train train) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panel.setOpaque(false);

        if(status == TicketStatus.BOOKED) {
            JButton cancelTicketBtn = UIFactory.createButton("Cancel Ticket");
            cancelTicketBtn.setForeground(Color.RED);
            cancelTicketBtn.setFont(UIFactory.createDefaultBoldFont(16));
            cancelTicketBtn.setBorder(null);
            cancelTicketBtn.addActionListener(e -> {
                int option = JOptionPane.showConfirmDialog(
                    SwingUtilities.getWindowAncestor(this), 
                    "Cancel this ticket? (This action cannot be undone)",
                    "Confirm cancellation",
                    JOptionPane.YES_NO_OPTION
                );

                if(option == JOptionPane.YES_OPTION) {
                    TicketService.cancelTicket(ticket.getTicketId());
                    showTickets(status, searchTerm);
                }
            });

            LocalDateTime now = LocalDateTime.now();
            long diff = Duration.between(now, schedule.getDepartureTime()).toMinutes();
            if(diff <= 30) {
                cancelTicketBtn.setEnabled(false);
            }
            
            panel.add(cancelTicketBtn);
            panel.add(Box.createHorizontalStrut(20));
        }

        JButton viewDetailsButton = UIFactory.createButton("View Ticket →");
        viewDetailsButton.setForeground(Color.BLUE);
        viewDetailsButton.setFont(UIFactory.createDefaultBoldFont(16));
        viewDetailsButton.setBorder(null);
        viewDetailsButton.addActionListener(e -> {
            ViewTicketDialog dialog = new ViewTicketDialog((JFrame) SwingUtilities.getWindowAncestor(this), ticket, schedule, route, train, user);
            dialog.setVisible(true);
        });
        panel.add(viewDetailsButton);

        return panel;
    }

    public int getNumberOfTickets() {
        return numOfTickets;
    }
}

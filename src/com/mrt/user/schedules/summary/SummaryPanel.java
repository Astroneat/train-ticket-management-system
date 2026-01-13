package com.mrt.user.schedules.summary;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import com.mrt.factory.UIFactory;
import com.mrt.models.Route;
import com.mrt.models.Schedule;
import com.mrt.models.Seat;
import com.mrt.models.Train;
import com.mrt.models.User;
import com.mrt.services.CurrencyService;
import com.mrt.services.RouteService;
import com.mrt.services.SeatService;
import com.mrt.services.TrainService;
import com.mrt.user.schedules.BookingPage;
import com.mrt.user.schedules.BookingStep;
import com.mrt.user.schedules.SchedulesPanel;

public class SummaryPanel extends JPanel implements BookingPage {
    
    private SchedulesPanel parent;
    private Schedule schedule;
    private Route route;
    private Train train;
    private User user;

    private int summaryFontSize = 18;

    public SummaryPanel(SchedulesPanel parent) {
        this.parent = parent;
    }
    
    public void onShow() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        this.schedule = parent.getSelectedSchedule();
        this.route = RouteService.getRouteById(schedule.getRouteId());
        this.train = TrainService.getTrainById(schedule.getTrainId());
        this.user = parent.getUser();

        removeAll();
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createInfoPanel(), BorderLayout.WEST);
        add(createIllustrationPanel(), BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        panel.setOpaque(false);
        panel.add(UIFactory.createBoldLabel("Booking Summary", 28));
        return panel;
    }

    private JScrollPane createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        // panel.setPreferredSize(new Dimension(500, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        panel.add(createTripInfoPanel());
        panel.add(Box.createVerticalStrut(30));
        panel.add(createPricingPanel());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createReassurancePanel());
        panel.add(Box.createVerticalStrut(10));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(500, 0));
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        return scrollPane;
    }

    private JPanel createTripInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(LEFT_ALIGNMENT);

        int verticalStrut = 10;

        JPanel trainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        trainPanel.add(UIFactory.createBoldLabel("Train:", summaryFontSize));
        trainPanel.add(Box.createHorizontalStrut(10));
        trainPanel.add(UIFactory.createPlainLabel(train.getTrainSummary(), summaryFontSize));
        formatInfoPanel(trainPanel);
        panel.add(trainPanel);

        panel.add(Box.createVerticalStrut(verticalStrut));

        FlowLayout fl = new FlowLayout(FlowLayout.LEFT, 0, 0);
        fl.setAlignOnBaseline(true);
        JPanel routePanel = new JPanel(fl);
        JLabel routeLabel = UIFactory.createBoldLabel("Route:", summaryFontSize);
        routePanel.add(routeLabel);  
        routePanel.add(Box.createHorizontalStrut(10));
        JTextArea routeTextArea = UIFactory.createTextArea(0, 25);
        routeTextArea.setText(route.getRouteSummary());
        routeTextArea.setEditable(false);
        routeTextArea.setBorder(null);
        routeTextArea.setFont(UIFactory.createDefaultPlainFont(summaryFontSize));
        routeTextArea.setOpaque(false);
        routeTextArea.setRows(routeTextArea.getLineCount());
        routePanel.add(routeTextArea);
        formatInfoPanel(routePanel);
        panel.add(routePanel);

        panel.add(Box.createVerticalStrut(verticalStrut));

        JPanel departurePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        departurePanel.add(UIFactory.createBoldLabel("Departure:", summaryFontSize));
        departurePanel.add(Box.createHorizontalStrut(10));
        departurePanel.add(UIFactory.createPlainLabel(schedule.getFormattedDepartureTime(), summaryFontSize));
        formatInfoPanel(departurePanel);
        panel.add(departurePanel);

        panel.add(Box.createVerticalStrut(verticalStrut));

        JPanel arrivalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        arrivalPanel.add(UIFactory.createBoldLabel("Arrival:", summaryFontSize));
        arrivalPanel.add(Box.createHorizontalStrut(10));
        arrivalPanel.add(UIFactory.createPlainLabel(schedule.getFormattedArrivalTime(), summaryFontSize));
        formatInfoPanel(arrivalPanel);
        panel.add(arrivalPanel);

        panel.add(Box.createVerticalStrut(verticalStrut));

        JPanel passengerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        passengerPanel.add(UIFactory.createBoldLabel("Passenger:", summaryFontSize));
        passengerPanel.add(Box.createHorizontalStrut(10));
        passengerPanel.add(UIFactory.createPlainLabel(user.getFullName(), summaryFontSize));
        formatInfoPanel(passengerPanel);
        panel.add(passengerPanel);

        panel.add(Box.createVerticalStrut(50));

        JPanel selectedSeatsLabelWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        selectedSeatsLabelWrapper.add(UIFactory.createBoldLabel("Selected Seats:", summaryFontSize));
        formatInfoPanel(selectedSeatsLabelWrapper);
        panel.add(selectedSeatsLabelWrapper);

        panel.add(Box.createVerticalStrut(verticalStrut));

        JPanel seatsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        List<Seat> selectedSeats = parent.getSelectedSeats();
        Map<Integer, List<String>> carBucket = new HashMap<>();
        for(Seat seat: selectedSeats) {
            List<String> seats = carBucket.get(seat.getCarNo());
            if(seats == null) {
                seats = new ArrayList<>();
                carBucket.put(seat.getCarNo(), seats);
            }
            seats.add(SeatService.toSeatCode(seat.getSeatIndex()));
        }
        String seatsStr = "<html>";
        for(Map.Entry<Integer, List<String>> entry: carBucket.entrySet()) {
            if(seatsStr.length() > 0) seatsStr += "\n";
            seatsStr += "<p style='margin-bottom: 5px;'>• Car " + entry.getKey() + ": " + String.join(", ", entry.getValue()) + "</p>";
        }
        seatsStr += "</html>";
        seatsPanel.add(UIFactory.createPlainLabel(seatsStr, summaryFontSize));
        formatInfoPanel(seatsPanel);
        panel.add(seatsPanel);

        panel.add(Box.createVerticalStrut(verticalStrut));

        return panel;
    }

    private JPanel createPricingPanel() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        // wrapper.setBorder(BorderFactory.createEmptyBorder(20, 40, 0, 0));

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 50);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(UIFactory.createBoldLabel("Total Tickets:", summaryFontSize), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel("" + parent.getSelectedSeats().size(), summaryFontSize), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(UIFactory.createBoldLabel("Price Per Ticket:", summaryFontSize), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel(CurrencyService.formatVnd(SeatService.getPricePerTicket()), summaryFontSize), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(UIFactory.createBoldLabel("Subtotal:", summaryFontSize), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel(CurrencyService.formatVnd(parent.getSelectedSeats().size() * SeatService.getPricePerTicket()), summaryFontSize), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(UIFactory.createBoldLabel("Fees:", summaryFontSize), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel(CurrencyService.formatVnd(parent.getSelectedSeats().size() * SeatService.getFeesPerTicket()), summaryFontSize), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(UIFactory.createBoldLabel("Total:", summaryFontSize), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createBoldLabel(CurrencyService.formatVnd(parent.getSelectedSeats().size() * SeatService.getPricePerTicket()), summaryFontSize), gbc);

        formatInfoPanel(panel);
        wrapper.add(panel);
        return wrapper;
    }

    private void formatInfoPanel(JPanel panel) {
        panel.setOpaque(false);
        panel.setMaximumSize(panel.getPreferredSize());
        panel.setAlignmentX(LEFT_ALIGNMENT);
    }

    private JPanel createReassurancePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        // panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 0));
        panel.setAlignmentX(LEFT_ALIGNMENT);

        int verticalStrut = 10;

        panel.add(UIFactory.createItalicLabel("• Boarding opens 30 minutes before departure.", summaryFontSize - 4));
        panel.add(Box.createVerticalStrut(verticalStrut));
        panel.add(UIFactory.createItalicLabel("• Ensure that all selected seats and personal details are correct.", summaryFontSize - 4));
        panel.add(Box.createVerticalStrut(verticalStrut));
        panel.add(UIFactory.createItalicLabel("• By confirming your booking, you agree to our terms and conditions.", summaryFontSize - 4));

        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        // panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        panel.setOpaque(false);

        JButton backBtn = UIFactory.createButton("Back");
        backBtn.setPreferredSize(new Dimension(100, 36));
        backBtn.addActionListener(e -> {
            parent.showStep(BookingStep.SEATS);
        });
        panel.add(backBtn, BorderLayout.WEST);

        JButton confirmBtn = UIFactory.createButton("Confirm & Pay");
        confirmBtn.setPreferredSize(new Dimension(150, 36));
        confirmBtn.setFont(UIFactory.createDefaultBoldFont(14));
        confirmBtn.addActionListener(e -> {
            parent.confirmBooking();
        });
        panel.add(confirmBtn, BorderLayout.EAST);

        return panel;
    }

    private JPanel createIllustrationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        panel.setOpaque(false);

        JLabel imgLabel = UIFactory.createImageLabel("src/com/mrt/img/information.png", new Dimension(200, 200));

        panel.add(imgLabel);

        return panel;
    }
}

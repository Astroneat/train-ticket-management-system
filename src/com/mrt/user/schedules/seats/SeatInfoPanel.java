package com.mrt.user.schedules.seats;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.models.Seat;
import com.mrt.services.CurrencyService;
import com.mrt.services.SeatService;

public class SeatInfoPanel extends JPanel {

    private SeatSelectionPanel parent;
    private JLabel seatsLabel;
    private JLabel totalTicketsLabel;
    private JLabel totalPriceLabel;

    public SeatInfoPanel(SeatSelectionPanel parent) {
        this.parent = parent;

        setOpaque(false);
        setPreferredSize(new Dimension(0, 220));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));

        add(createLegendPanel(), BorderLayout.WEST);
        add(createInfoPanel(), BorderLayout.EAST);
    }

    public void refreshSeatStatus() {
        List<Seat> selectedSeats = parent.getSelectedSeats();

        String seatsText = "<html>";
        if(selectedSeats.isEmpty()) {
            seatsText += "(No seat selected)";
        }
        else {
            Map<Integer, List<String>> carBucket = new HashMap<>();
            for(Seat s: selectedSeats) {
                // seatsText += "\t• Car " + s.getCarNo() + " - " + SeatService.toSeatCode(s.getSeatIndex()) + "<br>";
                List<String> seats = carBucket.get(s.getCarNo());
                if(seats == null) {
                    seats = new ArrayList<>();
                    carBucket.put(s.getCarNo(), seats);
                }
                seats.add(SeatService.toSeatCode(s.getSeatIndex()));
            }

            for(Map.Entry<Integer, List<String>> entry: carBucket.entrySet()) {
                seatsText += "<p style='margin-bottom: 5px;'>• Car " + entry.getKey() + " - ";
                List<String> seats = entry.getValue();
                for(String seat: seats) {
                    if(seat != seats.get(0)) seatsText += ", ";
                    seatsText += seat;
                }
                seatsText += "</p>";
            }
        }
        seatsText += "</html>";
        seatsLabel.setText(seatsText);

        String ticketsText = "Tickets: " + selectedSeats.size();
        totalTicketsLabel.setText(ticketsText);

        String totalPriceText = "Total: " + CurrencyService.formatVnd(selectedSeats.size() * SeatService.getPricePerTicket());
        totalPriceLabel.setText(totalPriceText);
    }

    private JScrollPane createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 50));

        int fontSize = 16;
        int verticalStrut = 10;

        panel.add(UIFactory.createPlainLabel("Selected seats:", fontSize));
        panel.add(Box.createVerticalStrut(verticalStrut));

        seatsLabel = UIFactory.createPlainLabel("", fontSize);
        panel.add(seatsLabel);
        panel.add(Box.createVerticalStrut(verticalStrut));
        
        totalTicketsLabel = UIFactory.createPlainLabel("", fontSize);
        panel.add(totalTicketsLabel);
        panel.add(Box.createVerticalStrut(verticalStrut));

        panel.add(UIFactory.createPlainLabel("Price per ticket: " + CurrencyService.formatVnd(SeatService.getPricePerTicket()), fontSize));
        
        panel.add(Box.createVerticalStrut(verticalStrut));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(verticalStrut));

        totalPriceLabel = UIFactory.createBoldLabel("", fontSize);
        panel.add(totalPriceLabel);
        panel.add(Box.createVerticalStrut(verticalStrut));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(500, 0));
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Universal.BACKGROUND_BLACK, 2, true), 
            " Booking Information ", 
            TitledBorder.CENTER, 
            TitledBorder.TOP, 
            UIFactory.createDefaultPlainFont(fontSize)
        ));
        
        refreshSeatStatus();
        return scrollPane;
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(300, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        Dimension legendBtnDim = new Dimension(25, 25);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JToggleButton availableSeat = parent.createSeatToggleButton();
        availableSeat.setEnabled(false);
        availableSeat.setPreferredSize(legendBtnDim);
        panel.add(parent.createSeatBorderWrapper(availableSeat), gbc);

        gbc.gridx++;
        panel.add(UIFactory.createPlainLabel("Available", 16), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JToggleButton yourSeat = parent.createSeatToggleButton();
        parent.markSeatMine(yourSeat);
        yourSeat.setPreferredSize(legendBtnDim);
        panel.add(parent.createSeatBorderWrapper(yourSeat), gbc);

        gbc.gridx++;
        panel.add(UIFactory.createPlainLabel("Your seats", 16), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JToggleButton bookedSeat = parent.createSeatToggleButton();
        parent.markSeatBooked(bookedSeat);
        bookedSeat.setPreferredSize(legendBtnDim);
        panel.add(parent.createSeatBorderWrapper(bookedSeat), gbc);

        gbc.gridx++;
        panel.add(UIFactory.createPlainLabel("Booked", 16), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JToggleButton boardedSeat = parent.createSeatToggleButton();
        parent.markSeatBoarded(boardedSeat);
        boardedSeat.setPreferredSize(legendBtnDim);
        panel.add(parent.createSeatBorderWrapper(boardedSeat), gbc);

        gbc.gridx++;
        panel.add(UIFactory.createPlainLabel("Boarded", 16), gbc);

        return panel;
    }
}

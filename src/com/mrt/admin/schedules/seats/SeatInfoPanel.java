package com.mrt.admin.schedules.seats;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.models.Schedule;
import com.mrt.models.Seat;
import com.mrt.models.User;
import com.mrt.services.UserService;

public class SeatInfoPanel extends JPanel {

    private SeatViewPanel parent;
    private JLabel seatsLabel;

    public SeatInfoPanel(SeatViewPanel parent) {
        this.parent = parent;

        setOpaque(false);
        setPreferredSize(new Dimension(0, 220));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));

        add(createLegendPanel(), BorderLayout.WEST);
        add(createInfoPanel(), BorderLayout.EAST);
    }

    public void refreshSeatStatus() {
        Schedule selectedSchedule = parent.getSelectedSchedule();
        Seat selectedSeat = parent.getSelectedSeat();

        String seatsText = "<html>";
        if(selectedSeat == null) {
            seatsText += "(No seat selected)";
        }
        else {
            // Map<Integer, List<String>> carBucket = new HashMap<>();
            // for(Seat s: selectedSeats) {
            //     // seatsText += "\t• Car " + s.getCarNo() + " - " + SeatService.toSeatCode(s.getSeatIndex()) + "<br>";
            //     List<String> seats = carBucket.get(s.getCarNo());
            //     if(seats == null) {
            //         seats = new ArrayList<>();
            //         carBucket.put(s.getCarNo(), seats);
            //     }
            //     seats.add(SeatService.toSeatCode(s.getSeatIndex()));
            // }

            // for(Map.Entry<Integer, List<String>> entry: carBucket.entrySet()) {
            //     seatsText += "<p style='margin-bottom: 5px;'>• Car " + entry.getKey() + " - ";
            //     List<String> seats = entry.getValue();
            //     for(String seat: seats) {
            //         if(seat != seats.get(0)) seatsText += ", ";
            //         seatsText += seat;
            //     }
            //     seatsText += "</p>";
            // }
            User user = UserService.getUserBySeat(selectedSchedule, selectedSeat);
            if(user != null) {
                seatsText += user.getFullName();
                seatsText += " • ";
                seatsText += user.getEmail();
            }
            else {
                seatsText += "Empty seat";
            }
        }
        seatsText += "</html>";
        seatsLabel.setText(seatsText);
    }

    private JScrollPane createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 50));

        int fontSize = 16;

        panel.add(UIFactory.createPlainLabel("User Info:", fontSize));

        seatsLabel = UIFactory.createPlainLabel("", fontSize);
        panel.add(seatsLabel);

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
        JButton availableSeat = parent.createSeatButton();
        availableSeat.setEnabled(false);
        availableSeat.setPreferredSize(legendBtnDim);
        panel.add(parent.createSeatBorderWrapper(availableSeat), gbc);

        gbc.gridx++;
        panel.add(UIFactory.createPlainLabel("Available", 16), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JButton bookedSeat = parent.createSeatButton();
        parent.markSeatBooked(bookedSeat);
        bookedSeat.setPreferredSize(legendBtnDim);
        panel.add(parent.createSeatBorderWrapper(bookedSeat), gbc);

        gbc.gridx++;
        panel.add(UIFactory.createPlainLabel("Booked", 16), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JButton boardedSeat = parent.createSeatButton();
        parent.markSeatBoarded(boardedSeat);
        boardedSeat.setPreferredSize(legendBtnDim);
        panel.add(parent.createSeatBorderWrapper(boardedSeat), gbc);

        gbc.gridx++;
        panel.add(UIFactory.createPlainLabel("Boarded", 16), gbc);

        return panel;
    }
}

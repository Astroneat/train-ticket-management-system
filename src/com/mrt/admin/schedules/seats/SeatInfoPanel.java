package com.mrt.admin.schedules.seats;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.models.Route;
import com.mrt.models.Schedule;
import com.mrt.models.Seat;
import com.mrt.models.Ticket;
import com.mrt.models.Train;
import com.mrt.models.User;
import com.mrt.services.RouteService;
import com.mrt.services.SeatService;
import com.mrt.services.TicketService;
import com.mrt.services.TrainService;
import com.mrt.services.UserService;

public class SeatInfoPanel extends JPanel {

    private JFrame ancestorFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

    private SeatViewPanel parent;
    private Schedule schedule;
    private Route route;
    private Train train;

    private JLabel passengerLabel;
    private JLabel seatLabel;

    private JButton createBtn;
    private JButton boardBtn;
    private JButton cancelBtn;

    public SeatInfoPanel(SeatViewPanel parent) {
        this.parent = parent;
        this.schedule = parent.getSelectedSchedule();
        this.route = RouteService.getRouteById(schedule.getRouteId());
        this.train = TrainService.getTrainById(schedule.getTrainId());

        setOpaque(false);
        setPreferredSize(new Dimension(0, 220));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));

        add(createLegendPanel(), BorderLayout.WEST);
        add(createInfoActionPanel(), BorderLayout.CENTER);
        refreshSeatStatus();
    }

    public void refreshSeatStatus() {
        Seat selectedSeat = parent.getSelectedSeat();

        String passengerText = "", seatText = "";
        if(selectedSeat == null) {
            passengerText += "(No seat selected)";
            seatText = "(No seat selected)";

            createBtn.setEnabled(false);
            boardBtn.setEnabled(false);
            cancelBtn.setEnabled(false);
        }
        else {
            User user = UserService.getUserBySeat(schedule, selectedSeat);
            if(user != null) {
                passengerText += user.getFullName();
                passengerText += " • ";
                passengerText += user.getEmail();

                createBtn.setEnabled(false);
                boardBtn.setEnabled(true);
                cancelBtn.setEnabled(true);
            }
            else {
                passengerText += "Empty seat";

                createBtn.setEnabled(true);
                boardBtn.setEnabled(false);
                cancelBtn.setEnabled(false);
            }

            seatText = "Car %d - %s".formatted(
                selectedSeat.getCarNo(), 
                SeatService.toSeatCode(selectedSeat.getSeatIndex())
            );
        }
        passengerLabel.setText(passengerText);
        seatLabel.setText(seatText);
    }

    private JPanel createInfoActionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        panel.add(createInfoPanel(), BorderLayout.CENTER);
        panel.add(createActionPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 50));

        int fontSize = 16;

        panel.add(createRow(
            UIFactory.createBoldLabel("Route:", fontSize),
            UIFactory.createPlainLabel(route.getRouteSummary(), fontSize)
        ));

        panel.add(createRow(
            UIFactory.createBoldLabel("Train:", fontSize),
            UIFactory.createPlainLabel(train.getTrainSummary(), fontSize)
        ));

        passengerLabel = UIFactory.createPlainLabel("", fontSize);
        panel.add(createRow(
            UIFactory.createBoldLabel("Passenger:", fontSize),
            passengerLabel
        ));

        seatLabel = UIFactory.createPlainLabel("", fontSize);
        panel.add(createRow(
            UIFactory.createBoldLabel("Seat:", fontSize),
            seatLabel
        ));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        // scrollPane.setPreferredSize(new Dimension(600, 0));
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Universal.BACKGROUND_BLACK, 2, true), 
            " Booking Information ", 
            TitledBorder.CENTER, 
            TitledBorder.TOP, 
            UIFactory.createDefaultPlainFont(fontSize)
        ));
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scrollPane, BorderLayout.CENTER);
        wrapper.setOpaque(false);
        return wrapper;
    }

    private JPanel createRow(Component... components) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        for(Component c: components) {
            panel.add(c);
        }
        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        Dimension btnDim = new Dimension(120, 36);

        createBtn = UIFactory.createButton("Create Ticket");
        createBtn.setPreferredSize(btnDim);
        createBtn.setEnabled(false);
        createBtn.addActionListener(e -> {
            CreateTicketDialog dialog = new CreateTicketDialog(
                ancestorFrame, 
                schedule, 
                parent.getSelectedSeat()
            );
            dialog.setVisible(true);
            parent.refreshSeatInfo();
            parent.refreshCarPanel();
        });
        panel.add(createBtn);

        boardBtn = UIFactory.createButton("Board Ticket");
        boardBtn.setPreferredSize(btnDim);
        boardBtn.setEnabled(false);
        boardBtn.addActionListener(e -> {
            Ticket tk = TicketService.getTicketByScheduleAndSeat(schedule, parent.getSelectedSeat());
            if(!tk.getStatus().equals("booked")) {
                JOptionPane.showMessageDialog(
                    ancestorFrame, 
                    "Only booked tickets may be boarded",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            int option = JOptionPane.showConfirmDialog(
                ancestorFrame, 
                "Mark this ticket as boarded?",
                "Confirm boarding",
                JOptionPane.YES_NO_OPTION
            );

            if(option == JOptionPane.YES_OPTION) {
                TicketService.boardTicket(tk.getTicketId());
                parent.refreshCarPanel();
                parent.refreshSeatInfo();
            }
        });
        panel.add(boardBtn);

        cancelBtn = UIFactory.createButton("Cancel Ticket");
        cancelBtn.setPreferredSize(btnDim);
        cancelBtn.setEnabled(false);
        cancelBtn.addActionListener(e -> {
            Ticket tk = TicketService.getTicketByScheduleAndSeat(schedule, parent.getSelectedSeat());
            if(!tk.getStatus().equals("booked")) {
                JOptionPane.showMessageDialog(
                    ancestorFrame, 
                    "Only booked tickets may be cancelled",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            int option = JOptionPane.showConfirmDialog(
                ancestorFrame, 
                "Force cancel this ticket? (This action cannot be undone)",
                "Confirm cancellation",
                JOptionPane.YES_NO_OPTION
            );

            if(option == JOptionPane.YES_OPTION) {
                TicketService.cancelTicket(tk.getTicketId());
                parent.refreshCarPanel();
                parent.refreshSeatInfo();
            }
        });
        panel.add(cancelBtn);

        return panel;
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

        gbc.gridx = 0;
        gbc.gridy++;
        JButton expiredSeat = parent.createSeatButton();
        parent.markSeatExpired(expiredSeat);
        expiredSeat.setPreferredSize(legendBtnDim);
        panel.add(parent.createSeatBorderWrapper(expiredSeat), gbc);

        gbc.gridx++;
        panel.add(UIFactory.createPlainLabel("Expired", 16), gbc);

        return panel;
    }
}

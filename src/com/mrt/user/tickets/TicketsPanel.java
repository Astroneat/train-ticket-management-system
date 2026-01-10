package com.mrt.user.tickets;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.models.User;
import com.mrt.user.schedules.Page;

public class TicketsPanel extends JPanel implements Page {

    private TicketViewPanel ticketViewPanel;
    private JPanel numTicketsPanel;

    private JLabel bookedLabel;
    private JLabel boardedLabel;
    private JLabel cancelledLabel;
    private JLabel expiredLabel;
    
    public TicketsPanel(User user) {
        setBackground(Universal.BACKGROUND_WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createTitlePanel());
        ticketViewPanel = new TicketViewPanel(user);
        add(ticketViewPanel);
        updateTicketCounts();
    }

    private void updateTicketCounts() {
        bookedLabel.setText("Booked: " + ticketViewPanel.getNumberOfBookedTickets());
        boardedLabel.setText("Boarded: " + ticketViewPanel.getNumberOfBoardedTickets());
        cancelledLabel.setText("Cancelled: " + ticketViewPanel.getNumberOfCancelledTickets());
        expiredLabel.setText("Expired: " + ticketViewPanel.getNumberOfExpiredTickets());
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setOpaque(false);

        panel.add(UIFactory.createBoldLabel("My Tickets", 28));
        panel.add(Box.createHorizontalStrut(20));

        JButton refreshBtn = UIFactory.createIconButton("src/com/mrt/img/refresh.png", new Dimension(14, 14));
        refreshBtn.setPreferredSize(new Dimension(40, 40));
        refreshBtn.addActionListener(e -> {
            refreshPage();
        });
        panel.add(refreshBtn);

        numTicketsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        numTicketsPanel.setOpaque(false);
        panel.add(Box.createHorizontalStrut(20));

        int fontSize = 16;
        numTicketsPanel.add(UIFactory.createBoldLabel("Total Tickets:", fontSize));
        bookedLabel = UIFactory.createPlainLabel("", fontSize);
        numTicketsPanel.add(bookedLabel);
        numTicketsPanel.add(UIFactory.createPlainLabel("|", fontSize));
        boardedLabel = UIFactory.createPlainLabel("", fontSize);
        numTicketsPanel.add(boardedLabel);
        numTicketsPanel.add(UIFactory.createPlainLabel("|", fontSize));
        cancelledLabel = UIFactory.createPlainLabel("", fontSize);
        numTicketsPanel.add(cancelledLabel);
        numTicketsPanel.add(UIFactory.createPlainLabel("|", fontSize));
        expiredLabel = UIFactory.createPlainLabel("", fontSize);
        numTicketsPanel.add(expiredLabel);

        panel.add(numTicketsPanel);

        return panel;
    }

    public void refreshPage() {
        ticketViewPanel.refreshTickets();
        updateTicketCounts();
    }
}

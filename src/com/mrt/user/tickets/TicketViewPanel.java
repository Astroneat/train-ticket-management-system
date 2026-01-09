package com.mrt.user.tickets;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.models.User;

public class TicketViewPanel extends JPanel {

    private User user;
    private CardLayout cardLayout;
    private JPanel ticketListPanel;

    private TicketListScrollPane bookedScrollPane;
    private TicketListScrollPane boardedScrollPane;
    private TicketListScrollPane cancelledScrollPane;
    private TicketListScrollPane expiredScrollPane;

    private JButton activeBtn;
    
    public TicketViewPanel(User user) {
        this.user = user;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createLineBorder(Universal.BACKGROUND_BLACK, 2, true));

        add(createTicketListPanel(), BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.EAST);
    }

    public int getNumberOfBookedTickets() {
        return bookedScrollPane.getNumberOfTickets();
    }
    public int getNumberOfBoardedTickets() {
        return boardedScrollPane.getNumberOfTickets();
    }
    public int getNumberOfCancelledTickets() {
        return cancelledScrollPane.getNumberOfTickets();
    }
    public int getNumberOfExpiredTickets() {
        return expiredScrollPane.getNumberOfTickets();
    }

    public void refreshTickets() {
        bookedScrollPane.showTickets(TicketStatus.BOOKED);
        boardedScrollPane.showTickets(TicketStatus.BOARDED);
        cancelledScrollPane.showTickets(TicketStatus.CANCELLED);
        expiredScrollPane.showTickets(TicketStatus.EXPIRED);
    }

    private JPanel createTicketListPanel() {
        cardLayout = new CardLayout();
        ticketListPanel = new JPanel(cardLayout);

        bookedScrollPane = new TicketListScrollPane(user, TicketStatus.BOOKED);
        boardedScrollPane = new TicketListScrollPane(user, TicketStatus.BOARDED);
        cancelledScrollPane = new TicketListScrollPane(user, TicketStatus.CANCELLED);
        expiredScrollPane = new TicketListScrollPane(user, TicketStatus.EXPIRED);

        ticketListPanel.add(bookedScrollPane, TicketStatus.BOOKED.getStatus());
        ticketListPanel.add(boardedScrollPane, TicketStatus.BOARDED.getStatus());
        ticketListPanel.add(cancelledScrollPane, TicketStatus.CANCELLED.getStatus());
        ticketListPanel.add(expiredScrollPane, TicketStatus.EXPIRED.getStatus());

        cardLayout.show(ticketListPanel, TicketStatus.BOOKED.getStatus());
        return ticketListPanel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Universal.BACKGROUND_BLACK);
        panel.setPreferredSize(new Dimension(150, 0));
        // panel.setOpaque(false);

        JButton bookedBtn = createActionButton("Booked");
        setActiveBtn(bookedBtn);
        bookedBtn.addActionListener(e -> {
            cardLayout.show(ticketListPanel, TicketStatus.BOOKED.getStatus());
            setActiveBtn(bookedBtn);
        });
        panel.add(bookedBtn);

        JButton boardedBtn = createActionButton("Boarded");
        boardedBtn.addActionListener(e -> {
            cardLayout.show(ticketListPanel, TicketStatus.BOARDED.getStatus());
            setActiveBtn(boardedBtn);
        });
        panel.add(boardedBtn);

        JButton cancelledBtn = createActionButton("Cancelled");
        cancelledBtn.addActionListener(e -> {
            cardLayout.show(ticketListPanel, TicketStatus.CANCELLED.getStatus());
            setActiveBtn(cancelledBtn);
        });
        panel.add(cancelledBtn);

        JButton expiredBtn = createActionButton("Expired");
        expiredBtn.addActionListener(e -> {
            cardLayout.show(ticketListPanel, TicketStatus.EXPIRED.getStatus());
            setActiveBtn(expiredBtn);
        });
        panel.add(expiredBtn);

        return panel;
    }

    private void setActiveBtn(JButton btn) {
        if (activeBtn != null) {
            activeBtn.setBackground(Universal.BACKGROUND_BLACK);
        }
        activeBtn = btn;
        activeBtn.setBackground(Color.BLACK);
    }

    private JButton createActionButton(String title) {
        JButton btn = UIFactory.createButton(title);
        btn.setFont(UIFactory.createDefaultPlainFont(18));
        btn.setForeground(Universal.BACKGROUND_WHITE);
        btn.setBackground(Universal.BACKGROUND_BLACK);
        btn.setBorder(null);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(1000, 50));
        btn.setAlignmentX(CENTER_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(Color.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != activeBtn) {
                    btn.setBackground(Universal.BACKGROUND_BLACK);
                }
            }
        });
        return btn;
    }
}

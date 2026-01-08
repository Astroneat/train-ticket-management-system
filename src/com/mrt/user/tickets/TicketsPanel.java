package com.mrt.user.tickets;

import javax.swing.JPanel;

import com.mrt.models.User;

public class TicketsPanel extends JPanel {

    private User user;
    
    public TicketsPanel(User user) {
        this.user = user;
    }
}

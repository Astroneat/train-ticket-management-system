package com.mrt.user.home;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.frames.Page;
import com.mrt.frames.UserFrame;
import com.mrt.models.User;

public class HomePanel extends JPanel implements Page {

    private UserFrame userFrame;
    private User user;

    public HomePanel(UserFrame userFrame, User user) {
        setLayout(new BorderLayout());
        setBackground(Universal.BACKGROUND_WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        this.userFrame = userFrame;
        this.user = user;

        add(createWelcomePanel(), BorderLayout.NORTH);
        add(createActionPanel(), BorderLayout.CENTER);
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.add(UIFactory.createPlainLabel("<html>Welcome back, <strong>" + user.getFullName() + "</strong></html>", 40));
        return panel;   
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        JButton searchBtn = UIFactory.createButton("Search New Schedules");
        searchBtn.setPreferredSize(new Dimension(200, 40));
        searchBtn.addActionListener(e -> {
            userFrame.goToPage(UserFrame.schedulesPanel);
        });
        panel.add(searchBtn);

        JButton viewBtn = UIFactory.createButton("View My Tickets");
        viewBtn.setPreferredSize(new Dimension(140, 40));
        viewBtn.addActionListener(e -> {
            userFrame.goToPage(UserFrame.ticketsPanel);
        });
        panel.add(viewBtn);

        JButton profileBtn = UIFactory.createButton("Edit Profile");
        profileBtn.setPreferredSize(new Dimension(140, 40));
        profileBtn.addActionListener(e -> {
            userFrame.goToPage(UserFrame.profilePanel);
        });
        panel.add(profileBtn);

        JButton feedbackBtn = UIFactory.createButton("Send Feedback");
        feedbackBtn.setPreferredSize(new Dimension(140, 40));
        feedbackBtn.addActionListener(e -> {
            userFrame.goToPage(UserFrame.feedbackPanel);
        });
        panel.add(feedbackBtn);

        return panel;
    }

    public void refreshPage() {
        
    }
}

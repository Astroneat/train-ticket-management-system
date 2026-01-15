package com.mrt.frames;
import javax.swing.*;

import com.mrt.models.User;
import com.mrt.user.feedback.FeedbackPanel;
import com.mrt.user.home.HomePanel;
import com.mrt.user.profile.ProfilePanel;
import com.mrt.user.schedules.SchedulesPanel;
import com.mrt.user.tickets.TicketsPanel;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class UserFrame extends JFrame implements MyFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private User currentUser;

    private SidebarPanel sidebar;
    private static final String HOME      = "HOME";
    private static final String SCHEDULES = "SCHEDULES";
    private static final String TICKETS   = "TICKETS";
    private static final String PROFILE   = "PROFILE";
    private static final String FEEDBACK  = "FEEDBACK";

    private JButton homeBtn;
    private JButton schedulesBtn;
    private JButton ticketsBtn;
    private JButton profileBtn;
    private JButton feedbackBtn;

    private Map<Page, String> pages = new HashMap<>();

    public static Page homePanel;
    public static Page schedulesPanel;
    public static Page ticketsPanel;
    public static Page profilePanel;
    public static Page feedbackPanel;

    public UserFrame(User user) {
        this.currentUser = user;

        setTitle("MRT Viet Nam");
        setSize(1200, 800);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        homePanel = new HomePanel(this, user);
        schedulesPanel = new SchedulesPanel(this, user);
        ticketsPanel = new TicketsPanel(user);
        profilePanel = new ProfilePanel(this, user);
        feedbackPanel = new FeedbackPanel(this);
        pages.put(homePanel, HOME);
        pages.put(schedulesPanel, SCHEDULES);
        pages.put(ticketsPanel, TICKETS);
        pages.put(profilePanel, PROFILE);
        pages.put(feedbackPanel, FEEDBACK);

        add(createSidebarPanel(), BorderLayout.WEST);
        add(new HeaderPanel(currentUser), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(HOME, (JPanel) homePanel);
        contentPanel.add(SCHEDULES, (JPanel) schedulesPanel);
        contentPanel.add(TICKETS, (JPanel) ticketsPanel);
        contentPanel.add(PROFILE, (JPanel) profilePanel);
        contentPanel.add(FEEDBACK, (JPanel) feedbackPanel);
        
        add(contentPanel, BorderLayout.CENTER);

        goToPage(homePanel);
    }

    public void goToPage(Page page) {
        if(pages.get(page) == null) return;
        String pageName = pages.get(page);
        page.refreshPage();
        cardLayout.show(contentPanel, pageName);

        switch(pageName) {
            case HOME:
                sidebar.setActiveSidebarButton(homeBtn);
                break;
            case SCHEDULES:
                sidebar.setActiveSidebarButton(schedulesBtn);
                break;
            case TICKETS:
                sidebar.setActiveSidebarButton(ticketsBtn);
                break;
            case PROFILE:
                sidebar.setActiveSidebarButton(profileBtn);
                break;
            case FEEDBACK:
                sidebar.setActiveSidebarButton(feedbackBtn);
                break;
        }
    }
    public void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }
    public JFrame getJFrame() {
        return this;
    }

    private JPanel createSidebarPanel() {
        sidebar = new SidebarPanel(this);

        homeBtn = sidebar.createSidebarButton("Home", "src/com/mrt/img/home.png", homePanel);
        sidebar.addToMenuPanel(homeBtn);
        schedulesBtn = sidebar.createSidebarButton("Search Schedules", "src/com/mrt/img/search_light.png", schedulesPanel);
        sidebar.addToMenuPanel(schedulesBtn);
        ticketsBtn = sidebar.createSidebarButton("My Tickets", "src/com/mrt/img/ticket.png", ticketsPanel);
        sidebar.addToMenuPanel(ticketsBtn);
        profileBtn = sidebar.createSidebarButton("Profile", "src/com/mrt/img/user.png", profilePanel);
        sidebar.addToMenuPanel(profileBtn);
        feedbackBtn = sidebar.createSidebarButton("Feedback", "src/com/mrt/img/chat.png", feedbackPanel);
        sidebar.addToMenuPanel(feedbackBtn);
        
        if(currentUser.getRole().equals("admin")) {
            int verticalStrut = 5;
            sidebar.addToMenuPanel(Box.createVerticalStrut(verticalStrut));
            sidebar.addToMenuPanel(new JSeparator());
            sidebar.addToMenuPanel(Box.createVerticalStrut(verticalStrut));

            JButton toAdmin = sidebar.createSidebarButton("Admin", "src/com/mrt/img/gears.png", null);
            toAdmin.addActionListener(e -> {
                dispose();
                new AdminFrame(currentUser).setVisible(true);
            });

            sidebar.addToMenuPanel(toAdmin);
        }

        sidebar.addToLogoutPanel(sidebar.createLogoutButton());

        return sidebar;
    }
}

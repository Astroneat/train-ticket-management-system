package com.mrt.frames;
import javax.swing.*;

import com.mrt.models.User;
import com.mrt.user.home.HomePanel;
import com.mrt.user.schedules.Page;
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

    private JButton homeBtn;
    private JButton schedulesBtn;
    private JButton ticketsBtn;
    private JButton profileBtn;

    private Map<Page, String> pages = new HashMap<>();

    public static Page homePanel;
    public static Page schedulesPanel;
    public static Page ticketsPanel;

    public UserFrame(User user) {
        this.currentUser = user;

        setTitle("MRT Viet Nam");
        setSize(1200, 800);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        homePanel = new HomePanel();
        schedulesPanel = new SchedulesPanel(this, user);
        ticketsPanel = new TicketsPanel(user);
        pages.put(homePanel, HOME);
        pages.put(schedulesPanel, SCHEDULES);
        pages.put(ticketsPanel, TICKETS);

        add(createSidebarPanel(), BorderLayout.WEST);
        add(new HeaderPanel(currentUser), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(HOME, (JPanel) homePanel);
        contentPanel.add(SCHEDULES, (JPanel) schedulesPanel);
        contentPanel.add(TICKETS, (JPanel) ticketsPanel);
        
        add(contentPanel, BorderLayout.CENTER);

        goToPage(ticketsPanel);
    }

    public void goToPage(Page page) {
        if(pages.get(page) == null) return;
        String pageName = pages.get(page);
        page.refreshPage();
        cardLayout.show(contentPanel, pageName);

        switch(pageName) {
            case "HOME":
                sidebar.setActiveSidebarButton(homeBtn);
                break;
            case "SCHEDULES":
                sidebar.setActiveSidebarButton(schedulesBtn);
                break;
            case "TICKETS":
                sidebar.setActiveSidebarButton(ticketsBtn);
                break;
            case "PROFILE":
                sidebar.setActiveSidebarButton(profileBtn);
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
        profileBtn = sidebar.createSidebarButton("Profile", "src/com/mrt/img/user.png", null);
        sidebar.addToMenuPanel(profileBtn);
        
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

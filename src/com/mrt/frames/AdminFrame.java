package com.mrt.frames;
import javax.swing.*;

import com.mrt.admin.dashboard.DashboardPanel;
import com.mrt.admin.reports.ReportsPanel;
import com.mrt.admin.routes.RouteManagementPanel;
import com.mrt.admin.stations.StationManagementPanel;
import com.mrt.admin.tickets.TicketManagementPanel;
import com.mrt.admin.trains.TrainManagementPanel;
import com.mrt.admin.users.UserManagementPanel;
import com.mrt.models.User;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;

public class AdminFrame extends JFrame implements MyFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private User currentUser;

    private static final String DASHBOARD = "DASHBOARD";
    private static final String TRAINS    = "TRAINS";
    private static final String STATIONS  = "STATIONS";
    private static final String ROUTES    = "ROUTES";
    private static final String TICKETS   = "TICKETS";
    private static final String USERS     = "USERS";
    private static final String REPORTS   = "REPORTS";

    private Map<Page, String> pages = new HashMap<>();
    private Page dashboard;
    private Page trains;
    private Page stations;
    private Page routes;
    private Page tickets;
    private Page users;
    private Page reports;

    public AdminFrame(User user) {
        this.currentUser = user;

        setTitle("MRT Viet Nam - Admin");
        setSize(1200, 800);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        dashboard = new DashboardPanel();
        trains = new TrainManagementPanel(this);
        stations = new StationManagementPanel(this);
        routes = new RouteManagementPanel(this);
        tickets = new TicketManagementPanel(this);
        users = new UserManagementPanel(this, user);
        reports = new ReportsPanel();

        pages.put(dashboard, DASHBOARD);
        pages.put(trains,    TRAINS);
        pages.put(stations,  STATIONS);
        pages.put(routes,    ROUTES);
        pages.put(tickets,   TICKETS);
        pages.put(users,     USERS);
        pages.put(reports,   REPORTS);

        add(createSidebarPanel(), BorderLayout.WEST);
        add(new HeaderPanel(currentUser), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(DASHBOARD, (JPanel) dashboard);
        contentPanel.add(TRAINS,    (JPanel) trains);
        contentPanel.add(STATIONS,  (JPanel) stations);
        contentPanel.add(ROUTES,    (JPanel) routes);
        contentPanel.add(TICKETS,   (JPanel) tickets);
        contentPanel.add(USERS,     (JPanel) users);
        contentPanel.add(REPORTS,   (JPanel) reports);
        
        add(contentPanel, BorderLayout.CENTER);
        // goToPage(dashboard);
        goToPage(reports);
    }

    public void goToPage(Page page) {
        if(pages.get(page) == null) return;
        String pageName = pages.get(page);
        page.refreshPage();
        cardLayout.show(contentPanel, pageName);
    } 
    public void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }
    public JFrame getJFrame() {
        return this;
    }

    private JPanel createSidebarPanel() {
        SidebarPanel sidebar = new SidebarPanel(this);

        JButton dashboardBtn = sidebar.createSidebarButton("Dashboard", "src/com/mrt/img/dashboard.png", dashboard);
        sidebar.addToMenuPanel(dashboardBtn);
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Trains", "src/com/mrt/img/train.png", trains));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Stations", "src/com/mrt/img/location.png", stations));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Routes", "src/com/mrt/img/route.png", routes));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Tickets", "src/com/mrt/img/ticket.png", tickets));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Users", "src/com/mrt/img/user.png", users));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Reports", "src/com/mrt/img/chart.png", reports));
        sidebar.setActiveSidebarButton(dashboardBtn);

        JButton toHome = sidebar.createSidebarButton("Back to Home", "src/com/mrt/img/home.png", null);
        toHome.addActionListener(e -> {
            dispose();
            new UserFrame(currentUser).setVisible(true);;
        });
        int verticalStrut = 5;
        sidebar.addToMenuPanel(Box.createVerticalStrut(verticalStrut));
        sidebar.addToMenuPanel(new JSeparator());
        sidebar.addToMenuPanel(Box.createVerticalStrut(verticalStrut));
        sidebar.addToMenuPanel(toHome);

        sidebar.addToLogoutPanel(sidebar.createLogoutButton());

        return sidebar;
    }
}

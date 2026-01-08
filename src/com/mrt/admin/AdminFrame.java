package com.mrt.admin;
import javax.swing.*;

import com.mrt.HeaderPanel;
import com.mrt.LoginFrame;
import com.mrt.MyFrame;
import com.mrt.SidebarPanel;
import com.mrt.Universal;
import com.mrt.admin.routes.RouteManagementPanel;
import com.mrt.admin.stations.StationManagementPanel;
import com.mrt.admin.tickets.TicketManagementPanel;
import com.mrt.admin.trains.TrainManagementPanel;
import com.mrt.admin.users.UserManagementPanel;
import com.mrt.models.User;
import com.mrt.user.UserFrame;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

    public AdminFrame(User user) {
        this.currentUser = user;

        setTitle("MRT Viet Nam - Admin");
        setSize(1200, 800);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        add(createSidebarPanel(), BorderLayout.WEST);
        add(new HeaderPanel(currentUser), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(DASHBOARD, new AdminDashboardPanel());
        contentPanel.add(TRAINS, new TrainManagementPanel(this));
        contentPanel.add(STATIONS, new StationManagementPanel(this));
        contentPanel.add(ROUTES, new RouteManagementPanel(this));
        contentPanel.add(TICKETS, new TicketManagementPanel(this));
        contentPanel.add(USERS, new UserManagementPanel(this, user));
        
        add(contentPanel, BorderLayout.CENTER);
        goToPage(DASHBOARD);
        // showPage("USERS");
        // showPage("TRAINS");
        // showPage("STATIONS");
        // showPage("ROUTES");
        // goToPage(TICKETS);
    }

    public void goToPage(String page) {
        cardLayout.show(contentPanel, page);
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

        JButton dashboardBtn = sidebar.createSidebarButton("Dashboard", "src/com/mrt/img/dashboard.png", DASHBOARD);
        sidebar.addToMenuPanel(dashboardBtn);
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Trains", "src/com/mrt/img/train.png", TRAINS));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Stations", "src/com/mrt/img/location.png", STATIONS));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Routes", "src/com/mrt/img/route.png", ROUTES));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Tickets", "src/com/mrt/img/ticket.png", TICKETS));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Users", "src/com/mrt/img/user.png", USERS));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Reports", "src/com/mrt/img/chart.png", REPORTS));
        sidebar.setActiveSidebarButton(dashboardBtn);

        JButton toHome = sidebar.createSidebarButton("Back to Home", "src/com/mrt/img/home.png", "");
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

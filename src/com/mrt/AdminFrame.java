package com.mrt;
import javax.swing.*;

import com.mrt.admin.AdminDashboardPanel;
import com.mrt.admin.TrainManagementPanel;
import com.mrt.admin.UserManagementPanel;
import com.mrt.model.User;

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

        contentPanel.add("DASHBOARD", new AdminDashboardPanel());
        contentPanel.add("USERS", new UserManagementPanel(this, user));
        contentPanel.add("TRAINS", new TrainManagementPanel(this));
        
        add(contentPanel, BorderLayout.CENTER);
        // showPage("DASHBOARD");
        // showPage("USERS");
        showPage("TRAINS");
    }

    public void showPage(String page) {
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

        JButton dashboardBtn = sidebar.createSidebarButton("Dashboard", "src/com/mrt/img/dashboard.png", "DASHBOARD");
        sidebar.addToMenuPanel(dashboardBtn);
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Trains", "src/com/mrt/img/train.png", "TRAINS"));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Routes", "src/com/mrt/img/route.png", "ROUTES"));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Tickets", "src/com/mrt/img/ticket.png", "TICKETS"));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Users", "src/com/mrt/img/user.png", "USERS"));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Reports", "src/com/mrt/img/chart.png", "REPORTS"));
        sidebar.setActiveSidebarButton(dashboardBtn);
        
        JButton toHome = new JButton("Back to Home");
        toHome.setAlignmentX(CENTER_ALIGNMENT);
        toHome.setPreferredSize(new Dimension(500, 50));
        toHome.setMaximumSize(new Dimension(500, 50));
        toHome.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 16));

        toHome.setBorderPainted(false);
        toHome.setFocusPainted(false);
        toHome.setForeground(Universal.BACKGROUND_WHITE);
        toHome.setBackground(Universal.BACKGROUND_BLACK);
        toHome.setOpaque(true);

        toHome.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                toHome.setBackground(Color.BLACK);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                toHome.setBackground(Universal.BACKGROUND_BLACK);
            }
        });

        toHome.addActionListener(e -> {
            dispose();
            new MainFrame(currentUser).setVisible(true);;
        });
        sidebar.addToLogoutPanel(toHome);
        sidebar.addToLogoutPanel(sidebar.createLogoutButton());

        return sidebar;
    }
}

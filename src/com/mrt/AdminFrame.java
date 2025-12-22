package com.mrt;
import javax.swing.*;

import com.mrt.admin.UserManagementPanel;

import java.awt.*;

public class AdminFrame extends JFrame implements MyFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private User currentUser;

    public AdminFrame(User user) {
        this.currentUser = user;

        setTitle("MRT Viet Nam - Admin");
        setSize(1100, 700);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        add(createSidebarPanel(), BorderLayout.WEST);
        add(new HeaderPanel(currentUser), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add("USERS", new UserManagementPanel());
        
        add(contentPanel, BorderLayout.CENTER);
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

        int verticalStrut = 10;
        sidebar.addToMenuPanel(Box.createVerticalStrut(verticalStrut));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Dashboard", "DASHBOARD"));
        sidebar.addToMenuPanel(Box.createVerticalStrut(verticalStrut));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Users", "USERS"));
        sidebar.addToMenuPanel(Box.createVerticalStrut(verticalStrut));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Trains", "TRAINS"));
        sidebar.addToMenuPanel(Box.createVerticalStrut(verticalStrut));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Routes", "ROUTES"));
        sidebar.addToMenuPanel(Box.createVerticalStrut(verticalStrut));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Train-Route Assignment", "TRAIN-ROUTE"));
        sidebar.addToMenuPanel(Box.createVerticalStrut(verticalStrut));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Tickets", "TICKETS"));
        sidebar.addToMenuPanel(Box.createVerticalStrut(verticalStrut));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Reports & Analytics", "REPORTS"));
        
        JButton toHome = new JButton("Back to Home");
        toHome.setAlignmentX(CENTER_ALIGNMENT);
        toHome.setPreferredSize(new Dimension(180, 40));
        toHome.setMaximumSize(new Dimension(180, 40));
        toHome.setBackground(Universal.SKYBLUE);
        toHome.addActionListener(e -> {
            dispose();
            new MainFrame(currentUser).setVisible(true);;
        });
        sidebar.addToLogoutPanel(toHome);
        sidebar.addToLogoutPanel(Box.createVerticalStrut(verticalStrut));
        sidebar.addToLogoutPanel(sidebar.createLogoutButton());
        sidebar.addToLogoutPanel(Box.createVerticalStrut(verticalStrut));

        return sidebar;
    }
}

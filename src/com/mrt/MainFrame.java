package com.mrt;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame implements MyFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private User currentUser;

    public MainFrame(User user) {
        this.currentUser = user;

        setTitle("MRT Viet Nam");
        setSize(1100, 700);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        add(createSidebarPanel(), BorderLayout.WEST);
        add(new HeaderPanel(currentUser), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        add(contentPanel, BorderLayout.CENTER);

        showPage("HOME");
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
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Home", "HOME"));
        sidebar.addToMenuPanel(Box.createVerticalStrut(verticalStrut));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Search Trains", "SEARCH"));
        sidebar.addToMenuPanel(Box.createVerticalStrut(verticalStrut));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("My Tickets", "TICKETS"));
        sidebar.addToMenuPanel(Box.createVerticalStrut(verticalStrut));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Profile", "PROFILE"));

        if(currentUser.getRole().equals("admin")) {
            sidebar.addToMenuPanel(Box.createVerticalStrut(verticalStrut));
            sidebar.addToMenuPanel(new JSeparator());
            sidebar.addToMenuPanel(Box.createVerticalStrut(verticalStrut));

            JButton toAdmin = new JButton("Admin");
            toAdmin.setAlignmentX(CENTER_ALIGNMENT);
            toAdmin.setPreferredSize(new Dimension(180, 40));
            toAdmin.setMaximumSize(new Dimension(180, 40));
            toAdmin.setBackground(Universal.SKYBLUE);
            toAdmin.addActionListener(e -> {
                dispose();
                new AdminFrame(currentUser).setVisible(true);
            });
            sidebar.addToMenuPanel(toAdmin);
        }

        sidebar.addToLogoutPanel(sidebar.createLogoutButton());
        sidebar.addToLogoutPanel(Box.createVerticalStrut(verticalStrut));

        return sidebar;
    }
}

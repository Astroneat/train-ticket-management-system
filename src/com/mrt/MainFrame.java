package com.mrt;
import javax.swing.*;

import com.mrt.admin.AdminMainFrame;
import com.mrt.model.User;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Home", "src/com/mrt/img/home.png", "HOME"));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Search Trains", "src/com/mrt/img/train.png", "TRAINS"));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("My Tickets", "src/com/mrt/img/ticket.png", "TICKETS"));
        sidebar.addToMenuPanel(sidebar.createSidebarButton("Profile", "src/com/mrt/img/user.png", "PROFILE"));

        if(currentUser.getRole().equals("admin")) {
            sidebar.addToMenuPanel(Box.createVerticalStrut(verticalStrut));
            sidebar.addToMenuPanel(new JSeparator());
            sidebar.addToMenuPanel(Box.createVerticalStrut(verticalStrut));

            JButton toAdmin = new JButton("Admin");
            toAdmin.setAlignmentX(CENTER_ALIGNMENT);
            toAdmin.setPreferredSize(new Dimension(500, 50));
            toAdmin.setMaximumSize(new Dimension(500, 50));

            toAdmin.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 16));

            toAdmin.setBorderPainted(false);
            toAdmin.setFocusPainted(false);
            toAdmin.setForeground(Universal.BACKGROUND_WHITE);
            toAdmin.setBackground(Universal.BACKGROUND_BLACK);
            toAdmin.setOpaque(true);

            toAdmin.addActionListener(e -> {
                dispose();
                new AdminMainFrame(currentUser).setVisible(true);
            });
            toAdmin.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    toAdmin.setBackground(Color.BLACK);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    toAdmin.setBackground(Universal.BACKGROUND_BLACK);
                }
            });
            sidebar.addToMenuPanel(toAdmin);
        }

        sidebar.addToLogoutPanel(sidebar.createLogoutButton());

        return sidebar;
    }
}

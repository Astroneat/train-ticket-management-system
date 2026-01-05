package com.mrt.user;
import javax.swing.*;

import com.mrt.HeaderPanel;
import com.mrt.LoginFrame;
import com.mrt.MyFrame;
import com.mrt.SidebarPanel;
import com.mrt.Universal;
import com.mrt.admin.AdminFrame;
import com.mrt.model.User;
import com.mrt.user.schedules.ScheduleSearchPanel;
import com.mrt.user.schedules.SchedulesPanel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

    public UserFrame(User user) {
        this.currentUser = user;

        setTitle("MRT Viet Nam");
        setSize(1200, 800);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        add(createSidebarPanel(), BorderLayout.WEST);
        add(new HeaderPanel(currentUser), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(HOME, new UserHomePanel());
        contentPanel.add(SCHEDULES, new SchedulesPanel());
        
        add(contentPanel, BorderLayout.CENTER);

        // showPage("HOME");
        goToPage(SCHEDULES);
    }

    public void goToPage(String page) {
        cardLayout.show(contentPanel, page);

        switch(page) {
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

        int verticalStrut = 10;
        homeBtn = sidebar.createSidebarButton("Home", "src/com/mrt/img/home.png", HOME);
        sidebar.addToMenuPanel(homeBtn);
        schedulesBtn = sidebar.createSidebarButton("Search Schedules", "src/com/mrt/img/search_light.png", SCHEDULES);
        sidebar.addToMenuPanel(schedulesBtn);
        ticketsBtn = sidebar.createSidebarButton("My Tickets", "src/com/mrt/img/ticket.png", TICKETS);
        sidebar.addToMenuPanel(ticketsBtn);
        profileBtn = sidebar.createSidebarButton("Profile", "src/com/mrt/img/user.png", PROFILE);
        sidebar.addToMenuPanel(profileBtn);

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
                new AdminFrame(currentUser).setVisible(true);
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

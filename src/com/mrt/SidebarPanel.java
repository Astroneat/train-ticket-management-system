package com.mrt;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SidebarPanel extends JPanel {
    private MyFrame frame;
    private JPanel menuPanel;
    private JPanel logoutPanel;

    public SidebarPanel(MyFrame frame) {
        this.frame = frame;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 0));
        setBackground(Universal.BACKGROUND_BLACK);

        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);

        logoutPanel = new JPanel();
        logoutPanel.setLayout(new BoxLayout(logoutPanel, BoxLayout.Y_AXIS));
        logoutPanel.setOpaque(false);

        add(menuPanel, BorderLayout.NORTH);
        add(logoutPanel, BorderLayout.SOUTH);
    }

    public void addToMenuPanel(Component comp) {
        menuPanel.add(comp);
    }
    public void addToLogoutPanel(Component comp) {
        logoutPanel.add(comp);
    }

    public JButton createSidebarButton(String text, String toPage) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        btn.setBackground(Universal.SKYBLUE);
        btn.addActionListener(e -> {
            frame.showPage(toPage);
        });
        return btn;
    }

    public JButton createLogoutButton() {
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        logoutButton.setAlignmentX(CENTER_ALIGNMENT);
        logoutButton.setPreferredSize(new Dimension(180, 40));
        logoutButton.setMaximumSize(new Dimension(180, 40));
        logoutButton.setForeground(Color.RED);
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                frame.getJFrame(), 
                "Are you sure you want to logout?",
                "Confirm logout",
                JOptionPane.YES_NO_OPTION
            );
            
            if(choice == JOptionPane.YES_OPTION) {
                frame.logout();
            }
        });

        return logoutButton;
    }
}   

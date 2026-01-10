package com.mrt.frames;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.mrt.Universal;
import com.mrt.user.schedules.Page;

public class SidebarPanel extends JPanel {

    private MyFrame frame;
    private JPanel menuPanel;
    private JPanel logoutPanel;

    private JButton activeSidebarButton;

    public SidebarPanel(MyFrame frame) {
        this.frame = frame;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 0));
        setBackground(Universal.BACKGROUND_BLACK);

        activeSidebarButton = new JButton();

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
    public void setActiveSidebarButton(JButton btn) {
        // activeSidebarButton = btn;
        // activeSidebarButton.setBackground(Color.BLACK);
        activeSidebarButton.setBackground(Universal.BACKGROUND_BLACK);
        btn.setBackground(Color.BLACK);
        activeSidebarButton = btn;
    }

    public JButton createSidebarButton(String text, String imageFileName, Page toPage) {
        JButton btn;
        if(imageFileName.isBlank()) {
            btn = new JButton(text);
        } else {
            ImageIcon icon = new ImageIcon(imageFileName);
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
            
            btn = new JButton(text, new ImageIcon(newImg));
            btn.setIconTextGap(10);
        }
        btn.setAlignmentX(CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setPreferredSize(new Dimension(500, 50));
        btn.setMaximumSize(new Dimension(500, 50));
        btn.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 16));
        
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setForeground(Universal.BACKGROUND_WHITE);
        btn.setBackground(Universal.BACKGROUND_BLACK);
        btn.setOpaque(true);
        
        btn.addActionListener(e -> {
            if(toPage == null) return;
            frame.goToPage(toPage);
        });
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(Color.BLACK);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if(activeSidebarButton != btn) {
                    btn.setBackground(Universal.BACKGROUND_BLACK);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if(btn.getMousePosition() != null) {
                    setActiveSidebarButton(btn);
                }
            }
        });
        return btn;
    }

    public JButton createLogoutButton() {
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 16));
        logoutButton.setAlignmentX(CENTER_ALIGNMENT);
        logoutButton.setPreferredSize(new Dimension(500, 50));
        logoutButton.setMaximumSize(new Dimension(500, 50));

        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setForeground(Color.RED);
        logoutButton.setBackground(Universal.BACKGROUND_BLACK);
        logoutButton.setOpaque(true);

        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutButton.setBackground(Color.BLACK);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                logoutButton.setBackground(Universal.BACKGROUND_BLACK);
            }
        });
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

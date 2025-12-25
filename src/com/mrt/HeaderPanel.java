package com.mrt;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.mrt.dbobject.User;

public class HeaderPanel extends JPanel {
    private User user;

    public HeaderPanel(User user) {
        this.user = user;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 50));
        setBackground(Universal.SKYBLUE);

        add(createLeftPanel(), BorderLayout.WEST);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createRightPanel(), BorderLayout.EAST);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftPanel.setOpaque(false);
        try {
            ImageIcon icon = new ImageIcon("src/com/mrt/img/logo_train.png");
            Image img = icon.getImage();
            float imgScaleFactor = 0.03f;
            float newWidth = img.getWidth(this) * imgScaleFactor;
            float newHeight = img.getHeight(this) * imgScaleFactor;
            Image scaledImg = img.getScaledInstance((int) newWidth, (int) newHeight, Image.SCALE_SMOOTH);
            JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));
            leftPanel.add(imgLabel);
        } catch(Exception e) {
            e.printStackTrace();
        }

        JLabel title = new JLabel("MRT Viet Nam");
        title.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 26));
        leftPanel.add(title);
        return leftPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        centerPanel.setOpaque(false);

        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Consolas", Font.BOLD, 20));

        int delay = 1000;
        ActionListener timerListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalDateTime currentTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String dateTimeString = formatter.format(currentTime);
                timeLabel.setText(dateTimeString);
            }
        };
        Timer timer = new Timer(delay, timerListener);
        timer.setInitialDelay(0);
        timer.start();

        centerPanel.add(timeLabel, gbc);

        return centerPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        rightPanel.setOpaque(false);

        LocalTime morningStart = LocalTime.of(6, 0);
        LocalTime afternoonStart = LocalTime.of(12, 0);
        LocalTime eveningStart = LocalTime.of(18, 0);
        LocalTime nightStart = LocalTime.of(21, 0);
        LocalTime currentTime = LocalTime.now();

        String timeMsg = "";
        if(currentTime.equals(nightStart) || currentTime.isAfter(nightStart) || currentTime.isBefore(morningStart)) {
            timeMsg = "night";
        } else if(currentTime.isBefore(afternoonStart)) {
            timeMsg = "morning";
        } else if(currentTime.isBefore(eveningStart)) {
            timeMsg = "afternoon";
        } else {
            timeMsg = "evening";
        }
        JLabel welcomeMsg = new JLabel("<html>Good " + timeMsg + ", <strong>" + user.getFullName() + "</strong>!</html>");
        welcomeMsg.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        rightPanel.add(welcomeMsg, gbc);

        return rightPanel;
    }
}   

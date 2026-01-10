package com.mrt.user.schedules.success;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.mrt.factory.UIFactory;
import com.mrt.frames.UserFrame;
import com.mrt.user.schedules.BookingPage;
import com.mrt.user.schedules.BookingStep;
import com.mrt.user.schedules.SchedulesPanel;

public class SuccessPanel extends JPanel implements BookingPage {

    private UserFrame userFrame;
    private SchedulesPanel schedulesPanel;

    public SuccessPanel(UserFrame userFrame, SchedulesPanel schedulesPanel) {
        this.userFrame = userFrame;
        this.schedulesPanel = schedulesPanel;        
    }

    public void onShow() {
        removeAll();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        add(Box.createVerticalStrut(100));
        add(createImagePanel());
        add(createMessagePanel());
        add(Box.createVerticalStrut(10));
        add(createButtonsPanel());
    }

    private JPanel createImagePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        panel.setOpaque(false);
        panel.add(UIFactory.createImageLabel("src/com/mrt/img/success.png", new Dimension(200, 200)));
        panel.setMaximumSize(panel.getPreferredSize());
        return panel;
    }

    private JPanel createMessagePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        panel.setOpaque(false);
        panel.add(UIFactory.createBoldLabel("Booking Successful!", 24));
        panel.setMaximumSize(panel.getPreferredSize());
        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setOpaque(false);

        Dimension btnSize = new Dimension(150, 40);

        JButton myTicketsBtn = UIFactory.createButton("My Tickets");
        myTicketsBtn.setPreferredSize(btnSize);
        myTicketsBtn.addActionListener(e -> {
            userFrame.goToPage(UserFrame.ticketsPanel);
            schedulesPanel.showStep(BookingStep.SEARCH);
        });
        panel.add(myTicketsBtn);

        JButton homeBtn = UIFactory.createButton("Home");
        homeBtn.setPreferredSize(btnSize);
        homeBtn.addActionListener(e -> {
            userFrame.goToPage(UserFrame.homePanel);
            schedulesPanel.showStep(BookingStep.SEARCH);
        });
        panel.add(homeBtn);

        JButton bookAnotherBtn = UIFactory.createButton("Book Another Ticket");
        bookAnotherBtn.setPreferredSize(btnSize);
        bookAnotherBtn.addActionListener(e -> {
            userFrame.goToPage(UserFrame.schedulesPanel);
            schedulesPanel.showStep(BookingStep.SEARCH);
        });
        panel.add(bookAnotherBtn);

        panel.setMaximumSize(panel.getPreferredSize());
        return panel;
    }
}

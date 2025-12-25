package com.mrt.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.mrt.Universal;
import com.mrt.dbobject.Route;
import com.mrt.dbobject.Train;

public class ScheduleDialog extends JDialog {

    private Train train;
    private Route route;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;

    public ScheduleDialog(JDialog dialog, Train train, Route route) {
        super(dialog, "Schedules - Train " + train.toString(), true);

        this.train = train;
        this.route = route;
        
        setSize(new Dimension(700, 500));
        setResizable(false);
        setLocationRelativeTo(dialog);
        setLayout(new BorderLayout(0, 0));
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setBackground(Universal.BACKGROUND_WHITE);

        add(createTopPanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        topPanel.setOpaque(false);

        topPanel.add(createHeaderPanel());
        topPanel.add(createActionPanel());

        return topPanel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(createFutureSchedulesPanel());
        panel.add(createPastSchedulesPanel());

        return panel;
    }

    private JPanel createFutureSchedulesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel label = new JLabel("Future schedules:");
        label.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 16));
        panel.add(label, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createPastSchedulesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel("<html>Route: <font color='#FF4646'>" + route.toString() + "</font></html>");
        headerLabel.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 16));
        headerPanel.add(headerLabel);
        return headerPanel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        addButton = createActionButton("Add");
        editButton = createActionButton("Edit");
        deleteButton = createActionButton("Delete");
        refreshButton = createActionButton("Refresh");


        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(refreshButton);
        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        panel.setOpaque(false);

        JButton exitBtn = new JButton("Exit");
        exitBtn.setPreferredSize(new Dimension(80, 33));
        exitBtn.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        exitBtn.addActionListener(e -> {
            dispose();
        });

        panel.add(exitBtn);
        return panel;
    }

    private JButton createActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(100, 34));
        btn.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        return btn;
    }
}

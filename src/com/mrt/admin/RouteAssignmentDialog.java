package com.mrt.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mrt.Universal;
import com.mrt.dbobject.Train;

public class RouteAssignmentDialog extends JDialog {

    private Train train;

    private JTextField searchField;

    public RouteAssignmentDialog(JFrame frame) {
        super(frame, "Route Assignment", true);
        setSize(500, 500);
        setLocationRelativeTo(frame);
        setResizable(false);
        getContentPane().setBackground(Universal.BACKGROUND_WHITE);
        setLayout(new BorderLayout(10, 10));

        add(createTopPanel(), BorderLayout.NORTH);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        topPanel.add(createSearchPanel());

        return topPanel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        panel.add(searchLabel);

        searchField = new JTextField(15);
        searchField.setToolTipText("Search by route code or station");
        searchField.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel.add(searchField);

        JButton searchButton = new JButton();
        try {
            ImageIcon icon = new ImageIcon("src/com/mrt/img/search.png");
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            searchButton.setIcon(new ImageIcon(newImg));
        } catch (Exception ignored) {}
        searchButton.addActionListener(e -> {

        });
        panel.add(searchButton);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();

        HashMap<String, List<String>> adjStation = new HashMap<>();

        return panel;
    }
}

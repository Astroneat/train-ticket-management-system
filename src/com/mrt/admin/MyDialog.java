package com.mrt.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mrt.Universal;

public class MyDialog extends JDialog {

    private JPanel formPanel;
    private JPanel buttonsPanel;
    private GridBagConstraints gbc;

    public MyDialog(JFrame frame, String title) {
        super(frame, title, true);
        setSize(400, 300);
        setLocationRelativeTo(frame);
        setLayout(new BorderLayout(10, 10));

        formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonsPanel.setOpaque(false);

        add(formPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    public JTextField addTextField(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        gbc.gridx = 0;
        formPanel.add(label, gbc);

        JTextField field = new JTextField(18);
        field.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        formPanel.add(field, gbc);

        gbc.gridy++;
        return field;
    }

    public <T> JComboBox<T> addComboBox(String title, T[] options) {
        JLabel label = new JLabel(title);
        label.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        gbc.gridx = 0;
        formPanel.add(label, gbc);

        JComboBox<T> box = new JComboBox<>(options);
        box.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        gbc.gridx = 1;
        formPanel.add(box, gbc);

        gbc.gridy++;
        return box;
    }

    public JButton addButtonRow() {
        JButton cancelBtn = new JButton("Cancel");
        JButton saveBtn = new JButton("Save");
        cancelBtn.addActionListener(e -> dispose());

        buttonsPanel.add(cancelBtn);
        buttonsPanel.add(saveBtn);
        return saveBtn;
    }
}

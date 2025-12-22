package com.mrt.admin;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mrt.Universal;

public class UserManagementPanel extends JPanel {
    public UserManagementPanel() {

    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));

        JLabel title = new JLabel("User Management");
        title.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 28));


        return header;
    }
}

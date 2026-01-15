package com.mrt.user.profile;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.frames.Page;
import com.mrt.frames.UserFrame;
import com.mrt.models.User;
import com.mrt.services.UserService;

public class ProfilePanel extends JPanel implements Page {

    private UserFrame userFrame;
    private User user;
    
    public ProfilePanel(UserFrame userFrame, User user) {
        this.userFrame = userFrame;
        this.user = user;

        setLayout(new BorderLayout());
        setBackground(Universal.BACKGROUND_WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        panel.add(UIFactory.createBoldLabel("Profile Information", 28));
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int fontSize = 16;
        int columnCnt = 30;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(UIFactory.createBoldLabel("Basic Information", fontSize + 4), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(UIFactory.createBoldLabel("Email:", fontSize), gbc);

        gbc.gridx++;
        JTextField emailField = UIFactory.createTextField(columnCnt);

        emailField.setText(user.getEmail());
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(UIFactory.createBoldLabel("Full Name:", fontSize), gbc);

        gbc.gridx++;
        JTextField nameField = UIFactory.createTextField(columnCnt);
        nameField.setText(user.getFullName());
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(UIFactory.createBoldLabel("Change password", fontSize + 4), gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(UIFactory.createBoldLabel("New password:", fontSize), gbc);

        gbc.gridx++;
        JPasswordField passwordField = UIFactory.createPasswordField(columnCnt);
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(UIFactory.createBoldLabel("Confirm password", fontSize), gbc);

        gbc.gridx++;
        JPasswordField confirmPasswordField = UIFactory.createPasswordField(columnCnt);
        panel.add(confirmPasswordField, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        JCheckBox showPassBox = UIFactory.createCheckBox("Show password");
        char defaultEchoChar = passwordField.getEchoChar();
        showPassBox.addActionListener(e -> {
            if(showPassBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
                confirmPasswordField.setEchoChar((char) 0);
            }
            else {
                passwordField.setEchoChar(defaultEchoChar);
                confirmPasswordField.setEchoChar(defaultEchoChar);
            }
        });
        panel.add(showPassBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton saveBtn = UIFactory.createButton("Save changes");
        saveBtn.setPreferredSize(new Dimension(200, 36));
        saveBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String fullName = nameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            if(!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(userFrame, "Passwords don't match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int state = UserService.updateUser(user, email, fullName, password);
            switch(state) {
                case UserService.OK:
                    JOptionPane.showMessageDialog(userFrame, "Profile successfully updated", "Success", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case UserService.BLANK_INPUT:
                    JOptionPane.showMessageDialog(userFrame, "Cannot leave blank fields", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
                case UserService.EMAIL_IN_USE:
                    JOptionPane.showMessageDialog(userFrame, "Email already in use", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        });
        panel.add(saveBtn, gbc);

        return panel;
    }

    public void refreshPage() {

    }
}

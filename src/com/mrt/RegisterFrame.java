package com.mrt;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class RegisterFrame extends JFrame {
    public RegisterFrame() {
        setTitle("MRT Viet Nam - Register Account");
        setSize(800, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(245, 247, 249));

        add(createContentPanel(), new GridBagConstraints());
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setPreferredSize(new Dimension(400, 550));
        contentPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(220, 220, 220)));

        contentPanel.add(createBrandPanel());

        contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        contentPanel.add(createRegisterForm());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 100)));

        return contentPanel;
    }

    private JPanel createBrandPanel() {
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new FlowLayout());
        brandPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        brandPanel.setBackground(new Color(135, 206, 235));
        brandPanel.setOpaque(true);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("./img/logo_train.png"));
            Image img = icon.getImage();
            float imgScaleFactor = 0.12f;
            float newWidth = img.getWidth(brandPanel) * imgScaleFactor;
            float newHeight = img.getHeight(brandPanel) * imgScaleFactor;
            Image scaledImg = img.getScaledInstance((int) newWidth, (int) newHeight, Image.SCALE_SMOOTH);
            JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));
            brandPanel.add(imgLabel);
        } catch(Exception e) {
            e.printStackTrace();
        }

        brandPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        JLabel brandName = new JLabel("<html>MRT<br>Viet Nam</html>");
        brandName.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 35));
        brandPanel.add(brandName);

        return brandPanel;
    }

    private JPanel createRegisterForm() {
        JPanel registerForm = new JPanel();
        registerForm.setLayout(new GridBagLayout());
        registerForm.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 20, 6, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel emailLabel = new JLabel("Enter email address:");
        emailLabel.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        registerForm.add(emailLabel, gbc);

        JTextField emailField = new JTextField();
        emailField.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5))
        );
        gbc.gridy = 1;
        gbc.weightx = 1;
        registerForm.add(emailField, gbc);

        JLabel fullNameLabel = new JLabel("Enter full name:");
        fullNameLabel.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        gbc.gridy = 2;
        gbc.weightx = 1;
        registerForm.add(fullNameLabel, gbc);

        JTextField fullNameField = new JTextField();
        fullNameField.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        fullNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5))
        );
        gbc.gridy = 3;
        gbc.weightx = 1;
        registerForm.add(fullNameField, gbc);

        JLabel passwordLabel = new JLabel("Enter password:");
        passwordLabel.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        gbc.gridy = 4;
        gbc.weightx = 1;
        registerForm.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridy = 5;
        gbc.weightx = 1;
        registerForm.add(passwordField, gbc);

        JLabel confirmPasswordLabel = new JLabel("Confirm password:");
        confirmPasswordLabel.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        gbc.gridy = 6;
        gbc.weightx = 1;
        registerForm.add(confirmPasswordLabel, gbc);

        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridy = 7;
        gbc.weightx = 1;
        registerForm.add(confirmPasswordField, gbc);

        JButton registerButton = new JButton("Register");
        registerButton.setOpaque(true);
        registerButton.setBorderPainted(true);
        registerButton.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        registerButton.setBackground(Color.WHITE);
        registerButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        gbc.gridy = 8;
        gbc.weightx = 1;

        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(new Color(245, 245, 245));
                registerButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK, 1, true),
                    BorderFactory.createMatteBorder(12, 16, 12, 16, new Color(245, 245, 245))
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(Color.WHITE);
                registerButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK, 1, true),
                    BorderFactory.createMatteBorder(12, 16, 12, 16, Color.WHITE)
                ));
            }
        });
        registerButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String fullName = fullNameField.getText();
            String pass = new String(passwordField.getPassword()).trim();
            String confirmPass = new String(confirmPasswordField.getPassword()).trim();

            if(email.isBlank()) {
                JOptionPane.showMessageDialog(this, "Please enter your email", "Error", JOptionPane.ERROR_MESSAGE);
            } 
            else if(fullName.isBlank()) {
                JOptionPane.showMessageDialog(this, "Please enter your full name", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else if(pass.isBlank()) {
                JOptionPane.showMessageDialog(this, "Please enter your password", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else if(confirmPass.isBlank()) {
                JOptionPane.showMessageDialog(this, "Please confirm your password", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else if(!pass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "Passwords don\'t match", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                User checkDup = Universal.db().queryOne(
                    "SELECT * FROM users WHERE email = ?;",
                    rs -> new User(
                        rs.getInt("user_id"), 
                        rs.getString("email"), 
                        rs.getString("full_name"), 
                        rs.getString("role")),
                    email
                );
                if(checkDup != null) {
                    JOptionPane.showMessageDialog(this, "Email already in use! Please choose another one", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    Universal.db().execute(
                        "INSERT INTO users(email, full_name, password, role) VALUES (?, ?, ?, ?);", 
                        email,
                        fullName,
                        pass,
                        "customer"
                    );
                    JOptionPane.showMessageDialog(this, "Account successfully registered!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new LoginFrame().setVisible(true);
                }
            }
        });
        registerForm.add(registerButton, gbc);

        return registerForm;
    }
}

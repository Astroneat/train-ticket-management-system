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
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.mrt.model.User;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("MRT Viet Nam - Login");
        setSize(800, 600);
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
        contentPanel.setPreferredSize(new Dimension(400, 500));
        contentPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(220, 220, 220)));

        contentPanel.add(createBrandPanel());

        contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        contentPanel.add(createLoginForm());
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

    private JPanel createLoginForm() {
        JPanel loginForm = new JPanel();
        loginForm.setLayout(new GridBagLayout());
        loginForm.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 20, 6, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel emailLabel = new JLabel("Email address");
        emailLabel.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        loginForm.add(emailLabel, gbc);

        JTextField emailField = new JTextField();
        emailField.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5))
        );
        emailField.setToolTipText("Enter email address");
        gbc.gridy = 1;
        gbc.weightx = 1;
        loginForm.add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        gbc.gridy = 2;
        gbc.weightx = 1;
        loginForm.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField();
        char defaultEchoChar = passwordField.getEchoChar();
        passwordField.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        passwordField.setToolTipText("Enter password");
        gbc.gridy = 3;
        gbc.weightx = 1;
        loginForm.add(passwordField, gbc);
        
        JCheckBox showPassword = new JCheckBox("Show password");
        showPassword.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        showPassword.addActionListener(e -> {
            if(showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar(defaultEchoChar);
            }
        });
        gbc.gridy = 4;
        gbc.weightx = 1;
        loginForm.add(showPassword, gbc);

        JLabel forgotPass = new JLabel("<html><u>Forgot password?</u></html>");
        forgotPass.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        forgotPass.setForeground(Color.BLUE);
        gbc.gridy = 5;
        gbc.weightx = 1;
        loginForm.add(forgotPass, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(true);
        loginButton.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        loginButton.setBackground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        gbc.gridy = 6;
        gbc.weightx = 1;

        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(245, 245, 245));
                loginButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK, 1, true),
                    BorderFactory.createMatteBorder(12, 16, 12, 16, new Color(245, 245, 245))
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(Color.WHITE);
                loginButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK, 1, true),
                    BorderFactory.createMatteBorder(12, 16, 12, 16, Color.WHITE)
                ));
            }
        });
        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();

            if(email.isBlank() || pass.isBlank()) {
                JOptionPane.showMessageDialog(this, "Please enter your credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                User result = Universal.db().queryOne(
                    "SELECT * FROM users WHERE email = ? AND password = ?;",
                    rs -> User.parseResultSet(rs),
                    email,
                    pass
                );
                if(result == null) {
                    JOptionPane.showMessageDialog(this, "Email or password is incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(this, "<html>Logged in successfully as <strong>" + email + "</strong>!</html>", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new MainFrame(result).setVisible(true);
                }
            }
        });

        loginForm.add(loginButton, gbc);

        JPanel registerTextPanel = new JPanel(new FlowLayout());
        registerTextPanel.setOpaque(false);
        JLabel registerPrompt = new JLabel("Don\'t have an account?");
        registerPrompt.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        JButton registerLink = new JButton("[Register]");
        registerLink.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        registerLink.setPreferredSize(new Dimension(80, 20));
        registerLink.setForeground(Color.BLUE);
        registerLink.setBorder(BorderFactory.createEmptyBorder());
        registerLink.setFocusable(false);
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new RegisterFrame().setVisible(true);
            }
        });

        registerTextPanel.add(registerPrompt);
        registerTextPanel.add(registerLink);
        gbc.gridy = 7;
        gbc.weightx = 1;
        loginForm.add(registerTextPanel, gbc);

        return loginForm;
    }
}

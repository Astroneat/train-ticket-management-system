package com.mrt.frames;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.models.EmailMessage;
import com.mrt.services.EmailService;
import com.mrt.services.UserService;

public class ForgotPasswordFrame extends JFrame {

    public ForgotPasswordFrame() {
        setTitle("MRT Viet Nam - Forgot Password");
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
        contentPanel.setPreferredSize(new Dimension(420, 520));
        contentPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(220, 220, 220)));

        contentPanel.add(createBrandPanel());

        contentPanel.add(Box.createVerticalStrut(16));

        contentPanel.add(createFormPanel());
        // contentPanel.add(Box.createVerticalStrut(100));

        return contentPanel;
    }

    private JPanel createBrandPanel() {
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new FlowLayout());
        brandPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        brandPanel.setBackground(Universal.SKYBLUE);
        brandPanel.setOpaque(true);

        brandPanel.add(UIFactory.createImageLabel("src/com/mrt/img/logo_train.png", new Dimension(100, 100)));
        brandPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        JLabel brandName = new JLabel("<html>MRT<br>Viet Nam</html>");
        brandName.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 35));
        brandPanel.add(brandName);

        brandPanel.setMaximumSize(new Dimension(1000, brandPanel.getPreferredSize().height));
        return brandPanel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        // gbc.fill = GridBagConstraints.HORIZONTAL;

        int fontSize = 16;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JButton backBtn = UIFactory.createButton("[Back to Login]");
        backBtn.setForeground(Color.BLUE);
        backBtn.setBorder(null);
        backBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        panel.add(backBtn, gbc);

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(UIFactory.createBoldLabel("Reset password", fontSize + 4), gbc);
        gbc.gridwidth = 1;

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createBoldLabel("Email address:", fontSize), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField emailField = UIFactory.createTextField(20);
        panel.add(emailField, gbc);

        gbc.gridy++;
        JButton sendCodeBtn = UIFactory.createButton("Send code");
        sendCodeBtn.addActionListener(e1 -> {
            String email = emailField.getText().trim();
            
            if(email.isBlank()) {
                JOptionPane.showMessageDialog(this, "Please enter your email", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(!EmailService.isValidEmailAddress(email)) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }   

            String newSendCodeText = "Resend code";
            String code = UserService.generateResetPasswordCode(email);
            if(!sendCodeBtn.getText().trim().equals(newSendCodeText)) {
                sendCodeBtn.setText(newSendCodeText);
                
                gbc.gridx = 0;
                gbc.gridy++;
                gbc.anchor = GridBagConstraints.EAST;
                panel.add(UIFactory.createBoldLabel("Code:", fontSize), gbc);

                gbc.gridx++;
                gbc.anchor = GridBagConstraints.WEST;
                JTextField codeField = UIFactory.createTextField(20);
                panel.add(codeField, gbc);

                gbc.gridx = 0;
                gbc.gridy++;
                gbc.gridwidth = 2;
                gbc.anchor = GridBagConstraints.CENTER;
                JButton submitBtn = UIFactory.createButton("Submit");
                submitBtn.addActionListener(e2 -> {
                    String enteredCode = codeField.getText().trim();
                    if(enteredCode.isBlank()) {
                        JOptionPane.showMessageDialog(this, "Please enter the code", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(!enteredCode.equals(code)) {
                        JOptionPane.showMessageDialog(this, "Invalid code", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    gbc.gridx = 0;
                    gbc.gridy++;
                    gbc.anchor = GridBagConstraints.EAST;
                    panel.add(UIFactory.createBoldLabel("New password:", fontSize), gbc);

                    gbc.gridx++;
                    gbc.anchor = GridBagConstraints.WEST;
                    JPasswordField newPassField = UIFactory.createPasswordField(20);
                    panel.add(newPassField, gbc);

                    gbc.gridx = 0;
                    gbc.gridy++;
                    gbc.anchor = GridBagConstraints.EAST;
                    panel.add(UIFactory.createBoldLabel("Re-enter:", fontSize), gbc);

                    gbc.gridx++;
                    gbc.anchor = GridBagConstraints.WEST;
                    JPasswordField confirmPassField = UIFactory.createPasswordField(20);
                    panel.add(confirmPassField, gbc);

                    gbc.gridx = 0;
                    gbc.gridy++;
                    gbc.gridwidth = 2;
                    gbc.anchor = GridBagConstraints.CENTER;
                    JButton resetBtn = UIFactory.createButton("Reset");
                    resetBtn.addActionListener(e3 -> {
                        String newPass = new String(newPassField.getPassword()).trim();
                        String confirmPass = new String(confirmPassField.getPassword()).trim();

                        if(newPass.isBlank() || confirmPass.isBlank()) {
                            JOptionPane.showMessageDialog(this, "Please enter your password", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if(!newPass.equals(confirmPass)) {
                            JOptionPane.showMessageDialog(this, "Passwords don't match", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        UserService.changePassword(email, newPass);
                        JOptionPane.showMessageDialog(this, "Password changed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new LoginFrame().setVisible(true);
                    });
                    panel.add(resetBtn, gbc);
                    gbc.gridwidth = 1;
                    panel.setMaximumSize(panel.getPreferredSize());

                    panel.repaint();
                    panel.revalidate();
                });
                panel.add(submitBtn, gbc);
                gbc.gridwidth = 1;
                panel.setMaximumSize(panel.getPreferredSize());
            }

            EmailMessage msg = new EmailMessage(
                email, 
                code + " is your password reset code", 
                """
                    <p>We received your request to change your password. Your code is: <strong>%s</strong></p>
                    <p>Enter this code in your app to reset your password. Do not share this code with anyone.</p>    
                """.formatted(code)
            );
            EmailService.send(msg);
        });
        panel.add(sendCodeBtn, gbc);

        panel.setMaximumSize(panel.getPreferredSize());
        return panel;
    }
}

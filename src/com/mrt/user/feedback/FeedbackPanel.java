package com.mrt.user.feedback;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.frames.Page;
import com.mrt.frames.UserFrame;
import com.mrt.models.EmailMessage;
import com.mrt.services.EmailService;

public class FeedbackPanel extends JPanel implements Page {

    private UserFrame userFrame;

    public FeedbackPanel(UserFrame userFrame) {
        this.userFrame = userFrame;

        setLayout(new BorderLayout());
        setBackground(Universal.BACKGROUND_WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        panel.add(UIFactory.createBoldLabel("Feedback", 28));
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int fontSize = 16;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(UIFactory.createBoldLabel("Subject:", fontSize), gbc);

        gbc.gridx++;
        JTextField subjectField = UIFactory.createTextField(30);
        panel.add(subjectField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(UIFactory.createBoldLabel("Content:", fontSize), gbc);

        gbc.gridx++;
        JTextArea contentArea = UIFactory.createTextArea(5, 30);
        panel.add(contentArea, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(UIFactory.createItalicLabel("* Your feedback will be anonymous", fontSize - 2), gbc);
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(UIFactory.createBoldLabel("Just write anything - We would love to hear your feedback!", fontSize), gbc);
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton sendBtn = UIFactory.createButton("Send!");
        sendBtn.setFont(UIFactory.createDefaultBoldFont(fontSize));
        sendBtn.setPreferredSize(new Dimension(120, 36));
        sendBtn.addActionListener(e -> {
            String subject = subjectField.getText().trim();
            String content = contentArea.getText();

            if(content.isBlank()) {
                JOptionPane.showMessageDialog(userFrame, "Cannot leave content blank", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(subject.isBlank()) subject = "(No subject)";

            content = content.replace("\n", "<br>");
            EmailMessage msg = new EmailMessage(
                EmailService.getHostEmailAddress(),
                "[User Feedback] " + subject,
                content
            );

            try {
                EmailService.send(msg);
                JOptionPane.showMessageDialog(userFrame, "Feedback sent!", "Success", JOptionPane.INFORMATION_MESSAGE);
                subjectField.setText("");
                contentArea.setText("");

            } catch(Exception ex) {
                JOptionPane.showMessageDialog(userFrame, "Failed to send feedback", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(sendBtn, gbc);

        return panel;
    }

    public void refreshPage() {

    }
}

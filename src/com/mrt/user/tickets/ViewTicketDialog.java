package com.mrt.user.tickets;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.models.EmailMessage;
import com.mrt.models.Route;
import com.mrt.models.Schedule;
import com.mrt.models.Ticket;
import com.mrt.models.Train;
import com.mrt.models.User;
import com.mrt.services.CurrencyService;
import com.mrt.services.EmailService;
import com.mrt.services.QRCodeService;
import com.mrt.services.SeatService;
import com.mrt.services.TicketPdfService;

public class ViewTicketDialog extends JDialog {

    private Ticket ticket;
    private Schedule schedule;
    private Route route;
    private Train train;
    private User user;

    private BufferedImage qrImage;

    public ViewTicketDialog(JFrame parent, Ticket ticket, Schedule schedule, Route route, Train train, User user) {
        super(parent, "Viewing Ticket #" + ticket.getTicketId(), true);

        this.ticket = ticket;
        this.schedule = schedule;
        this.route = route;
        this.train = train;
        this.user = user;
        
        setSize(500, 750);
        setLocationRelativeTo(parent);
        setResizable(false);
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setBackground(Universal.PASTEL_WHITE);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        contentPane.add(createHeaderPanel());
        contentPane.add(createInfoPanel());
        contentPane.add(createQRCodePanel());
        contentPane.add(Box.createVerticalStrut(10));
        contentPane.add(createActionPanel());
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.setBackground(Universal.SKYBLUE);

        JLabel iconLabel = UIFactory.createImageLabel("src/com/mrt/img/logo_train.png", new Dimension(50, 50));
        panel.add(iconLabel);

        panel.add(Box.createHorizontalStrut(10));

        JLabel titleLabel = UIFactory.createBoldLabel("MRT Viet Nam", 24);
        panel.add(titleLabel);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int fontSize = 16;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(UIFactory.createBoldLabel("Ticket Details", fontSize + 4), gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(UIFactory.createBoldLabel("Train:", fontSize), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel(train.getTrainSummary(), fontSize), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(UIFactory.createBoldLabel("Route:", fontSize), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel routeLabel = UIFactory.createPlainLabel("<html><div style='width:280px; text-align: right;'>" + route.getRouteSummary() + "</div></html>", fontSize);
        panel.add(routeLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(UIFactory.createBoldLabel("Departure:", fontSize), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel(schedule.getFormattedDepartureTime(), fontSize), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(UIFactory.createBoldLabel("Arrival:", fontSize), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel(schedule.getFormattedArrivalTime(), fontSize), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(UIFactory.createBoldLabel("Passenger:", fontSize), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel(user.getFullName(), fontSize), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(UIFactory.createBoldLabel("Seat:", fontSize), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel("Car " + ticket.getCarNo() + " - " + SeatService.toSeatCode(ticket.getSeatIndex()), fontSize), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(UIFactory.createBoldLabel("Price:", fontSize), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel(CurrencyService.formatVnd(ticket.getPrice()), fontSize), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(UIFactory.createBoldLabel("Fees:", fontSize), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel(CurrencyService.formatVnd(ticket.getFees()), fontSize), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(UIFactory.createBoldLabel("Total:", fontSize), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createBoldLabel(CurrencyService.formatVnd(ticket.getPrice() + ticket.getFees()), fontSize), gbc);

        return panel;
    }

    private JPanel createQRCodePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setOpaque(false);

        String data = QRCodeService.buildQRData(ticket);
        qrImage = QRCodeService.generateQRCode(data, 200);

        JLabel qrLabel = new JLabel(new ImageIcon(qrImage));
        // qrLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(qrLabel, BorderLayout.CENTER);

        JPanel notePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        notePanel.setOpaque(false);
        notePanel.add(UIFactory.createBoldLabel("Scan at gate", 16));
        panel.add(notePanel, BorderLayout.SOUTH);

        panel.setMaximumSize(panel.getPreferredSize());
        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        Dimension btnDim = new Dimension(150, 36);

        JButton downloadBtn = UIFactory.createButton("Download PDF");
        downloadBtn.setPreferredSize(btnDim);
        downloadBtn.addActionListener(e -> {
            try {
                File pdf = TicketPdfService.generateTicketPdf(ticket, schedule, route, train, user, qrImage);
                
                JFileChooser chooser = new JFileChooser();
                String ticketFileName = "ticket_" + ticket.getTicketId() + ".pdf";
                chooser.setSelectedFile(new File(ticketFileName));
                chooser.setDialogTitle("Download Ticket");
                if(chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    Path path = Files.copy(
                        pdf.toPath(),
                        chooser.getSelectedFile().toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    );
                    JOptionPane.showMessageDialog(this, "<html>Ticket successfully downloaded to <strong>" + path + "</strong></html>", "Download Successful", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch(Exception ignored) {
                JOptionPane.showMessageDialog(this, "Failed to download PDF", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(downloadBtn);

        JButton emailBtn = UIFactory.createButton("Email PDF");
        emailBtn.setPreferredSize(btnDim);
        emailBtn.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(this, "Send this ticket to your email?", "Send e-ticket confirmation", JOptionPane.YES_NO_OPTION);
            if(option == JOptionPane.YES_OPTION) {
                try {
                    File pdf = TicketPdfService.generateTicketPdf(ticket, schedule, route, train, user, qrImage);

                    EmailMessage msg = new EmailMessage(
                        user.getEmail(), 
                        "Your MRT Ticket - Booking #" + ticket.getTicketId(), 
                        """
                            <p>Dear %s,</p>
                            <p>Your MRT ticket is attached to this email.<br>
                            Please present the QR code at the gate before departure.</p>
                            <p>Thank you for choosing MRT Viet Nam.</p>
                        """.formatted(user.getFullName())
                    );
                    msg.addAttachment(pdf);
                    EmailService.send(msg);

                    JOptionPane.showMessageDialog(this, "<html>Ticket sent to <strong>" + user.getEmail() + "</strong></html>", "Success", JOptionPane.INFORMATION_MESSAGE);

                } catch(Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to send email", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(emailBtn);

        return panel;
    }
}

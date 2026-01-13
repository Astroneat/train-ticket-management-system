package com.mrt.admin.tickets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.models.Ticket;
import com.mrt.services.QRCodeService;
import com.mrt.services.SeatService;
import com.mrt.services.TicketService;
import com.mrt.services.TicketService.ScanResult;

public class QRScanDialog extends JDialog {

    private JLabel qrPreviewLabel;

    private JLabel statusLabel;
    private JLabel detailLabel;
    
    public QRScanDialog(JFrame parent) {
        super(parent, "Ticket Scanning", true);

        setSize(500, 690);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setBackground(Universal.BACKGROUND_WHITE);
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        add(createQRPanel(), BorderLayout.NORTH);
        add(createStatusPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }   

    private JPanel createQRPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 500));

        qrPreviewLabel = UIFactory.createBoldLabel("No Ticket Selected", 30);
        qrPreviewLabel.setForeground(Color.GRAY);
        qrPreviewLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(qrPreviewLabel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        actionPanel.setOpaque(false);
        JButton uploadBtn = UIFactory.createButton("Upload Image");
        uploadBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Image Files (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg"));

            if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File selectedFile = chooser.getSelectedFile();

                    BufferedImage image = ImageIO.read(selectedFile);
                    int height = image.getHeight();
                    int width = image.getWidth();
                    int targetHeight = 400;
                    int targetWidth = 400;
                    double scale = Math.min((double) targetWidth / width, (double) targetHeight / height);
                    int scaledWidth = (int) (width * scale);
                    int scaledHeight = (int) (height * scale);
                    Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                    qrPreviewLabel.setText("");
                    qrPreviewLabel.setIcon(new ImageIcon(scaledImage));

                    String qrText = QRCodeService.decode(selectedFile);
                    int ticketId = Integer.valueOf(qrText);
                    handleScannedQR(ticketId);
                } catch(Exception ex) {
                    showError("Invalid QR code");
                }
            }
        });
        actionPanel.add(uploadBtn);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void handleScannedQR(int ticketId) {
        Ticket tk = TicketService.getTicketById(ticketId);
        ScanResult result = TicketService.scan(tk);

        switch(result) {
            case SUCCESS:
                showSuccess(tk);
                break;
            case ALREADY_USED:
                showWarning("Ticket #" + ticketId + ": Already used");
                break;
            case EXPIRED:
                showError("Ticket #" + ticketId + ": Expired");
                break;
            case CANCELLED:
                showError("Ticket #" + ticketId + ": Cancelled");
                break;
            case WRONG_TIME:
                showError("Ticket #" + ticketId + ": Train is ongoing");
                break;
            default:
                showError("Invalid ticket");
                break;
        }
    }

    private void showSuccess(Ticket ticket) {
        statusLabel.setText("Boarding allowed");
        statusLabel.setForeground(new Color(0, 200, 0));

        detailLabel.setText("Ticket #" + ticket.getTicketId() + " • Car " + ticket.getCarNo() + " - " + SeatService.toSeatCode(ticket.getSeatIndex()));
    }
    private void showError(String msg) {
        statusLabel.setText(msg);
        statusLabel.setForeground(Color.RED);
        detailLabel.setText("");
    }
    private void showWarning(String msg) {
        statusLabel.setText(msg);
        statusLabel.setForeground(Color.decode("#ffaa00"));
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setOpaque(false);
        statusPanel.add(UIFactory.createBoldLabel("Status:", 16));
        statusPanel.add(Box.createHorizontalStrut(10));
        statusLabel = UIFactory.createPlainLabel("Waiting for scan...", 16);
        statusPanel.add(statusLabel);

        JPanel detailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        detailPanel.setOpaque(false);
        detailPanel.add(UIFactory.createBoldLabel("Details:", 16));
        detailPanel.add(Box.createHorizontalStrut(10));
        detailLabel = UIFactory.createPlainLabel("", 16);
        detailPanel.add(detailLabel);

        panel.add(statusPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(detailPanel);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panel.setOpaque(false);

        JButton closeBtn = UIFactory.createButton("Close");
        closeBtn.setPreferredSize(new Dimension(100, 36));
        closeBtn.addActionListener(e -> {
            dispose();
        });
        panel.add(closeBtn);

        return panel;
    }
}
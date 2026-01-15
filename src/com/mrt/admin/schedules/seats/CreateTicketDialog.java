package com.mrt.admin.schedules.seats;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.models.Schedule;
import com.mrt.models.Seat;
import com.mrt.models.User;
import com.mrt.services.TicketService;

public class CreateTicketDialog extends JDialog {

    private JTextField userField;
    private JButton pickBtn;

    private JButton addBtn;

    private User selectedUser;
    private Schedule schedule;
    private Seat seat;
    
    public CreateTicketDialog(JFrame frame, Schedule schedule, Seat seat) {
        super(frame, "Create Ticket", true);

        setSize(420, 320);
        setResizable(false);
        setLocationRelativeTo(frame);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Universal.BACKGROUND_WHITE);

        this.schedule = schedule;
        this.seat = seat;

        add(createFormPanel(), BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int fontSize = 16;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(UIFactory.createBoldLabel("Passenger:", fontSize), gbc);

        gbc.gridx++;
        panel.add(createUserPickerRow(), gbc);

        return panel;
    }

    private JPanel createUserPickerRow() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);

        userField = UIFactory.createTextField(10);
        userField.setEnabled(false);
        userField.setEditable(false);
        panel.add(userField);

        pickBtn = UIFactory.createButton("...");
        pickBtn.setPreferredSize(new Dimension(36, 36));
        pickBtn.addActionListener(e -> {
            UserPickerDialog dialog = new UserPickerDialog(this);
            dialog.setVisible(true);

            User pickedUser = dialog.getSelectedUser();
            if(pickedUser != null) {
                selectedUser = pickedUser;
                userField.setText(selectedUser.getFullName());
                addBtn.setEnabled(true);
            }
        });
        panel.add(pickBtn);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setOpaque(false);

        Dimension btnDim = new Dimension(100, 36);

        JButton cancelBtn = UIFactory.createButton("Cancel");
        cancelBtn.setPreferredSize(btnDim);
        cancelBtn.addActionListener(e -> {
            selectedUser = null;
            dispose();
        });
        panel.add(cancelBtn);

        addBtn = UIFactory.createButton("Add");
        addBtn.setPreferredSize(btnDim);
        addBtn.addActionListener(e -> {
            TicketService.bookTicket(selectedUser, schedule, seat);
            dispose();
        });
        panel.add(addBtn);

        return panel;
    }

    public User getSelectedUser() {
        return selectedUser;
    }
}

package com.mrt.admin.schedules.search;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.models.Schedule;
import com.mrt.models.Train;
import com.mrt.services.ScheduleService;
import com.mrt.services.TrainService;

public class EditScheduleDialog extends JDialog {

    private Schedule schedule;

    private JSpinner departureSpinner;
    private JSpinner arrivalSpinner;

    private Train selectedTrain;
    // private Route selectedRoute;

    private JButton saveBtn;

    public EditScheduleDialog(JFrame parent, Schedule schedule) {
        super(parent, "Edit Schedule", true);

        setSize(new Dimension(400, 300));
        setResizable(false);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setBackground(Universal.BACKGROUND_WHITE);

        this.schedule = schedule;
        selectedTrain = TrainService.getTrainById(schedule.getTrainId());

        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        // gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel("Departure:", 14), gbc);
        
        departureSpinner = UIFactory.createDateTimeSpinner("HH:mm dd-MM-yyyy", schedule.getDepartureTime());
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(departureSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel("Arrival:", 14), gbc);

        arrivalSpinner = UIFactory.createDateTimeSpinner("HH:mm dd-MM-yyyy", schedule.getArrivalTime());
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(arrivalSpinner, gbc);

        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setOpaque(false);

        Dimension btnDim = new Dimension(80, 33);
        JButton cancelBtn = UIFactory.createButton("Cancel");
        cancelBtn.setPreferredSize(btnDim);
        saveBtn = UIFactory.createButton("Add");
        saveBtn.setPreferredSize(btnDim);
        saveBtn.setEnabled(false);

        cancelBtn.addActionListener(e -> dispose());
        saveBtn.addActionListener(e -> {
            if(selectedTrain == null) {
                JOptionPane.showMessageDialog(this, "Please select a train", "Schedule Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDateTime departure = getLocalDateTime(departureSpinner);
            LocalDateTime arrival = getLocalDateTime(arrivalSpinner);
            // System.out.println("ldt " + departure + " " + arrival);

            int state = ScheduleService.updateSchedule(schedule.getScheduleId(), selectedTrain.getTrainId(), departure, arrival);
            if(state == ScheduleService.SCHEDULE_CONFLICT) {
                JOptionPane.showMessageDialog(this, "Another schedule already exists", "Schedule Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(state == ScheduleService.SCHEDULE_DEPARTURE_AFTER_ARRIVAL) {
                JOptionPane.showMessageDialog(this, "Departure must be before arrival", "Schedule Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(state == ScheduleService.SCHEDULE_DEPARTURE_IN_THE_PAST) {
                JOptionPane.showMessageDialog(this, "Departure must occur in the future", "Schedule Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dispose();
        });

        panel.add(cancelBtn);
        panel.add(saveBtn);
        return panel;
    }

    private LocalDateTime getLocalDateTime(JSpinner spinner) {
        try {
            spinner.commitEdit();
        } catch(Exception ignored) {}

        Date date = (Date) spinner.getValue();
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}

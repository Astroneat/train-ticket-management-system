package com.mrt.admin.routes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.models.Route;
import com.mrt.models.Train;
import com.mrt.services.ScheduleService;

public class RouteAddScheduleDialog extends JDialog {

    private Route route;

    private JSpinner departureSpinner;
    private JSpinner arrivalSpinner;

    private JTextField routeField;
    private JButton pickRouteBtn;

    private Train selectedTrain;

    private JButton saveBtn;

    public RouteAddScheduleDialog(JDialog parent, Route route) {
        super(parent, "Add Schedule", true);

        this.route = route;

        setSize(new Dimension(400, 300));
        setResizable(false);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setBackground(Universal.BACKGROUND_WHITE);

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
        
        departureSpinner = createDateTimeSpinner();
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(departureSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel("Arrival:", 14), gbc);

        arrivalSpinner = createDateTimeSpinner();
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(arrivalSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel("Train:", 14), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(createTrainPickerRow(), gbc);

        return panel;
    }

    private JSpinner createDateTimeSpinner() {
        SpinnerDateModel spinnerModel = new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);
        JSpinner spinner = new JSpinner(spinnerModel);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm dd-MM-yyyy");
        spinner.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        editor.getTextField().setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        spinner.setEditor(editor);
        return spinner;
    }

    private JPanel createTrainPickerRow() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        routeField = UIFactory.createTextField(10);
        routeField.setEnabled(false);
        routeField.setEditable(false);

        pickRouteBtn = UIFactory.createButton("...");
        pickRouteBtn.setPreferredSize(new Dimension(40, 40));
        pickRouteBtn.addActionListener(e -> {
            TrainPickerDialog dialog = new TrainPickerDialog(this);
            dialog.setVisible(true);

            Train pickedTrain = dialog.getSelectedTrain();
            if(pickedTrain != null) {
                routeField.setText(pickedTrain.getTrainCode());
                selectedTrain = pickedTrain;
                saveBtn.setEnabled(true);
            } else {
                saveBtn.setEnabled(false);
            }
        });

        panel.add(routeField);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(pickRouteBtn);
        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setOpaque(false);

        Dimension btnDim = new Dimension(80, 33);
        JButton cancelBtn = UIFactory.createButton("Cancel");
        cancelBtn.setPreferredSize(btnDim);
        saveBtn = UIFactory.createButton("Save");
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

            int state = ScheduleService.createSchedule(selectedTrain.getTrainId(), route.getRouteId(), departure, arrival);
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

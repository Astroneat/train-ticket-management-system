package com.mrt.admin.routes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.model.Route;
import com.mrt.model.Station;
import com.mrt.services.RouteService;

public class EditRouteDialog extends JDialog {

    private Route route;

    private JTextField routeCodeField;
    private StationPickerField originPicker;
    private StationPickerField destinationPicker;
    private JComboBox<String> statusBox;

    private JButton saveBtn;
    
    public EditRouteDialog(JFrame parent, Route route) {
        super(parent, "Add Route", true);

        this.route = route;

        setSize(new Dimension(500, 350));
        setLocationRelativeTo(parent);
        setResizable(false);
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setBackground(Universal.BACKGROUND_WHITE);
        setLayout(new BorderLayout(0, 0));

        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridy = 0;

        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel("Route Code:", 14), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        routeCodeField = UIFactory.createTextField(10);
        routeCodeField.setText(route.getRouteCode());
        panel.add(routeCodeField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel("Origin Station:", 14), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        originPicker = new StationPickerField(this);
        originPicker.setSelectedStation(Station.getStationFromId(route.getOriginStationId()));
        panel.add(originPicker, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel("Destination Station:", 14), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        destinationPicker = new StationPickerField(this);
        destinationPicker.setSelectedStation(Station.getStationFromId(route.getDestinationStationId()));
        panel.add(destinationPicker, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(UIFactory.createPlainLabel("Status:", 14), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        statusBox = UIFactory.createComboBox(new String[] {
            "active", "inactive"
        });
        statusBox.setSelectedItem(route.getStatus());
        panel.add(statusBox, gbc);

        if(RouteService.hasSchedule(route)) {
            originPicker.disableEditing();
            destinationPicker.disableEditing();

            gbc.gridy++;
            panel.add(UIFactory.createPlainLabel("⚠ This route has existing schedules", 12), gbc);
        }

        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setOpaque(false);

        Dimension btnDim = new Dimension(80, 33);

        JButton cancelBtn = UIFactory.createButton("Cancel");
        cancelBtn.setPreferredSize(btnDim);
        cancelBtn.addActionListener(e -> {
            originPicker.clear();
            destinationPicker.clear();
            dispose();
        });

        saveBtn = UIFactory.createButton("Save");
        saveBtn.setPreferredSize(btnDim);
        saveBtn.addActionListener(e -> {
            String routeCode = routeCodeField.getText().trim();
            Station origin = originPicker.getSelectedStation();
            Station destination = destinationPicker.getSelectedStation();
            String status = statusBox.getSelectedItem().toString();

            int state = RouteService.updateRoute(route.getRouteId(), routeCode, origin, destination, status);
            switch(state) {
                case RouteService.BLANK_ROUTE_CODE:
                    JOptionPane.showMessageDialog(this, "Please enter a route code", "Route Error", JOptionPane.ERROR_MESSAGE);
                    break;
                case RouteService.BLANK_ORIGIN_OR_DESTINATION:
                    JOptionPane.showMessageDialog(this, "Please select origin and destination stations", "Route Error", JOptionPane.ERROR_MESSAGE);
                    break;
                case RouteService.ANOTHER_ROUTE_CODE_EXISTS:
                    JOptionPane.showMessageDialog(this, "Another route with this code already exists", "Route Error", JOptionPane.ERROR_MESSAGE);
                    break;
                case RouteService.ANOTHER_ROUTE_EXISTS:
                    JOptionPane.showMessageDialog(this, "This route already exists", "Route Error", JOptionPane.ERROR_MESSAGE);
                    break;
                case RouteService.OK:
                    dispose();
                    break;
            }

        });

        panel.add(cancelBtn);
        panel.add(saveBtn);
        return panel;
    }
}

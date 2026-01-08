package com.mrt.admin.routes;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mrt.factory.UIFactory;
import com.mrt.models.Station;

public class StationPickerField extends JPanel {
    
    private JTextField displayField;
    private Station selectedStation;
    private JButton pickStationBtn;

    public StationPickerField(JDialog parent) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setOpaque(false);

        displayField = UIFactory.createTextField(10);
        displayField.setEnabled(false);
        displayField.setEditable(false);

        pickStationBtn = UIFactory.createButton("...");
        pickStationBtn.setPreferredSize(new Dimension(40, 40));
        pickStationBtn.addActionListener(e -> {
            StationPickerDialog dialog = new StationPickerDialog(parent);
            dialog.setVisible(true);

            Station pickedStation = dialog.getSelectedStation();
            if(pickedStation != null) {
                setSelectedStation(pickedStation);
            }
        });

        add(displayField);
        add(Box.createHorizontalStrut(10));
        add(pickStationBtn);
    }

    public void clear() {
        selectedStation = null;
        displayField.setText("");
    }

    public void setSelectedStation(Station selectedStation) {
        this.selectedStation = selectedStation;
        displayField.setText(selectedStation.getStationSummary());
    }

    public Station getSelectedStation() {
        return selectedStation;
    }

    public void disableEditing() {
        pickStationBtn.setEnabled(false);
    }
}

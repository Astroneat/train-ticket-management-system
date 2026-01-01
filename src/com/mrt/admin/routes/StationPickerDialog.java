package com.mrt.admin.routes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.model.City;
import com.mrt.model.Station;

public class StationPickerDialog extends JDialog {

    private JTextField searchField;

    private JTable stationTable;
    private DefaultTableModel tableModel;

    private JButton pickBtn;

    private Station selectedStation;

    public StationPickerDialog(JDialog frame) {
        super(frame, "Station Picker", true);

        setSize(800, 600);
        setLocationRelativeTo(frame);
        setResizable(false);
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setBackground(Universal.BACKGROUND_WHITE);
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout(0, 0));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        panel.add(UIFactory.createBoldLabel(
            "Select a Station",
            18
        ));

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        wrapper.setOpaque(false);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);

        top.add(createSearchPanel());
        wrapper.add(top, BorderLayout.NORTH);

        wrapper.add(createScrollPane(), BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        panel.add(UIFactory.createPlainLabel("Search:", 14));

        searchField = UIFactory.createTextField(15);
        searchField.setToolTipText("Search by code, name, or city");
        searchField.addActionListener(e -> {
            loadStations();
        });
        panel.add(searchField);

        JButton searchButton = UIFactory.createIconButton("src/com/mrt/img/search.png", new Dimension(24, 24));
        searchButton.setPreferredSize(new Dimension(40, 40));
        searchButton.addActionListener(e -> {
            loadStations();
        });
        panel.add(searchButton);

        return panel;
    }

    private JScrollPane createScrollPane() {
        tableModel = new DefaultTableModel(
            new String[] {"obj_station", "Station Code", "Station Name", "City"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if(column == 0) return true;
                return false;
            }
        };

        stationTable = new JTable(tableModel);
        stationTable.setRowHeight(30);
        stationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stationTable.getTableHeader().setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        stationTable.getTableHeader().setPreferredSize(new Dimension(0, 25));
        stationTable.getTableHeader().setReorderingAllowed(false);
        stationTable.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        stationTable.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                int row = stationTable.getSelectedRow();
                if(row != -1) {
                    pickBtn.setEnabled(true);
                } else {
                    pickBtn.setEnabled(false);
                }
            }
        });

        TableColumnModel columnModel = stationTable.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(0));
        columnModel.getColumn(0).setMaxWidth(100);
        columnModel.getColumn(0).setMinWidth(100);

        stationTable.setFocusable(false);

        loadStations();

        JScrollPane scrollPane = new JScrollPane(stationTable);
        return scrollPane;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);

        JButton cancelBtn = UIFactory.createButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(100, 33));
        cancelBtn.addActionListener(e -> {
            selectedStation = null;
            dispose();
        });

        pickBtn = UIFactory.createButton("Pick");
        pickBtn.setPreferredSize(new Dimension(100, 33));
        pickBtn.setEnabled(false);
        pickBtn.addActionListener(e -> {
            int row = stationTable.getSelectedRow();
            if(row != -1) {
                selectedStation = (Station) tableModel.getValueAt(row, 0);
                dispose();
            }
            else selectedStation = null;
        });

        panel.add(cancelBtn);
        panel.add(pickBtn);
        return panel;
    }

    private void loadStations() {
        String searchTerm = searchField.getText().trim();

        List<Object> args = new ArrayList<>();
        String sql = 
        """
        SELECT * FROM stations s
        INNER JOIN cities c ON s.city_id = c.city_id
        WHERE TRUE
        """;

        if(!searchTerm.isBlank()) {
            sql += " AND (s.station_code LIKE ? OR s.station_name LIKE ? OR c.city_name LIKE ?)";
            args.add("%" + searchTerm + "%");
            args.add("%" + searchTerm + "%");
            args.add("%" + searchTerm + "%");
        }
        sql += ";";

        List<Station> stationList = Universal.db().query(
            sql,
            rs -> Station.parseResultSet(rs),
            args.toArray()
        );

        tableModel.setRowCount(0);
        for(Station s: stationList) {
            tableModel.addRow(new Object[] {
                s,
                s.getStationCode(),
                s.getStationName(),
                City.getCityFromId(s.getCityId()).getCityName()
            });
        }
    }

    public Station getSelectedStation() {
        return selectedStation;
    }
}

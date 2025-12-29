package com.mrt.admin.trains;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import com.mrt.model.Route;
import com.mrt.model.Station;

public class RoutePickerDialog extends JDialog {

    private JTextField searchField;

    private JComboBox<Station> originBox;
    private JComboBox<Station> destBox;

    private JTable routeTable;
    private DefaultTableModel tableModel;

    private JButton pickBtn;

    private Route selectedRoute;

    private static final Station ALL_STATIONS = new Station(-1, "ALL", "All", "");

    public RoutePickerDialog(JDialog frame) {
        super(frame, "Route Picker", true);

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
            "Select a route",
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
        top.add(createFilterPanel());
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
        searchField.setToolTipText("Search by route code or station");
        searchField.addActionListener(e -> {
            loadRoutes();
        });
        panel.add(searchField);

        JButton searchButton = UIFactory.createIconButton("src/com/mrt/img/search.png", new Dimension(24, 24));
        searchButton.setPreferredSize(new Dimension(40, 40));
        searchButton.addActionListener(e -> {
            loadRoutes();
        });
        panel.add(searchButton);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setOpaque(false);

        panel.add(UIFactory.createPlainLabel("Origin:", 14));

        originBox = new JComboBox<>();
        panel.add(originBox);
        
        panel.add(UIFactory.createPlainLabel("Destination:", 14));
        
        destBox = new JComboBox<>();
        panel.add(destBox);

        loadAllStations();
        originBox.addActionListener(e -> {
            loadRoutes();
        });
        destBox.addActionListener(e -> {
            loadRoutes();
        });

        return panel;
    }

    private JScrollPane createScrollPane() {
        tableModel = new DefaultTableModel(
            new String[] {"route_id", "origin_station_id", "destination_station_id", "Route Code", "Origin", "Destination", "Distance (km)"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if(column == 0) return true;
                return false;
            }
        };

        routeTable = new JTable(tableModel);
        routeTable.setRowHeight(30);
        routeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        routeTable.getTableHeader().setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        routeTable.getTableHeader().setPreferredSize(new Dimension(0, 25));
        routeTable.getTableHeader().setReorderingAllowed(false);
        routeTable.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        routeTable.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                int row = routeTable.getSelectedRow();
                if(row != -1) {
                    pickBtn.setEnabled(true);
                } else {
                    pickBtn.setEnabled(false);
                }
            }
        });

        TableColumnModel columnModel = routeTable.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(2));
        columnModel.removeColumn(columnModel.getColumn(1));
        columnModel.removeColumn(columnModel.getColumn(0));
        columnModel.getColumn(0).setMaxWidth(100);
        columnModel.getColumn(0).setMinWidth(100);

        routeTable.setFocusable(false);

        loadRoutes();

        JScrollPane scrollPane = new JScrollPane(routeTable);
        return scrollPane;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);

        // JButton cancelBtn = new JButton("Exit");
        // cancelBtn.setPreferredSize(new Dimension(100, 33));
        // cancelBtn.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        // cancelBtn.addActionListener(e -> dispose());
        JButton cancelBtn = UIFactory.createButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(100, 33));
        cancelBtn.addActionListener(e -> {
            selectedRoute = null;
            dispose();
        });

        pickBtn = UIFactory.createButton("Pick");
        pickBtn.setPreferredSize(new Dimension(100, 33));
        pickBtn.setEnabled(false);
        pickBtn.addActionListener(e -> {
            int row = routeTable.getSelectedRow();
            if(row != -1) {
                selectedRoute = new Route(
                    (int) tableModel.getValueAt(row, 0),
                    (String) tableModel.getValueAt(row, 3),
                    (int) tableModel.getValueAt(row, 1),
                    (int) tableModel.getValueAt(row, 2),
                    (BigDecimal) tableModel.getValueAt(row, 4)
                );
                dispose();
            }
            else selectedRoute = null;
        });

        panel.add(cancelBtn);
        panel.add(pickBtn);
        return panel;
    }

    private void loadRoutes() {
        String searchTerm = searchField.getText().trim();
        Station origin = (Station) originBox.getSelectedItem();
        Station dest = (Station) destBox.getSelectedItem();

        List<Object> args = new ArrayList<>();
        String sql = 
        """
        SELECT * 
        FROM train_routes tr
        INNER JOIN stations s1 ON tr.origin_station_id = s1.station_id
        INNER JOIN stations s2 ON tr.destination_station_id = s2.station_id
        WHERE TRUE 
        """;

        if(!searchTerm.isBlank()) {
            sql += " AND (tr.route_code LIKE ? OR s1.station_name LIKE ? OR s2.station_name LIKE ?)";
            args.add("%" + searchTerm + "%");
            args.add("%" + searchTerm + "%");
            args.add("%" + searchTerm + "%");
        }
        if(origin != ALL_STATIONS) {
            sql += " AND (s1.station_code = ?)";
            args.add(origin.getStationCode());
        }
        if(dest != ALL_STATIONS) {
            sql += " AND (s2.station_code = ?)";
            args.add(dest.getStationCode());
        }

        sql += "\n";
        sql += "GROUP BY tr.route_id, tr.route_code, s1.station_name, s2.station_name, tr.distance_km\n";
        sql += "ORDER BY tr.route_code ASC;";

        List<Route> routes = Universal.db().query(
            sql,
            rs -> new Route(
                rs.getInt("route_id"),
                rs.getString("route_code"),
                rs.getInt("s1.station_id"),
                rs.getInt("s2.station_id"),
                rs.getBigDecimal("distance_km")
            ),
            args.toArray()
        );

        tableModel.setRowCount(0);
        for(Route row: routes) {
            tableModel.addRow(new Object[] {
                row.getRouteId(),
                row.getOriginStationId(),
                row.getDestinationStationId(),
                row.getRouteCode(),
                Station.getStationFromId(row.getOriginStationId()).getStationName(),
                Station.getStationFromId(row.getDestinationStationId()).getStationId(),
                row.getDistanceKm(),
            });
        }
    }

    private void loadAllStations() {
        List<Station> allStations = Universal.db().query(
            "SELECT * FROM stations ORDER BY station_name",
            rs -> Station.parseResultSet(rs)
        );

        originBox.addItem(ALL_STATIONS);
        destBox.addItem(ALL_STATIONS);
        for(Station s: allStations) {
            originBox.addItem(s);
            destBox.addItem(s);
        }
    }

    public Route getSelectedRoute() {
        return selectedRoute;
    }
}

package com.mrt.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.mrt.Universal;
import com.mrt.dialog.ScheduleDialog;
import com.mrt.model.Route;
import com.mrt.model.Station;
import com.mrt.model.Train;

public class TrainToRouteViewDialog extends JDialog {

    private Train train;

    private JTextField searchField;
    private JButton scheduleButton;

    private JComboBox<Station> originBox;
    private JComboBox<Station> destBox;

    private JTable routeTable;
    private DefaultTableModel tableModel;

    private List<RouteRow> routeList;

    private Runnable parentRefresh;

    private static final Station ALL_STATIONS = new Station(-1, "ALL", "All", "");

    public TrainToRouteViewDialog(JFrame frame, Train train, Runnable parentRefresh) {
        super(frame, "Route Assignment", true);
        this.train = train;
        this.parentRefresh = parentRefresh;

        setSize(900, 700);
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

        JLabel headerLabel = new JLabel(
            "<html>Editing train: <font color='#00b8ff'>" + train.toString() + "</font></html>"
        );
        headerLabel.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 18));
        panel.add(headerLabel);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
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

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        panel.add(searchLabel);

        searchField = new JTextField(15);
        searchField.setToolTipText("Search by route code or station");
        searchField.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        searchField.addActionListener(e -> {
            loadRoutesWithConstraints();
        });
        panel.add(searchField);

        JButton searchButton = new JButton();
        try {
            ImageIcon icon = new ImageIcon("src/com/mrt/img/search.png");
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            searchButton.setIcon(new ImageIcon(newImg));
        } catch (Exception ignored) {}
        searchButton.addActionListener(e -> {
            loadRoutesWithConstraints();
        });
        panel.add(searchButton);

        scheduleButton = new JButton("Schedules...");
        scheduleButton.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        scheduleButton.setPreferredSize(new Dimension(120, 36));
        scheduleButton.setEnabled(false);
        scheduleButton.addActionListener(e -> {
            ScheduleDialog scheduleDialog = new ScheduleDialog(
                this, 
                train, 
                Universal.db().queryOne(
                    "SELECT *\n" + 
                    "FROM train_routes tr\n" +
                    "INNER JOIN stations s1 ON s1.station_id = tr.origin_station_id\n" +
                    "INNER JOIN stations s2 ON s2.station_id = tr.destination_station_id\n" +
                    "WHERE route_id = ?",
                    rs -> Route.parseResultSet(rs),
                    tableModel.getValueAt(routeTable.getSelectedRow(), 0)
                ),
                () -> loadRoutesWithConstraints()
            );

            scheduleDialog.setVisible(true);
        });
        panel.add(scheduleButton);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setOpaque(false);

        JLabel originLabel = new JLabel("Origin:");
        originLabel.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        panel.add(originLabel);

        originBox = new JComboBox<>();
        panel.add(originBox);
        
        JLabel destLabel = new JLabel("Destination:");
        destLabel.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        panel.add(destLabel);
        
        destBox = new JComboBox<>();
        panel.add(destBox);

        loadAllStations();
        originBox.addActionListener(e -> {
            loadRoutesWithConstraints();
        });
        destBox.addActionListener(e -> {
            loadRoutesWithConstraints();
        });

        return panel;
    }

    private JScrollPane createScrollPane() {
        tableModel = new DefaultTableModel(
            new String[] {"ID", "Route Code", "Origin", "Destination", "Distance (km)", "Schedules"},
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
                    scheduleButton.setEnabled(true);
                } else {
                    scheduleButton.setEnabled(false);
                }
            }
        });

        TableColumnModel columnModel = routeTable.getColumnModel();
        columnModel.getColumn(1).setMaxWidth(100);
        columnModel.getColumn(1).setMinWidth(100);
        columnModel.removeColumn(columnModel.getColumn(0));

        routeTable.setFocusable(false);

        loadAllRoutes();

        JScrollPane scrollPane = new JScrollPane(routeTable);
        return scrollPane;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);

        JButton cancelBtn = new JButton("Exit");
        cancelBtn.setPreferredSize(new Dimension(100, 33));
        cancelBtn.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        cancelBtn.addActionListener(e -> dispose());

        panel.add(cancelBtn);
        return panel;
    }

    private void loadRoutesWithConstraints() {
        String searchTerm = searchField.getText().trim();
        Station origin = (Station) originBox.getSelectedItem();
        Station dest = (Station) destBox.getSelectedItem();

        List<Object> args = new ArrayList<>();
        String sql = "SELECT \n" + 
                    "    tr.route_id,\n" + 
                    "    tr.route_code,\n" + 
                    "    s1.station_name AS origin,\n" + 
                    "    s2.station_name AS destination,\n" + 
                    "    tr.distance_km,\n" + 
                    "    SUM(\n" + 
                    "        CASE \n" + 
                    "            WHEN ts.status = 'scheduled' THEN 1 \n" + 
                    "            ELSE 0 \n" + 
                    "        END\n" + 
                    "    ) AS active_trips,\n" + 
                    "    SUM(\n" + 
                    "        CASE \n" + 
                    "            WHEN ts.status IS NOT NULL \n" + 
                    "             AND ts.status = 'completed' THEN 1 \n" + 
                    "            ELSE 0 \n" + 
                    "        END\n" + 
                    "    ) AS total_trips\n" + 
                    "FROM train_routes tr\n" + 
                    "LEFT JOIN train_schedules ts ON tr.route_id = ts.route_id AND ts.train_id = ?\n" + 
                    "INNER JOIN stations s1  ON tr.origin_station_id = s1.station_id\n" + 
                    "INNER JOIN stations s2  ON tr.destination_station_id = s2.station_id\n" +
                    "WHERE TRUE";

        args.add(train.getTrainId());
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
        sql += "ORDER BY active_trips DESC, total_trips DESC, tr.route_code ASC;";

        List<RouteRow> routes = Universal.db().query(
            sql,
            rs -> new RouteRow(
                rs.getInt("route_id"),
                rs.getString("route_code"),
                rs.getString("origin"),
                rs.getString("destination"),
                rs.getBigDecimal("distance_km"),
                rs.getInt("active_trips"),
                rs.getInt("total_trips")
            ),
            args.toArray()
        );
        updateTable(routes);
    }

    private void loadAllRoutes() {
        routeList = Universal.db().query(
            "SELECT \n" + 
            "    tr.route_id,\n" + 
            "    tr.route_code,\n" + 
            "    s1.station_name AS origin,\n" + 
            "    s2.station_name AS destination,\n" + 
            "    tr.distance_km,\n" + 
            "    SUM(\n" + 
            "        CASE \n" + 
            "            WHEN ts.status = 'scheduled' THEN 1 \n" + 
            "            ELSE 0 \n" + 
            "        END\n" + 
            "    ) AS active_trips,\n" + 
            "    SUM(\n" + 
            "        CASE \n" + 
            "            WHEN ts.status IS NOT NULL \n" + 
            "             AND ts.status = 'completed' THEN 1 \n" + 
            "            ELSE 0 \n" + 
            "        END\n" + 
            "    ) AS total_trips\n" + 
            "FROM train_routes tr\n" + 
            "LEFT JOIN train_schedules ts ON tr.route_id = ts.route_id AND ts.train_id = ?\n" + 
            "INNER JOIN stations s1  ON tr.origin_station_id = s1.station_id\n" + 
            "INNER JOIN stations s2  ON tr.destination_station_id = s2.station_id\n" + 
            "GROUP BY tr.route_id, tr.route_code, s1.station_name, s2.station_name, tr.distance_km\n" + 
            "ORDER BY active_trips DESC, total_trips DESC, tr.route_code ASC;",
            rs -> new RouteRow(
                rs.getInt("route_id"),
                rs.getString("route_code"),
                rs.getString("origin"),
                rs.getString("destination"),
                rs.getBigDecimal("distance_km"),
                rs.getInt("active_trips"),
                rs.getInt("total_trips")
            ),
            train.getTrainId()
        );
        updateTable(routeList);
    }

    private void updateTable(List<RouteRow> routes) {
        tableModel.setRowCount(0);
        for(RouteRow row: routes) {
            tableModel.addRow(new Object[] {
                row.getRouteId(),
                row.getRouteCode(),
                row.getOrigin(),
                row.getDestination(),
                row.getDistanceKm(),
                row.getActiveTrips() + " scheduled, " + row.getCompletedTrips() + " completed"
            });
        }
        parentRefresh.run();
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

    private class RouteRow {
        private int routeId;
        private String routeCode;
        private String origin;
        private String destinaton;
        private BigDecimal distanceKm;
        private int activeTrips;
        private int completedTrips;
        
        public RouteRow(int routeId, String routeCode, String origin, String destinaton, BigDecimal distanceKm, int activeTrips, int completedTrips) {
            this.routeId = routeId;
            this.routeCode = routeCode;
            this.origin = origin;
            this.destinaton = destinaton;
            this.distanceKm = distanceKm;
            this.activeTrips = activeTrips;
            this.completedTrips = completedTrips;
        }

        public int getRouteId() { return routeId; }
        public String getRouteCode() { return routeCode; }
        public String getOrigin() { return origin; }
        public String getDestination() { return destinaton; }
        public BigDecimal getDistanceKm() { return distanceKm; }
        public int getActiveTrips() { return activeTrips; }
        public int getCompletedTrips() { return completedTrips; }
    }
}

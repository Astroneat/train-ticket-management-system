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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import com.mrt.Universal;
import com.mrt.dbobject.Station;
import com.mrt.dbobject.Train;

class RouteRow {
    private boolean isActive;
    private int routeId;
    private String routeCode;
    private String origin;
    private String destinaton;
    private BigDecimal distanceKm;
    
    public RouteRow(boolean isActive, int routeId, String routeCode, String origin, String destinaton, BigDecimal distanceKm) {
        this.isActive = isActive;
        this.routeId = routeId;
        this.routeCode = routeCode;
        this.origin = origin;
        this.destinaton = destinaton;
        this.distanceKm = distanceKm;
    }

    public boolean getIsActive() { return isActive; }
    public int getRouteId() { return routeId; }
    public String getRouteCode() { return routeCode; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destinaton; }
    public BigDecimal getDistanceKm() { return distanceKm; }
}

class RouteAssignmentRow {
    private String routeCode;
    private boolean originalActive;
    private boolean currentActive;
    private boolean hasSchedule;

    public RouteAssignmentRow(String routeCode, boolean active) {
        this.routeCode = routeCode;
        this.originalActive = active;
        this.currentActive = active;
    }
    public void setCurrentActive(boolean currentActive) { this.currentActive = currentActive; }
    public boolean getCurrentActive() { return currentActive; }
    public void setOriginalActive(boolean originalActive) { this.originalActive = originalActive; }
    public boolean getOriginalActive() { return originalActive; }
    public String getRouteCode() { return routeCode; }
    public boolean getHasSchedule() { return hasSchedule; }
    public void setHasSchedule(boolean hasSchedule) { this.hasSchedule = hasSchedule; } 

    public boolean isChanged() {
        return originalActive != currentActive;
    }
}

public class RouteAssignmentDialog extends JDialog {

    private Train train;
    private Runnable onSuccess;

    private JTextField searchField;
    private JComboBox<Station> originBox;
    private JComboBox<Station> destBox;

    private JTable routeTable;
    private DefaultTableModel tableModel;

    private List<RouteRow> routeList;
    private HashMap<Integer, RouteAssignmentRow> changedRows;

    private static final Station ALL_STATIONS = new Station(-1, "ALL", "All", "");

    public RouteAssignmentDialog(JFrame frame, Train train, Runnable onSuccess) {
        super(frame, "Route Assignment", true);
        this.train = train;
        this.onSuccess = onSuccess;

        changedRows = new HashMap<>();

        setSize(800, 550);
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
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(createSearchPanel());
        panel.add(createFilterPanel());
        panel.add(createScrollPane());

        return panel;
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
            new String[] {"Active", "ID", "Route Code", "Origin", "Destination", "Distance (km)"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if(column == 0) return true;
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if(column == 0) return Boolean.class;
                if(column == 5) return BigDecimal.class;
                return String.class;
            }
        };
        tableModel.addTableModelListener(e -> {
            if(e.getType() == TableModelEvent.UPDATE && e.getColumn() == 0) {
                int row = e.getFirstRow();
                boolean active = (boolean) tableModel.getValueAt(row, 0);
                int routeId = (int) tableModel.getValueAt(row, 1);
                changedRows.get(routeId).setCurrentActive(active);
            }
        });

        routeTable = new JTable(tableModel);
        routeTable.setRowHeight(30);
        routeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        routeTable.getTableHeader().setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        routeTable.getTableHeader().setPreferredSize(new Dimension(0, 20));
        routeTable.getTableHeader().setReorderingAllowed(false);
        routeTable.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        routeTable.getColumnModel().getColumn(0).setMaxWidth(50);
        routeTable.getColumnModel().getColumn(0).setMinWidth(50);
        routeTable.getColumnModel().getColumn(1).setMaxWidth(50);
        routeTable.getColumnModel().getColumn(1).setMinWidth(50);
        routeTable.getColumnModel().getColumn(2).setMaxWidth(100);
        routeTable.getColumnModel().getColumn(2).setMinWidth(100);
        routeTable.getColumnModel().getColumn(5).setMaxWidth(100);
        routeTable.getColumnModel().getColumn(5).setMinWidth(100);
        routeTable.setFocusable(false);

        loadAllRoutes();

        JScrollPane scrollPane = new JScrollPane(routeTable);
        scrollPane.setPreferredSize(new Dimension(1000, routeTable.getRowHeight() * 10 + routeTable.getTableHeader().getPreferredSize().height));
        return scrollPane;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = new JButton("Save changes");
        saveBtn.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        saveBtn.addActionListener(e -> {
            // for(RouteAssignmentRow row: changedRows) {
            List<String> unassignedRoutes = new ArrayList<>();
            for(Map.Entry<Integer, RouteAssignmentRow> entry: changedRows.entrySet()) {
                int routeId = entry.getKey();
                RouteAssignmentRow row = entry.getValue();
                if(!row.isChanged()) continue;

                if(row.getCurrentActive()) {
                    Universal.db().execute(
                        "INSERT INTO train_route_assignments(train_id, route_id) VALUES (?, ?)",
                        train.getTrainId(),
                        routeId
                    );
                }
                else {
                    if(row.getOriginalActive() && row.getHasSchedule()) {
                        unassignedRoutes.add(row.getRouteCode());
                    }
                    else {
                        Universal.db().execute(
                            "DELETE FROM train_route_assignments WHERE train_id = ? AND route_id = ?",
                            train.getTrainId(),
                            routeId
                        );
                    }
                }
            }

            if(!unassignedRoutes.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Cannot unassign scheduled routes: " + String.join(", ", unassignedRoutes),
                    "Assignment Warning",
                    JOptionPane.WARNING_MESSAGE
                );
            }
            onSuccess.run();
            dispose();
        });

        panel.add(cancelBtn);
        panel.add(saveBtn);
        return panel;
    }

    private void loadRoutesWithConstraints() {
        String searchTerm = searchField.getText().trim();
        Station origin = (Station) originBox.getSelectedItem();
        Station dest = (Station) destBox.getSelectedItem();

        List<Object> args = new ArrayList<>();
        String sql = "SELECT \n" + //
                    "    tra.train_id IS NOT NULL is_assigned,\n" + //
                    "    tr.route_id,\n" + //
                    "    tr.route_code,\n" + //
                    "    s1.station_name origin,\n" + //
                    "    s2.station_name destination,\n" + //
                    "    tr.distance_km\n" + //
                    "FROM train_routes tr\n" + //
                    "LEFT JOIN train_route_assignments tra ON tr.route_id = tra.route_id AND tra.train_id = ?\n" + //
                    "INNER JOIN stations s1 ON tr.origin_station_id = s1.station_id\n" + //
                    "INNER JOIN stations s2 ON tr.destination_station_id = s2.station_id\n" + //
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

        sql += " ORDER BY is_assigned DESC, tr.route_id ASC;";

        List<RouteRow> routes = Universal.db().query(
            sql,
            rs -> new RouteRow(
                rs.getBoolean("is_assigned"),
                rs.getInt("route_id"),
                rs.getString("route_code"),
                rs.getString("origin"),
                rs.getString("destination"),
                rs.getBigDecimal("distance_km")
            ),
            args.toArray()
        );
        updateTable(routes);
    }

    private void loadAllRoutes() {
        routeList = Universal.db().query(
            "SELECT \n" + //
            "    tra.train_id IS NOT NULL is_assigned,\n" + //
            "    tr.route_id,\n" + //
            "    tr.route_code,\n" + //
            "    s1.station_name origin,\n" + //
            "    s2.station_name destination,\n" + //
            "    tr.distance_km\n" + //
            "FROM train_routes tr\n" + //
            "LEFT JOIN train_route_assignments tra ON tr.route_id = tra.route_id AND tra.train_id = ?\n" + //
            "INNER JOIN stations s1 ON tr.origin_station_id = s1.station_id\n" + //
            "INNER JOIN stations s2 ON tr.destination_station_id = s2.station_id\n" + //
            "ORDER BY is_assigned DESC, tr.route_id ASC;",
            rs -> new RouteRow(
                rs.getBoolean("is_assigned"),
                rs.getInt("route_id"),
                rs.getString("route_code"),
                rs.getString("origin"),
                rs.getString("destination"),
                rs.getBigDecimal("distance_km")
            ),
            train.getTrainId()
        );
        updateTable(routeList);
    }

    private void updateTable(List<RouteRow> routes) {
        tableModel.setRowCount(0);
        for(RouteRow row: routes) {
            // changedRows.add(new RouteAssignmentRow(row.getRouteId(), row.getIsActive()));
            boolean activeOverride = false;
            RouteAssignmentRow currentAssignmentRow = changedRows.get(row.getRouteId());
            if(currentAssignmentRow == null) {
                changedRows.put(row.getRouteId(), new RouteAssignmentRow(row.getRouteCode(), row.getIsActive()));
                currentAssignmentRow = changedRows.get(row.getRouteId());
            }
            else {
                activeOverride = true;
            }

            currentAssignmentRow.setHasSchedule(Universal.db().queryOne(
                "SELECT EXISTS (\n" + //
                "   SELECT ts.schedule_id \n" + //
                "   FROM train_schedules ts \n" + //
                "   INNER JOIN train_route_assignments tra ON ts.assignment_id = tra.assignment_id\n" + //
                "   WHERE tra.train_id = ? AND tra.route_id = ?\n" + //
                ") exist;",
                rs -> rs.getBoolean("exist"),
                train.getTrainId(),
                row.getRouteId()
            ));

            tableModel.addRow(new Object[] {
                activeOverride ? changedRows.get(row.getRouteId()).getCurrentActive() : row.getIsActive(),
                row.getRouteId(),
                row.getRouteCode(),
                row.getOrigin(),
                row.getDestination(),
                row.getDistanceKm()
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
}

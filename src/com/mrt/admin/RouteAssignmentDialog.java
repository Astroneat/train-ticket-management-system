package com.mrt.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.util.List;

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

import com.mrt.Universal;
import com.mrt.dbobject.Station;
import com.mrt.dbobject.Train;

class RouteRow {
    private boolean isActive;
    private String routeCode;
    private String origin;
    private String destinaton;
    private float distanceKm;
    
    public RouteRow(boolean isActive, String routeCode, String origin, String destinaton, float distanceKm) {
        this.isActive = isActive;
        this.routeCode = routeCode;
        this.origin = origin;
        this.destinaton = destinaton;
        this.distanceKm = distanceKm;
    }

    public boolean getIsActive() { return isActive; }
    public String getRouteCode() { return routeCode; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destinaton; }
    public float getDistanceKm() { return distanceKm; }
}

public class RouteAssignmentDialog extends JDialog {

    private Train train;

    private JTextField searchField;
    private JComboBox<Station> originBox;
    private JComboBox<Station> destBox;

    private JTable routeTable;
    private DefaultTableModel tableModel;

    private static final Station ALL_STATIONS = new Station(-1, "ALL", "All", "");

    public RouteAssignmentDialog(JFrame frame, Train train) {
        super(frame, "Route Assignment", true);
        this.train = train;

        setSize(800, 500);
        setLocationRelativeTo(frame);
        setResizable(false);
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setBackground(Universal.BACKGROUND_WHITE);
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout(0, 0));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
        add(createScrollPane(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        JLabel headerLabel = new JLabel(
            "<html>Editing train: <font color='red'>" + train.toString() + "</font></html>"
        );
        headerLabel.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 18));
        panel.add(headerLabel);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        topPanel.add(createSearchPanel());
        topPanel.add(createFilterPanel());

        return topPanel;
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
        panel.add(searchField);

        JButton searchButton = new JButton();
        try {
            ImageIcon icon = new ImageIcon("src/com/mrt/img/search.png");
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            searchButton.setIcon(new ImageIcon(newImg));
        } catch (Exception ignored) {}
        searchButton.addActionListener(e -> {

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

        return panel;
    }

    private JScrollPane createScrollPane() {
        tableModel = new DefaultTableModel(
            new String[] {"Active", "Route Code", "Origin", "Destination", "Distance (km)"},
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
        routeTable.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        routeTable.getColumnModel().getColumn(0).setMaxWidth(60);
        routeTable.getColumnModel().getColumn(0).setMinWidth(60);
        routeTable.getColumnModel().getColumn(1).setMaxWidth(100);
        routeTable.getColumnModel().getColumn(1).setMinWidth(100);
        routeTable.setFocusable(false);

        loadAllRoutes();

        JScrollPane scrollPane = new JScrollPane(routeTable);
        scrollPane.setPreferredSize(new Dimension(1000, routeTable.getRowHeight() * 10 + routeTable.getTableHeader().getPreferredSize().height));
        return scrollPane;
    }

    private void loadAllRoutes() {
        List<RouteRow> routes = Universal.db().query(
            "SELECT \n" + //
            "    tra.train_id IS NOT NULL is_assigned,\n" + //
            "    tr.route_code,\n" + //
            "    s1.station_name origin,\n" + //
            "    s2.station_name destination,\n" + //
            "    tr.distance_km\n" + //
            "FROM train_routes tr\n" + //
            "LEFT JOIN train_route_assignments tra ON tr.route_id = tra.route_id AND tra.train_id = ?\n" + //
            "INNER JOIN stations s1 ON tr.origin_station_id = s1.station_id\n" + //
            "INNER JOIN stations s2 ON tr.destination_station_id = s2.station_id\n" + //
            "ORDER BY is_assigned DESC, tr.route_code ASC;",
            rs -> new RouteRow(
                rs.getBoolean("is_assigned"),
                rs.getString("route_code"),
                rs.getString("origin"),
                rs.getString("destination"),
                rs.getFloat("distance_km")
            ),
            train.getTrainId()
        );

        tableModel.setRowCount(0);
        for(RouteRow row: routes) {
            tableModel.addRow(new Object[] {
                row.getIsActive(),
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

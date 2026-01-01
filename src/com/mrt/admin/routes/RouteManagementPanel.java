package com.mrt.admin.routes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.mrt.Universal;
import com.mrt.admin.AdminMainFrame;
import com.mrt.dialog.FormDialog;
import com.mrt.factory.UIFactory;
import com.mrt.model.City;
import com.mrt.model.Route;
import com.mrt.model.Station;
import com.mrt.model.Train;
import com.mrt.services.RouteService;

public class RouteManagementPanel extends JPanel {

    private AdminMainFrame frame;

    private JTable routeTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> activeFilter;
    private JComboBox<String> sortBox;
    private JCheckBox sortDesc;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton schedulesButton;

    private JLabel numTableRowCount;
    private JLabel numActiveTrains;

    public RouteManagementPanel(AdminMainFrame frame) {
        this.frame = frame;

        setLayout(new BorderLayout(0, 10));
        // setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Universal.BACKGROUND_WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        topPanel.add(createTitlePanel());
        topPanel.add(createSearchPanel());
        topPanel.add(createFilterPanel());
        topPanel.add(createSortPanel());
        topPanel.add(createActionPanel());

        add(topPanel, BorderLayout.NORTH);
        add(createScrollPane(), BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel header = new JPanel();
        header.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(1000, 50));

        // JLabel title = new JLabel("Route Management");
        // title.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 28));
        // header.add(title);
        header.add(UIFactory.createBoldLabel("Route Management", 28));

        header.add(Box.createHorizontalStrut(5));

        // numActiveTrains = new JLabel();
        // numActiveTrains.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        numActiveTrains = UIFactory.createPlainLabel("", 14);
        header.add(numActiveTrains);

        return header;
    }

    private JPanel createSearchPanel() {
        JPanel search = new JPanel();
        search.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        search.setOpaque(false);
        search.setMaximumSize(new Dimension(1000, 50));

        // JLabel searchLabel = new JLabel("Search:");
        // searchLabel.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 16));
        // search.add(searchLabel);
        search.add(UIFactory.createPlainLabel("Search:", 16));

        // searchField = new JTextField(30);
        // searchField.setToolTipText("Search by code");
        // searchField.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        // searchField.setBorder(BorderFactory.createCompoundBorder(
        //     BorderFactory.createLineBorder(Color.BLACK, 1),
        //     BorderFactory.createEmptyBorder(5, 5, 5, 5)
        // ));
        searchField = UIFactory.createTextField(30);
        searchField.setToolTipText("Search by code or station");
        searchField.addActionListener(e -> {
            loadRoutes();
        });
        search.add(searchField);

        // JButton searchButton = new JButton();
        // try {
        //     ImageIcon icon = new ImageIcon("src/com/mrt/img/search.png");
        //     Image img = icon.getImage();
        //     Image newImg = img.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        //     searchButton.setIcon(new ImageIcon(newImg));
        // } catch (Exception ignored) {}
        JButton searchButton = UIFactory.createIconButton("src/com/mrt/img/search.png", new Dimension(24, 24));
        searchButton.addActionListener(e -> {
            loadRoutes();
        });
        search.add(searchButton);

        // numTableRowCount = new JLabel();
        // numTableRowCount.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        numTableRowCount = UIFactory.createPlainLabel("", 14);
        search.add(numTableRowCount);

        return search;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setMaximumSize(new Dimension(1000, 30));
        panel.setOpaque(false);

        // JLabel filterLabel = new JLabel("Filter by status:");
        // filterLabel.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 16));
        // panel.add(filterLabel);
        panel.add(UIFactory.createPlainLabel("Filter by status:", 16));

        activeFilter = new JComboBox<>(new String[] {
            "---", "active", "inactive"
        });
        activeFilter.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        activeFilter.addActionListener(e -> {
            loadRoutes();
        });
        panel.add(activeFilter);

        JButton clearFilterButton = new JButton("Clear filter");
        clearFilterButton.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        clearFilterButton.addActionListener(e -> {
            activeFilter.setSelectedIndex(0);
            loadRoutes();
        });
        panel.add(clearFilterButton);

        return panel;
    }

    private JPanel createSortPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setMaximumSize(new Dimension(1000, 30));
        panel.setOpaque(false);

        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 16));
        panel.add(sortLabel);

        sortBox = new JComboBox<>(new String[] {
            "ID", "Scheduled"
        });
        sortBox.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        sortBox.addActionListener(e -> {
            loadRoutes();
        });
        panel.add(sortBox);

        sortDesc = new JCheckBox("Descending");
        sortDesc.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        sortDesc.addActionListener(e -> {
            loadRoutes();
        });
        panel.add(sortDesc);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setOpaque(false);

        addButton = createActionButton("Add");
        editButton = createActionButton("Edit");
        deleteButton = createActionButton("Delete");
        refreshButton = createActionButton("Refresh");
        schedulesButton = createActionButton("Schedules...");

        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        schedulesButton.setEnabled(false);

        addButton.addActionListener(e -> {
            AddRouteDialog dialog = new AddRouteDialog(frame);

            dialog.setVisible(true);
            loadRoutes();
        });

        editButton.addActionListener(e -> {
            int row = routeTable.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(frame, "This cannot happen. Please contact nvtd.", "???", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Route selectedRoute = (Route) tableModel.getValueAt(row, 0);
            EditRouteDialog dialog = new EditRouteDialog(frame, selectedRoute);
            dialog.setVisible(true);

            loadRoutes();
        });

        deleteButton.addActionListener(e -> {
            int row = routeTable.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(frame, "This cannot happen. Please contact nvtd.", "???", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Route selectedRoute = (Route) tableModel.getValueAt(row, 0);
            int option = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this route?", "Confirm deletion", JOptionPane.YES_NO_OPTION);
            if(option == JOptionPane.YES_OPTION) {
                boolean canDelete = RouteService.deleteRoute(selectedRoute);
                if(!canDelete) {
                    JOptionPane.showMessageDialog(frame, "Cannot delete this route because it has existing schedules", "Error", JOptionPane.ERROR_MESSAGE);;
                    return;
                }
                loadRoutes();
            }
        });

        schedulesButton.addActionListener(e -> {
            int row = routeTable.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(frame, "This cannot happen. Please contact nvtd.", "???", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Route selectedRoute = (Route) tableModel.getValueAt(row, 0);
            RouteSchedulesDialog dialog = new RouteSchedulesDialog(frame, selectedRoute);
            dialog.setVisible(true);
            loadRoutes();
        });

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            activeFilter.setSelectedIndex(0);
            loadRoutes();
        });

        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(schedulesButton);
        actionPanel.add(refreshButton);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setMaximumSize(new Dimension(1000, 60));
        wrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        wrapper.add(actionPanel, gbc);
        return wrapper;
    }

    private JButton createActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(120, 36));
        btn.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        return btn;
    }

    private JScrollPane createScrollPane() {
        tableModel = new DefaultTableModel(
            new String[] {"obj_route", "Route Code", "Origin", "Destination", "Schedules", "Status"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        routeTable = new JTable(tableModel);
        routeTable.setRowHeight(30);
        routeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        routeTable.getTableHeader().setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        routeTable.getTableHeader().setPreferredSize(new Dimension(10, 25));
        routeTable.getTableHeader().setReorderingAllowed(false);
        routeTable.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));

        TableColumnModel columnModel = routeTable.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(0));
        columnModel.getColumn(0).setMaxWidth(100);
        columnModel.getColumn(0).setMinWidth(100);
        columnModel.getColumn(3).setMaxWidth(100);
        columnModel.getColumn(3).setMinWidth(100);
        columnModel.getColumn(4).setMaxWidth(100);
        columnModel.getColumn(4).setMinWidth(100);
        
        routeTable.setFocusable(false);
        routeTable.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                int selectedRow = routeTable.getSelectedRow();
                if(selectedRow != -1) {
                    editButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    schedulesButton.setEnabled(true);
                }
                else {
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                    schedulesButton.setEnabled(false);
                }
            }
        });

        loadRoutes();

        JScrollPane scrollPane = new JScrollPane(routeTable);
        return scrollPane;
    }

    private int countAllRoutes() {
        return Universal.db().queryOne(
            "SELECT COUNT(*) cnt FROM train_routes;",
            rs -> rs.getInt("cnt")
        );
    }
    private int countActiveRoutes() {
        return Universal.db().queryOne(
            "SELECT COUNT(*) cnt FROM train_routes WHERE status = \'active\'", 
            rs -> rs.getInt("cnt")
        );
    }

    private void loadRoutes() {
        tableModel.setRowCount(0);
        String searchTerm = searchField.getText().trim();
        String status = "";
        if(activeFilter.getSelectedIndex() != 0) status = activeFilter.getSelectedItem().toString();

        String orderBy = "";
        switch(sortBox.getSelectedItem().toString()) {
            case "ID":
                orderBy = "tr.route_id";
                break;
            case "Scheduled":
                orderBy = "scheduled";
                break;
        }
        String sortOrder = "ASC";
        if(sortDesc.isSelected()) sortOrder = "DESC";

        List<String> args = new ArrayList<String>();
        String sql = 
        """
            SELECT
                tr.route_id,
                tr.route_code,
                tr.origin_station_id,
                tr.destination_station_id,
                s1.station_name,
                c1.city_name,
                s2.station_name,
                c2.city_name,
                COUNT(ts.schedule_id) scheduled,
                tr.status
            FROM train_routes tr
            INNER JOIN stations s1 ON tr.origin_station_id = s1.station_id
            INNER JOIN cities c1 ON s1.city_id = c1.city_id
            INNER JOIN stations s2 ON tr.destination_station_id = s2.station_id
            INNER JOIN cities c2 ON s2.city_id = c2.city_id
            LEFT JOIN train_schedules ts ON ts.route_id = tr.route_id AND ts.status = 'scheduled'
            WHERE TRUE
        """;
        if(!searchTerm.isBlank()) {
            sql += " AND (tr.route_code LIKE ? OR s1.station_name LIKE ? OR c1.city_name LIKE ? OR s2.station_name LIKE ? OR c2.city_name LIKE ?)";
            args.add("%" + searchTerm + "%");
            args.add("%" + searchTerm + "%");
            args.add("%" + searchTerm + "%");
            args.add("%" + searchTerm + "%");
            args.add("%" + searchTerm + "%");
        }
        if(!status.isBlank()) {
            sql += " AND (tr.status = ?)";
            args.add(status);
        }
        sql += "\n";
        sql += "GROUP BY tr.route_id\n";
        sql += "ORDER BY " + orderBy + " " + sortOrder + ";";

        List<Integer> numScheduled = new ArrayList<>();
        List<Route> routeList = Universal.db().query(
            sql,
            rs -> {
                Route route = new Route(
                    rs.getInt("tr.route_id"),
                    rs.getString("tr.route_code"),
                    rs.getInt("tr.origin_station_id"),
                    rs.getInt("tr.destination_station_id"),
                    rs.getString("tr.status")
                );
                numScheduled.add(rs.getInt("scheduled"));
                return route;
            },
            args.toArray(new Object[args.size()])
        );

        int numScheduledIdx = 0;
        for(Route route: routeList) {
            Station origin = Station.getStationFromId(route.getOriginStationId());
            Station destination = Station.getStationFromId(route.getDestinationStationId());
            tableModel.addRow(new Object[] {
                route,
                route.getRouteCode(),
                origin.getStationName() + ", " + City.getCityFromId(origin.getCityId()).getCityName(),
                destination.getStationName() + ", " + City.getCityFromId(destination.getCityId()).getCityName(),
                numScheduled.get(numScheduledIdx),
                route.getStatus()
            });
            numScheduledIdx++;
        }

        int returnedSize = routeList.size();
        int numAllRoutes = countAllRoutes();
        numTableRowCount.setText(returnedSize + " result" + (returnedSize > 1 ? "s" : "") + (returnedSize == numAllRoutes ? "" : " (" + numAllRoutes + " total)"));
        numActiveTrains.setText(countActiveRoutes() + " active routes");
    }
}
package com.mrt.admin.stations;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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
import com.mrt.model.Station;

public class StationManagementPanel extends JPanel {

    private AdminMainFrame frame;

    private JTable stationTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private List<City> cities;
    private JComboBox<City> filterBox;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;

    private JLabel numTableRowCount;

    private City ALL_CITIES = new City(-1, "All");

    public StationManagementPanel(AdminMainFrame frame) {
        this.frame = frame;

        setLayout(new BorderLayout(0, 10));
        // setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Universal.BACKGROUND_WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        loadAllCities();

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        topPanel.add(createTitlePanel());
        topPanel.add(createSearchPanel());
        topPanel.add(createFilterPanel());
        topPanel.add(createActionPanel());

        add(topPanel, BorderLayout.NORTH);
        add(createstationTablePanel(), BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel header = new JPanel();
        header.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(1000, 50));

        // JLabel title = new JLabel("Station Management");
        // title.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 28));
        // header.add(title);
        header.add(UIFactory.createBoldLabel("Station Management", 28));

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
        // searchField.setToolTipText("Search by code, name, or city");
        // searchField.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        // searchField.setBorder(BorderFactory.createCompoundBorder(
        //     BorderFactory.createLineBorder(Color.BLACK, 1),
        //     BorderFactory.createEmptyBorder(5, 5, 5, 5)
        // ));
        searchField = UIFactory.createTextField(30);
        searchField.setToolTipText("Search by code, name, or city");
        searchField.addActionListener(e -> {
            loadStations();
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
            loadStations();
        });
        search.add(searchButton);

        // numTableRowCount = new JLabel();
        // numTableRowCount.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        // search.add(numTableRowCount);
        numTableRowCount = UIFactory.createPlainLabel("", 14);
        search.add(numTableRowCount);

        return search;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setMaximumSize(new Dimension(1000, 30));
        panel.setOpaque(false);

        // JLabel filterLabel = new JLabel("Filter by role:");
        // filterLabel.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 16));
        // panel.add(filterLabel);
        panel.add(UIFactory.createPlainLabel("City:", 16));

        // filterBox = new JComboBox<>();
        // filterBox.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        filterBox = UIFactory.createComboBox();
        filterBox.addItem(ALL_CITIES);
        for(City c: cities) {
            filterBox.addItem(c);
        }
        filterBox.addActionListener(e -> {
            loadStations();
        });
        panel.add(filterBox);

        // JButton clearFilterButton = new JButton("Clear filter");
        // clearFilterButton.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        JButton clearFilterButton = UIFactory.createButton("Clear filter");
        clearFilterButton.addActionListener(e -> {
            filterBox.setSelectedIndex(0);
            loadStations();
        });
        panel.add(clearFilterButton);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setOpaque(false);

        // addButton = createActionButton("Add");
        // editButton = createActionButton("Edit");
        // deleteButton = createActionButton("Delete");
        // refreshButton = createActionButton("Refresh");
        Dimension btnDim = new Dimension(120, 36);
        addButton = UIFactory.createButton("Add");
        addButton.setPreferredSize(btnDim);
        editButton = UIFactory.createButton("Edit");
        editButton.setPreferredSize(btnDim);
        deleteButton = UIFactory.createButton("Delete");
        deleteButton.setPreferredSize(btnDim);
        refreshButton = UIFactory.createButton("Refresh");
        refreshButton.setPreferredSize(btnDim);

        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        addButton.addActionListener(e -> {
            FormDialog addDialog = new FormDialog(frame, "Add Station", new Dimension(400, 310));

            JTextField codeField = addDialog.addTextField("Station code:");
            JTextField nameField = addDialog.addTextField("Station name:");
            JComboBox<City> cityBox = addDialog.addComboBox("City:", cities.toArray(new City[cities.size()]));

            JButton saveBtn = addDialog.addButtonRow();
            saveBtn.addActionListener(saveEv -> {
                try {
                    String code = codeField.getText().trim();
                    String name = nameField.getText().trim();
                    City city = (City) cityBox.getSelectedItem();

                    if(code.isBlank() || name.isBlank()) {
                        JOptionPane.showMessageDialog(addDialog, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Station station = Universal.db().queryOne(
                        "SELECT * FROM stations WHERE station_code = ?",
                        rs -> Station.parseResultSet(rs),
                        code
                    );
                    if(station != null) {
                        JOptionPane.showMessageDialog(addDialog, "Another station with this code already exists", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Universal.db().execute(
                        "INSERT INTO stations(station_code, station_name, city_id) VALUES (?, ?, ?);",
                        code,
                        name,
                        city.getCityId()
                    );

                    loadStations();
                    addDialog.dispose();
                } catch(Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(addDialog, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            addDialog.setVisible(true);
        });

        editButton.addActionListener(e -> {
            int row = stationTable.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(frame, "This cannot happen. Please contact nvtd.", "???", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int stationId = (int) tableModel.getValueAt(row, 0);
            int cityId = (int) tableModel.getValueAt(row, 1);
            String currentCode = tableModel.getValueAt(row, 2).toString();
            String currentName = tableModel.getValueAt(row, 3).toString();
            City currentCity = City.getCityFromId(cityId);
            System.out.println(cityId);

            FormDialog editDialog = new FormDialog(frame, "Edit Station", new Dimension(400, 310));
            JTextField codeField = editDialog.addTextField("Station code:");
            JTextField nameField = editDialog.addTextField("Station name:");
            JComboBox<City> cityBox = editDialog.addComboBox("City:", cities.toArray(new City[cities.size()]));

            codeField.setText(currentCode);
            nameField.setText(currentName);
            cityBox.setSelectedItem(currentCity);

            JButton saveBtn = editDialog.addButtonRow();
            saveBtn.addActionListener(editEv -> {
                try {
                    String code = codeField.getText().trim();
                    String name = nameField.getText().trim();
                    City city = (City) cityBox.getSelectedItem();

                    if(code.isBlank() || name.isBlank()) {
                        JOptionPane.showMessageDialog(editDialog, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Station station = Universal.db().queryOne(
                        "SELECT * FROM stations WHERE station_code = ?",
                        rs -> Station.parseResultSet(rs),
                        code
                    );
                    if(!code.equals(currentCode) && station != null) {
                        JOptionPane.showMessageDialog(editDialog, "Another station with this code already exists", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Universal.db().execute(
                        "UPDATE stations SET station_code = ?, station_name = ?, city_id = ? WHERE station_id = ?;",
                        code,
                        name,
                        city.getCityId(),
                        stationId
                    );

                    loadStations();
                    editDialog.dispose();
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(editDialog, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            editDialog.setVisible(true);
        });

        deleteButton.addActionListener(e -> {
            int row = stationTable.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(frame, "This cannot happen. Please contact nvtd.", "???", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String stationId = tableModel.getValueAt(row, 0).toString();

            Boolean used = Universal.db().queryOne(
                """
                SELECT 1 AS exist FROM train_routes
                WHERE origin_station_id = ? OR destination_station_id = ?;    
                """,
                rs -> rs.getBoolean("exist"),
                stationId,
                stationId
            );
            if(used != null) {
                JOptionPane.showMessageDialog(this, "Cannot delete this station as it is being used", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this station?", "Confirm deletion", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION) {
                Universal.db().execute(
                    "DELETE FROM stations WHERE station_id = ?",
                    stationId
                );
                loadStations();
            }
        });

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            filterBox.setSelectedIndex(0);
            loadStations();
        });

        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
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

    // private JButton createActionButton(String text) {
    //     JButton btn = new JButton(text);
    //     // btn.setMaximumSize(new Dimension(180, 40));
    //     btn.setPreferredSize(new Dimension(120, 36));
    //     btn.setVerticalAlignment(SwingConstants.CENTER);
    //     btn.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
    //     return btn;
    // }

    private JScrollPane createstationTablePanel() {
        tableModel = new DefaultTableModel(
            new String[] {"station_id", "city_id", "Station Code", "Station Name", "City"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        stationTable = new JTable(tableModel);
        stationTable.setRowHeight(30);
        stationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stationTable.getTableHeader().setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        stationTable.getTableHeader().setReorderingAllowed(false);
        stationTable.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        stationTable.getTableHeader().setPreferredSize(new Dimension(10, 25));

        TableColumnModel columnModel = stationTable.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(1));
        columnModel.removeColumn(columnModel.getColumn(0));
        // columnModel.getColumn(0).setMaxWidth(40);
        // columnModel.getColumn(0).setMinWidth(40);

        stationTable.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                int selectedRow = stationTable.getSelectedRow();
                if(selectedRow != -1) {
                    editButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                }
                else {
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }
            }
        });

        loadStations();

        JScrollPane scrollPane = new JScrollPane(stationTable);
        return scrollPane;
    }

    private int countStations() {
        return Universal.db().queryOne(
            "SELECT COUNT(*) cnt FROM stations;",
            rs -> rs.getInt("cnt")
        );
    }

    private void loadStations() {
        tableModel.setRowCount(0);
        String searchTerm = searchField.getText().trim();
        String city = "";
        if(filterBox.getSelectedItem() != ALL_CITIES) city = filterBox.getSelectedItem().toString();

        List<String> args = new ArrayList<String>();

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
        if(!city.isBlank()) {
            sql += " AND (c.city_name = ?)";
            args.add(city);
        }
        sql += ";";

        List<Station> stationList = Universal.db().query(
            sql,
            rs -> Station.parseResultSet(rs),
            args.toArray(new Object[args.size()])
        );
;
        for(Station s: stationList) {
            tableModel.addRow(new Object[] {
                s.getStationId(),
                s.getCityId(),
                s.getStationCode(),
                s.getStationName(),
                City.getCityFromId(s.getCityId()).getCityName()
            });
        }

        int returnedSize = stationList.size();
        int numStations = countStations();
        numTableRowCount.setText(returnedSize + " result" + (returnedSize > 1 ? "s" : "") + (returnedSize == numStations ? "" : " (" + numStations + " total)"));
    }

    private void loadAllCities() {
        cities = Universal.db().query(
            """
            SELECT * FROM cities;
            """,
            rs -> City.parseResultSet(rs)
        );
    }
}
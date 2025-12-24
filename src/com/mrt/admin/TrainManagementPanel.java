package com.mrt.admin;

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
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.mrt.AdminFrame;
import com.mrt.Universal;
import com.mrt.dbobject.Train;

public class TrainManagementPanel extends JPanel {

    private AdminFrame frame;

    private JTable trainTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> filterBox;
    private JComboBox<String> sortBox;
    private JCheckBox sortDesc;

    private JButton addButton;
    private JButton editButton;
    private JButton refreshButton;
    private JButton manageRoutesButton;
    private JButton manageSchedulesButton;

    private JLabel numTableRowCount;
    private JLabel numActiveTrains;

    public TrainManagementPanel(AdminFrame frame) {
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

        JLabel title = new JLabel("Train Management");
        title.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 28));
        header.add(title);

        header.add(Box.createHorizontalStrut(5));

        numActiveTrains = new JLabel();
        numActiveTrains.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        header.add(numActiveTrains);

        return header;
    }

    private JPanel createSearchPanel() {
        JPanel search = new JPanel();
        search.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        search.setOpaque(false);
        search.setMaximumSize(new Dimension(1000, 50));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 16));
        search.add(searchLabel);

        searchField = new JTextField(30);
        searchField.setToolTipText("Search by code");
        searchField.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        searchField.addActionListener(e -> {
            loadTrainsWithConstraints();
        });
        search.add(searchField);

        JButton searchButton = new JButton();
        try {
            ImageIcon icon = new ImageIcon("src/com/mrt/img/search.png");
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            searchButton.setIcon(new ImageIcon(newImg));
        } catch (Exception ignored) {}
        searchButton.addActionListener(e -> {
            loadTrainsWithConstraints();
        });
        search.add(searchButton);

        numTableRowCount = new JLabel();
        numTableRowCount.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        search.add(numTableRowCount);

        return search;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setMaximumSize(new Dimension(1000, 30));
        panel.setOpaque(false);

        JLabel filterLabel = new JLabel("Filter by status:");
        filterLabel.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 16));
        panel.add(filterLabel);

        filterBox = new JComboBox<>(new String[] {
            "---", "active", "maintenance", "retired"
        });
        filterBox.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        filterBox.addActionListener(e -> {
            loadTrainsWithConstraints();
        });
        panel.add(filterBox);

        JButton clearFilterButton = new JButton("Clear filter");
        clearFilterButton.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        clearFilterButton.addActionListener(e -> {
            filterBox.setSelectedIndex(0);
            loadTrainsWithConstraints();
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
            "ID", "Active Routes"
        });
        sortBox.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        sortBox.addActionListener(e -> {
            loadTrainsWithConstraints();
        });
        panel.add(sortBox);

        sortDesc = new JCheckBox("Descending");
        sortDesc.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        sortDesc.addActionListener(e -> {
            loadTrainsWithConstraints();
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
        refreshButton = createActionButton("Refresh");
        manageRoutesButton = createActionButton("Routes...");
        manageSchedulesButton = createActionButton("Schedules...");

        editButton.setEnabled(false);
        manageRoutesButton.setEnabled(false);
        manageSchedulesButton.setEnabled(false);

        addButton.addActionListener(e -> {
            FormDialog addDialog = new FormDialog(frame, "Add Train");

            JTextField codeField = addDialog.addTextField("Train Code:");
            JTextField seatCapacityField = addDialog.addTextField("Seat Capacity:");
            JComboBox<String> statusBox = addDialog.addComboBox("Status:", new String[]{
                "active", "maintenance", "retired"
            });

            JButton saveBtn = addDialog.addButtonRow();
            saveBtn.addActionListener(saveEv -> {
                try {
                    String code = codeField.getText().trim();
                    int seatCapacity = Integer.valueOf(seatCapacityField.getText().trim());
                    String status = statusBox.getSelectedItem().toString();

                    if(code.isBlank() || seatCapacityField.getText().trim().isBlank() || status.isBlank()) {
                        JOptionPane.showMessageDialog(addDialog, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Train train = Universal.db().queryOne(
                        "SELECT * FROM trains WHERE train_code = ?",
                        rs -> Train.parseResultSet(rs),
                        code
                    );
                    if(train != null) {
                        JOptionPane.showMessageDialog(addDialog, "Another train with this code already exists! Please use another one", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Universal.db().execute(
                        "INSERT INTO trains(train_code, seat_capacity, status) VALUES (?, ?, ?);",
                        code,
                        seatCapacity,
                        status
                    );

                    loadAllTrains();
                    addDialog.dispose();
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(addDialog, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            addDialog.setVisible(true);
        });

        editButton.addActionListener(e -> {
            int row = trainTable.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(frame, "This cannot happen. Please contact nvtd.", "???", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int trainId = (int) trainTable.getValueAt(row, 0);
            String currentTrainCode = trainTable.getValueAt(row, 1).toString();
            int currentSeatCapacity = (int) trainTable.getValueAt(row, 2);
            String currentStatus = trainTable.getValueAt(row, 3).toString();

            FormDialog editDialog = new FormDialog(frame, "Edit Train");
            JTextField codeField = editDialog.addTextField("Train Code:");
            JTextField seatCapacityField = editDialog.addTextField("Seat Capacity:");
            JComboBox<String> statusBox = editDialog.addComboBox("Role:", new String[] {
                "active", "maintenance", "retired"
            });

            codeField.setText(currentTrainCode);
            seatCapacityField.setText(String.valueOf(currentSeatCapacity));
            statusBox.setSelectedItem(currentStatus);

            JButton saveBtn = editDialog.addButtonRow();
            saveBtn.addActionListener(saveEv -> {
                String code = codeField.getText().trim();
                String seatCapacity = seatCapacityField.getText().trim();
                String status = statusBox.getSelectedItem().toString();

                if(code.isBlank() || seatCapacity.isBlank() || status.isBlank()) {
                    JOptionPane.showMessageDialog(editDialog, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Train train = Universal.db().queryOne(
                    "SELECT * FROM trains WHERE train_code = ?", 
                    rs -> Train.parseResultSet(rs), 
                    code
                );
                if(!code.equals(currentTrainCode) && train != null) {
                    JOptionPane.showMessageDialog(editDialog, "Another train with this code already exists! Please use another one", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Universal.db().execute(
                    "UPDATE trains SET train_code = ?, seat_capacity = ?, status = ? WHERE train_id = ?;",
                    code,
                    seatCapacity,
                    status,
                    trainId
                );

                loadTrainsWithConstraints();
                editDialog.dispose();
            });

            editDialog.setVisible(true);
        });

        manageRoutesButton.addActionListener(e -> {
            int row = trainTable.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(frame, "This cannot happen. Please contact nvtd.", "???", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int trainId = (int) trainTable.getValueAt(row, 0);
            String trainCode = trainTable.getValueAt(row, 1).toString();
            int seatCapacity = (int) trainTable.getValueAt(row, 2);
            String status = trainTable.getValueAt(row, 3).toString();

            RouteAssignmentDialog dialog = new RouteAssignmentDialog(
                frame,
                new Train(trainId, trainCode, seatCapacity, status),
                () -> loadTrainsWithConstraints()
            );

            dialog.setVisible(true);
        });

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            filterBox.setSelectedIndex(0);
            loadAllTrains();
        });

        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(manageRoutesButton);
        actionPanel.add(manageSchedulesButton);
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
        // btn.setMaximumSize(new Dimension(180, 40));
        btn.setPreferredSize(new Dimension(120, 36));
        btn.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        btn.setVerticalAlignment(SwingConstants.CENTER);
        return btn;
    }

    private JScrollPane createScrollPane() {
        tableModel = new DefaultTableModel(
            new String[] {"ID", "Train Code", "Seat Capacity", "Status", "Active Routes"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        trainTable = new JTable(tableModel);
        trainTable.setRowHeight(30);
        trainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        trainTable.getTableHeader().setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        trainTable.getTableHeader().setPreferredSize(new Dimension(10, 25));
        trainTable.getTableHeader().setReorderingAllowed(false);
        trainTable.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        trainTable.getColumnModel().getColumn(0).setMaxWidth(40);
        trainTable.getColumnModel().getColumn(0).setMinWidth(40);
        trainTable.setFocusable(false);
        trainTable.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                int selectedRow = trainTable.getSelectedRow();
                if(selectedRow != -1) {
                    editButton.setEnabled(true);

                    String status = trainTable.getValueAt(selectedRow, 3).toString();
                    if(status.equals("active")) {
                        manageRoutesButton.setEnabled(true);
                        manageSchedulesButton.setEnabled(true);
                    }
                    else {
                        manageRoutesButton.setEnabled(false);
                        manageSchedulesButton.setEnabled(false);
                    }
                }
                else {
                    editButton.setEnabled(false);
                    manageRoutesButton.setEnabled(false);
                    manageSchedulesButton.setEnabled(false);
                }
            }
        });

        loadAllTrains();

        JScrollPane scrollPane = new JScrollPane(trainTable);
        return scrollPane;
    }

    private int countAllTrains() {
        return Universal.db().queryOne(
            "SELECT COUNT(*) cnt FROM trains;",
            rs -> rs.getInt("cnt")
        );
    }
    private int countActiveTrains() {
        return Universal.db().queryOne(
            "SELECT COUNT(*) cnt FROM trains WHERE status = \'active\'", 
            rs -> rs.getInt("cnt")
        );
    }

    private void loadAllTrains() {
        tableModel.setRowCount(0);

        List<Integer> numRoutes = new ArrayList<>();
        List<Train> trainList = Universal.db().query(
            "SELECT\n" + //
            "    t.train_id,\n" + //
            "    t.train_code,\n" + //
            "    t.seat_capacity,\n" + //
            "    t.status,\n" + //
            "    COUNT(tra.route_id) routes\n" + //
            "FROM trains t\n" + //
            "LEFT JOIN train_route_assignments tra ON t.train_id = tra.train_id\n" + //
            "GROUP BY t.train_id;",
            rs -> {
                Train ret = Train.parseResultSet(rs);
                numRoutes.add(rs.getInt("routes"));
                return ret;
            }
        );

        int numRoutesIdx = 0;
        for(Train Train: trainList) {
            tableModel.addRow(new Object[] {
                Train.getTrainId(),
                Train.getTrainCode(),
                Train.getSeatCapacity(),
                Train.getStatus(),
                numRoutes.get(numRoutesIdx)
            });
            numRoutesIdx++;
        }

        numTableRowCount.setText(trainList.size() + " result" + (trainList.size() > 1 ? "s" : ""));
        numActiveTrains.setText(countActiveTrains() + " active trains");
    }

    private void loadTrainsWithConstraints() {
        tableModel.setRowCount(0);
        String searchTerm = searchField.getText().trim();
        String status = "";
        if(filterBox.getSelectedIndex() != 0) status = filterBox.getSelectedItem().toString();

        String orderBy = "";
        switch(sortBox.getSelectedItem().toString()) {
            case "ID":
                orderBy = "t.train_id";
                break;
            case "Active Routes":
                orderBy = "routes";
                break;
        }
        String sortOrder = "ASC";
        if(sortDesc.isSelected()) sortOrder = "DESC";

        List<String> args = new ArrayList<String>();

        String sql = "SELECT \n" + //
                    "   t.train_id, \n" + //
                    "   t.train_code, \n" + //
                    "   t.seat_capacity, \n" + //
                    "   t.status, \n" + //
                    "   COUNT(tra.route_id) routes\n" + //
                    "FROM trains t\n" + //
                    "LEFT JOIN train_route_assignments tra ON t.train_id = tra.train_id\n" + //
                    "WHERE true";
        if(!searchTerm.isBlank()) {
            sql += " AND (t.train_code LIKE ?)";
            args.add("%" + searchTerm + "%");
        }
        if(!status.isBlank()) {
            sql += " AND (t.status = ?)";
            args.add(status);
        }
        sql += "\n";
        sql += "GROUP BY t.train_id\n" + //
                "ORDER BY " + orderBy + " " + sortOrder + ";";

        List<Integer> numRoutes = new ArrayList<>();
        List<Train> trainList = Universal.db().query(
            sql,
            rs -> {
                Train ret = Train.parseResultSet(rs);
                numRoutes.add(rs.getInt("routes"));
                return ret;
            },
            args.toArray(new Object[args.size()])
        );

        int numRoutesIdx = 0;
        for(Train Train: trainList) {
            tableModel.addRow(new Object[] {
                Train.getTrainId(),
                Train.getTrainCode(),
                Train.getSeatCapacity(),
                Train.getStatus(),
                numRoutes.get(numRoutesIdx)
            });
            numRoutesIdx++;
        }

        int returnedSize = trainList.size();
        int numAllTrains = countAllTrains();
        numTableRowCount.setText(returnedSize + " result" + (returnedSize > 1 ? "s" : "") + (returnedSize == numAllTrains ? "" : " (" + numAllTrains + " total)"));
        numActiveTrains.setText(countActiveTrains() + " active trains");
    }
}
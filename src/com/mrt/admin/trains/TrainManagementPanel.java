package com.mrt.admin.trains;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import com.mrt.dialog.FormDialog;
import com.mrt.factory.UIFactory;
import com.mrt.frames.AdminFrame;
import com.mrt.frames.Page;
import com.mrt.models.Train;
import com.mrt.services.ScheduleService;
import com.mrt.services.TrainService;

public class TrainManagementPanel extends JPanel implements Page {

    private AdminFrame frame;

    private JTable trainTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> filterBox;
    private JComboBox<String> sortBox;
    private JCheckBox sortDesc;

    private JButton addBtn;
    private JButton editBtn;
    private JButton refreshBtn;
    private JButton schedulesBtn;

    private JLabel numTableRowCount;
    private JLabel numActiveTrains;

    public TrainManagementPanel(AdminFrame frame) {
        this.frame = frame;

        setLayout(new BorderLayout(0, 10));
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

        header.add(UIFactory.createBoldLabel("Train Management", 28));

        header.add(Box.createHorizontalStrut(5));

        numActiveTrains = UIFactory.createPlainLabel("", 14);
        header.add(numActiveTrains);

        return header;
    }

    private JPanel createSearchPanel() {
        JPanel search = new JPanel();
        search.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        search.setOpaque(false);

        search.add(UIFactory.createPlainLabel("Search:", 16));

        searchField = UIFactory.createTextField(30);
        searchField.setToolTipText("Search by code or seat capacity");
        searchField.addActionListener(e -> {
            refreshPage();
        });
        search.add(searchField);

        JButton searchBtn = UIFactory.createIconButton("src/com/mrt/img/search.png", new Dimension(24, 24));
        searchBtn.addActionListener(e -> {
            refreshPage();
        });
        search.add(searchBtn);

        numTableRowCount = UIFactory.createPlainLabel("", 14);
        search.add(numTableRowCount);

        return search;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setMaximumSize(new Dimension(1000, 30));
        panel.setOpaque(false);

        panel.add(UIFactory.createPlainLabel("Filter by status:", 16));

        filterBox = UIFactory.createComboBox(new String[] {
            "---", "active", "maintenance", "retired"
        });
        filterBox.addActionListener(e -> {
            refreshPage();
        });
        panel.add(filterBox);

        JButton clearFilterBtn = UIFactory.createButton("Clear filter");
        clearFilterBtn.addActionListener(e -> {
            filterBox.setSelectedIndex(0);
            refreshPage();
        });
        panel.add(clearFilterBtn);

        return panel;
    }

    private JPanel createSortPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setMaximumSize(new Dimension(1000, 30));
        panel.setOpaque(false);

        panel.add(UIFactory.createPlainLabel("Sort by:", 16));

        sortBox = UIFactory.createComboBox(new String[] {
            "Code", "Scheduled"
        });
        sortBox.addActionListener(e -> {
            refreshPage();
        });
        panel.add(sortBox);

        sortDesc = UIFactory.createCheckBox("Descending");
        sortDesc.addActionListener(e -> {
            refreshPage();
        });
        panel.add(sortDesc);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setOpaque(false);

        Dimension btnDim = new Dimension(120, 36);
        addBtn = UIFactory.createButton("Add");
        addBtn.setPreferredSize(btnDim);
        editBtn = UIFactory.createButton("Edit");
        editBtn.setPreferredSize(btnDim);
        refreshBtn = UIFactory.createButton("Refresh");
        refreshBtn.setPreferredSize(btnDim);
        schedulesBtn = UIFactory.createButton("Schedules...");
        schedulesBtn.setPreferredSize(btnDim);

        editBtn.setEnabled(false);
        schedulesBtn.setEnabled(false);

        addBtn.addActionListener(e -> {
            FormDialog dialog = new FormDialog(frame, "Add Train", new Dimension(400, 300));

            JTextField codeField = dialog.addTextField("Train Code:");
            JTextField seatCapacityField = dialog.addTextField("Seat Capacity:");

            JButton saveBtn = dialog.addButtonRow();
            saveBtn.addActionListener(saveEv -> {
                String code = codeField.getText().trim();
                int seatCapacity = Integer.valueOf(seatCapacityField.getText().trim());

                int state = TrainService.createTrain(code, seatCapacity);
                switch(state) {
                    case TrainService.BLANK_TRAIN_CODE:
                        JOptionPane.showMessageDialog(dialog, "Please provide a train code", "Train Error", JOptionPane.ERROR_MESSAGE);
                        break;
                    case TrainService.INVALID_SEAT_CAPACITY:
                        JOptionPane.showMessageDialog(dialog, "Please provide a valid seat capacity", "Train Error", JOptionPane.ERROR_MESSAGE);
                        break;
                    case TrainService.ANOTHER_TRAIN_CODE_EXISTS:
                        JOptionPane.showConfirmDialog(dialog, "Another train with this code already exists", "Train Error", JOptionPane.ERROR_MESSAGE);
                        break;
                    case TrainService.OK:
                        dialog.dispose();
                        refreshPage();
                        break;
                }
            });

            dialog.setVisible(true);
        });

        editBtn.addActionListener(e -> {
            int row = trainTable.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(frame, "This cannot happen. Please contact nvtd.", "???", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            FormDialog dialog = new FormDialog(frame, "Edit Train", new Dimension(400, 300));
            JTextField codeField = dialog.addTextField("Train Code:");
            JTextField seatCapacityField = dialog.addTextField("Seat Capacity:");
            JComboBox<String> statusBox = dialog.addComboBox("Role:", new String[] {
                "active", "maintenance", "retired"
            });
            
            Train selectedTrain = (Train) tableModel.getValueAt(row, 0);
            codeField.setText(selectedTrain.getTrainCode());
            seatCapacityField.setText(String.valueOf(selectedTrain.getSeatCapacity()));
            statusBox.setSelectedItem(selectedTrain.getStatus());

            JButton saveBtn = dialog.addButtonRow();
            saveBtn.addActionListener(saveEv -> {
                String code = codeField.getText().trim();
                int seatCapacity = Integer.valueOf(seatCapacityField.getText().trim());
                String status = statusBox.getSelectedItem().toString();

                int state = TrainService.updateTrain(selectedTrain.getTrainId(), code, seatCapacity, status);
                switch(state) {
                    case TrainService.BLANK_TRAIN_CODE:
                        JOptionPane.showMessageDialog(dialog, "Please provide a train code", "Train Error", JOptionPane.ERROR_MESSAGE);
                        break;
                    case TrainService.INVALID_SEAT_CAPACITY:
                        JOptionPane.showMessageDialog(dialog, "Please provide a valid seat capacity", "Train Error", JOptionPane.ERROR_MESSAGE);
                        break;
                    case TrainService.ANOTHER_TRAIN_CODE_EXISTS:
                        JOptionPane.showConfirmDialog(dialog, "Another train with this code already exists", "Train Error", JOptionPane.ERROR_MESSAGE);
                        break;
                    case TrainService.OK:
                        dialog.dispose();
                        refreshPage();
                        break;
                }

                dialog.dispose();
            });

            dialog.setVisible(true);
        });

        schedulesBtn.addActionListener(e -> {
            int row = trainTable.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(frame, "This cannot happen. Please contact nvtd.", "???", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Train selectedTrain = (Train) tableModel.getValueAt(row, 0);
            TrainSchedulesDialog dialog = new TrainSchedulesDialog(
                frame,
                selectedTrain
            );

            dialog.setVisible(true);
        });

        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            filterBox.setSelectedIndex(0);
            refreshPage();
        });

        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(schedulesBtn);
        actionPanel.add(refreshBtn);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setMaximumSize(new Dimension(1000, 60));
        wrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        wrapper.add(actionPanel, gbc);
        return wrapper;
    }

    private JScrollPane createScrollPane() {
        tableModel = new DefaultTableModel(
            new String[] {"obj_train", "Train Code", "Seat Capacity", "Status", "Schedules"},
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

        TableColumnModel columnModel = trainTable.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(0));
        
        trainTable.setFocusable(false);
        trainTable.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                int selectedRow = trainTable.getSelectedRow();
                if(selectedRow != -1) {
                    editBtn.setEnabled(true);
                    schedulesBtn.setEnabled(true);
                }
                else {
                    editBtn.setEnabled(false);
                    schedulesBtn.setEnabled(false);
                }
            }
        });

        refreshPage();

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

    private void loadTrains() {
        tableModel.setRowCount(0);
        String searchTerm = searchField.getText().trim();
        String status = "";
        if(filterBox.getSelectedIndex() != 0) status = filterBox.getSelectedItem().toString();

        String orderBy = "";
        switch(sortBox.getSelectedItem().toString()) {
            case "Code":
                orderBy = "t.train_code";
                break;
            case "Scheduled":
                orderBy = "scheduled";
                break;
        }
        String sortOrder = "ASC";
        if(sortDesc.isSelected()) sortOrder = "DESC";

        List<String> args = new ArrayList<String>();

        // String sql = "SELECT \n" +
        //             "    t.train_id,\n" +
        //             "    t.train_code,\n" +
        //             "    t.seat_capacity,\n" +
        //             "    t.status,\n" +
        //             "    COUNT(ts.route_id) scheduled\n" +
        //             "FROM trains t\n" +
        //             "LEFT JOIN train_schedules ts ON t.train_id = ts.train_id AND ts.status = 'scheduled'\n" +
        //             "WHERE true";
        String sql = 
        """
            SELECT
                t.train_id,
                t.train_code,
                t.seat_capacity,
                t.status,
                COUNT(DISTINCT ts.route_id) scheduled,
                COUNT(DISTINCT ts2.route_id) completed
            FROM trains t
            LEFT JOIN train_schedules ts ON t.train_id = ts.train_id AND ts.status = 'scheduled'
            LEFT JOIN train_schedules ts2 ON t.train_id = ts2.train_id AND ts2.status = 'completed'
            WHERE TRUE        
        """;
        if(!searchTerm.isBlank()) {
            sql += " AND (t.train_code LIKE ?)";
            args.add("%" + searchTerm + "%");
        }
        if(!status.isBlank()) {
            sql += " AND (t.status = ?)";
            args.add(status);
        }
        sql += "\n";
        sql += "GROUP BY t.train_id, t.train_code, t.seat_capacity\n" +
                "ORDER BY " + orderBy + " " + sortOrder + ";";

        List<Integer> numScheduled = new ArrayList<>();
        List<Integer> numCompleted = new ArrayList<>();
        List<Train> trainList = Universal.db().query(
            sql,
            rs -> {
                Train ret = Train.parseResultSet(rs);
                numScheduled.add(rs.getInt("scheduled"));
                numCompleted.add(rs.getInt("completed"));
                return ret;
            },
            args.toArray(new Object[args.size()])
        );

        int rowIdx = 0;
        for(Train train: trainList) {
            tableModel.addRow(new Object[] {
                train,
                train.getTrainCode(),
                train.getSeatCapacity(),
                train.getStatus(),
                numScheduled.get(rowIdx) + " scheduled, " + numCompleted.get(rowIdx) + " completed"
            });
            rowIdx++;
        }

        int returnedSize = trainList.size();
        int numAllTrains = countAllTrains();
        numTableRowCount.setText(returnedSize + " result" + (returnedSize > 1 ? "s" : "") + (returnedSize == numAllTrains ? "" : " (of " + numAllTrains + " total)"));
        numActiveTrains.setText(countActiveTrains() + " active trains");
    }

    public void refreshPage() {
        ScheduleService.refreshSchedulesStatus();
        loadTrains();
    }
}
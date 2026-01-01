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
import javax.swing.JCheckBox;
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
import com.mrt.model.Train;

public class TrainPickerDialog extends JDialog {

    private JTextField searchField;
    private JComboBox<String> sortByBox;
    private JCheckBox sortOrderBox;

    private JTable trainTable;
    private DefaultTableModel tableModel;

    private JButton pickBtn;

    private Train selectedTrain;

    public TrainPickerDialog(JDialog frame) {
        super(frame, "Train Picker", true);

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
            "Select a train",
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
        top.add(createSortPanel());
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
        searchField.setToolTipText("Search by train code or seat capacity");
        searchField.addActionListener(e -> {
            loadTrains();
        });
        panel.add(searchField);

        JButton searchButton = UIFactory.createIconButton("src/com/mrt/img/search.png", new Dimension(24, 24));
        searchButton.setPreferredSize(new Dimension(40, 40));
        searchButton.addActionListener(e -> {
            loadTrains();
        });
        panel.add(searchButton);

        return panel;
    }

    private JPanel createSortPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setOpaque(false);

        panel.add(UIFactory.createPlainLabel("Sort by:", 14));

        sortByBox = UIFactory.createComboBox(new String[] {
            "Code", "Seat Capacity"
        });
        sortByBox.addActionListener(e -> {
            loadTrains();
        });
        panel.add(sortByBox);

        sortOrderBox = UIFactory.createCheckBox("Descending");
        sortByBox.addActionListener(e -> {
            loadTrains();
        });
        panel.add(sortOrderBox);

        return panel;
    }

    private JScrollPane createScrollPane() {
        tableModel = new DefaultTableModel(
            new String[] {"obj_train", "Train Code", "Seat Capacity", "Status"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if(column == 0) return true;
                return false;
            }
        };

        trainTable = new JTable(tableModel);
        trainTable.setRowHeight(30);
        trainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        trainTable.getTableHeader().setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        trainTable.getTableHeader().setPreferredSize(new Dimension(0, 25));
        trainTable.getTableHeader().setReorderingAllowed(false);
        trainTable.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        trainTable.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                int row = trainTable.getSelectedRow();
                if(row != -1) {
                    pickBtn.setEnabled(true);
                } else {
                    pickBtn.setEnabled(false);
                }
            }
        });

        TableColumnModel columnModel = trainTable.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(0));
        // columnModel.getColumn(0).setMaxWidth(100);
        // columnModel.getColumn(0).setMinWidth(100);

        trainTable.setFocusable(false);

        loadTrains();

        JScrollPane scrollPane = new JScrollPane(trainTable);
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
            selectedTrain = null;
            dispose();
        });

        pickBtn = UIFactory.createButton("Pick");
        pickBtn.setPreferredSize(new Dimension(100, 33));
        pickBtn.setEnabled(false);
        pickBtn.addActionListener(e -> {
            int row = trainTable.getSelectedRow();
            if(row != -1) {
                selectedTrain = (Train) tableModel.getValueAt(row, 0);
                dispose();
            }
            else selectedTrain = null;
        });

        panel.add(cancelBtn);
        panel.add(pickBtn);
        return panel;
    }

    private void loadTrains() {
        String searchTerm = searchField.getText().trim();
        String sortBy = sortByBox.getSelectedItem().toString();
        if(sortBy.equals("Code")) {
            sortBy = "train_code";
        } else if(sortBy.equals("Seat Capacity")) {
            sortBy = "seat_capacity";
        }
        String sortOrder = sortOrderBox.isSelected() ? "DESC" : "ASC";

        List<Object> args = new ArrayList<>();
        String sql = 
        """
        SELECT * 
        FROM trains 
        WHERE status = 'active' 
        """;

        if(!searchTerm.isBlank()) {
            sql += " AND (train_code LIKE ? OR seat_capacity LIKE ?)";
            args.add("%" + searchTerm + "%");
            args.add("%" + searchTerm + "%");
        }
        sql += "\n";
        sql += "ORDER BY " + sortBy + " " + sortOrder + ";";

        List<Train> trains = Universal.db().query(
            sql,
            rs -> Train.parseResultSet(rs),
            args.toArray()
        );

        tableModel.setRowCount(0);
        for(Train t: trains) {
            tableModel.addRow(new Object[] {
                t,
                t.getTrainCode(),
                t.getSeatCapacity(),
                "active"
            });
        }
    }

    public Train getSelectedTrain() {
        return selectedTrain;
    }
}

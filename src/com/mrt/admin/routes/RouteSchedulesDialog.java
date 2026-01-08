package com.mrt.admin.routes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.models.Route;
import com.mrt.models.Schedule;
import com.mrt.models.Train;
import com.mrt.services.ScheduleService;
import com.mrt.services.TrainService;

public class RouteSchedulesDialog extends JDialog {

    private Route route;

    private DefaultTableModel futureModel;
    private DefaultTableModel pastModel;
    private JTable futureTable;
    private JTable pastTable;
    private ScheduleCellRenderer renderer;

    private JButton addButton;
    private JButton cancelButton;
    private JButton editButton;
    private JButton refreshButton;

    public RouteSchedulesDialog(JFrame parent, Route route) {
        super(parent, "Schedules - Route " + route.getRouteSummary(), true);

        this.route = route;
        renderer = new ScheduleCellRenderer();
        
        setSize(new Dimension(700, 600));
        setResizable(false);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(0, 0));
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setBackground(Universal.BACKGROUND_WHITE);

        add(createTopPanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        topPanel.setOpaque(false);

        topPanel.add(createHeaderPanel());

        return topPanel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        panel.add(createFutureSchedulesPanel());
        panel.add(Box.createVerticalStrut(10));
        // panel.add(createOngoingSchedulePanel());
        panel.add(Box.createVerticalStrut(10));
        panel.add(createPastSchedulesPanel());

        return panel;
    }

    private JPanel createFutureSchedulesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        panel.add(createActionPanel(), BorderLayout.NORTH);
        panel.add(createFutureSchedulesScrollPane(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPastSchedulesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        panel.add(UIFactory.createBoldLabel("Past schedules:", 16), BorderLayout.NORTH);

        panel.add(createPastSchedulesScrollPane(), BorderLayout.CENTER);

        return panel;
    }

    // private JPanel createOngoingSchedulePanel() {
    //     JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    //     return panel;
    // }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        headerPanel.add(UIFactory.createBoldLabel(
            "<html>Schedule of Route: <font color='#00b8ff'>" + route.getRouteSummary() + "</font></html>",
            18
        ));
        return headerPanel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);

        panel.add(UIFactory.createBoldLabel("Future schedules:", 16), BorderLayout.WEST);

        Dimension btnDim = new Dimension(32, 32);
        Dimension iconDim = new Dimension(10, 10);
        addButton = UIFactory.createIconButton("src/com/mrt/img/plus.png", iconDim);
        addButton.setPreferredSize(btnDim);
        cancelButton = UIFactory.createIconButton("src/com/mrt/img/minus.png", iconDim);
        cancelButton.setPreferredSize(btnDim);
        editButton = UIFactory.createIconButton("src/com/mrt/img/gear.png", iconDim);
        editButton.setPreferredSize(btnDim);
        refreshButton = UIFactory.createIconButton("src/com/mrt/img/refresh.png", iconDim);
        refreshButton.setPreferredSize(btnDim);

        addButton.addActionListener(e -> {
            RouteAddScheduleDialog dialog = new RouteAddScheduleDialog(this, route);
            dialog.setVisible(true);
            refresh();
        });

        cancelButton.addActionListener(e -> {
            int row = futureTable.getSelectedRow();
            if(row != -1) {
                String status = (String) futureModel.getValueAt(row, 5);
                if(status.equals("scheduled")) {
                    int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this schedule? (This action cannot be undone)", "Confirm cancellation", JOptionPane.YES_NO_OPTION);
                    if(option == JOptionPane.YES_OPTION) {  
                        ScheduleService.cancelSchedule((int) futureModel.getValueAt(row, 0));
                        refresh();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "This schedule is already cancelled", "Schedule Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        editButton.addActionListener(e -> {
            // int row = futureTable.getSelectedRow();
            // if(row != -1) {
            //     String status = (String) futureModel.getValueAt(row, 5);
            //     if(status.equals("scheduled")) {
            //         int scheduleId = (int) futureModel.getValueAt(row, 0);
            //         TrainEditScheduleDialog dialog = new TrainEditScheduleDialog(this, Schedule.getScheduleFromId(scheduleId));
            //         dialog.setVisible(true);

            //         refresh();
            //     } else {
            //         JOptionPane.showMessageDialog(this, "Cannot edit a cancelled schedule", "Error", JOptionPane.ERROR_MESSAGE);
            //     }
            // }
        });

        refreshButton.addActionListener(e -> {
            refresh();
        });

        editButton.setEnabled(false);
        cancelButton.setEnabled(false);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(addButton);
        btnPanel.add(cancelButton);
        btnPanel.add(editButton);
        btnPanel.add(refreshButton);
        panel.add(btnPanel, BorderLayout.EAST);
        return panel;
    }

    private JScrollPane createFutureSchedulesScrollPane() {
        futureModel = new DefaultTableModel(
            new String[] {"obj_schedule", "Departs", "Arrives", "Train", "Status"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        futureTable = new JTable(futureModel);
        futureTable.setRowHeight(25);
        futureTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        futureTable.getTableHeader().setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        futureTable.getTableHeader().setPreferredSize(new Dimension(0, 25));
        futureTable.getTableHeader().setReorderingAllowed(false);

        TableColumnModel columnModel = futureTable.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(0));

        int width = 136;
        columnModel.getColumn(0).setMaxWidth(width);
        columnModel.getColumn(0).setMinWidth(width);
        columnModel.getColumn(1).setMaxWidth(width);
        columnModel.getColumn(1).setMinWidth(width);
        columnModel.getColumn(3).setMaxWidth(width);
        columnModel.getColumn(3).setMinWidth(width);

        futureTable.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        futureTable.setFocusable(false);
        futureTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = futureTable.getSelectedRow();
            if(selectedRow != -1) {
                pastTable.clearSelection();
                cancelButton.setEnabled(true);
                editButton.setEnabled(true);
            }
            else {
                cancelButton.setEnabled(false);
                editButton.setEnabled(false);
            }
        });
        for(int i = 0; i < futureTable.getColumnCount(); i++) {
            columnModel.getColumn(i).setCellRenderer(renderer);
        }

        loadAllSchedules(futureModel);

        JScrollPane scrollPane = new JScrollPane(futureTable);
        return scrollPane;
    }

    private JScrollPane createPastSchedulesScrollPane() {
        pastModel = new DefaultTableModel(
            new String[] {"obj_schedule", "Departed", "Arrived", "Train", "Status"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        pastTable = new JTable(pastModel);
        pastTable.setRowHeight(25);
        pastTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pastTable.getTableHeader().setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        pastTable.getTableHeader().setPreferredSize(new Dimension(0, 25));
        pastTable.getTableHeader().setReorderingAllowed(false);

        TableColumnModel columnModel = pastTable.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(0));

        int width = 136;
        columnModel.getColumn(0).setMaxWidth(width);
        columnModel.getColumn(0).setMinWidth(width);
        columnModel.getColumn(1).setMaxWidth(width);
        columnModel.getColumn(1).setMinWidth(width);
        columnModel.getColumn(3).setMaxWidth(width);
        columnModel.getColumn(3).setMinWidth(width);
        
        pastTable.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        pastTable.setFocusable(false);
        pastTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = pastTable.getSelectedRow();
            if(selectedRow != -1) {
                futureTable.clearSelection();
                // editButton.setEnabled(true);
                // deleteButton.setEnabled(true);
            }
            else {
                // editButton.setEnabled(false);
                // deleteButton.setEnabled(false);
            }
        });

        for(int i = 0; i < pastTable.getColumnCount(); i++) {
            pastTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        loadAllSchedules(pastModel);

        JScrollPane scrollPane = new JScrollPane(pastTable);
        return scrollPane;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        panel.setOpaque(false);

        JButton exitBtn = UIFactory.createButton("Exit");
        exitBtn.setPreferredSize(new Dimension(80, 33));
        exitBtn.addActionListener(e -> {
            dispose();
        });

        panel.add(exitBtn);
        return panel;
    }

    private void refresh() {
        ScheduleService.refreshSchedulesStatus();
        loadAllSchedules(futureModel);
        loadAllSchedules(pastModel);
    }

    private void loadAllSchedules(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);

        String status = "";
        if(tableModel == futureModel) status = "scheduled";
        else if(tableModel == pastModel) status = "completed";
        List<Schedule> schedules = ScheduleService.getSchedulesByRoute(route.getRouteId(), status);

        for(Schedule s: schedules) {
            Train train = TrainService.getTrainById(s.getTrainId());
            tableModel.addRow(new Object[] {
                s,
                s.getDepartureTime(),
                s.getArrivalTime(),
                train.getTrainSummary(),
                s.getStatus()
            });
        }
    }

    private class ScheduleCellRenderer extends DefaultTableCellRenderer {
        private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            boolean isStatus = false;
            if(value instanceof LocalDateTime) {
                setText(((LocalDateTime) value).format(formatter));
            } else if(column == 3) {
                isStatus = true;
                setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
            }

            if(isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
                return this;
            }

            if(row % 2 == 0) {
                setBackground(table.getBackground());
            } else {
                setBackground(new Color(240, 240, 240));
            }

            if(isStatus) {  
                String status = (String) value;
                switch(status) {
                    case "scheduled": 
                        setForeground(new Color(255, 200, 0));
                        break;
                    case "completed":
                        setForeground(new Color(0, 200, 0));
                        break;
                    case "cancelled":
                        setForeground(Color.RED);
                        break;
                }
            } else {
                setFont(table.getFont());
                setForeground(Color.BLACK);
            }
            return this;
        }
    }
}

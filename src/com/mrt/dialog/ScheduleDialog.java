package com.mrt.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.mrt.Universal;
import com.mrt.model.Route;
import com.mrt.model.Train;

public class ScheduleDialog extends JDialog {

    private Train train;
    private Route route;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;

    private DefaultTableModel futureModel;
    private DefaultTableModel pastModel;
    private JTable futureTable;
    private JTable pastTable;
    private ScheduleCellRenderer renderer;

    private Runnable parentRefresh;

    public ScheduleDialog(JDialog dialog, Train train, Route route, Runnable parentRefresh) {
        super(dialog, "Schedules - Train " + train.toString(), true);

        this.train = train;
        this.route = route;
        this.parentRefresh = parentRefresh;
        renderer = new ScheduleCellRenderer();
        
        setSize(new Dimension(500, 600));
        setResizable(false);
        setLocationRelativeTo(dialog);
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
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(createFutureSchedulesPanel());
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

        JLabel label = new JLabel("Past schedules:");
        label.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 16));
        panel.add(label, BorderLayout.NORTH);

        panel.add(createPastSchedulesScrollPane(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel("<html>Route: <font color='#00b8ff'>" + route.toString() + "</font></html>");
        headerLabel.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 18));
        headerPanel.add(headerLabel);
        return headerPanel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Future schedules:");
        title.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 16));
        panel.add(title, BorderLayout.WEST);

        addButton = createActionButton("src/com/mrt/img/plus.png");
        deleteButton = createActionButton("src/com/mrt/img/minus.png");
        editButton = createActionButton("src/com/mrt/img/gear.png");
        refreshButton = createActionButton("src/com/mrt/img/refresh.png");

        addButton.addActionListener(e -> {
            FormDialog addDialog = new FormDialog(this, "Add new schedule", new Dimension(300, 200));
            JSpinner departureSpinner = addDialog.addDateTimePicker("Departure:", LocalDateTime.now());
            JSpinner arrivalSpinner = addDialog.addDateTimePicker("Arrival:", LocalDateTime.now());

            JButton saveBtn = addDialog.addButtonRow();
            saveBtn.addActionListener(new ScheduleValidator(
                addDialog, 
                departureSpinner,
                arrivalSpinner,
                () -> {
                    // LocalDateTime localDeparture = LocalDateTime.ofInstant(((Date) departureSpinner.getValue()).toInstant(), ZoneId.systemDefault());
                    // LocalDateTime localArrival = LocalDateTime.ofInstant(((Date) arrivalSpinner.getValue()).toInstant(), ZoneId.systemDefault());
                    // Date departure = Date.from(localDeparture.atZone(ZoneId.systemDefault()).toInstant());
                    // Date arrival = Date.from(localArrival.atZone(ZoneId.systemDefault()).toInstant());
                    // System.out.println(departure.getTime());
                    // System.out.println(arrival.getTime());
                    Date departure = (Date) departureSpinner.getValue();
                    Date arrival = (Date) arrivalSpinner.getValue();

                    Universal.db().execute(
                        "INSERT INTO train_schedules(train_id, route_id, departure_time, arrival_time) VALUES (?, ?, ?, ?);",
                        train.getTrainId(),
                        route.getRouteId(),
                        new Timestamp(departure.getTime()),
                        new Timestamp(arrival.getTime())
                    );
                    refresh();
                    addDialog.dispose();
                }
            ));

            addDialog.setVisible(true);
        });

        deleteButton.addActionListener(e -> {
            int row = futureTable.getSelectedRow();
            if(row != -1) {
                int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this schedule?",
                    "Confirm deletion",
                    JOptionPane.YES_NO_OPTION
                );

                if(choice == JOptionPane.YES_OPTION) {
                    int scheduleId = (int) futureModel.getValueAt(row, 0);
                    Universal.db().execute(
                        "DELETE FROM train_schedules WHERE schedule_id = ?",
                        scheduleId
                    );
                }
                refresh();
            }
        });

        refreshButton.addActionListener(e -> {
            refresh();
        });

        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(addButton);
        btnPanel.add(deleteButton);
        btnPanel.add(editButton);
        btnPanel.add(refreshButton);
        panel.add(btnPanel, BorderLayout.EAST);
        return panel;
    }

    private JScrollPane createFutureSchedulesScrollPane() {
        futureModel = new DefaultTableModel(
            new String[] {"ID", "Departs", "Arrives", "Status"},
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
        futureTable.getColumnModel().removeColumn(futureTable.getColumnModel().getColumn(0));
        futureTable.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        futureTable.setFocusable(false);
        futureTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = futureTable.getSelectedRow();
            if(selectedRow != -1) {
                pastTable.clearSelection();
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }
            else {
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }
        });
        for(int i = 0; i < futureTable.getColumnCount(); i++) {
            futureTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        loadAllSchedules(futureModel);

        JScrollPane scrollPane = new JScrollPane(futureTable);
        return scrollPane;
    }

    private JScrollPane createPastSchedulesScrollPane() {
        pastModel = new DefaultTableModel(
            new String[] {"ID", "Departed", "Arrived", "Status"},
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
        pastTable.getColumnModel().removeColumn(pastTable.getColumnModel().getColumn(0));
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

        JButton exitBtn = new JButton("Exit");
        exitBtn.setPreferredSize(new Dimension(80, 33));
        exitBtn.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        exitBtn.addActionListener(e -> {
            dispose();
        });

        panel.add(exitBtn);
        return panel;
    }

    private JButton createActionButton(String iconUrl) {
        ImageIcon imgIcon = new ImageIcon(iconUrl);
        Image img = imgIcon.getImage().getScaledInstance(10, 10, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(img);

        JButton btn = new JButton(icon);
        btn.setPreferredSize(new Dimension(30, 30));
        btn.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        return btn;
    }

    private void refresh() {
        loadAllSchedules(futureModel);
        loadAllSchedules(pastModel);
        parentRefresh.run();
    }

    private void loadAllSchedules(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);

        String sql = 
            "SELECT *\n" +
            "FROM train_schedules ts\n" +
            "WHERE ts.train_id = ? AND ts.route_id = ? AND (ts.status = 'cancelled' OR ts.status = ?)";

        String status = "";
        if(tableModel == futureModel) {
            status = "scheduled";
            sql += " AND Now() <= ts.departure_time\n";
            sql += "ORDER BY ts.departure_time ASC, ts.status DESC";
        } else if(tableModel == pastModel) {
            status = "completed";
            sql += " AND ts.arrival_time < NOW()";
            sql += "ORDER BY ts.departure_time DESC, ts.status DESC";
        }
        sql += ";";

        List<ScheduleRow> schedules = Universal.db().query(
            sql,
            rs -> new ScheduleRow(
                rs.getInt("schedule_id"),
                rs.getObject("departure_time", LocalDateTime.class),
                rs.getObject("arrival_time", LocalDateTime.class),
                rs.getString("status")
            ),
            train.getTrainId(),
            route.getRouteId(),
            status
        );

        for(ScheduleRow s: schedules) {
            tableModel.addRow(new Object[] {
                s.getScheduleId(),
                s.getDepartureTime(),
                s.getArrivalTime(),
                s.getStatus()
            });
        }
    }

    private class ScheduleValidator implements ActionListener {
        private JDialog parent;
        private JSpinner departureSpinner;
        private JSpinner arrivalSpinner;
        private Runnable onSuccess;

        public ScheduleValidator(JDialog parent, JSpinner departureSpinner, JSpinner arrivalSpinner, Runnable onSuccess) {
            this.parent = parent;
            this.departureSpinner = departureSpinner;
            this.arrivalSpinner = arrivalSpinner;
            this.onSuccess = onSuccess;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Date departure = (Date) departureSpinner.getValue();
            Date arrival = (Date) arrivalSpinner.getValue();

            if(departure.after(arrival)) {
                JOptionPane.showMessageDialog(parent, "Departure time must be before arrival time.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
            // System.out.println(departure);
            // System.out.println(arrival);
            // System.out.println(now);
            if(departure.before(now)) {
                JOptionPane.showMessageDialog(parent, "Departure time must occur in the future.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            onSuccess.run();
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
            } else if(value instanceof String) {
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

    private class ScheduleRow {
        private int scheduleId;
        private LocalDateTime departureTime;
        private LocalDateTime arrivalTime;
        private String status;

        public ScheduleRow(int scheduleId, LocalDateTime departureTime, LocalDateTime arrivalTime, String status) {
            this.scheduleId = scheduleId;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
            this.status = status;
        }

        public int getScheduleId() { return scheduleId; }
        public LocalDateTime getDepartureTime() { return departureTime; }
        public LocalDateTime getArrivalTime() { return arrivalTime; }
        public String getStatus() { return status; }
    }
}

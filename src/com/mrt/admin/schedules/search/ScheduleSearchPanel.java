package com.mrt.admin.schedules.search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.mrt.Universal;
import com.mrt.admin.schedules.SchedulesPanel;
import com.mrt.factory.UIFactory;
import com.mrt.frames.Page;
import com.mrt.models.Route;
import com.mrt.models.Schedule;
import com.mrt.models.Train;
import com.mrt.services.RouteService;
import com.mrt.services.ScheduleService;
import com.mrt.services.TicketService;
import com.mrt.services.TrainService;

public class ScheduleSearchPanel extends JPanel implements Page {

    private SchedulesPanel parentPanel;

    private JTextField searchField;
    private JComboBox<Route> routeBox;
    private JCheckBox fromCheckBox;
    private JSpinner fromSpinner;
    private JCheckBox toCheckBox;
    private JSpinner toSpinner;

    private JButton viewBtn;

    private DefaultTableModel tableModel;
    private JTable scheduleTable;
    private ScheduleCellRenderer renderer;

    private Route ALL_ROUTES = new Route(-1, "All", -1, -1, "");
    
    public ScheduleSearchPanel(SchedulesPanel parentPanel) {
        this.parentPanel = parentPanel;
        renderer = new ScheduleCellRenderer();

        setLayout(new BorderLayout(0, 0));
        // setOpaque(false);
        setBackground(Universal.BACKGROUND_WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createScheduleScrollPane(), BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(createTitlePanel());
        panel.add(createSearchPanel());
        panel.add(createFilterPanel());
        // panel.add(Box.createVerticalStrut(10));
        // JPanel upcomingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // upcomingPanel.setOpaque(false);
        // panel.add(upcomingPanel);
        // panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        panel.add(UIFactory.createBoldLabel("Upcoming Schedules", 24));
        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);

        panel.add(UIFactory.createPlainLabel("Search:", 16));

        searchField = UIFactory.createTextField(30);
        searchField.setToolTipText("Search schedule...");
        searchField.addActionListener(e -> {
            loadSchedules();
        });
        panel.add(searchField);

        JButton searchBtn = UIFactory.createIconButton("src/com/mrt/img/search.png", new Dimension(25, 25));
        searchBtn.addActionListener(e -> {
            loadSchedules();
        });
        panel.add(searchBtn);

        JButton refreshBtn = UIFactory.createIconButton("src/com/mrt/img/refresh.png", new Dimension(14, 14));
        refreshBtn.setPreferredSize(new Dimension(36, 36));
        refreshBtn.addActionListener(e -> {
            clearFilters();
            loadSchedules();
        });
        panel.add(refreshBtn);

        viewBtn = UIFactory.createButton("View Seats");
        viewBtn.setPreferredSize(new Dimension(100, 36));
        viewBtn.setFont(UIFactory.createDefaultBoldFont(14));
        viewBtn.setEnabled(false);
        viewBtn.addActionListener(e -> {
            int row = scheduleTable.getSelectedRow();
            if(row != -1) {
                Schedule selectedSchedule = (Schedule) tableModel.getValueAt(row, 0);
                parentPanel.setSelectedSchedule(selectedSchedule);
                parentPanel.showPage(SchedulesPanel.SEATS);
            }
        });
        panel.add(viewBtn);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(createRouteFilterPanel());
        panel.add(createDateFilterPanel());
        return panel;
    }

    private JPanel createRouteFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);

        panel.add(UIFactory.createPlainLabel("Filter by route", 16));

        routeBox = UIFactory.createComboBox(RouteService.getAllRoutes().toArray(new Route[0]));
        routeBox.insertItemAt(ALL_ROUTES, 0);
        routeBox.setSelectedIndex(0);
        routeBox.addActionListener(e -> {
            loadSchedules();
        });
        panel.add(routeBox);

        JButton clearFilterBtn = UIFactory.createButton("Clear filter");
        clearFilterBtn.addActionListener(e -> {
            clearFilters();
            loadSchedules();
        });
        panel.add(clearFilterBtn);

        return panel;
    }

    private JPanel createDateFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        
        JLabel fromLabel = UIFactory.createPlainLabel("From", 16);
        fromSpinner = UIFactory.createDateTimeSpinner("dd-MM-yyyy");
        fromLabel.setEnabled(false);
        fromSpinner.setEnabled(false);
        fromCheckBox = UIFactory.createCheckBox();
        fromCheckBox.addActionListener(e -> {
            if(fromCheckBox.isSelected()) {
                fromLabel.setEnabled(true);
                fromSpinner.setEnabled(true);
                loadSchedules();
            }
            else {
                fromLabel.setEnabled(false);
                fromSpinner.setEnabled(false);
                loadSchedules();
            }
        });
        panel.add(fromCheckBox);
        panel.add(fromLabel);
        panel.add(fromSpinner);

        JLabel toLabel = UIFactory.createPlainLabel("To", 16);
        toSpinner = UIFactory.createDateTimeSpinner("dd-MM-yyyy");
        toLabel.setEnabled(false);
        toSpinner.setEnabled(false);
        toCheckBox = UIFactory.createCheckBox();
        toCheckBox.addActionListener(e -> {
            if(toCheckBox.isSelected()) {
                toLabel.setEnabled(true);
                toSpinner.setEnabled(true);
                loadSchedules();
            }
            else {
                toLabel.setEnabled(false);
                toSpinner.setEnabled(false);
                loadSchedules();
            }
        });
        panel.add(toCheckBox);
        panel.add(toLabel);
        panel.add(toSpinner);

        JButton applyBtn = UIFactory.createButton("Apply");
        applyBtn.setPreferredSize(new Dimension(100, 36));
        applyBtn.addActionListener(e -> {
            try {
                fromSpinner.commitEdit();
            } catch(Exception ignored) {}
            try {
                toSpinner.commitEdit();
            } catch(Exception ignored) {}

            loadSchedules();
        });
        panel.add(applyBtn);

        return panel;
    }

    private void clearFilters() {
        searchField.setText("");
        routeBox.setSelectedIndex(0);
        fromCheckBox.setSelected(false);
        fromSpinner.setEnabled(false);
        toCheckBox.setSelected(false);
        toSpinner.setEnabled(false);
    }

    private void loadSchedules() {
        tableModel.setRowCount(0);
        ScheduleService.refreshSchedulesStatus();
        TicketService.refreshTicketsStatus();

        String searchTerm = searchField.getText().trim();
        Route routeFilter = (Route) routeBox.getSelectedItem();

        String sql = 
        """
            SELECT 
                ts.schedule_id,
                tr.route_id,
                t.train_id,
                ts.departure_utc,
                ts.arrival_utc,
                ts.status,
                t.seat_capacity - COUNT(tk.ticket_id) AS available_seats,
                CASE
                    WHEN ts.status = 'cancelled' THEN 'cancelled'
                    WHEN UTC_TIMESTAMP() BETWEEN ts.departure_utc AND ts.arrival_utc THEN 'ongoing'
                    ELSE ts.status
                END AS ui_status
            FROM train_schedules ts
            LEFT JOIN tickets tk ON tk.schedule_id = ts.schedule_id AND tk.status IN ('booked', 'boarded')
            INNER JOIN trains t ON ts.train_id = t.train_id
            INNER JOIN train_routes tr ON tr.route_id = ts.route_id
            WHERE TRUE
        """;

        List<Object> args = new ArrayList<>();
        if(!searchTerm.isBlank()) {
            sql +=
            """
                AND (
                    tr.route_code LIKE ? OR
                    t.train_code LIKE ?
                )        
            """;
            String pat = "%" + searchTerm + "%";
            args.add(pat);
            args.add(pat);
        }
        if(routeFilter != ALL_ROUTES) {
            sql +=
            """
                AND (tr.route_id = ?)    
            """;
            args.add(routeFilter.getRouteId());
        }
        if(fromCheckBox.isSelected()) {
            LocalDateTime fromLdt = getLocalDateTime(fromSpinner);
            fromLdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            sql +=
            """
                AND (? <= ts.departure_utc)        
            """;
            args.add(Timestamp.from(fromLdt.atZone(ZoneId.systemDefault()).toInstant()));
        }
        if(toCheckBox.isSelected()) {
            LocalDateTime toLdt = getLocalDateTime(toSpinner);
            toLdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            sql += 
            """
                AND (ts.departure_utc <= DATE_ADD(?, INTERVAL 1 DAY))        
            """;
            args.add(Timestamp.from(toLdt.atZone(ZoneId.systemDefault()).toInstant()));
        }
            
        sql += 
        """
            GROUP BY ts.schedule_id, tr.route_id, t.train_id, ts.departure_utc, ts.arrival_utc, ui_status
            ORDER BY ts.status, ts.departure_utc ASC;
        """;

        List<Integer> availableSeats = new ArrayList<>();
        List<String> uiStatus = new ArrayList<>();
        List<Schedule> schedules = Universal.db().query(
            sql,
            rs -> {
                Schedule s = Schedule.parseResultSet(rs);
                availableSeats.add(rs.getInt("available_seats"));
                uiStatus.add(rs.getString("ui_status"));
                return s;
            },
            args.toArray()
        );

        for(int i = 0; i < schedules.size(); i++) {
            Schedule s = schedules.get(i);
            Route r = RouteService.getRouteById(s.getRouteId());
            Train t = TrainService.getTrainById(s.getTrainId());

            tableModel.addRow(new Object[] {
                s,
                r.getRouteSummary(),
                t.getTrainSummary(),
                s.getDepartureTime(),
                s.getArrivalTime(),
                availableSeats.get(i),
                uiStatus.get(i)
            });
        }
    }

    private JScrollPane createScheduleScrollPane() {
        tableModel = new DefaultTableModel(
            new String[] {"obj_schedule", "Route", "Train", "Departure", "Arrival", "Available Seats", "Status"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        scheduleTable = new JTable(tableModel);
        scheduleTable.setRowHeight(30);
        scheduleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleTable.setFocusable(false);
        scheduleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scheduleTable.setPreferredSize(null);

        scheduleTable.getTableHeader().setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        scheduleTable.getTableHeader().setPreferredSize(new Dimension(10, 25));
        scheduleTable.getTableHeader().setReorderingAllowed(false);

        scheduleTable.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));

        TableColumnModel columnModel = scheduleTable.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(0));

        setColumnWidth(columnModel, 0, 450);
        setColumnWidth(columnModel, 1, 150);
        setColumnWidth(columnModel, 2, 150);
        setColumnWidth(columnModel, 3, 150);
        setColumnWidth(columnModel, 4, 130);
        setColumnWidth(columnModel, 5, 100);

        scheduleTable.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                int selectedRow = scheduleTable.getSelectedRow();
                if(selectedRow != -1) {
                    viewBtn.setEnabled(true);
                }
                else {
                    viewBtn.setEnabled(false);
                }
            }
        });

        for(int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setCellRenderer(renderer);
        }

        loadSchedules();

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        return scrollPane;
    }

    public void refreshPage() {
        loadSchedules();
    }

    private void setColumnWidth(TableColumnModel columnModel, int col, int width) {
        columnModel.getColumn(col).setMaxWidth(width);
        columnModel.getColumn(col).setMinWidth(width);
    }

    private LocalDateTime getLocalDateTime(JSpinner spinner) {
        try {
            spinner.commitEdit();
        } catch(Exception ignored) {}

        Date date = (Date) spinner.getValue();
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private class ScheduleCellRenderer extends DefaultTableCellRenderer {
        private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            boolean isStatus = false;
            if(value instanceof LocalDateTime) {
                setText(((LocalDateTime) value).format(formatter));
            } else if(column == 5) {
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
                    case "ongoing":
                        setForeground(Color.BLUE);
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

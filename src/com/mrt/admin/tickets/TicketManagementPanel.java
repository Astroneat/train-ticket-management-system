package com.mrt.admin.tickets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.frames.AdminFrame;
import com.mrt.models.Route;
import com.mrt.models.Schedule;
import com.mrt.models.Ticket;
import com.mrt.models.Train;
import com.mrt.services.RouteService;
import com.mrt.services.ScheduleService;
import com.mrt.services.TicketService;
import com.mrt.services.TrainService;
import com.mrt.services.UserService;

public class TicketManagementPanel extends JPanel {

    private AdminFrame frame;

    private int numBookedTickets;
    private int numTotalTickets;
    private JLabel displayNumBookedTickets;
    private JLabel displayNumSearchResults;
    private JTextField searchField;

    private JComboBox<String> statusBox;
    private JComboBox<Train> trainBox;
    private JComboBox<Route> routeBox;

    // private JButton addBtn;
    // private JButton viewBtn;
    private JButton cancelBtn;
    private JButton boardBtn;
    private JButton expireBtn;
    private JButton refreshBtn;

    private DefaultTableModel tableModel;
    private JTable ticketTable;
    private TicketCellRenderer renderer;

    private static final Train ALL_TRAINS = new Train(-1, "All", -1, "");
    private static final Route ALL_ROUTES = new Route(-1, "All", -1, -1, "");
    
    public TicketManagementPanel(AdminFrame frame) {
        this.frame = frame;
        renderer = new TicketCellRenderer();

        setLayout(new BorderLayout(0, 0));
        setBackground(Universal.BACKGROUND_WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createScrollPane(), BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(createHeaderPanel());
        panel.add(createSearchPanel());
        panel.add(createFilterPanel());
        panel.add(createActionButtonsPanel());

        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);

        panel.add(UIFactory.createBoldLabel("Ticket Management", 28));

        panel.add(Box.createHorizontalStrut(5));
        
        displayNumBookedTickets = UIFactory.createPlainLabel("", 14);
        panel.add(displayNumBookedTickets);

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);

        panel.add(UIFactory.createPlainLabel("Search:", 16));

        searchField = UIFactory.createTextField(30);
        searchField.setToolTipText("Search ticket, user, train...");
        searchField.addActionListener(e -> {
            loadAllTickets();
        });
        panel.add(searchField);

        JButton searchBtn = UIFactory.createIconButton("src/com/mrt/img/search.png", new Dimension(24, 24));
        searchBtn.addActionListener(e -> {
            loadAllTickets();
        });
        panel.add(searchBtn);

        displayNumSearchResults = UIFactory.createPlainLabel("", 14);
        panel.add(displayNumSearchResults);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);

        panel.add(UIFactory.createPlainLabel("Filter by:", 16));

        panel.add(UIFactory.createPlainLabel("Status", 16));
        statusBox = UIFactory.createComboBox(new String[] {
            "All", "booked", "boarded", "cancelled", "expired"
        });
        statusBox.addActionListener(e -> {
            loadAllTickets();
        });
        panel.add(statusBox);

        panel.add(UIFactory.createPlainLabel("Train", 16));
        loadAllTrains();
        trainBox.addActionListener(e -> {
            loadAllTickets();
        });
        panel.add(trainBox);

        panel.add(UIFactory.createPlainLabel("Route", 16));
        loadAllRoutes();
        routeBox.addActionListener(e -> {
            loadAllTickets();
        });
        panel.add(routeBox);

        JButton clearFilterBtn = UIFactory.createButton("Clear filter");
        clearFilterBtn.addActionListener(e -> {
            clearFilters();
            loadAllTickets();
        });
        panel.add(clearFilterBtn);

        return panel;
    }

    private JPanel createActionButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);

        Dimension btnDim = new Dimension(120, 36);
        cancelBtn = UIFactory.createButton("Cancel");
        cancelBtn.setPreferredSize(btnDim);
        boardBtn = UIFactory.createButton("Board");
        boardBtn.setPreferredSize(btnDim);
        expireBtn = UIFactory.createButton("Expire");
        expireBtn.setPreferredSize(btnDim);
        refreshBtn = UIFactory.createButton("Refresh");
        refreshBtn.setPreferredSize(btnDim);

        cancelBtn.addActionListener(e -> {
            int row = ticketTable.getSelectedRow();
            if(row == -1) return;

            Ticket selectedTicket = (Ticket) tableModel.getValueAt(row, 0);
            if(selectedTicket.getStatus().equals("booked")) {
                int option = JOptionPane.showConfirmDialog(frame, "Are you sure you want to cancel this ticket? (This action cannot be undone)", "Confirm cancellation", JOptionPane.YES_NO_OPTION);
                if(option == JOptionPane.YES_OPTION) {
                    TicketService.cancelTicket(selectedTicket.getTicketId());
                    loadAllTickets();
                }
            }
            else {
                JOptionPane.showMessageDialog(frame, "Only booked tickets can be cancelled", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        boardBtn.addActionListener(e -> {
            int row = ticketTable.getSelectedRow();
            if(row == -1) return;

            Ticket selectedTicket = (Ticket) tableModel.getValueAt(row, 0);
            if(selectedTicket.getStatus().equals("booked")) {
                int option = JOptionPane.showConfirmDialog(frame, "Mark this ticket as boarded?", "Confirm boarding", JOptionPane.YES_NO_OPTION);
                if(option == JOptionPane.YES_OPTION) {
                    TicketService.boardTicket(selectedTicket.getTicketId());
                    loadAllTickets();
                }
            }
            else {
                JOptionPane.showMessageDialog(frame, "Only booked tickets can be boarded", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        expireBtn.addActionListener(e -> {
            int row = ticketTable.getSelectedRow();
            if(row == -1) return;

            Ticket selectedTicket = (Ticket) tableModel.getValueAt(row, 0);
            if(selectedTicket.getStatus().equals("booked")) {
                int option = JOptionPane.showConfirmDialog(frame, "Force expire this ticket?", "Confirm force expiry", JOptionPane.YES_NO_OPTION);
                if(option == JOptionPane.YES_OPTION) {
                    TicketService.expireTicket(selectedTicket.getTicketId());
                    loadAllTickets();
                }
            }
            else {
                JOptionPane.showMessageDialog(frame, "Only booked tickets can be forced to expire", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            clearFilters();
            loadAllTickets();
        });

        panel.add(cancelBtn);
        panel.add(boardBtn);
        panel.add(expireBtn);
        panel.add(refreshBtn);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        return panel;
    }

    private void clearFilters() {
        statusBox.setSelectedIndex(0);
        trainBox.setSelectedIndex(0);
        routeBox.setSelectedIndex(0);
    }

    private void loadAllTrains() {
        trainBox = UIFactory.createComboBox();
        trainBox.setPreferredSize(new Dimension(200, 25));
        List<Train> trains = TrainService.getAllTrains();
        trainBox.addItem(ALL_TRAINS);
        for(Train t: trains) trainBox.addItem(t);
    }

    private void loadAllRoutes() {
        routeBox = UIFactory.createComboBox();
        routeBox.setPreferredSize(new Dimension(200, 25));
        List<Route> routes = RouteService.getAllRoutes();
        routeBox.addItem(ALL_ROUTES);
        for(Route r: routes) routeBox.addItem(r);
    }

    private void recountSearchDisplay() {
        int ignored = Universal.db().queryOne(
            """
            SELECT 
                COUNT(tk1.ticket_id) total_cnt,
                COUNT(tk2.ticket_id) booked_cnt
            FROM tickets tk1
            LEFT JOIN tickets tk2 ON tk1.ticket_id = tk2.ticket_id AND tk1.status = 'booked';        
            """,
            rs -> {
                numTotalTickets = rs.getInt("total_cnt");
                numBookedTickets = rs.getInt("booked_cnt");
                return 1;
            }
        );
    }

    private void loadAllTickets() {
        tableModel.setRowCount(0);

        String searchTerm = searchField.getText().trim();
        String statusFilter = statusBox.getSelectedItem().toString();
        if(statusBox.getSelectedIndex() == 0) statusFilter = "";
        Train trainFilter = (Train) trainBox.getSelectedItem();
        Route routeFilter = (Route) routeBox.getSelectedItem();

        String sql = 
        """
            SELECT * FROM tickets tk
            INNER JOIN users u ON tk.user_id = u.user_id
            INNER JOIN train_schedules ts ON tk.schedule_id = ts.schedule_id
            INNER JOIN train_routes tr ON ts.route_id = tr.route_id
            INNER JOIN trains t ON ts.train_id = t.train_id
            WHERE TRUE
        """;

        List<Object> args = new ArrayList<>();
        if(!searchTerm.isBlank()) {
            sql += 
            """
                AND (
                    tk.ticket_id LIKE ? OR
                    u.email LIKE ? OR
                    t.train_code LIKE ? OR
                    tr.route_code LIKE ? OR
                    DATE_FORMAT(departure_utc, '%H:%i %d-%m-%Y') LIKE ?
                )
            """;
            String pat = "%" + searchTerm + "%";
            args.add(pat);
            args.add(pat);
            args.add(pat);
            args.add(pat);
            args.add(pat);
        }
        if(!statusFilter.isBlank()) {
            sql += 
            """
                AND (tk.status = ?)    
            """;
            args.add(statusFilter);
        }
        if(trainFilter != ALL_TRAINS) {
            sql += 
            """
                AND (t.train_id = ?)        
            """;
            args.add(trainFilter.getTrainId());
        }
        if(routeFilter != ALL_ROUTES) {
            sql += 
            """
                AND (tr.route_id = ?)        
            """;
            args.add(routeFilter.getRouteId());
        }

        sql += 
        """
            ORDER BY tk.ticket_id        
        """;
        sql += ";";

        List<Ticket> tickets = Universal.db().query(
            sql,
            rs -> Ticket.parseResultSet(rs),
            args.toArray()
        );

        for(Ticket t: tickets) {
            Schedule s = ScheduleService.getScheduleById(t.getScheduleId());
            LocalDateTime scannedAt = t.getScannedAt();
            String scannedAtDisplay = "---";
            if(scannedAt != null) scannedAtDisplay = scannedAt.toString();

            tableModel.addRow(new Object[] {
                t,
                t.getTicketId(),
                UserService.getUserById(t.getUserId()).getEmail(),
                TrainService.getTrainById(s.getTrainId()).getTrainSummary(),
                RouteService.getRouteById(s.getRouteId()).getRouteSummary(),
                t.getCarNo() + "-" + t.getSeatIndex(),
                s.getDepartureTime(),
                s.getArrivalTime(),
                scannedAtDisplay,
                t.getStatus()
            });
        }

        recountSearchDisplay();
        displayNumBookedTickets.setText(numBookedTickets + " booked tickets");

        int numSearchResults = tickets.size();
        displayNumSearchResults.setText(numSearchResults + " result" + (numSearchResults > 1 ? "s" : "") + (numSearchResults == numTotalTickets ? "" : " (of " + numTotalTickets + " total)"));
    }

    private JScrollPane createScrollPane() {
        tableModel = new DefaultTableModel(
            new String[] { "obj_ticket", "Ticket ID", "User", "Train", "Route", "Seat", "Departure", "Arrival", "Scanned At", "Status"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        ticketTable = new JTable(tableModel);
        ticketTable.setRowHeight(30);
        ticketTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ticketTable.setFocusable(false);
        ticketTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        ticketTable.setPreferredSize(null);

        ticketTable.getTableHeader().setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        ticketTable.getTableHeader().setPreferredSize(new Dimension(10, 25));
        ticketTable.getTableHeader().setReorderingAllowed(false);

        ticketTable.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));

        TableColumnModel columnModel = ticketTable.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(0));

        setColumnWidth(columnModel, 0, 80);
        setColumnWidth(columnModel, 1, 200);
        setColumnWidth(columnModel, 2, 180);
        setColumnWidth(columnModel, 3, 350);
        setColumnWidth(columnModel, 4, 80);
        setColumnWidth(columnModel, 5, 130);
        setColumnWidth(columnModel, 6, 130);
        setColumnWidth(columnModel, 7, 130);
        setColumnWidth(columnModel, 8, 100);

        ticketTable.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                int selectedRow = ticketTable.getSelectedRow();
                if(selectedRow != -1) {
                    cancelBtn.setEnabled(true);
                    boardBtn.setEnabled(true);
                    expireBtn.setEnabled(true);
                }
                else {
                    cancelBtn.setEnabled(false);
                    boardBtn.setEnabled(false);
                    expireBtn.setEnabled(false);
                }
            }
        });

        for(int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setCellRenderer(renderer);
        }

        loadAllTickets();

        JScrollPane scrollPane = new JScrollPane(ticketTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    private void setColumnWidth(TableColumnModel columnModel, int col, int width) {
        columnModel.getColumn(col).setMaxWidth(width);
        columnModel.getColumn(col).setMinWidth(width);
    }

    private class TicketCellRenderer extends DefaultTableCellRenderer {
        private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            boolean isStatus = false;
            if(value instanceof LocalDateTime) {
                setText(((LocalDateTime) value).format(formatter));
            } else if(column == 8) {
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
                    case "booked": 
                        setForeground(Color.BLUE);
                        break;
                    case "boarded":
                        setForeground(new Color(0, 200, 0));
                        break;
                    case "cancelled":
                        setForeground(Color.RED);
                        break;
                    case "expired":
                        setForeground(Color.GRAY);
                }
            } else {
                setFont(table.getFont());
                setForeground(Color.BLACK);
            }
            return this;
        }
    }
}

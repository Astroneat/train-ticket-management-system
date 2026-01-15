package com.mrt.admin.schedules.seats;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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
import com.mrt.models.User;

public class UserPickerDialog extends JDialog {

    private JTextField searchField;
    private JComboBox<String> filterByBox;

    private JTable userTable;
    private DefaultTableModel tableModel;

    private JButton pickBtn;

    private User selectedUser;

    public UserPickerDialog(JDialog dialog) {
        super(dialog, "User Picker", true);

        setSize(800, 600);
        setLocationRelativeTo(dialog);
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
            "Select a user",
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
        top.add(createFilterPanel());
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
        searchField.setToolTipText("Search user");
        searchField.addActionListener(e -> {
            loadUsers();
        });
        panel.add(searchField);

        JButton searchButton = UIFactory.createIconButton("src/com/mrt/img/search.png", new Dimension(24, 24));
        searchButton.setPreferredSize(new Dimension(40, 40));
        searchButton.addActionListener(e -> {
            loadUsers();
        });
        panel.add(searchButton);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setOpaque(false);

        panel.add(UIFactory.createPlainLabel("Filter by role:", 14));

        filterByBox = UIFactory.createComboBox(new String[] {
            "All", "customer", "admin"
        });
        filterByBox.addActionListener(e -> {
            loadUsers();
        });
        panel.add(filterByBox);

        return panel;
    }

    private JScrollPane createScrollPane() {
        tableModel = new DefaultTableModel(
            new String[] {"obj_user", "Email", "Full Name", "Role"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if(column == 0) return true;
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setRowHeight(30);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        userTable.getTableHeader().setPreferredSize(new Dimension(0, 25));
        userTable.getTableHeader().setReorderingAllowed(false);
        userTable.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                int row = userTable.getSelectedRow();
                if(row != -1) {
                    pickBtn.setEnabled(true);
                } else {
                    pickBtn.setEnabled(false);
                }
            }
        });

        TableColumnModel columnModel = userTable.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(0));
        // columnModel.getColumn(0).setMaxWidth(100);
        // columnModel.getColumn(0).setMinWidth(100);

        userTable.setFocusable(false);

        loadUsers();

        JScrollPane scrollPane = new JScrollPane(userTable);
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
            selectedUser = null;
            dispose();
        });

        pickBtn = UIFactory.createButton("Pick");
        pickBtn.setPreferredSize(new Dimension(100, 33));
        pickBtn.setEnabled(false);
        pickBtn.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if(row != -1) {
                selectedUser = (User) tableModel.getValueAt(row, 0);
                dispose();
            }
            else selectedUser = null;
        });

        panel.add(cancelBtn);
        panel.add(pickBtn);
        return panel;
    }

    private void loadUsers() {
        String searchTerm = searchField.getText().trim();
        String filter = filterByBox.getSelectedItem().toString();

        List<Object> args = new ArrayList<>();
        String sql = 
        """
        SELECT * 
        FROM users 
        WHERE TRUE
        """;

        if(!searchTerm.isBlank()) {
            sql += " AND (email LIKE ? OR full_name LIKE ?)";
            args.add("%" + searchTerm + "%");
            args.add("%" + searchTerm + "%");
        }
        if(!filter.equals(filterByBox.getItemAt(0))) {
            sql += " AND (role = ?)";
            args.add(filter);
        }
        sql += ";";

        List<User> users = Universal.db().query(
            sql,
            rs -> User.parseResultSet(rs),
            args.toArray()
        );

        tableModel.setRowCount(0);
        for(User u: users) {
            tableModel.addRow(new Object[] {
                u,
                u.getEmail(),
                u.getFullName(),
                u.getRole()
            });
        }
    }

    public User getSelectedUser() {
        return selectedUser;
    }
}

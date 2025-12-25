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
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import com.mrt.dbobject.User;

public class UserManagementPanel extends JPanel {

    private AdminFrame frame;
    private User currentUser;

    private JTable userTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> filterBox;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;

    private JLabel numTableRowCount;

    public UserManagementPanel(AdminFrame frame, User currentUser) {
        this.frame = frame;
        this.currentUser = currentUser;

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
        topPanel.add(createActionPanel());

        add(topPanel, BorderLayout.NORTH);
        add(createUserTablePanel(), BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel header = new JPanel();
        header.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(1000, 50));

        JLabel title = new JLabel("User Management");
        title.setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 28));
        header.add(title);

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
        searchField.setToolTipText("Search by email or name");
        searchField.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        searchField.addActionListener(e -> {
            loadUsersWithConstraints();
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
            loadUsersWithConstraints();
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

        JLabel filterLabel = new JLabel("Filter by role:");
        filterLabel.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 16));
        panel.add(filterLabel);

        filterBox = new JComboBox<>(new String[] {
            "---", "customer", "admin"
        });
        filterBox.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        filterBox.addActionListener(e -> {
            loadUsersWithConstraints();
        });
        panel.add(filterBox);

        JButton clearFilterButton = new JButton("Clear filter");
        clearFilterButton.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        clearFilterButton.addActionListener(e -> {
            filterBox.setSelectedIndex(0);
            loadUsersWithConstraints();
        });
        panel.add(clearFilterButton);

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

        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        addButton.addActionListener(e -> {
            FormDialog addDialog = new FormDialog(frame, "Add User", new Dimension(400, 310));

            JTextField emailField = addDialog.addTextField("Email:");
            JTextField fullNameField = addDialog.addTextField("Full Name:");
            JTextField passwordField = addDialog.addTextField("Password:");
            JComboBox<String> roleField = addDialog.addComboBox("Role:", new String[]{
                "customer", "admin"
            });

            JButton saveBtn = addDialog.addButtonRow();
            saveBtn.addActionListener(saveEv -> {
                try {
                    String email = emailField.getText().trim();
                    String fullName = fullNameField.getText().trim();
                    String pass = passwordField.getText().trim();
                    String role = roleField.getSelectedItem().toString();

                    if(email.isBlank() || fullName.isBlank() || pass.isBlank() || role.isBlank()) {
                        JOptionPane.showMessageDialog(addDialog, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    User user = Universal.db().queryOne(
                        "SELECT * FROM users WHERE email = ?",
                        rs -> User.parseResultSet(rs),
                        email
                    );
                    if(user != null) {
                        JOptionPane.showMessageDialog(addDialog, "Email already in use! Please use another one", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Universal.db().execute(
                        "INSERT INTO users(email, full_name, password, role) VALUES (?, ?, ?, ?);",
                        email,
                        fullName,
                        pass,
                        role
                    );

                    loadAllUsers();
                    addDialog.dispose();
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(addDialog, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            addDialog.setVisible(true);
        });

        editButton.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(frame, "This cannot happen. Please contact nvtd.", "???", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String userId = String.valueOf((int) userTable.getValueAt(row, 0));
            if(userId.equals(String.valueOf(currentUser.getUserId()))) {
                JOptionPane.showMessageDialog(frame, "You cannot edit yourself. Please edit in the database", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String currentEmail = userTable.getValueAt(row, 1).toString();
            String currentFullName = userTable.getValueAt(row, 2).toString();
            String currentRole = userTable.getValueAt(row, 3).toString();

            FormDialog editDialog = new FormDialog(frame, "Edit User", new Dimension(400, 310));
            JTextField emailField = editDialog.addTextField("Email:");
            JTextField fullNameField = editDialog.addTextField("Full Name:");
            JComboBox<String> roleBox = editDialog.addComboBox("Role:", new String[] {
                "customer", "admin"
            });

            emailField.setText(currentEmail);
            fullNameField.setText(currentFullName);
            roleBox.setSelectedItem(currentRole);

            JButton saveBtn = editDialog.addButtonRow();
            saveBtn.addActionListener(editEv -> {
                try {
                    String email = emailField.getText().trim();
                    String fullName = fullNameField.getText().trim();
                    String role = roleBox.getSelectedItem().toString();

                    if(email.isBlank() || fullName.isBlank() || role.isBlank()) {
                        JOptionPane.showMessageDialog(editDialog, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    User user = Universal.db().queryOne(
                        "SELECT * FROM users WHERE email = ?", 
                        rs -> User.parseResultSet(rs), 
                        email
                    );
                    if(!email.equals(currentEmail) && user != null) {
                        JOptionPane.showMessageDialog(editDialog, "Email already in use! Please use another one.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Universal.db().execute(
                        "UPDATE users SET email = ?, full_name = ?, role = ? WHERE user_id = ?;",
                        email,
                        fullName,
                        role,
                        userId
                    );

                    loadUsersWithConstraints();
                    editDialog.dispose();
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(editDialog, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            editDialog.setVisible(true);
        });

        deleteButton.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(frame, "This cannot happen. Please contact nvtd.", "???", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String userId = String.valueOf((int) userTable.getValueAt(row, 0));
            if(userId.equals(String.valueOf(currentUser.getUserId()))) {
                JOptionPane.showMessageDialog(frame, "Don\'t delete yourself", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this user?", "Confirm", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION) {
                Universal.db().execute(
                    "DELETE FROM users WHERE user_id = ?",
                    userId
                );
                loadUsersWithConstraints();
            }
        });

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            filterBox.setSelectedIndex(0);
            loadAllUsers();
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

    private JButton createActionButton(String text) {
        JButton btn = new JButton(text);
        // btn.setMaximumSize(new Dimension(180, 40));
        btn.setPreferredSize(new Dimension(120, 36));
        btn.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        btn.setVerticalAlignment(SwingConstants.CENTER);
        return btn;
    }

    private JScrollPane createUserTablePanel() {
        tableModel = new DefaultTableModel(
            new String[] {"ID", "Email", "Full Name", "Role"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setRowHeight(30);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 14));
        userTable.getTableHeader().setReorderingAllowed(false);
        userTable.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        userTable.getTableHeader().setPreferredSize(new Dimension(10, 25));
        userTable.getColumnModel().getColumn(0).setMaxWidth(40);
        userTable.getColumnModel().getColumn(0).setMinWidth(40);
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                int selectedRow = userTable.getSelectedRow();
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

        loadAllUsers();

        JScrollPane scrollPane = new JScrollPane(userTable);
        return scrollPane;
    }

    private int countUsers() {
        return Universal.db().queryOne(
            "SELECT COUNT(*) cnt FROM users;",
            rs -> rs.getInt("cnt")
        );
    }

    private void loadAllUsers() {
        tableModel.setRowCount(0);

        List<User> userList = Universal.db().query(
            "SELECT * FROM users;",
            rs -> User.parseResultSet(rs)
        );

        for(User user: userList) {
            tableModel.addRow(new Object[] {
                user.getUserId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole()
            });
        }

        numTableRowCount.setText(userList.size() + " result" + (userList.size() > 1 ? "s" : ""));
    }

    private void loadUsersWithConstraints() {
        tableModel.setRowCount(0);
        String searchTerm = searchField.getText().trim();
        String role = "";
        if(filterBox.getSelectedIndex() != 0) role = filterBox.getSelectedItem().toString();

        List<String> args = new ArrayList<String>();

        String sql = "SELECT * FROM users WHERE true";
        if(!searchTerm.isBlank()) {
            sql += " AND (email LIKE ? OR full_name LIKE ?)";
            args.add("%" + searchTerm + "%");
            args.add("%" + searchTerm + "%");
        }
        if(!role.isBlank()) {
            sql += " AND (role = ?)";
            args.add(role);
        }
        sql += ";";

        List<User> userList = Universal.db().query(
            sql,
            rs -> User.parseResultSet(rs),
            args.toArray(new Object[args.size()])
        );

        for(User user: userList) {
            tableModel.addRow(new Object[] {
                user.getUserId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole()
            });
        }

        int returnedSize = userList.size();
        int numUsers = countUsers();
        numTableRowCount.setText(returnedSize + " result" + (returnedSize > 1 ? "s" : "") + (returnedSize == numUsers ? "" : " (" + numUsers + " total)"));
    }
}
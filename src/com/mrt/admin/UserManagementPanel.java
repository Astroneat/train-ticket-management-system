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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.mrt.Universal;
import com.mrt.User;

public class UserManagementPanel extends JPanel {

    private JTable userTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> filterBox;

    private JLabel numTableRowCount;
    private int numUsers;

    public UserManagementPanel() {
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
        search.add(searchField);

        JButton searchButton = new JButton();
        try {
            ImageIcon icon = new ImageIcon("/Volumes/Data/ThanhDat/CS/VKU_uni/year1/java_oop/finals/src/com/mrt/img/search.png");
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            searchButton.setIcon(new ImageIcon(newImg));
        } catch (Exception ignored) {}
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText();
            String role = "";
            if(filterBox.getSelectedIndex() != 0) role = filterBox.getSelectedItem().toString();
            searchUsers(searchTerm, role);
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
            "---", "Customer", "Admin"
        });
        filterBox.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        panel.add(filterBox);

        JButton clearFilterButton = new JButton("Clear filter");
        clearFilterButton.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        clearFilterButton.addActionListener(e -> {
            filterBox.setSelectedIndex(0);
        });
        panel.add(clearFilterButton);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setOpaque(false);

        JButton addButton = createActionButton("Add");
        JButton editButton = createActionButton("Edit");
        JButton deleteButton = createActionButton("Delete");
        JButton refreshButton = createActionButton("Refresh");

        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            filterBox.setSelectedIndex(0);
            loadUsers();
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
        userTable.getTableHeader().setFont(new Font(Universal.defaultFontFamily, Font.BOLD, 16));
        userTable.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));

        userTable.getColumnModel().getColumn(0).setMaxWidth(40);

        loadUsers();

        JScrollPane scrollPane = new JScrollPane(userTable);
        return scrollPane;
    }

    private void loadUsers() {
        tableModel.setRowCount(0);

        List<User> userList = Universal.db().query(
            "SELECT * FROM users;",
            rs -> new User(
                rs.getInt("user_id"), 
                rs.getString("email"), 
                rs.getString("full_name"), 
                rs.getString("role")
            )
        );

        for(User user: userList) {
            tableModel.addRow(new Object[] {
                user.getUserId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole()
            });
        }

        int rowCount = tableModel.getRowCount();
        numUsers = rowCount;
        numTableRowCount.setText(rowCount + " result" + (rowCount > 1 ? "s" : ""));
    }

    private void searchUsers(String searchTerm, String role) {
        tableModel.setRowCount(0);

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
            rs -> new User(
                rs.getInt("user_id"), 
                rs.getString("email"), 
                rs.getString("full_name"), 
                rs.getString("role")
            ),
            args.toArray(new String[args.size()])
        );

        for(User user: userList) {
            tableModel.addRow(new Object[] {
                user.getUserId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole()
            });
        }

        int rowCount = tableModel.getRowCount();
        numTableRowCount.setText(rowCount + " result" + (rowCount > 1 ? "s" : "") + " (" + numUsers + " total)");
    }
}

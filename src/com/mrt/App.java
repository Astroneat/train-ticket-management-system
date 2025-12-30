package com.mrt;
import java.awt.Color;

import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import com.mrt.admin.AdminMainFrame;
import com.mrt.model.User;

public class App {
    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ignored) {}
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("App shutting down...");
            Universal.db().closeConnection();
        }));

        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        if(defaults.get("Table.alternateRowColor") == null)
            defaults.put("Table.alternateRowColor", new Color(240, 240, 240));

        SwingUtilities.invokeLater(() -> {
            Universal.db().establishConnection();
            // new LoginFrame().setVisible(true);

            // new MainFrame(new User(2, "admin", "admin", "admin")).setVisible(true);;
            new AdminMainFrame(new User(2, "admin", "admin", "admin")).setVisible(true);;
        });
    }
}

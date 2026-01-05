package com.mrt;
import java.awt.Color;

import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import com.mrt.model.User;
import com.mrt.user.UserFrame;

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

            new UserFrame(new User(2, "admin", "admin", "admin")).setVisible(true);;
            // new AdminFrame(new User(2, "admin", "admin", "admin")).setVisible(true);;
        });
    }
}

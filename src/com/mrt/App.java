package com.mrt;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {
    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ignored) {}

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("App shutting down...");
            Universal.db().closeConnection();
        }));

        SwingUtilities.invokeLater(() -> {
            Universal.db().establishConnection();
            // new LoginFrame().setVisible(true);

            // new MainFrame(new User(2, "admin", "admin", "admin")).setVisible(true);;
            new AdminFrame(new User(2, "admin", "admin", "admin")).setVisible(true);;
        });
    }
}

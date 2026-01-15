package com.mrt;
import java.awt.Color;
import java.time.format.DateTimeFormatter;

public class Universal {
    public static final String defaultFontFamily = "Helvetica";

    public static final Color SKYBLUE = new Color(135, 207, 235);
    public static final Color BACKGROUND_WHITE = new Color(250, 250, 250);
    public static final Color RAYWHITE = new Color(245, 245, 245);
    public static final Color BACKGROUND_BLACK = new Color(36, 36, 36);

    public static final Color PASTEL_RED = Color.decode("#ffadad");
    public static final Color PASTEL_ORANGE = Color.decode("#ffd6a5");
    public static final Color PASTEL_YELLOW = Color.decode("#fdffb6");
    public static final Color PASTEL_GREEN = Color.decode("#caffbf");
    public static final Color PASTEL_CYAN = Color.decode("#9bf6ff");
    public static final Color PASTEL_BLUE = Color.decode("#a0c4ff");
    public static final Color PASTEL_PURPLE = Color.decode("#bdb2ff");
    public static final Color PASTEL_PINK = Color.decode("#ffc6ff");
    public static final Color PASTEL_WHITE = Color.decode("#fffffc");
    public static final Color PASTEL_GREY = Color.decode("#d0d0d0");

    private static final EasyDB DB = new EasyDB(3306, "TrainTicketManagement", "root", "");

    public static EasyDB db() {
        return DB;
    }
    public static DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
    }
    public static DateTimeFormatter getDateFormatter() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }
}

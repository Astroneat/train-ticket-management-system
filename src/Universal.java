import java.awt.Color;

public class Universal {
    public static final String defaultFontFamily = "Helvetica";

    public static final Color SKYBLUE = new Color(135, 207, 235);
    public static final Color BACKGROUND_WHITE = new Color(245, 247, 249);
    public static final Color RAYWHITE = new Color(245, 245, 245);
    public static final Color BACKGROUND_BLACK = new Color(36, 36, 36);

    private static final EasyDB DB = new EasyDB(3306, "TrainTicketManagement", "root", "");

    public static EasyDB db() {
        return DB;
    }
}

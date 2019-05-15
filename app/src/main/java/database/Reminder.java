package database;

import java.text.SimpleDateFormat;

public class Reminder {

    private String datePattern = "dd/MM/yyyy H:mm:ss a";
    private SimpleDateFormat format = new SimpleDateFormat(datePattern);

}

package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MADA1.db";

    private static final String MOVIE_TABLE_NAME = "movie_table";
    private static final String MOVIE_COL_1 = "ID";
    private static final String MOVIE_COL_2 = "NAME";
    private static final String MOVIE_COL_3 = "YEAR";
    private static final String MOVIE_COL_4 = "PIC";

    private static final String EVENT_TABLE_NAME = "event_table";
    private static final String EVENT_COL_1 = "ID";
    private static final String EVENT_COL_2 = "NAME";
    private static final String EVENT_COL_3 = "START_TIME";
    private static final String EVENT_COL_4 = "END_TIME";
    private static final String EVENT_COL_5 = "VENUE";
    private static final String EVENT_COL_6 = "LATITUDE";
    private static final String EVENT_COL_7 = "LONGITUDE";
    private static final String EVENT_COL_8 = "MOVIE_ID";

    private static final String ATTENDANCE_TABLE_NAME = "attendance_table";
    private static final String ATTENDANCE_COL_1 = "EVENT_ID";
    private static final String ATTENDANCE_COL_2 = "ATTENDEE_NAME";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MOVIE_TABLE_NAME + " (" +
                MOVIE_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MOVIE_COL_2 + " TEXT, " +
                MOVIE_COL_3 + " NUMERIC, " +
                MOVIE_COL_4 + " TEXT);");
        db.execSQL("CREATE TABLE " + EVENT_TABLE_NAME + " (" +
                EVENT_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EVENT_COL_2 + " TEXT, " +
                EVENT_COL_3 + " TEXT, " +
                EVENT_COL_4 + " TEXT, " +
                EVENT_COL_5 + " TEXT, " +
                EVENT_COL_6 + " REAL, " +
                EVENT_COL_7 + " REAL, " +
                EVENT_COL_8 + " INTEGER, " +
                "FOREIGN KEY (" + EVENT_COL_8 + ") REFERENCES " + MOVIE_TABLE_NAME + "(" + MOVIE_COL_1 + "));");
        db.execSQL("CREATE TABLE " + ATTENDANCE_TABLE_NAME + " (" +
                ATTENDANCE_COL_1 + " INTEGER, " +
                ATTENDANCE_COL_2 + " TEXT, " +
                "FOREIGN KEY (" + ATTENDANCE_COL_1 + ") REFERENCES " + EVENT_TABLE_NAME + "(" + EVENT_COL_1 + "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MOVIE_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + ATTENDANCE_TABLE_NAME + ";");
        onCreate(db);
    }
}

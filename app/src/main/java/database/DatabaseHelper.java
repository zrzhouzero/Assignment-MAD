package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

import model.EventImpl;
import model.Location;
import model.MovieImpl;

public class DatabaseHelper extends SQLiteOpenHelper implements Serializable {

    private Context context;

    private static final String TAG = "DatabaseHelper";

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
        this.context = context;
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "DatabaseHelper: Constructed.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MOVIE_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + ATTENDANCE_TABLE_NAME + ";");
        onCreate(db);
    }

    public void createDatabase(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MOVIE_TABLE_NAME + " (" +
                MOVIE_COL_1 + " TEXT PRIMARY KEY, " +
                MOVIE_COL_2 + " TEXT, " +
                MOVIE_COL_3 + " NUMERIC, " +
                MOVIE_COL_4 + " TEXT);");

        db.execSQL("CREATE TABLE " + EVENT_TABLE_NAME + " (" +
                EVENT_COL_1 + " TEXT PRIMARY KEY, " +
                EVENT_COL_2 + " TEXT, " +
                EVENT_COL_3 + " TEXT, " +
                EVENT_COL_4 + " TEXT, " +
                EVENT_COL_5 + " TEXT, " +
                EVENT_COL_6 + " REAL, " +
                EVENT_COL_7 + " REAL, " +
                EVENT_COL_8 + " TEXT, " +
                "FOREIGN KEY (" + EVENT_COL_8 + ") REFERENCES " + MOVIE_TABLE_NAME + "(" + MOVIE_COL_1 + "));");

        db.execSQL("CREATE TABLE " + ATTENDANCE_TABLE_NAME + " (" +
                ATTENDANCE_COL_1 + " TEXT, " +
                ATTENDANCE_COL_2 + " TEXT, " +
                "PRIMARY KEY (" + ATTENDANCE_COL_1 + ", " + ATTENDANCE_COL_2 + "), " +
                "FOREIGN KEY (" + ATTENDANCE_COL_1 + ") REFERENCES " + EVENT_TABLE_NAME + "(" + EVENT_COL_1 + "));");

        Log.d(TAG, "onCreate: onCreate.");
    }

    public void loadMoviesFromFile() throws IOException, NumberFormatException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        InputStream movieStream = context.getAssets().open("movies.txt");
        Scanner in = new Scanner(movieStream);
        while (in.hasNext()) {
            String str = in.nextLine();
            if (str.contains("//")) {
                continue;
            }
            String[] temp = str.replace("\"", "").split(",");
            values.put(MOVIE_COL_1, temp[0]);
            values.put(MOVIE_COL_2, temp[1]);
            values.put(MOVIE_COL_3, Integer.parseInt(temp[2]));
            values.put(MOVIE_COL_4, temp[3]);
            db.insert(MOVIE_TABLE_NAME, null, values);
        }
    }

    public void loadEventsFromFile() throws IOException, NumberFormatException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        InputStream eventStream = context.getAssets().open("events.txt");
        Scanner in = new Scanner(eventStream);
        while (in.hasNext()) {
            String str = in.nextLine();
            if (str.contains("//")) {
                continue;
            }
            String[] temp = str.replace("\"", "").split(",");
            values.put(EVENT_COL_1, temp[0]);
            values.put(EVENT_COL_2, temp[1]);
            values.put(EVENT_COL_3, temp[2].toUpperCase());
            values.put(EVENT_COL_4, temp[3].toUpperCase());
            values.put(EVENT_COL_5, temp[4]);
            values.put(EVENT_COL_6, Double.parseDouble(temp[5]));
            values.put(EVENT_COL_7, Double.parseDouble(temp[6]));

            db.insert(EVENT_TABLE_NAME, null, values);
        }
    }

    public MovieImpl findMovieById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + MOVIE_TABLE_NAME + " WHERE " +
                MOVIE_COL_1 + " = \"" + id + "\";", null);
        cursor.moveToFirst();
        MovieImpl movie = new MovieImpl(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3));
        cursor.close();
        return movie;
    }

    public String findMovieIdByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + MOVIE_COL_1 + " FROM " + MOVIE_TABLE_NAME + " WHERE " +
                MOVIE_COL_2 + " = \"" + name + "\";", null);
        String ans = "";
        if (cursor.moveToFirst()) {
            ans = cursor.getString(0);
        }
        cursor.close();
        return ans;
    }

    public EventImpl findEventById(String id) throws ParseException {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor eventCursor = db.rawQuery("SELECT * FROM " + EVENT_TABLE_NAME + " WHERE " +
                EVENT_COL_1 + " = \"" + id + "\";", null);
        eventCursor.moveToFirst();

        String datePattern = "dd/MM/yyyy H:mm:ss a";
        SimpleDateFormat format = new SimpleDateFormat(datePattern);

        EventImpl event = new EventImpl(eventCursor.getString(0), eventCursor.getString(1), format.parse(eventCursor.getString(2)),
                format.parse(eventCursor.getString(3)), eventCursor.getString(4), new Location(eventCursor.getDouble(5), eventCursor.getDouble(6)));

        // set movie
        if (eventCursor.getString(7) != null && !eventCursor.getString(7).equals("")) {
            event.setMovie(findMovieById(eventCursor.getString(7)));
        }

        // set attendee
        if (getAttendeesNamesByEventId(id) != null) {
            event.setAttendees(getAttendeesNamesByEventId(id));
        }

        eventCursor.close();
        return event;
    }

    public ArrayList<String> getAttendeesNamesByEventId(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> result = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT " + ATTENDANCE_COL_2 + " FROM " + ATTENDANCE_TABLE_NAME + " WHERE " +
                ATTENDANCE_COL_1 + " = \"" + id + "\";", null);

        if (cursor.moveToFirst()) {
            do {
                result.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return result;
    }

    public ArrayList<MovieImpl> reloadMovieList() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<MovieImpl> movies = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT " + MOVIE_COL_1 + " FROM " + MOVIE_TABLE_NAME + ";", null);

        if (cursor.moveToFirst()) {
            do {
                ids.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        for (String id : ids) {
            movies.add(findMovieById(id));
        }

        cursor.close();
        return movies;
    }

    public ArrayList<EventImpl> reloadEventList() throws ParseException {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<EventImpl> events = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT " + EVENT_COL_1 + " FROM " + EVENT_TABLE_NAME + ";", null);

        if (cursor.moveToFirst()) {
            do {
                ids.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        for (String id : ids) {
            events.add(findEventById(id));
        }

        cursor.close();
        return events;
    }

    public void insertEvent(EventImpl event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String datePattern = "dd/MM/yyyy H:mm:ss a";
        SimpleDateFormat format = new SimpleDateFormat(datePattern);

        values.put(EVENT_COL_1, event.getId());
        values.put(EVENT_COL_2, event.getTitle());
        values.put(EVENT_COL_3, format.format(event.getStartDate()));
        values.put(EVENT_COL_4, format.format(event.getEndDate()));
        values.put(EVENT_COL_5, event.getVenue());
        values.put(EVENT_COL_6, event.getLocation().getLatitude());
        values.put(EVENT_COL_7, event.getLocation().getLongitude());

        if (event.getMovie() != null) {
            String movieId = findMovieIdByName(event.getMovie().getTitle());
            values.put(EVENT_COL_8, movieId);
        }

        db.insert(EVENT_TABLE_NAME, null, values);

        if (event.getAttendees() != null && event.getAttendees().size() > 0) {
            db.rawQuery("DELETE FROM " + ATTENDANCE_TABLE_NAME + " WHERE " + ATTENDANCE_COL_1 + " = \"" + event.getId() + "\";", null);

            ContentValues attendeeValues = new ContentValues();
            for (String s : event.getAttendees()) {
                attendeeValues.put(ATTENDANCE_COL_1, event.getId());
                attendeeValues.put(ATTENDANCE_COL_2, s);

                db.insert(ATTENDANCE_TABLE_NAME, null, attendeeValues);
            }
        }
    }

    public void updateEvent(EventImpl event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String datePattern = "dd/MM/yyyy H:mm:ss a";
        SimpleDateFormat format = new SimpleDateFormat(datePattern);

        values.put(EVENT_COL_2, event.getTitle());
        values.put(EVENT_COL_3, format.format(event.getStartDate()));
        values.put(EVENT_COL_4, format.format(event.getEndDate()));
        values.put(EVENT_COL_5, event.getVenue());
        values.put(EVENT_COL_6, event.getLocation().getLatitude());
        values.put(EVENT_COL_7, event.getLocation().getLongitude());

        if (event.getMovie() != null) {
            String movieId = findMovieIdByName(event.getMovie().getTitle());
            values.put(EVENT_COL_8, movieId);
        }

        String con = EVENT_COL_1 + " = \"" + event.getId() + "\"";

        db.update(EVENT_TABLE_NAME, values, con, null);

        if (event.getAttendees() != null && event.getAttendees().size() > 0) {
            db.rawQuery("DELETE FROM " + ATTENDANCE_TABLE_NAME + " WHERE " + ATTENDANCE_COL_1 + " = \"" + event.getId() + "\";", null);

            ContentValues attendeeValues = new ContentValues();
            for (String s : event.getAttendees()) {
                attendeeValues.put(ATTENDANCE_COL_1, event.getId());
                attendeeValues.put(ATTENDANCE_COL_2, s);

                db.insert(ATTENDANCE_TABLE_NAME, null, attendeeValues);
            }
        }
    }

    public void deleteEvent(EventImpl event) {
        SQLiteDatabase db = this.getWritableDatabase();

        String eventCon = EVENT_COL_1 + " = \"" + event.getId() + "\"";
        db.delete(EVENT_TABLE_NAME, eventCon, null);

        String attendeeCon = ATTENDANCE_COL_1 + " = \"" + event.getId() + "\"";
        db.delete(ATTENDANCE_TABLE_NAME, attendeeCon, null);
    }

}

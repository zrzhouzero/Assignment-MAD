package android.mad.assignment1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Scanner;

import database.DatabaseHelper;
import model.EventImpl;
import model.EventModel;

public class EventList extends AppCompatActivity {

    private final static String TAG = "EventList";
    private EventModel model;
    private ConstraintLayout constraintLayout;
    private RecyclerView eventList;
    private EventListAdapter adapter;
    private DatabaseHelper myDb;

    private final static File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "mad_ass_1");
    private final static File movieFile = new File(Environment.getExternalStorageDirectory() + File.separator + "mad_ass_1/movies.txt");
    private final static File eventFile = new File(Environment.getExternalStorageDirectory() + File.separator + "mad_ass_1/events.txt");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        Log.d(TAG, "onCreate: started.");

        myDb = new DatabaseHelper(this);
        try {
            myDb.loadMoviesFromFile();
            myDb.loadEventsFromFile();
        } catch (Exception e) {
            Log.e(TAG, "load file: failed");
            e.printStackTrace();
        }

        try {
            appInitialisation();
        } catch (IOException e) {
            Log.e(TAG, "onCreate: Cannot find or load app.");
        }

        Button sortEventButton = findViewById(R.id.button_sort_event);
        sortEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortEventLatestFirst();
            }
        });

        Button addEventButton = findViewById(R.id.button_add_event);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showAddEventPage = new Intent(v.getContext(), AddEvent.class);
                v.getContext().startActivity(showAddEventPage);
            }
        });

        Button recentEventsOnMapButton = findViewById(R.id.mapButton);
        recentEventsOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displaySoonestEvent = new Intent(v.getContext(), DisplaySoonestEventOnMapsActivity.class);
                displaySoonestEvent.putExtra("SoonestEvents", model.getSoonest3Events());
                v.getContext().startActivity(displaySoonestEvent);
            }
        });

        Button viewCalendar = findViewById(R.id.calendar_button);
        viewCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showCalendarView = new Intent(v.getContext(), MyCalendar.class);
                showCalendarView.putExtra("EVENTS", model.getEvents());
                v.getContext().startActivity(showCalendarView);
            }
        });

        constraintLayout = findViewById(R.id.event_list_page_layout);

        try {
            initialiseModel();
        } catch (IOException i) {
            Log.d(TAG, "onCreate: file not found.");
        } catch (ParseException p) {
            Log.d(TAG, "onCreate: parse error.");
        }
    }

    private void initialiseModel() throws IOException, ParseException {
        model = new EventModel();
        model.initialisation(eventFile, movieFile);
        initialiseRecyclerView();
    }

    private void initialiseRecyclerView() {
        Log.d(TAG, "initialiseRecyclerView: initialisation.");
        eventList = findViewById(R.id.event_list);
        adapter = new EventListAdapter(model.getEvents(), this);
        eventList.setAdapter(adapter);
        eventList.setLayoutManager(new LinearLayoutManager(this));

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final EventImpl item = adapter.getEvents().get(position);

                adapter.removeItem(position);

                model.removeEventById(item.getId());

                // remove line from the source file
                try {
                    PrintWriter writer = new PrintWriter(new FileWriter(eventFile, false));
                    String tarDateTimeFormat = "M/dd/yyyy H:mm:ss a";
                    SimpleDateFormat writeFormat = new SimpleDateFormat(tarDateTimeFormat);
                    for (EventImpl e : model.getEvents()) {
                        writer.write("\"" + e.getId() + "\",\"" + e.getTitle() + "\",\"" + writeFormat.format(e.getStartDate()) +
                                "\",\"" + writeFormat.format(e.getEndDate()) + "\",\"" + e.getVenue() +
                                "\",\"" + e.getLocation().getLatitude() + ", " + e.getLocation().getLongitude() + "\"\n");
                    }
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Snackbar snackbar = Snackbar
                        .make(constraintLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        adapter.restoreItem(item, position);
                        eventList.scrollToPosition(position);
                        // get the removed line back
                        try {
                            PrintWriter writer = new PrintWriter(new FileWriter(eventFile, true));
                            String tarDateTimeFormat = "M/dd/yyyy H:mm:ss a";
                            SimpleDateFormat writeFormat = new SimpleDateFormat(tarDateTimeFormat);
                            writer.write("\"" + item.getId() + "\",\"" + item.getTitle() + "\",\"" + writeFormat.format(item.getStartDate()) +
                                    "\",\"" + writeFormat.format(item.getEndDate()) + "\",\"" + item.getVenue() +
                                    "\",\"" + item.getLocation().getLatitude() + ", " + item.getLocation().getLongitude() + "\"\n");
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(eventList);
    }

    private void appInitialisation() throws IOException {
        if (!directory.exists() && !directory.isDirectory()) {
            if (directory.mkdirs()) {
                Log.i("CreateDir", "App dir created");
                InputStream movieStream = getAssets().open("movies.txt");
                InputStream eventStream = getAssets().open("events.txt");
                Scanner movieScanner = new Scanner(movieStream);
                Scanner eventScanner = new Scanner(eventStream);
                StringBuilder movieStringBuilder = new StringBuilder();
                StringBuilder eventStringBuilder = new StringBuilder();
                while (movieScanner.hasNext()) {
                    movieStringBuilder.append(movieScanner.nextLine()).append(System.lineSeparator());
                }
                while (eventScanner.hasNext()) {
                    eventStringBuilder.append(eventScanner.nextLine()).append(System.lineSeparator());
                }
                PrintWriter movieWriter = new PrintWriter(new FileWriter(movieFile, false));
                PrintWriter eventWriter = new PrintWriter(new FileWriter(eventFile, false));
                movieWriter.write(movieStringBuilder.toString());
                eventWriter.write(eventStringBuilder.toString());
                movieWriter.close();
                eventWriter.close();
            } else {
                Log.w("CreateDir", "Unable to create app dir!");
            }
        } else {
            Log.i("CreateDir", "App dir already exists");
        }
    }

    private void sortEventLatestFirst() {
        model.getEvents().sort(new Comparator<EventImpl>() {
            @Override
            public int compare(EventImpl o1, EventImpl o2) {
                return o2.getStartDate().compareTo(o1.getStartDate());
            }
        });
        reloadRecyclerView();
    }

    private void reloadRecyclerView() {
        adapter = new EventListAdapter(model.getEvents(), this);
        eventList.setAdapter(adapter);
        eventList.setLayoutManager(new LinearLayoutManager(this));
    }

}

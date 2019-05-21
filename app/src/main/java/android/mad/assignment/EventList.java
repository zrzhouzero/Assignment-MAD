package android.mad.assignment;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.Button;

import java.text.ParseException;

import database.DatabaseHelper;
import model.ApplicationSettingsHandler;
import model.EventImpl;
import model.EventModel;

public class EventList extends AppCompatActivity {

    private final static String TAG = "EventList";
    private EventModel model;
    private ConstraintLayout constraintLayout;
    private RecyclerView eventList;
    private EventListAdapter adapter;
    private DatabaseHelper myDb;

    private static final int REQUEST_LOCATION_PERMISSION = 404;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        Log.d(TAG, "onCreate: started.");

        myDb = new DatabaseHelper(this);
        try {
            myDb.loadMoviesFromFile();
            myDb.loadEventsFromFile();
            ApplicationSettingsHandler.readSettingFile();
        } catch (Exception e) {
            Log.e(TAG, "load file: failed");
            e.printStackTrace();
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        NetworkStatusChangeReceiver receiver = new NetworkStatusChangeReceiver();
        registerReceiver(receiver, intentFilter);

        Button sortEventButton = findViewById(R.id.button_sort_event);
        sortEventButton.setOnClickListener(v -> sortEventLatestFirst());

        Button settingButton = findViewById(R.id.setting_button);
        settingButton.setOnClickListener(l -> {
            FragmentManager manager = getSupportFragmentManager();
            SettingPageFragment fragment = new SettingPageFragment();
            fragment.show(manager, "settings");
        });

        Button addEventButton = findViewById(R.id.button_add_event);
        addEventButton.setOnClickListener(v -> {
            Intent showAddEventPage = new Intent(v.getContext(), AddEvent.class);
            v.getContext().startActivity(showAddEventPage);
        });

        Button recentEventsOnMapButton = findViewById(R.id.mapButton);
        recentEventsOnMapButton.setOnClickListener(v -> {
            Intent displaySoonestEvent = new Intent(v.getContext(), DisplaySoonestEventOnMapsActivity.class);
            displaySoonestEvent.putExtra("SoonestEvents", model.getSoonest3Events());
            v.getContext().startActivity(displaySoonestEvent);
        });

        Button viewCalendar = findViewById(R.id.calendar_button);
        viewCalendar.setOnClickListener(v -> {
            Intent showCalendarView = new Intent(v.getContext(), MyCalendar.class);
            showCalendarView.putExtra("EVENTS", model.getEvents());
            v.getContext().startActivity(showCalendarView);
        });

        constraintLayout = findViewById(R.id.event_list_page_layout);

        try {
            initialiseModel();
        } catch (ParseException p) {
            Log.d(TAG, "onCreate: parse error.");
        }

        runtimePermissions();

        Intent notificationServiceIntent = new Intent(getApplicationContext(), NotificationService.class);
        startService(notificationServiceIntent);
    }

    private void initialiseModel() throws ParseException {
        model = new EventModel(myDb.reloadMovieList(), myDb.reloadEventList());
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

                myDb.deleteEvent(item);

                Snackbar snackbar = Snackbar
                        .make(constraintLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", view -> {

                    adapter.restoreItem(item, position);
                    eventList.scrollToPosition(position);
                    myDb.insertEvent(item);
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(eventList);
    }

    private void sortEventLatestFirst() {
        model.getEvents().sort((o1, o2) -> o2.getStartDate().compareTo(o1.getStartDate()));
        reloadRecyclerView();
    }

    private void reloadRecyclerView() {
        adapter = new EventListAdapter(model.getEvents(), this);
        eventList.setAdapter(adapter);
        eventList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void runtimePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                runtimePermissions();
            }
        }
    }
}

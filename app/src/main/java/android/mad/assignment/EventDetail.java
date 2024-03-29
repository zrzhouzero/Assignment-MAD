package android.mad.assignment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.mad.assignment.task.DatabaseTask;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import model.EventImpl;
import model.MyLocation;
import model.MovieImpl;
import model.exceptions.DateTimeException;
import model.exceptions.EmptySlotException;
import model.exceptions.LatitudeOutOfBoundException;
import model.exceptions.LongitudeOutOfBoundException;

public class EventDetail extends AppCompatActivity implements MovieSelectionFragment.MoviePass {

    private static final String TAG = "EventDetail";
    private static final int CODE_PICK_CONTACTS = 101;

    private TextView id;
    private EditText title;
    private TextView startDate;
    private TextView startTime;
    private TextView endDate;
    private TextView endTime;
    private EditText venue;
    private EditText longitude;
    private EditText latitude;
    private TextView selectedMovie;
    private TextView selectedAttendees;

    private String[] errorMessages;

    private EventImpl currentEvent;
    private MovieImpl currentSelectedMovie;
    private ArrayList<String> currentSelectedAttendees;
    private ArrayList<EventImpl> allEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Intent in = getIntent();
        currentEvent = (EventImpl) in.getSerializableExtra("EVENT_INFO");
        allEvents = (ArrayList<EventImpl>) in.getSerializableExtra("ALL_EVENTS");
        initialiseErrorMessages();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        currentSelectedMovie = currentEvent.getMovie();
        currentSelectedAttendees = currentEvent.getAttendees();

        id = findViewById(R.id.event_id);
        id.setText(currentEvent.getId());

        title = findViewById(R.id.event_title);
        title.setText(currentEvent.getTitle());

        startDate = findViewById(R.id.start_date_text_view);
        startDate.setText(dateFormat.format(currentEvent.getStartDate()));
        startDate.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: clicked on start date button.");

            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (view, year, monthOfYear, dayOfMonth) -> startDate.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth), mYear, mMonth, mDay);
            datePickerDialog.show();
        });


        startTime = findViewById(R.id.start_time_text_view);
        startTime.setText(timeFormat.format(currentEvent.getStartDate()));
        startTime.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: clicked on start time button.");

            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), (view, hourOfDay, minute) -> startTime.setText(hourOfDay + ":" + minute), mHour, mMinute, false);
            timePickerDialog.show();
        });

        endDate = findViewById(R.id.end_date_text_view);
        endDate.setText(dateFormat.format(currentEvent.getEndDate()));
        endDate.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: clicked on end date button.");

            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (view, year, monthOfYear, dayOfMonth) -> endDate.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth), mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        endTime = findViewById(R.id.end_time_text_view);
        endTime.setText(timeFormat.format(currentEvent.getEndDate()));
        endTime.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: clicked on end time button.");

            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), (view, hourOfDay, minute) -> endTime.setText(hourOfDay + ":" + minute), mHour, mMinute, false);
            timePickerDialog.show();
        });

        venue = findViewById(R.id.venue);
        venue.setText(currentEvent.getVenue());

        longitude = findViewById(R.id.longitude);
        longitude.setText(String.valueOf(currentEvent.getMyLocation().getLongitude()));

        latitude = findViewById(R.id.latitude);
        latitude.setText(String.valueOf(currentEvent.getMyLocation().getLatitude()));

        selectedMovie = findViewById(R.id.movie);
        if (currentEvent.getMovie() != null) {
            selectedMovie.setText(currentEvent.getMovie().getTitle());
        } else {
            selectedMovie.setText(R.string.no_movie_found);
        }
        selectedMovie.setOnClickListener(v -> {
            FragmentManager manager = getSupportFragmentManager();
            MovieSelectionFragment fragment = new MovieSelectionFragment();
            fragment.show(manager, "select_a_movie");
        });

        selectedAttendees = findViewById(R.id.attendees);
        reloadAttendees();

        setInfoEnable(false);

        Button addAttendees = findViewById(R.id.add_attendee_button_on_detail_page);
        addAttendees.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(intent, CODE_PICK_CONTACTS);
        });

        Button removeAttendees = findViewById(R.id.remove_attendee_button_on_detail_page);
        removeAttendees.setOnClickListener(v -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(EventDetail.this);
            mBuilder.setTitle("Choose an item");
            String[] temp = currentSelectedAttendees.toArray(new String[currentSelectedAttendees.size()]);
            mBuilder.setSingleChoiceItems(temp, -1, (dialogInterface, i) -> {
                currentSelectedAttendees.remove(i);
                reloadAttendees();
                dialogInterface.dismiss();
            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        });

        Button editEvent = findViewById(R.id.edit_event_detail_button);
        editEvent.setOnLongClickListener(v -> {
            setInfoEnable(true);
            Toast.makeText(getApplicationContext(), "Edit Mode On", Toast.LENGTH_LONG).show();
            return true;
        });

        Button saveEventButton = findViewById(R.id.save_event_button);
        saveEventButton.setOnClickListener(v -> {
            // save event
            String srcDateTimeFormat = "yyyy/M/dd hh:mm";
            SimpleDateFormat readFormat = new SimpleDateFormat(srcDateTimeFormat);

            int step = 0;
            try {
                String tempEventTitle = title.getText().toString();
                if (tempEventTitle.trim().equals("")) throw new EmptySlotException();
                step++;

                Date tempStartDateTime = readFormat.parse(startDate.getText().toString() + " " + startTime.getText().toString());
                Date tempEndDateTime = readFormat.parse(endDate.getText().toString() + " " + endTime.getText().toString());
                if (tempStartDateTime.compareTo(tempEndDateTime) > 0) throw new DateTimeException();
                step++;

                String tempVenue = venue.getText().toString();
                if (tempVenue.trim().equals("")) throw new EmptySlotException();
                step++;

                double tempLongitude = Double.parseDouble(longitude.getText().toString());
                if (tempLongitude >= 180 || tempLongitude <= -180)
                    throw new LongitudeOutOfBoundException();
                step++;

                double tempLatitude = Double.parseDouble(latitude.getText().toString());
                if (tempLatitude >= 90 || tempLatitude <= -90)
                    throw new LatitudeOutOfBoundException();
                step++;

                String tempId = id.getText().toString();

                allEvents.removeIf(e -> e.getId().equals(id.getText().toString()));
                EventImpl event = new EventImpl(tempId, tempEventTitle, tempStartDateTime, tempEndDateTime, tempVenue, new MyLocation(tempLatitude, tempLongitude));
                event.setMovie(currentSelectedMovie);
                event.setAttendees(currentSelectedAttendees);
                allEvents.add(event);

                DatabaseTask task = new DatabaseTask(v.getContext(), false, event);
                task.execute();

                Toast.makeText(getApplicationContext(), "Event added!", Toast.LENGTH_LONG).show();
            } catch (ParseException | EmptySlotException | DateTimeException | NumberFormatException | LongitudeOutOfBoundException | LatitudeOutOfBoundException e) {
                Log.e(TAG, "onClick: " + e.getMessage());
                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage(errorMessages[step]);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
                return;
            }

            Log.d(TAG, "onClick: save button");
            setInfoEnable(false);
            Toast.makeText(v.getContext(), "Event Saved!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(v.getContext(), EventList.class);
            startActivity(intent);
        });

    }

    @Override
    public void moviePass(MovieImpl movie) {
        this.currentSelectedMovie = movie;
        this.selectedMovie.setText(movie.getTitle());
    }

    private void setInfoEnable(boolean b) {
        title.setEnabled(b);
        startDate.setEnabled(b);
        startTime.setEnabled(b);
        endDate.setEnabled(b);
        endTime.setEnabled(b);
        venue.setEnabled(b);
        longitude.setEnabled(b);
        latitude.setEnabled(b);
        selectedMovie.setEnabled(b);
        selectedAttendees.setEnabled(b);
    }

    private void reloadAttendees() {
        if (currentEvent.getAttendees().size() > 1) {
            selectedAttendees.setText(currentEvent.getAttendees().get(0) + " and " + (currentEvent.getAttendees().size() - 1) + " more.");
        } else if (currentEvent.getAttendees().size() == 1) {
            selectedAttendees.setText(currentEvent.getAttendees().get(0));
        } else {
            selectedAttendees.setText(R.string.no_attendees_selected);
        }
    }


    private void initialiseErrorMessages() {
        this.errorMessages = new String[7];
        errorMessages[0] = "event title cannot be empty";
        errorMessages[1] = "start time must be earlier than end time";
        errorMessages[2] = "venue cannot be empty";
        errorMessages[3] = "longitude should be in [-180, 180]";
        errorMessages[4] = "latitude should be in [-90, 90]";
        errorMessages[5] = "unknown exception happened";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_PICK_CONTACTS) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    String[] contact = getPhoneContacts(uri);
                    if (contact != null) {
                        String contactName = contact[0];
                        String contactPhoneNumber = contact[1];
                        Log.d(TAG, "onActivityResult: contact selected: " + contactName + " " + contactPhoneNumber);
                        this.currentSelectedAttendees.add(contactName);
                        HashSet<String> set = new HashSet<>(this.currentSelectedAttendees);
                        this.currentSelectedAttendees.clear();
                        this.currentSelectedAttendees.addAll(set);
                        reloadAttendees();
                    }
                }
            }
        }
    }

    private String[] getPhoneContacts(Uri uri) {
        String[] contact = new String[2];

        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0] = cursor.getString(nameFieldColumnIndex);
            contact[1] = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Log.i("contacts", contact[0]);
            Log.i("contactsUsername", contact[1]);
            cursor.close();
        } else {
            return null;
        }
        return contact;
    }

}

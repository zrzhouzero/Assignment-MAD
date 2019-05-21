package android.mad.assignment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
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
import model.exceptions.NoMovieFoundException;

public class AddEvent extends AppCompatActivity implements MovieSelectionFragment.MoviePass {

    private static final int CODE_PICK_CONTACTS = 101;
    private static final int PICK_NUMBER = 1;
    private static final String TAG = "AddEvent";

    private MovieImpl selectedMovie = null;
    private TextView selectedMovieTextView;

    private ArrayList<String> selectedAttendees = new ArrayList<>();
    private TextView selectedAttendeesTextView;

    private String[] errorMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Log.d(TAG, "onCreate: called.");

        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy/M/dd");
        initialiseErrorMessages();

        TextView titleInput = findViewById(R.id.title_input);

        TextView selectedStartDate = findViewById(R.id.selected_start_date);
        selectedStartDate.setText(format.format(new Date()));

        TextView selectedStartTime = findViewById(R.id.selected_start_time);
        selectedStartTime.setText(R.string.default_time);

        TextView selectedEndDate = findViewById(R.id.selected_end_date);
        selectedEndDate.setText(format.format(new Date()));

        TextView selectedEndTime = findViewById(R.id.selected_end_time);
        selectedEndTime.setText(R.string.default_time);

        TextView venueInput = findViewById(R.id.venue_input);

        TextView longitudeInput = findViewById(R.id.longitude_input);

        TextView latitudeInput = findViewById(R.id.latitude_input);

        selectedMovieTextView = findViewById(R.id.selected_movie);
        selectedMovieTextView.setText(R.string.no_movie_selected);

        selectedAttendeesTextView = findViewById(R.id.selected_attendees);
        selectedAttendeesTextView.setText(R.string.no_attendees_selected);

        Button startDateButton = findViewById(R.id.button_select_start_date);
        Button startTimeButton = findViewById(R.id.button_select_start_time);
        Button endDateButton = findViewById(R.id.button_select_end_date);
        Button endTimeButton = findViewById(R.id.button_select_end_time);
        Button movieButton = findViewById(R.id.button_select_movie);
        Button addAttendeeButton = findViewById(R.id.button_select_attendees);
        Button removeAttendeeButton = findViewById(R.id.button_delete_attendees);

        Button addEventButton = findViewById(R.id.button_add_event);

        startDateButton.setOnClickListener(e -> {

            Log.d(TAG, "onCreate: clicked on start date button.");

            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> selectedStartDate.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth), mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        startTimeButton.setOnClickListener(e -> {

            Log.d(TAG, "onCreate: clicked on start time button.");

            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> selectedStartTime.setText(hourOfDay + ":" + minute), mHour, mMinute, false);
            timePickerDialog.show();
        });

        endDateButton.setOnClickListener(e -> {

            Log.d(TAG, "onCreate: clicked on end date button.");

            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> selectedEndDate.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth), mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        endTimeButton.setOnClickListener(e -> {

            Log.d(TAG, "onCreate: clicked on end time button.");

            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> selectedEndTime.setText(hourOfDay + ":" + minute), mHour, mMinute, false);
            timePickerDialog.show();
        });

        movieButton.setOnClickListener(v -> {
            FragmentManager manager = getSupportFragmentManager();
            MovieSelectionFragment fragment = new MovieSelectionFragment();
            fragment.show(manager, "select_a_movie");
        });

        addAttendeeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(intent, CODE_PICK_CONTACTS);
        });

        removeAttendeeButton.setOnClickListener(v -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(AddEvent.this);
            mBuilder.setTitle("Choose an item");
            String[] temp = selectedAttendees.toArray(new String[selectedAttendees.size()]);
            mBuilder.setSingleChoiceItems(temp, -1, (dialogInterface, i) -> {
                selectedAttendees.remove(i);
                reloadAttendees();
                dialogInterface.dismiss();
            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        });

        addEventButton.setOnClickListener(v -> {

            String srcDateTimeFormat = "yyyy/M/dd hh:mm";
            SimpleDateFormat readFormat = new SimpleDateFormat(srcDateTimeFormat);

            int step = 0;
            try {
                String tempEventTitle = titleInput.getText().toString();
                if (tempEventTitle.trim().equals("")) throw new EmptySlotException();
                step++;

                Date tempStartDateTime = readFormat.parse(selectedStartDate.getText().toString() + " " + selectedStartTime.getText().toString());
                Date tempEndDateTime = readFormat.parse(selectedEndDate.getText().toString() + " " + selectedEndTime.getText().toString());
                if (tempStartDateTime.compareTo(tempEndDateTime) > 0) throw new DateTimeException();
                step++;

                String tempVenue = venueInput.getText().toString();
                if (tempVenue.trim().equals("")) throw new EmptySlotException();
                step++;

                double tempLongitude = Double.parseDouble(longitudeInput.getText().toString());
                if (tempLongitude >= 180 || tempLongitude <= -180)
                    throw new LongitudeOutOfBoundException();
                step++;

                double tempLatitude = Double.parseDouble(latitudeInput.getText().toString());
                if (tempLatitude >= 90 || tempLatitude <= -90)
                    throw new LatitudeOutOfBoundException();
                step++;

                if (selectedMovie == null) throw new NoMovieFoundException();
                step++;

                String tempId = (tempEventTitle.charAt(0) + "" + tempVenue.charAt(0)).trim();

                /*Runnable r = () -> {
                    DatabaseHelper helper = new DatabaseHelper(v.getContext());
                    helper.insertEvent(new EventImpl(tempId, tempEventTitle, tempStartDateTime, tempEndDateTime, tempVenue, new MyLocation(tempLatitude, tempLongitude), selectedMovie, selectedAttendees));
                };
                new Thread(r).start();*/

                EventImpl tempEvent = new EventImpl(tempId, tempEventTitle, tempStartDateTime, tempEndDateTime, tempVenue, new MyLocation(tempLatitude, tempLongitude), selectedMovie, selectedAttendees);
                DatabaseTask task = new DatabaseTask(v.getContext(), true, tempEvent);
                task.execute();

                Toast.makeText(getApplicationContext(), "Event added!", Toast.LENGTH_LONG).show();
            } catch (ParseException | EmptySlotException | DateTimeException | NumberFormatException | LongitudeOutOfBoundException | LatitudeOutOfBoundException | NoMovieFoundException e) {
                Log.e(TAG, "onClick: " + e.getMessage());
                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage(errorMessages[step]);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
                return;
            }

            Intent intent = new Intent(v.getContext(), EventList.class);
            startActivity(intent);
        });

    }

    @Override
    public void moviePass(MovieImpl movie) {
        this.selectedMovie = movie;
        this.selectedMovieTextView.setText(movie.getTitle());
    }

    private void initialiseErrorMessages() {
        this.errorMessages = new String[7];
        errorMessages[0] = "event title cannot be empty";
        errorMessages[1] = "start time must be earlier than end time";
        errorMessages[2] = "venue cannot be empty";
        errorMessages[3] = "longitude should be in [-180, 180]";
        errorMessages[4] = "latitude should be in [-90, 90]";
        errorMessages[5] = "please select a movie";
        errorMessages[6] = "unknown exception happened";
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
                        this.selectedAttendees.add(contactName);
                        HashSet<String> set = new HashSet<>(this.selectedAttendees);
                        this.selectedAttendees.clear();
                        this.selectedAttendees.addAll(set);
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

    private void reloadAttendees() {
        if (this.selectedAttendees.size() > 1) {
            this.selectedAttendeesTextView.setText(this.selectedAttendees.get(0) + " and " + (this.selectedAttendees.size() - 1) + " more.");
        } else if (this.selectedAttendees.size() == 1) {
            this.selectedAttendeesTextView.setText(this.selectedAttendees.get(0));
        } else {
            this.selectedAttendeesTextView.setText(R.string.no_attendees_selected);
        }
    }

}

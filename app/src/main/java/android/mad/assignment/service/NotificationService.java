package android.mad.assignment.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.mad.assignment.NotificationHelper;
import android.mad.assignment.task.DurationTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import database.DatabaseHelper;
import model.ApplicationSettingsHandler;
import model.EventImpl;
import model.MyLocation;

public class NotificationService extends Service {

    public static final int NOTIFICATION_ID = 9999;

    private LocationListener locationListener;
    private LocationManager locationManager;

    // in milliseconds
    private static int notificationPeriod = ApplicationSettingsHandler.getNotificationPeriod() * 60000;

    private static ArrayList<EventImpl> events;
    private static HashMap<EventImpl, Integer> drivingTime;
    private static HashMap<String, Date> eventTriggerTime = new HashMap<>();
    private static HashMap<String, Integer> eventOperated = new HashMap<>();

    private static final String TAG = "NotificationService";

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: service started.");
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {

        NotificationHelper helper = new NotificationHelper(getApplicationContext());

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                reloadEvents();
                updateDrivingTime(location);
                updateEventTriggerTime();

                for (EventImpl event : events) {
                    if (eventTriggerTime.containsKey(event.getTitle())) {
                        if (eventTriggerTime.get(event.getTitle()).compareTo(new Date()) < 0) {

                            NotificationCompat.Builder nb = helper.getChannelNotification("Notification",  event.getTitle(), event);
                            helper.getManager().notify(NOTIFICATION_ID, nb.build());
                        }
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, notificationPeriod, 0, locationListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private void reloadEvents() {
        DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
        try {
            events = helper.reloadEventList();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        drivingTime = new HashMap<>();
    }

    private void updateDrivingTime(Location location) {
        for (EventImpl e : events) {
            int duration = getDurationInMinutes(new MyLocation(location.getLatitude(), location.getLongitude()), e.getMyLocation());
            drivingTime.put(e, duration);
        }
        Log.d(TAG, drivingTime.toString());
    }

    private int getDurationInMinutes(MyLocation start, MyLocation end) {
        DurationTask task = new DurationTask();
        JSONObject o;
        String duration;
        try {
            o = task.execute(start, end).get();
            duration = o.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration")
                    .getString("text");
        } catch (ExecutionException | InterruptedException | JSONException | NullPointerException e) {
            return -1;
        }
        String[] temp = duration.split(" ");
        return Integer.parseInt(temp[0]);
    }

    public static void remindLater(EventImpl event) {
        Date d = new Date();
        Calendar cin = Calendar.getInstance();
        cin.setTime(d);
        cin.add(Calendar.MINUTE, ApplicationSettingsHandler.getRemindAgainDuration());
        eventTriggerTime.put(event.getTitle(), cin.getTime());
        eventOperated.put(event.getTitle(), 1);
    }

    public static void dismiss(EventImpl event) {
        drivingTime.remove(event);
        eventTriggerTime.remove(event.getTitle());
        eventOperated.put(event.getTitle(), -1);
    }

    public static void cancel(EventImpl event) {
        drivingTime.remove(event);
        eventTriggerTime.remove(event.getTitle());
        eventOperated.remove(event.getTitle());
    }

    private void updateEventTriggerTime() {
        for (EventImpl e : drivingTime.keySet()) {
            if (drivingTime.get(e) == null) {
                continue;
            }
            if (drivingTime.get(e) < 0) {
                continue;
            }
            Date triggerDate = e.getStartDate();
            Calendar c = Calendar.getInstance();
            c.setTime(triggerDate);
            c.add(Calendar.MINUTE, -drivingTime.get(e));
            c.add(Calendar.MINUTE, -ApplicationSettingsHandler.getThresholdInMinutes());
            if (eventOperated.containsKey(e.getTitle())) {
                if (eventOperated.get(e.getTitle()) > 0) {
                    eventOperated.put(e.getTitle(), 1);
                    continue;
                } else {
                    eventOperated.put(e.getTitle(), -1);
                    continue;
                }
            }
            eventTriggerTime.put(e.getTitle(), c.getTime());
        }
        Log.d(TAG, "updateEventTriggerTime: " + eventTriggerTime);
        Log.d(TAG, "updateEventTriggerTime: event trigger time size: " + eventTriggerTime.size());
    }

}

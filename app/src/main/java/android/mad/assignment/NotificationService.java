package android.mad.assignment;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import database.DatabaseHelper;
import model.ApplicationSettingsHandler;
import model.EventImpl;
import model.MyLocation;

public class NotificationService extends Service {

    private LocationListener locationListener;
    private LocationManager locationManager;

    // in milliseconds
    private int notificationPeriod = ApplicationSettingsHandler.getNotificationPeriod() * 60000;
    private int threshold = ApplicationSettingsHandler.getThresholdInMinutes() * 60000;
    private int remindAgainDuration = ApplicationSettingsHandler.getRemindAgainDuration() * 60000;

    private ArrayList<EventImpl> events;
    private HashMap<EventImpl, Integer> drivingTime;

    private static final String TAG = "NotificationService";

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: service started.");
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                reloadEvents();
                updateDrivingTime(location);
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
        } catch (ExecutionException | InterruptedException | JSONException e) {
            return -1;
        }
        String[] temp = duration.split(" ");
        return Integer.parseInt(temp[0]);
    }

}

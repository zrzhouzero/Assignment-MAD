package android.mad.assignment.task;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import model.MyLocation;

public class DurationTask extends AsyncTask<MyLocation, Void, JSONObject> {

    @Override
    protected JSONObject doInBackground(MyLocation... myLocations) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        StringBuilder requestUrl = new StringBuilder();
        requestUrl.append("https://maps.googleapis.com/maps/api/distancematrix/json?")
                .append("units=imperial&origins=").append(myLocations[0].getLatitude()).append(",").append(myLocations[0].getLongitude())
                .append("&destinations=").append(myLocations[1].getLatitude()).append(",").append(myLocations[1].getLongitude()).append("&mode=driving")
                .append("&key=").append("AIzaSyBa7orPOLzzbia1gXwaklKIkWd28gV8Ukg");
        try {
            URL url = new URL(requestUrl.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append(System.lineSeparator());
                // Log.d("Response: ", "> " + line);
            }
            return new JSONObject(buffer.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}

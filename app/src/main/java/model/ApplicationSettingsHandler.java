package model;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class ApplicationSettingsHandler {

    private static int thresholdInMinutes;
    private static int remindAgainDuration;
    private static int notificationPeriod;

    private static final String TAG = "ApplicationSettingsHandler";

    private final static File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "mad_ass");
    private final static File setting_file = new File(Environment.getExternalStorageDirectory() + File.separator + "mad_ass/settings.txt");

    public static int getNotificationPeriod() {
        return notificationPeriod;
    }

    public static int getRemindAgainDuration() {
        return remindAgainDuration;
    }

    public static int getThresholdInMinutes() {
        return thresholdInMinutes;
    }

    public static void createSettingFile() {
        if (!directory.exists() && !directory.isDirectory()) {
            if (directory.mkdirs()) {
                Log.d("Create Setting Dir: ", "App dir created");
                updateSettings(15, 5, 10);
            } else {
                Log.w("Create Setting Dir: ", "Unable to create app dir!");
            }
        } else {
            Log.i("Create Setting Dir: ", "App dir already exists");
        }
    }

    public static void updateSettings(int t, int r, int n) {
        StringBuilder settings = new StringBuilder();
        settings.append(t).append(System.lineSeparator())
                .append(r).append(System.lineSeparator())
                .append(n).append(System.lineSeparator());
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(setting_file, false));
            writer.println(settings.toString());
            writer.close();
            thresholdInMinutes = t;
            remindAgainDuration = r;
            notificationPeriod = n;
        } catch (IOException e) {
            Log.e(TAG, "updateSettings: ", e);
        }
    }

    private static void createDefaultSettings() {
        updateSettings(15, 5, 10);
    }

    public static void readSettingFile() {
        if (!directory.exists() && !directory.isDirectory()) {
            createSettingFile();
        } else if (!setting_file.exists()) {
            createDefaultSettings();
        } else {
            try (Scanner sc = new Scanner(setting_file)) {
                String[] temp = new String[3];
                int i = 0;
                while (sc.hasNext()) {
                    temp[i] = sc.nextLine();
                    i++;
                }
                thresholdInMinutes = Integer.parseInt(temp[0]);
                remindAgainDuration = Integer.parseInt(temp[1]);
                notificationPeriod = Integer.parseInt(temp[2]);
            } catch (IOException e) {
                createDefaultSettings();
            }
        }
    }

}

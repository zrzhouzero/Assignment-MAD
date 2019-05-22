package android.mad.assignment;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.mad.assignment.receiver.NotificationActionReceiver;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import model.EventImpl;

public class NotificationHelper extends ContextWrapper {

    public static final String CHANNEL_ID = "channel id";
    public static final String CHANNEL_NAME = "channel name";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(R.color.colorPrimary);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public NotificationCompat.Builder getChannelNotification(String title, String message, EventImpl event) {

        Intent remindLaterIntent = new Intent(getApplicationContext(), NotificationActionReceiver.class);
        remindLaterIntent.putExtra("action", 0);
        remindLaterIntent.putExtra("event", event);
        PendingIntent remindLaterPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, remindLaterIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent dismissIntent = new Intent(getApplicationContext(), NotificationActionReceiver.class);
        dismissIntent.putExtra("action", 1);
        dismissIntent.putExtra("event", event);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent cancelIntent = new Intent(getApplicationContext(), NotificationActionReceiver.class);
        cancelIntent.putExtra("action", 2);
        cancelIntent.putExtra("event", event);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 2, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.notification_icon_new)
                .addAction(R.drawable.notification_icon_new, "Remind", remindLaterPendingIntent)
                .addAction(R.drawable.notification_icon_new, "Dismiss", dismissPendingIntent)
                .addAction(R.drawable.notification_icon_new, "Cancel", cancelPendingIntent);

    }

}

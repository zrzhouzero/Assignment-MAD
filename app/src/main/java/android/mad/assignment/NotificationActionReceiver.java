package android.mad.assignment;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import database.DatabaseHelper;
import model.EventImpl;

public class NotificationActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int i = intent.getIntExtra("action", -1);
        EventImpl event = (EventImpl) intent.getSerializableExtra("event");

        if (i == 0) {
            NotificationService.remindLater(event);
            Toast.makeText(context, "Remind", Toast.LENGTH_SHORT).show();
            System.out.println("REMIND");
        } else if (i == 1) {
            NotificationService.dismiss(event);
            Toast.makeText(context, "Dismiss", Toast.LENGTH_SHORT).show();
            System.out.println("DISMISS");
        } else if (i == 2) {
            NotificationService.cancel(event);
            DatabaseHelper helper = new DatabaseHelper(context);
            helper.deleteEvent(event);
            Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show();
            System.out.println("CANCEL");
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NotificationService.NOTIFICATION_ID);
    }

}

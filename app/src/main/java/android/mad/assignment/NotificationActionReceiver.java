package android.mad.assignment;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int i = intent.getIntExtra("action", -1);
        System.out.println("EXTRA " + i);
        if (i == 0) {
            Toast.makeText(context, "Remind", Toast.LENGTH_SHORT).show();
            System.out.println("REMIND");
        } else if (i == 1) {
            Toast.makeText(context, "Dismiss", Toast.LENGTH_SHORT).show();
            System.out.println("DISMISS");
        } else if (i == 2) {
            Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show();
            System.out.println("CANCEL");
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NotificationService.NOTIFICATION_ID);
    }

}

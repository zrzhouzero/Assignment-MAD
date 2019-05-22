package android.mad.assignment.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.mad.assignment.service.NotificationService;
import android.mad.assignment.R;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkStatusChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            Intent notificationServiceIntent = new Intent(context, NotificationService.class);
            context.startService(notificationServiceIntent);
            Toast.makeText(context, R.string.internet_connected, Toast.LENGTH_SHORT).show();
        } else {
            Intent notificationServiceIntent = new Intent(context, NotificationService.class);
            context.stopService(notificationServiceIntent);
            Toast.makeText(context, R.string.internet_disconnected, Toast.LENGTH_SHORT).show();
        }
    }

}
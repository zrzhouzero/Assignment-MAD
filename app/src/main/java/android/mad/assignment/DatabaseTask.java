package android.mad.assignment;

import android.content.Context;
import android.os.AsyncTask;

import database.DatabaseHelper;
import model.EventImpl;

public class DatabaseTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private boolean insertOrUpdate;
    private EventImpl event;

    public DatabaseTask(Context context, boolean insertOrUpdate, EventImpl event) {
        this.context = context;
        this.insertOrUpdate = insertOrUpdate;
        this.event = event;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        DatabaseHelper helper = new DatabaseHelper(context);
        if (insertOrUpdate) {
            helper.insertEvent(event);
        } else {
            helper.updateEvent(event);
        }
        return null;
    }

}

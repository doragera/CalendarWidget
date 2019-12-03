package hu.bme.aut.calendarwidget;

import android.os.AsyncTask;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;

import java.io.IOException;

public class AsyncLoadEvents extends AsyncTask<Void, Void, Boolean> {
    private final CalendarModel model;
    private final Calendar client;

    public AsyncLoadEvents(CalendarModel model, Calendar client) {
        this.model = model;
        this.client = client;
    }

    static void run(CalendarModel model, Calendar client) {
        new AsyncLoadEvents(model, client).execute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        CalendarList feed = null;
        try {
            feed = client.calendarList().list().setFields(CalendarInfo.FEED_FIELDS).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        model.reset(feed.getItems());
        return true;
    }
}

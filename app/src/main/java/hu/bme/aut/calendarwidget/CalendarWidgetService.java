package hu.bme.aut.calendarwidget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class CalendarWidgetService extends RemoteViewsService {

    public static final String DAY_OF_WEEK = "dayOfWeek";

    private CalendarDownloader calendarDownloader;

    @Override
    public void onCreate() {
        calendarDownloader = new CalendarDownloader(this);
        super.onCreate();
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsDayFactory(this.getApplicationContext(), intent, calendarDownloader);
    }
}

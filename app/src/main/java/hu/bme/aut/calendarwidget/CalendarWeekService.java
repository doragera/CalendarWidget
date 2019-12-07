package hu.bme.aut.calendarwidget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class CalendarWeekService extends RemoteViewsService {
    private CalendarDownloader calendarDownloader;

    @Override
    public void onCreate() {
        calendarDownloader = new CalendarDownloader(this);
        super.onCreate();
        System.out.println("service");
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsWeekFactory(this.getApplicationContext(), intent, calendarDownloader);
    }
}

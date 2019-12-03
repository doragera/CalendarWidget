package hu.bme.aut.calendarwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Collections;

public class StackWidgetService extends RemoteViewsService {

    private CalendarDownloader calendarDownloader;

    @Override
    public void onCreate() {
        calendarDownloader = new CalendarDownloader(this);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent, calendarDownloader);
    }

}


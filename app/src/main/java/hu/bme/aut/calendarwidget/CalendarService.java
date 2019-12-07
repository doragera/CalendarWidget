package hu.bme.aut.calendarwidget;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViewsService;

public class CalendarService extends RemoteViewsService {

    private CalendarDownloader calendarDownloader;

//    IBinder mBinder = new LocalBinder();
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return mBinder;
//    }
//
//    public class LocalBinder extends Binder {
//        public CalendarService getService() {
//            return CalendarService.this;
//        }
//    }

    @Override
    public void onCreate() {
        calendarDownloader = new CalendarDownloader(this);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new hu.bme.aut.calendarwidget.RemoteViewsFactory(this.getApplicationContext(), intent, calendarDownloader);
    }
}


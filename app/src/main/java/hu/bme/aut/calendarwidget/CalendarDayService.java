package hu.bme.aut.calendarwidget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class CalendarDayService extends RemoteViewsService {

    private CalendarDownloader calendarDownloader;

//    IBinder mBinder = new LocalBinder();
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return mBinder;
//    }
//
//    public class LocalBinder extends Binder {
//        public CalendarDayService getService() {
//            return CalendarDayService.this;
//        }
//    }

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


package hu.bme.aut.calendarwidget;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.util.Calendar;


public class CalendarDownloader {

    public static final String PREF_NAME = "AUTHPREF";
    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static final int MY_CAL_REQ = 1;

    private Context context;


    public CalendarModel getDataFromCalendarTable() throws SecurityException {
        Cursor cur = null;
        System.out.println("getdata");
        CalendarModel model = new CalendarModel();
        ContentResolver cr = context.getContentResolver();

        String[] mProjection =
                {
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                        CalendarContract.Calendars.CALENDAR_COLOR,
                        CalendarContract.Calendars.VISIBLE,
                        CalendarContract.Calendars.SYNC_EVENTS,
                        CalendarContract.Calendars._ID
                };

        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = null ;
        String[] selectionArgs = null;


        cur = cr.query(uri, mProjection, selection, selectionArgs, null);

        while (cur.moveToNext()) {
            String displayName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));
            String accountName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME));
            String visible = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.VISIBLE));
            String syncEvents = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.SYNC_EVENTS));
            String color = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR));
            String id = cur.getString(cur.getColumnIndex(CalendarContract.Calendars._ID));


            System.out.println(displayName);
            System.out.println(accountName);
            System.out.println(visible);
            System.out.println(color);
            System.out.println(syncEvents);
            long i = Integer.parseInt(color);
            if (i < 0)
                i += Math.pow(2, 31);
            System.out.println(String.format("0x%08X", i));

            model.add(accountName, new CalendarInfo(id, displayName, Integer.parseInt(visible)!=0, Integer.parseInt(syncEvents)!=0));

        }
        cur.close();
        return model;
    }

    public void getAllEvents(CalendarModel model) throws SecurityException {
        Cursor cur = null;
        System.out.println("getdata");
        ContentResolver cr = context.getContentResolver();

        String[] mProjection =
                {
                        CalendarContract.Instances._ID,
                        CalendarContract.Instances.TITLE,
                        CalendarContract.Instances.BEGIN,
                        CalendarContract.Instances.END,
                        CalendarContract.Instances.ALL_DAY,
                        CalendarContract.Instances.CALENDAR_ID,
                        CalendarContract.Instances.DISPLAY_COLOR,

                };


        Calendar startTime = Calendar.getInstance();

        startTime.set(Calendar.HOUR_OF_DAY,0);
        startTime.set(Calendar.MINUTE,0);
        startTime.set(Calendar.SECOND, 0);

        Calendar endTime= Calendar.getInstance();
        endTime.add(Calendar.DATE, 1);

        String selection = "(( " + CalendarContract.Instances.DTSTART + " >= ?" +
                        " ) AND ( " + CalendarContract.Instances.DTSTART + " <= ?" +
                        " ) AND ( deleted != 1 ))"; ;
        String[] selectionArgs = new String[] { Long.toString(startTime.getTimeInMillis()), Long.toString(endTime.getTimeInMillis()) };

        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();
        ContentUris.appendId(eventsUriBuilder, startTime.getTimeInMillis());
        ContentUris.appendId(eventsUriBuilder, endTime.getTimeInMillis());

        Uri uri = eventsUriBuilder.build();
        cur = cr.query(uri, mProjection, selection, selectionArgs, null);

        while (cur.moveToNext()) {
            String eventId = cur.getString(cur.getColumnIndex(CalendarContract.Instances._ID));
            String title = cur.getString(cur.getColumnIndex(CalendarContract.Instances.TITLE));
            String begin = cur.getString(cur.getColumnIndex(CalendarContract.Instances.BEGIN));
            String end = cur.getString(cur.getColumnIndex(CalendarContract.Instances.END));
            String allDay = cur.getString(cur.getColumnIndex(CalendarContract.Instances.ALL_DAY));
            String color = cur.getString(cur.getColumnIndex(CalendarContract.Instances.DISPLAY_COLOR));


            Log.d("getAllEvents", "eventID: " + eventId);
            Log.d("getAllEvents", "title: " + title);
            Log.d("getAllEvents", "begin: " + begin);
            Log.d("getAllEvents", "end: " + end);
            Log.d("getAllEvents", "allDay: " + allDay);
            long i = Integer.parseInt(color);
//            if (i < 0)
//                i += Math.pow(2, 31);
//            System.out.println(String.format("0x%08X", i));


        }
        cur.close();

    }


    public CalendarDownloader(Context context) {
        this.context = context;
    }

    public void onCreate() {

    }

    public void onUpdate() {

    }

}

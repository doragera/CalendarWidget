package hu.bme.aut.calendarwidget;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class CalendarDownloader {

    public static final String PREF_NAME = "AUTHPREF";
    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static final int MY_CAL_REQ = 1;

    private Context context;
//    private CalendarModel model = new CalendarModel();;


    public CalendarModel getDataFromCalendarTable() throws SecurityException {
        Cursor cur = null;
        System.out.println("getdata");
        ContentResolver cr = context.getContentResolver();
        CalendarModel model = new CalendarModel();

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

    public List<EventInfo> getAllEvents(CalendarModel model) throws SecurityException {
        Cursor cur = null;
        System.out.println("getdata");
        ContentResolver cr = context.getContentResolver();

        ArrayList<EventInfo> events = new ArrayList<EventInfo>();

        List<String> visibleCalendarIDs = model.getVisibleCalendarIDs();

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
        startTime.add(Calendar.DATE, 3);
        /// TODO vedd ki azt, hogy nem aznapi
        Calendar endTime= Calendar.getInstance();
        endTime.add(Calendar.DATE, 3);

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
            String calendarID = cur.getString(cur.getColumnIndex(CalendarContract.Instances.CALENDAR_ID));
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

            if (visibleCalendarIDs.contains(calendarID) && Integer.parseInt(allDay)==0) {
                events.add(new EventInfo(Integer.parseInt(eventId), title, Long.parseLong(begin), Long.parseLong(end), Integer.parseInt(color)));
            }

        }
        cur.close();
        Collections.sort(events);


        ArrayList<EventInfo> disp = new ArrayList<EventInfo>();
        for (int i = 0; i < events.size(); ++i) {
            if (i != 0) {
                if (events.get(i-1).end > events.get(i).begin) {
                    disp.get(disp.size()-1).title = disp.get(disp.size()-1).title.concat("\n" + events.get(i).title);
                    disp.get(disp.size()-1).end = events.get(i).end;
                }
                else {
                    disp.add(events.get(i));
                }

            }
            else {
                disp.add(events.get(i));
            }
        }
        for (EventInfo ev : disp)
            Log.d("makebefore", ev.title);

        events.clear();
        for (int i = 0; i < disp.size(); ++i) {
            events.add(disp.get(i));
            if (i < disp.size()-1) {
//                float padding = (float)(disp.get(i+1).begin-events.get(i).end)/(1000*60*60);
                events.add(new EventInfo(0, "padding", disp.get(i).end, disp.get(i+1).begin, 0));
            }
        }

        for (EventInfo ev : events)
            Log.d("make", ev.title);


        return events;

    }


    public CalendarDownloader(Context context) {
        this.context = context;
    }

    public void onCreate() {

    }

    public void onUpdate() {

    }

}

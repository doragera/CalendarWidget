package hu.bme.aut.calendarwidget;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CalendarDownloader {

    private Context context;

    public CalendarModel getDataFromCalendarTable() throws SecurityException {
        Cursor cur = null;

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

            model.add(accountName, new CalendarInfo(id, displayName, Integer.parseInt(visible)!=0, Integer.parseInt(syncEvents)!=0));

        }
        cur.close();
        return model;
    }

    public long getEarliestTimeinWeek(long weekFirst, long weekLast) throws SecurityException {
        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();

        String[] mProjection =
                {
                        CalendarContract.Instances.BEGIN,
                        CalendarContract.Instances.ALL_DAY,
                };

        String selection = "(( " + CalendarContract.Instances.DTSTART + " >= ?" +
                " ) AND ( " + CalendarContract.Instances.DTSTART + " <= ?" +
                " ) AND ( deleted != 1 ))"; ;
        String[] selectionArgs = new String[] { Long.toString(weekFirst), Long.toString(weekLast) };

        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();
        ContentUris.appendId(eventsUriBuilder, weekFirst);
        ContentUris.appendId(eventsUriBuilder, weekLast);

        Uri uri = eventsUriBuilder.build();
        cur = cr.query(uri, mProjection, selection, selectionArgs, null);

        long min = Long.MAX_VALUE;

        while (cur.moveToNext()) {
            String begin = cur.getString(cur.getColumnIndex(CalendarContract.Instances.BEGIN));
            String allDay = cur.getString(cur.getColumnIndex(CalendarContract.Instances.ALL_DAY));

            if (Long.parseLong(begin) < min && Integer.parseInt(allDay)==0)
                min = Long.parseLong(begin);

        }
        cur.close();
        return min;
    }

    public List<EventInfo> getAllEvents(CalendarModel model, long fromDay, long toDay, long earliestEventBegin) throws SecurityException {
        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();

        ArrayList<EventInfo> events = new ArrayList<EventInfo>();

        String[] mProjection =
                {
                        CalendarContract.Instances.EVENT_ID,
                        CalendarContract.Instances.TITLE,
                        CalendarContract.Instances.BEGIN,
                        CalendarContract.Instances.END,
                        CalendarContract.Instances.ALL_DAY,
                        CalendarContract.Instances.CALENDAR_ID,
                        CalendarContract.Instances.DISPLAY_COLOR,

                };

        String selection = "(( " + CalendarContract.Instances.DTSTART + " >= ?" +
                        " ) AND ( " + CalendarContract.Instances.DTSTART + " <= ?" +
                        " ) AND ( deleted != 1 ))"; ;
        String[] selectionArgs = new String[] { Long.toString(fromDay), Long.toString(toDay) };

        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();
        ContentUris.appendId(eventsUriBuilder, fromDay);
        ContentUris.appendId(eventsUriBuilder, toDay);

        Uri uri = eventsUriBuilder.build();
        cur = cr.query(uri, mProjection, selection, selectionArgs, null);

        while (cur.moveToNext()) {
            String eventId = cur.getString(cur.getColumnIndex(CalendarContract.Instances.EVENT_ID));
            String calendarID = cur.getString(cur.getColumnIndex(CalendarContract.Instances.CALENDAR_ID));
            String title = cur.getString(cur.getColumnIndex(CalendarContract.Instances.TITLE));
            String begin = cur.getString(cur.getColumnIndex(CalendarContract.Instances.BEGIN));
            String end = cur.getString(cur.getColumnIndex(CalendarContract.Instances.END));
            String allDay = cur.getString(cur.getColumnIndex(CalendarContract.Instances.ALL_DAY));
            String color = cur.getString(cur.getColumnIndex(CalendarContract.Instances.DISPLAY_COLOR));

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean visible = prefs.getBoolean(calendarID, true);

            if (visible && Integer.parseInt(allDay)==0) {
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


        events.clear();
        for (int i = 0; i < disp.size(); ++i) {
            if (i == 0) {
                Date today = new Date(disp.get(i).begin);
                Date earliestToday = new Date(earliestEventBegin);
                earliestToday.setDate(today.getDate());
                if (earliestToday.before(today)) {
                    events.add(new EventInfo(0, "padding", earliestToday.getTime(), disp.get(i).begin, 0));
                }
            }
            events.add(disp.get(i));
            if (i < disp.size()-1) {
                events.add(new EventInfo(0, "padding", disp.get(i).end, disp.get(i+1).begin, 0));
            }
        }

        return events;

    }


    public CalendarDownloader(Context context) {
        this.context = context;
    }

}

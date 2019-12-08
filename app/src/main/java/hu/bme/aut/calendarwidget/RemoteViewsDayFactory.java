package hu.bme.aut.calendarwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.preference.PreferenceManager;

import java.util.Calendar;
import java.util.List;

import static hu.bme.aut.calendarwidget.CalendarWidgetService.DAY_OF_WEEK;

public class RemoteViewsDayFactory implements RemoteViewsService.RemoteViewsFactory {
    private static int mCount;

    private CalendarModel model = null;
    private Context mContext;
    private int mAppWidgetId;
    private List<EventInfo> events;

    private int dayOfWeek;

    private long startDate;
    private long endDate;

    private CalendarDownloader downloader;

    private String dateToString(Calendar date) {
        return date.get(Calendar.YEAR) + "-" + date.get(Calendar.MONTH) + "-" +date.get(Calendar.DAY_OF_MONTH) + " " +
                date.get(Calendar.HOUR) + ":" +date.get(Calendar.MINUTE) + ":" +date.get(Calendar.SECOND);
    }

    public RemoteViewsDayFactory(Context context, Intent intent, CalendarDownloader calendarDownloader) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        dayOfWeek = intent.getIntExtra(DAY_OF_WEEK, 0); // 0 == today, 1-7: monday-sunday of this week
        this.downloader = calendarDownloader;
    }
    public void onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.


        downloader.onCreate();
        model = downloader.getDataFromCalendarTable();

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY,0);
        startTime.set(Calendar.MINUTE,0);
        startTime.set(Calendar.SECOND, 0);

        // Log.d("onCreate", startTime.toString());

        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.HOUR_OF_DAY,0);
        endTime.set(Calendar.MINUTE,0);
        endTime.set(Calendar.SECOND, 0);

        long earliestEventBegin = 0;

        if (dayOfWeek == 0) {
            // TODO: remove this
            startTime.add(Calendar.DATE, 3);
            endTime.add(Calendar.DATE, 3);


            endTime.add(Calendar.DATE, 1);
        } else {
            startTime.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            startTime.add(Calendar.DATE, dayOfWeek - 8);

            endTime.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            endTime.add(Calendar.DATE, dayOfWeek - 7);

            Calendar firstDayinWeek = Calendar.getInstance();
            firstDayinWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            firstDayinWeek.add(Calendar.DATE, -8);

            Calendar lastDayinWeek = Calendar.getInstance();
            lastDayinWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            lastDayinWeek.add(Calendar.DATE, -1);

            earliestEventBegin = downloader.getEarliestTimeinWeek(firstDayinWeek.getTimeInMillis(), lastDayinWeek.getTimeInMillis());
        }


        startDate = startTime.getTimeInMillis();
        endDate = endTime.getTimeInMillis();
        events = downloader.getAllEvents(model, startDate, endDate, earliestEventBegin);
    }
    public void onDestroy() {
        events.clear();
    }
    public int getCount() {
        return events.size();
    }

    private RemoteViews createPadding(float paddingSize, int scale) {
        int layout = R.layout.widget_empty_item;
        int item = R.id.widget_empty_item;

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), layout);

        rv.setTextViewTextSize(item, TypedValue.COMPLEX_UNIT_DIP, paddingSize*scale);
        return rv;
    }

    private RemoteViews createItem(String text, float size, int position, int scale, int textSize, int color, int eventID) {
        int item = R.id.widget_item_text;
        int spacingItem = R.id.widget_item_spacing;

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        rv.setTextViewText(item, text);
        rv.setTextViewTextSize(item, TypedValue.COMPLEX_UNIT_DIP, textSize);
        rv.setTextViewTextSize(spacingItem, TypedValue.COMPLEX_UNIT_DIP, scale * size);
        rv.setInt(item, "setBackgroundColor", color);

        // Next, we set a fill-intent which will be used to fill-in the pending intent template
        // which is set on the collection view in CalendarWidgetDayProvider.
        Bundle extras = new Bundle();
        extras.putInt(CalendarWidgetProvider.EXTRA_ITEM, eventID);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
        return rv;
    }

    public RemoteViews getViewAt(int position) {

        downloader.onUpdate();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        int scale = pref.getInt("event_scale", 100);
        int textSize = pref.getInt("text_scale", 20);

        RemoteViews rv = null;

        EventInfo event = events.get(position);
//        EventInfo nextEvent = null;
//        if (position/2+1 < events.size())
//            nextEvent = events.get(position/2+1);
        float hour = (float)(event.end-event.begin)/(1000*60*60);

//        Log.d("RemoteViews", event.title+"  "+hour);

//        Calendar day =  Calendar.getInstance();
//        day.setTimeInMillis(event.begin);
//        Date dday = day.getTime();

        // int days = (int)((dday.getTime() - monday.getTime())/ 1000 / 60 / 60 / 24);
        // System.out.println(days);

        if (event.title.equals("padding")) {
            rv = createPadding(hour, scale);
        }
        else
            rv = createItem(event.title, hour, position, scale, textSize, event.color, event.eventID);

        return rv;
    }
    public RemoteViews getLoadingView() {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null;
    }
    public int getViewTypeCount() {
        return 2;
    }
    public long getItemId(int position) {
        return position;
    }
    public boolean hasStableIds() {
        return true;
    }
    public void onDataSetChanged() {
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.

        Log.d("onDatasetChanged", "onDatasetChanged");


//        events = downloader.getAllEvents(model);

    }

}

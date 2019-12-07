package hu.bme.aut.calendarwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


class RemoteViewsDayFactory implements RemoteViewsService.RemoteViewsFactory {
    private static int mCount;

    private CalendarModel model = null;
//    private List<WidgetItem> mWidgetItems = new ArrayList<WidgetItem>();
    private Context mContext;
    private int mAppWidgetId;
    private List<EventInfo> events;
    int displayedithPrev = -1;

    private long startDate;
    private long endDate;

    private CalendarDownloader downloader;

    public RemoteViewsDayFactory(Context context, Intent intent, CalendarDownloader calendarDownloader) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        this.downloader = calendarDownloader;
    }
    public void onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.

        mCount = 4;
//        for (int i = 0; i < mCount; i++) {
//            mWidgetItems.add(new WidgetItem(i + "!"));
//        }


        downloader.onCreate();
        model = downloader.getDataFromCalendarTable();

        Calendar startTime = Calendar.getInstance();

        startTime.set(Calendar.HOUR_OF_DAY,0);
        startTime.set(Calendar.MINUTE,0);
        startTime.set(Calendar.SECOND, 0);
        startTime.add(Calendar.DATE, 3);

        Calendar endTime= Calendar.getInstance();
        endTime.add(Calendar.DATE, 3);
        startDate = startTime.getTimeInMillis();
        endDate = endTime.getTimeInMillis();


        events = downloader.getAllEvents(model, startDate, endDate);
//        onDataSetChanged();

        // We sleep for 3 seconds here to show how the empty view appears in the interim.
        // The empty view is set in the CalendarDayWidgetProvider and should be a sibling of the
        // collection view.
        /*try {
            // Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
    public void onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        events.clear();
    }
    public int getCount() {
        return events.size();
    }

    private RemoteViews createPadding(float paddingSize, int scale) {
        int layout = R.layout.widget_empty_item;
        int item = R.id.widget_empty_item;

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), layout);
        // rv.setTextViewText(item, "padding");
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
        // which is set on the collection view in CalendarDayWidgetProvider.
        Bundle extras = new Bundle();
        extras.putInt(CalendarDayWidgetProvider.EXTRA_ITEM, eventID);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
        return rv;
    }

    public RemoteViews getViewAt(int position) {

        downloader.onUpdate();

//        System.out.println("onUpdate "+downloader.getModel().size());

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        int scale = pref.getInt("event_scale", 100);
        int textSize = pref.getInt("text_scale", 20);

        RemoteViews rv = null;


        EventInfo event = events.get(position);
//        EventInfo nextEvent = null;
//        if (position/2+1 < events.size())
//            nextEvent = events.get(position/2+1);
        float hour = (float)(event.end-event.begin)/(1000*60*60);

        Log.d("RemoteViews", event.title+"  "+hour);


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


        events = downloader.getAllEvents(model, startDate, endDate);

        // call the downloader with the date range, returns List<EventInfo>
    }

    private String createTextForEvent(String text, int heightInUnits) {
        if (heightInUnits <= 1) {
            return text;
        }

        String endingSpace = "";
        if (heightInUnits % 2 == 0) {
            endingSpace += "\n";
            heightInUnits -= 1;
        }

        String paddingLines = "";
        for (int i = 1; i <= heightInUnits / 2; ++i) {
            paddingLines += "\n";
        }

        return paddingLines + text + paddingLines + endingSpace;
    }
}
package hu.bme.aut.calendarwidget;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.widget.RemoteViews;

public abstract class CalendarWidgetProvider extends AppWidgetProvider {
    public static final String VIEW_ACTION = "hu.bme.aut.calendarwidget.VIEW_ACTION";
    public static final String EXTRA_ITEM = "hu.bme.aut.calendarwidget.EXTRA_ITEM";
    private static final String SETTINGS_CLICK = "hu.bme.aut.calendarwidget.SETTINGS_CLICK";
    private static final String REFRESH_CLICK = "hu.bme.aut.calendarwidget.REFRESH_CLICK";
    private static final String ADD_EVENT_CLICK = "hu.bme.aut.calendarwidget.ADD_EVENT_CLICK";

    protected abstract int dayFrom();
    protected abstract int dayTo();
    protected abstract int layoutId();

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(VIEW_ACTION)) {
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);

            Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, viewIndex);
            Intent viewIntent = new Intent(Intent.ACTION_VIEW).setData(uri);
            viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(viewIntent);
        }
        if (intent.getAction().equals(SETTINGS_CLICK)) {
            Intent startintent = new Intent(context, SettingsActivity.class);
            startintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startintent);
        }
        if (intent.getAction().equals(REFRESH_CLICK)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            for (int day = dayFrom(); day <= dayTo(); ++day) {
                int resId = context.getResources().getIdentifier("itemsview" + day, "id", context.getPackageName());
                AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, resId);
            }
        }
        if (intent.getAction().equals(ADD_EVENT_CLICK)) {
            Intent addIntent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(addIntent);
        }
        super.onReceive(context, intent);
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; ++i) {

            RemoteViews rv = new RemoteViews(context.getPackageName(), layoutId());
            rv.setOnClickPendingIntent(R.id.settings, PendingIntent.getBroadcast(context, 0, new Intent(context, getClass()).setAction(SETTINGS_CLICK), 0));
            rv.setOnClickPendingIntent(R.id.refresh, PendingIntent.getBroadcast(context, 0, new Intent(context, getClass()).setAction(REFRESH_CLICK).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]), 0));
            rv.setOnClickPendingIntent(R.id.addEvent, PendingIntent.getBroadcast(context, 0, new Intent(context, getClass()).setAction(ADD_EVENT_CLICK), 0));

            for (int day = dayFrom(); day <= dayTo(); ++day) {
                int resId = context.getResources().getIdentifier("itemsview" + day, "id", context.getPackageName());

                Intent intent = new Intent(context, CalendarWidgetService.class);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
                intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
                intent.putExtra(CalendarWidgetService.DAY_OF_WEEK, day);
                intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

                rv.setRemoteAdapter(resId, intent);

                Intent viewIntent = new Intent(context, getClass());
                viewIntent.setAction(CalendarWidgetProvider.VIEW_ACTION);
                viewIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
                PendingIntent viewPendingIntent = PendingIntent.getBroadcast(context, 0, viewIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                rv.setPendingIntentTemplate(resId, viewPendingIntent);
            }
            appWidgetManager.updateAppWidget(appWidgetIds, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
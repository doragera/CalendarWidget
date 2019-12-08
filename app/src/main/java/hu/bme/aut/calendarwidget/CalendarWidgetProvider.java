/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
//            Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();
        }
        if (intent.getAction().equals(SETTINGS_CLICK)) {
            Intent startintent = new Intent(context, SettingsActivity.class);
            startintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startintent);
        }
        if (intent.getAction().equals(REFRESH_CLICK)) {
            System.out.println("refreshweek");
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, layoutId());
        }
        super.onReceive(context, intent);
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {
            // Here we setup the intent which points to the StackViewService which will
            // provide the views for this collection.

            RemoteViews rv = new RemoteViews(context.getPackageName(), layoutId());
            rv.setOnClickPendingIntent(R.id.settings, PendingIntent.getBroadcast(context, 0, new Intent(context, getClass()).setAction(SETTINGS_CLICK), 0));
            rv.setOnClickPendingIntent(R.id.refresh, PendingIntent.getBroadcast(context, 0, new Intent(context, getClass()).setAction(REFRESH_CLICK).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]), 0));

            for (int day = dayFrom(); day <= dayTo(); ++day) {
                int resId = context.getResources().getIdentifier("itemsview" + day, "id", context.getPackageName());

                Intent intent = new Intent(context, CalendarWidgetService.class);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
                intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
                intent.putExtra(CalendarWidgetService.DAY_OF_WEEK, day);
                // When intents are compared, the extras are ignored, so we need to embed the extras
                // into the data so that the extras will not be ignored.
                intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

                rv.setRemoteAdapter(resId, intent);
                // The empty view is displayed when the collection has no items. It should be a sibling
                // of the collection view.
//                rv.setEmptyView(resId, R.id.empty_view_week);
                // Here we setup the a pending intent template. Individuals items of a collection
                // cannot setup their own pending intents, instead, the collection as a whole can
                // setup a pending intent template, and the individual items can set a fillInIntent
                // to create unique before on an item to item basis.
                Intent viewIntent = new Intent(context, getClass());
                viewIntent.setAction(CalendarWidgetProvider.VIEW_ACTION);
                viewIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
                // viewIntent.setData(Uri.parse(viewIntent.toUri(Intent.URI_INTENT_SCHEME)));
                PendingIntent viewPendingIntent = PendingIntent.getBroadcast(context, 0, viewIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                rv.setPendingIntentTemplate(resId, viewPendingIntent);
            }
            appWidgetManager.updateAppWidget(appWidgetIds, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
package hu.bme.aut.calendarwidget;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Collections;

public class CalendarDownloader {

    public static final String PREF_NAME = "AUTHPREF";
    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static final int MY_CAL_REQ = 1;

    private GoogleAccountCredential credential;
    private String accountName;
    private CalendarModel model = new CalendarModel();
    private Calendar client;
    private Context context;

    private final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    private final JacksonFactory jsonFactory = new JacksonFactory();

    public CalendarModel getModel() {
        return model;
    }

    private boolean chooseAcc() {
        if (accountName == null) {
            SharedPreferences settings = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            this.accountName = settings.getString(PREF_ACCOUNT_NAME, null);
            if (this.accountName != null)
                credential.setSelectedAccountName(this.accountName);
        }
        return this.accountName != null;
    }

    private boolean hasAcc() {
        return this.accountName != null;
    }

    public CalendarDownloader(Context context) {
        this.context = context;
        credential = GoogleAccountCredential.usingOAuth2(context, Collections.singleton(CalendarScopes.CALENDAR));
        chooseAcc();
    }

    public void onCreate() {
        if (chooseAcc())
            client = new Calendar.Builder(transport, jsonFactory, credential).setApplicationName("CalendarWidget").build();

        getDataFromCalendarTable();
    }

    public void onUpdate() {
        if (hasAcc())
            AsyncLoadEvents.run(this.model, this.client);
    }

    public void getDataFromCalendarTable() {
        Cursor cur = null;
        System.out.println("getdata");
        ContentResolver cr = context.getContentResolver();

        String[] mProjection =
                {
                        CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                        CalendarContract.Calendars.CALENDAR_LOCATION,
                        CalendarContract.Calendars.CALENDAR_TIME_ZONE
                };

        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = null ;
        String[] selectionArgs = null;
//        selectionArgs[0]="";

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, MY_CAL_REQ);
        }
        cur = cr.query(uri, mProjection, selection, selectionArgs, null);

        while (cur.moveToNext()) {
            String displayName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));
            String accountName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME));

            System.out.println("display: " + displayName);

        }

    }

}

package hu.bme.aut.calendarwidget;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.common.primitives.UnsignedInteger;

public class SettingsActivity extends AppCompatActivity {

    private static final int MY_CAL_REQ = 1;

    CalendarModel model = new CalendarModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getDataFromCalendarTable();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
//            case REQUEST_ACCOUNT_PICKER:
//                if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
//                    String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
//                    if (accountName != null) {
//                        credential.setSelectedAccountName(accountName);
//                        SharedPreferences settings = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = settings.edit();
//                        editor.putString(PREF_ACCOUNT_NAME, accountName);
//                        editor.apply();
//                        // TODO: notify downloader
//                    }
//                }
//                break;
        }
    }

//    private void chooseAcc() {
//        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
//    }

    public void getDataFromCalendarTable() {
        Cursor cur = null;
        System.out.println("getdata");
        ContentResolver cr = getContentResolver();

        String[] mProjection =
                {
                        CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                        CalendarContract.Calendars.CALENDAR_LOCATION,
                        CalendarContract.Calendars.CALENDAR_TIME_ZONE,
                        CalendarContract.Calendars.CALENDAR_COLOR,
                        CalendarContract.Calendars.VISIBLE
                };

        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = null ;
        String[] selectionArgs = null;
//        selectionArgs[0]="";

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, MY_CAL_REQ);
        }
        cur = cr.query(uri, mProjection, selection, selectionArgs, null);

        while (cur.moveToNext()) {
            String displayName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));
            String accountName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME));
            String visible = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.VISIBLE));
            String color = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR));


            System.out.println(displayName);
            System.out.println(accountName);
            System.out.println(visible);
            System.out.println(color);
            long i = Integer.parseInt(color);
            if (i < 0)
                i += Math.pow(2, 31);
            System.out.println(String.format("0x%08X", i));

//            model.add(new CalendarInfo(accountName, displayName, visible, syncEvents));

        }
        cur.close();

    }
}

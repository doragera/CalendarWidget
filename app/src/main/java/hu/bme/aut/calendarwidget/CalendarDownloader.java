package hu.bme.aut.calendarwidget;

import android.content.Context;
import android.content.SharedPreferences;

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
        System.out.println("onCreate");
        if (chooseAcc())
            client = new Calendar.Builder(transport, jsonFactory, credential).setApplicationName("CalendarWidget").build();
    }

    public void onUpdate() {
        if (hasAcc())
            AsyncLoadEvents.run(this.model, this.client);
    }

}

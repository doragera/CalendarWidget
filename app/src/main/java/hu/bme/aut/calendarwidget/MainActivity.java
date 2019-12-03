package hu.bme.aut.calendarwidget;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.Calendar;

import java.util.Collections;

import static hu.bme.aut.calendarwidget.CalendarDownloader.PREF_ACCOUNT_NAME;
import static hu.bme.aut.calendarwidget.CalendarDownloader.PREF_NAME;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ACCOUNT_PICKER = 0;

    GoogleAccountCredential credential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(CalendarScopes.CALENDAR));
        SharedPreferences settings = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String accountName = settings.getString(PREF_ACCOUNT_NAME, null);
        if (accountName == null)
            chooseAcc();

        credential.setSelectedAccountName(accountName);

        System.out.println("acc "+accountName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        // TODO: notify downloader
                    }
                }
                break;
        }
    }

    private void chooseAcc() {
        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }
}

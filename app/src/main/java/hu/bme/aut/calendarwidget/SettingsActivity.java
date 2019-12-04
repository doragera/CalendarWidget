package hu.bme.aut.calendarwidget;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private static final String TITLE_TAG = "settingsActivityTitle";

//    static boolean mBounded;
//    static CalendarService mServer;
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        Intent mIntent = new Intent(this, CalendarService.class);
//        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
//
//    };
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        System.out.println(mBounded);
//    }
//
//    ServiceConnection mConnection = new ServiceConnection() {
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
////            Toast.makeText(SettingsActivity.this, "Service is disconnected", 1000).show();
//            mBounded = false;
//            mServer = null;
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
////            Toast.makeText(SettingsActivity.this, "Service is connected", 1000).show();
//            mBounded = true;
//            CalendarService.LocalBinder mLocalBinder = (CalendarService.LocalBinder)service;
//            mServer = mLocalBinder.getService();
//
//        }
//    };
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(mBounded) {
//            unbindService(mConnection);
//            mBounded = false;
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new HeaderFragment())
                    .commit();
        } else {
            setTitle(savedInstanceState.getCharSequence(TITLE_TAG));
        }
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                            setTitle(R.string.title_activity_settings);
                        }
                    }
                });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, getTitle());
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().popBackStackImmediate()) {
            return true;
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit();
        setTitle(pref.getTitle());
        return true;
    }

    public static class HeaderFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.header_preferences, rootKey);
        }
    }

    public static class SyncFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.display_preferences, rootKey);
            PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getContext());

            CalendarDownloader downloader = new CalendarDownloader(getContext());
            CalendarModel model = downloader.getDataFromCalendarTable();

            Set<String> accounts = model.getAccounts();
            for (String acc : accounts) {
                PreferenceCategory category = new PreferenceCategory(getContext());
                category.setTitle(acc);
                screen.addPreference(category);

                List<CalendarInfo> calendars = model.getCalendars(acc);

                for (CalendarInfo info : calendars) {
                    CheckBoxPreference checkBoxPref = new CheckBoxPreference(getContext());
                    checkBoxPref.setTitle(info.displayName);
                    checkBoxPref.setChecked(info.visible);
                    checkBoxPref.setKey(info.id);
                    checkBoxPref.setEnabled(info.syncEvents);

                    category.addPreference(checkBoxPref);

                }
                setPreferenceScreen(screen);

            }


        }
    }
}

package hu.bme.aut.calendarwidget;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import java.util.List;
import java.util.Set;

import static androidx.appcompat.app.AlertDialog.*;

public class SettingsActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private static final String TITLE_TAG = "settingsActivityTitle";
    private static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 100;

//    static boolean mBounded;
//    static CalendarDayService mServer;
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        Intent mIntent = new Intent(this, CalendarDayService.class);
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
//            CalendarDayService.LocalBinder mLocalBinder = (CalendarDayService.LocalBinder)service;
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALENDAR)) {
//                showRationaleDialog();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        MY_PERMISSIONS_REQUEST_READ_CALENDAR);
            }
        } else {
            // Permission has already been granted
        }
    }

//    private void showRationaleDialog() {
//        String title = getResources().getString(R.string.rationale_dialog_title);
//        String explanation = getResources().getString(R.string.calendars_permission_explanation);
//         AlertDialog alertDialog = new AlertDialog.Builder(this)
//                .setTitle(title)
//                .setMessage(explanation)
//                 .setPositiveButton("OK", null)
//                 .setNegativeButton("Cancel", null)
//                 .create()
//                 .show();
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_READ_CALENDAR: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                } else {
//                }
//                return;
//            }
//        }
//    }


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

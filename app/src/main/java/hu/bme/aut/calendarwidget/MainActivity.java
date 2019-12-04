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

public class MainActivity extends AppCompatActivity {

    private static final int MY_CAL_REQ = 1;

    CalendarModel model = new CalendarModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, MY_CAL_REQ);
//        }
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


}

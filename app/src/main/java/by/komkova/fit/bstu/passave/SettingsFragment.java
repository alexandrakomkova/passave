package by.komkova.fit.bstu.passave;

import static by.komkova.fit.bstu.passave.DatabaseHelper.SETTINGS_COLUMN_FINGERPRINT;
import static by.komkova.fit.bstu.passave.DatabaseHelper.SETTINGS_COLUMN_ID;
import static by.komkova.fit.bstu.passave.DatabaseHelper.SETTINGS_COLUMN_NOTIFICATIONS;
import static by.komkova.fit.bstu.passave.DatabaseHelper.SETTINGS_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.DatabaseHelper.SETTINGS_TABLE;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener{

    ImageButton security_ibtn, language_ibtn;
    private SwitchCompat switchCompatNotifications, switchCompatNightMode;
    private SharedPreferences sharedPreferences = null;

    private final String log_tag = SettingsSecurityFragment.class.getName();
    private Context applicationContext;

    private SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        applicationContext = getActivity();
        DatabaseHelper databaseHelper = new DatabaseHelper(applicationContext);
        db = databaseHelper.getReadableDatabase();

        security_ibtn = view.findViewById(R.id.security_btn);
        security_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout, new SettingsSecurityFragment());
                fragmentTransaction.commit();
            }
        });

        language_ibtn = view.findViewById(R.id.language_btn);
        language_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout, new SettingsLanguagesFragment());
                fragmentTransaction.commit();
            }
        });

        switchCompatNightMode = view.findViewById(R.id.switch_compat_night_mode);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean nightModeValue = sharedPreferences.getBoolean("night_mode", true);
        if (nightModeValue) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switchCompatNightMode.setChecked(true);
        }

        switchCompatNightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    switchCompatNightMode.setChecked(true);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode", true);
                    editor.commit();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    switchCompatNightMode.setChecked(false);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode", false);
                    editor.commit();
                }
            }
        });


        switchCompatNotifications = view.findViewById(R.id.switch_compat_notifications);
        if (switchCompatNotifications != null) {
            setNotificationsValue();
            switchCompatNotifications.setOnCheckedChangeListener(this);
        }



        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        boolean turnOn = false;

        if(isChecked) {
            turnOn = true;
        }

        turnOnNotifications(turnOn);
    }

    private void turnOnNotifications(Boolean turnOn) {
        try {
            ContentValues cv = new ContentValues();

            if (turnOn) {
                cv.put(SETTINGS_COLUMN_NOTIFICATIONS, 1);
            } else {
                cv.put(SETTINGS_COLUMN_NOTIFICATIONS, 0);
            }

            cv.put(SETTINGS_COLUMN_UPDATED, DateFormatter.currentDate());

            db.update(SETTINGS_TABLE, cv, SETTINGS_COLUMN_ID + " = ?",
                    new String[] { "1" });

            AppLogs.log(applicationContext, log_tag, "Your settings changed");
        } catch (Exception e){
            AppLogs.log(applicationContext, log_tag, "Something went wrong");
            Log.d(log_tag, "error: " + e.getMessage());
        }
    }

    private void setNotificationsValue() {
        String whereclause = SETTINGS_COLUMN_ID + "=?";
        String[] whereargs = new String[]{ "1" };
        String [] columns = new String[] { SETTINGS_COLUMN_NOTIFICATIONS };
        Cursor cursor= null;
        if(db !=null)
        {
            // cursor = db.rawQuery(query, null);
            cursor = db.query(DatabaseHelper.SETTINGS_TABLE, columns, whereclause, whereargs,null,null,null);

        }
        assert cursor != null;
        cursor.moveToFirst();

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (cursor.getInt(0) == 1) {
                    switchCompatNotifications.setChecked(true);
                }

                cursor.moveToNext();
            }

            cursor.close();
        }
    }
}
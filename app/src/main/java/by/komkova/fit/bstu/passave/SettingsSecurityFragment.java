package by.komkova.fit.bstu.passave;

import static by.komkova.fit.bstu.passave.DatabaseHelper.FOLDER_COLUMN_FOLDER_NAME;
import static by.komkova.fit.bstu.passave.DatabaseHelper.FOLDER_COLUMN_TAG_ID;
import static by.komkova.fit.bstu.passave.DatabaseHelper.SETTINGS_COLUMN_FINGERPRINT;
import static by.komkova.fit.bstu.passave.DatabaseHelper.SETTINGS_COLUMN_ID;
import static by.komkova.fit.bstu.passave.DatabaseHelper.SETTINGS_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.DatabaseHelper.SETTINGS_TABLE;
import static by.komkova.fit.bstu.passave.MainActivity.TAG_ID;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SettingsSecurityFragment extends Fragment {

    private String log_tag = SettingsSecurityFragment.class.getName();
    private Context applicationContext;

    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;

    private ImageButton backSettings_ibtn;
    private Button save_settings_btn;
    private RadioButton fingerprint_radio, nothing_radio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_security, container, false);
        applicationContext = MainActivity.getContextOfApplication();
        databaseHelper = new DatabaseHelper(applicationContext);
        db = databaseHelper.getReadableDatabase();

        backSettings_ibtn = view.findViewById(R.id.backSettings_btn);
        backSettings_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout, new SettingsFragment());
                fragmentTransaction.commit();
            }
        });

        fingerprint_radio = view.findViewById(R.id.fingerprint_radio);
        nothing_radio = view.findViewById(R.id.nothing_radio);
        save_settings_btn = view.findViewById(R.id.save_security_btn);
        save_settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFingerprint(fingerprint_radio.isChecked());
            }
        });

        setFingerprintValue();

        return view;
    }

    private void changeFingerprint(Boolean fingerprintValue) {
        try {
            ContentValues cv = new ContentValues();

            if (fingerprintValue) {
                cv.put(SETTINGS_COLUMN_FINGERPRINT, 1);
            } else {
                cv.put(SETTINGS_COLUMN_FINGERPRINT, 0);
            }

            Date currentDate = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            cv.put(SETTINGS_COLUMN_UPDATED, df.format(currentDate));

            int updCount = db.update(SETTINGS_TABLE, cv, SETTINGS_COLUMN_ID + " = ?",
                    new String[] { "1" });

            AppLogs.log(applicationContext, log_tag, "Your settings changed");
        } catch (Exception e){
            AppLogs.log(applicationContext, log_tag, "Something went wrong");
            Log.d(log_tag, "error: " + e.getMessage());
        }
    }

    private void setFingerprintValue() {
        String whereclause = SETTINGS_COLUMN_ID + "=?";
        String[] whereargs = new String[]{ "1" };
        String [] columns = new String[] { SETTINGS_COLUMN_FINGERPRINT };
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
                    fingerprint_radio.setChecked(true);
                }

                cursor.moveToNext();
            }

            cursor.close();
        }
    }
}

package by.komkova.fit.bstu.passave.ui.fragments;

import static by.komkova.fit.bstu.passave.db.DatabaseHelper.SETTINGS_COLUMN_FINGERPRINT;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.SETTINGS_COLUMN_ID;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.SETTINGS_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.SETTINGS_TABLE;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import by.komkova.fit.bstu.passave.helpers.AppLogs;
import by.komkova.fit.bstu.passave.ui.custom_dialog.CustomAlertDialogClass;
import by.komkova.fit.bstu.passave.helpers.DateFormatter;
import by.komkova.fit.bstu.passave.R;
import by.komkova.fit.bstu.passave.db.DatabaseHelper;

public class SettingsSecurityFragment extends Fragment {

    private final String log_tag = SettingsSecurityFragment.class.getName();
    private Context applicationContext;

    private SQLiteDatabase db;
    private SharedPreferences sharedPreferences = null;

    private RadioButton fingerprint_radio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_security, container, false);
        applicationContext = getActivity();
        DatabaseHelper databaseHelper = new DatabaseHelper(applicationContext);
        db = databaseHelper.getReadableDatabase();

        ImageButton backSettings_ibtn = view.findViewById(R.id.backSettings_btn);
        backSettings_ibtn.setOnClickListener(view1 -> {
            FragmentTransaction fragmentTransaction = requireActivity()
                    .getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_layout, new SettingsFragment());
            fragmentTransaction.commit();
        });

        fingerprint_radio = view.findViewById(R.id.fingerprint_radio);
        Button save_settings_btn = view.findViewById(R.id.save_security_btn);
        save_settings_btn.setOnClickListener(view12 -> changeFingerprint(fingerprint_radio.isChecked()));

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

            cv.put(SETTINGS_COLUMN_UPDATED, DateFormatter.currentDate());

            db.update(SETTINGS_TABLE, cv, SETTINGS_COLUMN_ID + " = ?",
                    new String[] { "1" });

            AppLogs.log(applicationContext, log_tag, "Your settings changed");
        } catch (Exception e){
            CustomAlertDialogClass.showWarningOkDialog(getView(), applicationContext, R.string.error_details);
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

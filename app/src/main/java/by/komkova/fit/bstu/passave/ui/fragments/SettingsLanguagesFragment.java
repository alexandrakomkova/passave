package by.komkova.fit.bstu.passave.ui.fragments;

import static by.komkova.fit.bstu.passave.db.DatabaseHelper.SETTINGS_COLUMN_ID;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.SETTINGS_COLUMN_LANGUAGE;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.SETTINGS_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.SETTINGS_TABLE;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;

import java.util.Locale;

import by.komkova.fit.bstu.passave.helpers.AppLogs;
import by.komkova.fit.bstu.passave.ui.custom_dialog.CustomAlertDialogClass;
import by.komkova.fit.bstu.passave.helpers.DateFormatter;
import by.komkova.fit.bstu.passave.R;
import by.komkova.fit.bstu.passave.db.DatabaseHelper;

public class SettingsLanguagesFragment extends Fragment{

    private final String log_tag = getClass().getName();
    private Context applicationContext;
    private SQLiteDatabase db;
    private SharedPreferences sharedPreferences = null;

    private ImageButton backSettings_ibtn;
    private Button save_settings_btn;
    private RadioButton ru_radio, en_radio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_languages, container, false);
        applicationContext = getActivity();
        DatabaseHelper databaseHelper = new DatabaseHelper(applicationContext);
        db = databaseHelper.getReadableDatabase();

        backSettings_ibtn = view.findViewById(R.id.backSettings_btn);
        backSettings_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout, new SettingsFragment());
                fragmentTransaction.commit();

                // Log.d(log_tag, "TAP");
            }
        });

        ru_radio = view.findViewById(R.id.ru_radio);
        en_radio = view.findViewById(R.id.en_radio);

        save_settings_btn = view.findViewById(R.id.save_languages_btn);
        save_settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String languageCode = "";
                if (ru_radio.isChecked()) {
                    languageCode = "ru";
                }

                if (en_radio.isChecked()) {
                    languageCode = "en";
                }

//                changeLanguageInDatabase(languageCode);
//                changeLocale(languageCode);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("language", languageCode);
                editor.apply();
                AppLogs.log(applicationContext, log_tag, "Your settings changed");
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String languageValue = sharedPreferences.getString("language", "en");
        changeLocale(languageValue);

        if (languageValue.equals("ru")) {
            ru_radio.setChecked(true);
        } else { en_radio.setChecked(true); }

        return view;
    }

    private void changeLocale(String languageCode)
    {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getActivity().getResources().updateConfiguration(configuration, null);

//        getActivity().finish();
//        startActivity(getActivity().getIntent());
    }

    private void changeLanguageInDatabase(String languageCode) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(SETTINGS_COLUMN_LANGUAGE, languageCode);

            cv.put(SETTINGS_COLUMN_UPDATED, DateFormatter.currentDate());

            db.update(SETTINGS_TABLE, cv, SETTINGS_COLUMN_ID + " = ?",
                    new String[] { "1" });
        } catch (Exception e){
            CustomAlertDialogClass.showWarningOkDialog(getView(), applicationContext, R.string.error_details);
            Log.d(log_tag, "error: " + e.getMessage());
        }
    }

}
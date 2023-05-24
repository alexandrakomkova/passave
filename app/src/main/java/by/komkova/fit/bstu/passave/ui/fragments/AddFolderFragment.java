package by.komkova.fit.bstu.passave.ui.fragments;

import static by.komkova.fit.bstu.passave.db.DatabaseHelper.FOLDER_COLUMN_FOLDER_NAME;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.FOLDER_COLUMN_TAG_ID;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.FOLDER_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.db.providers.FolderProvider.FOLDER_URI;
import static by.komkova.fit.bstu.passave.ui.activities.MainActivity.TAG_ID;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import by.komkova.fit.bstu.passave.helpers.LocaleChanger;
import by.komkova.fit.bstu.passave.ui.custom_dialog.CustomAlertDialogClass;
import by.komkova.fit.bstu.passave.helpers.DateFormatter;
import by.komkova.fit.bstu.passave.R;

public class AddFolderFragment extends Fragment {

    final String log_tag = getClass().getName();
    private TextInputEditText enter_folder_title_tiet;
    private Context applicationContext;
    private SharedPreferences sharedPreferences = null;

    private String folder_title = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_folder, container, false);

        applicationContext = getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String languageValue = sharedPreferences.getString("language", "en");
        LocaleChanger.changeLocale(languageValue, applicationContext);


        enter_folder_title_tiet = view.findViewById(R.id.enter_folder_title_field);

        if (savedInstanceState != null) {
            folder_title = savedInstanceState.getString("folder_title");
            enter_folder_title_tiet.setText(folder_title);
        }

        Button save_folder_btn = view.findViewById(R.id.save_folder_btn);
        save_folder_btn.setOnClickListener(this::validateFolder);

        return view;
    }
    public void validateFolder(View v)
    {
        if (Objects.requireNonNull(enter_folder_title_tiet.getText()).toString().trim().isEmpty()) {
            // showWarningDialog(v);
            CustomAlertDialogClass.showWarningOkDialog(v, applicationContext, R.string.please_enter_folder_name);
            // AppLogs.log(applicationContext, log_tag , String.valueOf(R.string.please_enter_folder_name));
        } else {  addFolder(); }
    }

    public void addFolder() {
        ContentValues cv = new ContentValues();

        cv.put(FOLDER_COLUMN_FOLDER_NAME, Objects.requireNonNull(enter_folder_title_tiet.getText()).toString().trim());

        cv.put(FOLDER_COLUMN_UPDATED, DateFormatter.currentDate());
        cv.put(FOLDER_COLUMN_TAG_ID, TAG_ID);

        Uri res =  applicationContext.getContentResolver().insert(FOLDER_URI, cv);

        goHome();
    }

    public void goHome(){
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, new PasswordNotesFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        folder_title = Objects.requireNonNull(enter_folder_title_tiet.getText()).toString().trim();

        outState.putString("folder_title", folder_title);
    }

}
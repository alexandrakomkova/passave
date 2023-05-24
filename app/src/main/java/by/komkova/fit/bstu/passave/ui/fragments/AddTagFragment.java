package by.komkova.fit.bstu.passave.ui.fragments;

import static by.komkova.fit.bstu.passave.db.DatabaseHelper.TAG_COLUMN_TAG_NAME;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.TAG_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.db.providers.TagProvider.TAG_URI;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import java.util.Locale;
import java.util.Objects;

import by.komkova.fit.bstu.passave.helpers.LocaleChanger;
import by.komkova.fit.bstu.passave.ui.custom_dialog.CustomAlertDialogClass;
import by.komkova.fit.bstu.passave.helpers.DateFormatter;
import by.komkova.fit.bstu.passave.R;

public class AddTagFragment extends Fragment {

    final String log_tag = getClass().getName();
    private TextInputEditText enter_tag_name_field;
    private Context applicationContext;

    private String tag_name = "";
    private SharedPreferences sharedPreferences = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_tag, container, false);

        applicationContext =  getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String languageValue = sharedPreferences.getString("language", "en");
        // AppLogs.log(this, log_tag, "Main: " + languageValue);
        LocaleChanger.changeLocale(languageValue, applicationContext);

        enter_tag_name_field = view.findViewById(R.id.enter_tag_name_field);

        if (savedInstanceState != null) {
            tag_name = savedInstanceState.getString("tag_name");
            enter_tag_name_field.setText(tag_name);
        }

        Button save_tag_btn = view.findViewById(R.id.save_tag_btn);
        save_tag_btn.setOnClickListener(this::validateTag);

        return view;
    }


    public void validateTag(View v)
    {
        if (Objects.requireNonNull(enter_tag_name_field.getText()).toString().trim().isEmpty()) {
            // AppLogs.log(applicationContext, log_tag ,"Please enter tag name");
            CustomAlertDialogClass.showWarningOkDialog(v, applicationContext, R.string.please_enter_tag_name);
        } else {  addTag(); }
    }

    public void addTag() {
        ContentValues cv = new ContentValues();

        cv.put(TAG_COLUMN_TAG_NAME, Objects.requireNonNull(enter_tag_name_field.getText()).toString().trim());
        cv.put(TAG_COLUMN_UPDATED, DateFormatter.currentDate());

        Uri res =  applicationContext.getContentResolver().insert(TAG_URI, cv);

        goHome();
    }

    public void goHome(){
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, new HomeFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        tag_name = Objects.requireNonNull(enter_tag_name_field.getText()).toString().trim();

        outState.putString("tag_name", tag_name);
    }
}
package by.komkova.fit.bstu.passave.ui.fragments;

import static by.komkova.fit.bstu.passave.db.DatabaseHelper.TAG_COLUMN_TAG_NAME;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.TAG_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.db.providers.TagProvider.TAG_URI;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import by.komkova.fit.bstu.passave.helpers.AppLogs;
import by.komkova.fit.bstu.passave.helpers.LocaleChanger;
import by.komkova.fit.bstu.passave.ui.custom_dialog.CustomAlertDialogClass;
import by.komkova.fit.bstu.passave.helpers.DateFormatter;
import by.komkova.fit.bstu.passave.R;
import by.komkova.fit.bstu.passave.db.DatabaseHelper;

public class DetailsTagFragment extends Fragment {
    final String log_tag = getClass().getName();
    private TextInputEditText enter_tag_name_field;
    private Context applicationContext;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    private SharedPreferences sharedPreferences = null;

    private String tag_name = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details_tag, container, false);

        applicationContext = getActivity();
        databaseHelper = new DatabaseHelper(applicationContext);
        db = databaseHelper.getReadableDatabase();


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String languageValue = sharedPreferences.getString("language", "en");
        LocaleChanger.changeLocale(languageValue, applicationContext);

        enter_tag_name_field = view.findViewById(R.id.enter_tag_name_field);

        if (savedInstanceState != null) {
            tag_name = savedInstanceState.getString("tag_name");
            enter_tag_name_field.setText(tag_name);
        }

        Bundle bundleArgument = getArguments();
        if (bundleArgument != null) {
            // AppLogs.log(applicationContext, log_tag, String.valueOf(bundleArgument.getInt("tag_id")));
            setTagData(bundleArgument.getInt("tag_id"));
        }

        Button update_tag_btn = view.findViewById(R.id.update_tag_btn);
        update_tag_btn.setOnClickListener(view12 -> validateTag(view, Objects.requireNonNull(bundleArgument).getInt("tag_id")));


        Button delete_tag_btn = view.findViewById(R.id.delete_tag_btn);
        delete_tag_btn.setOnClickListener(view1 -> showWarningDialog(view, Objects.requireNonNull(bundleArgument).getInt("tag_id")));

        return view;
    }
    private void setTagData(Integer Id) {
        String query = "select * from " + DatabaseHelper.TAG_TABLE + " where "+ DatabaseHelper.TAG_COLUMN_ID + " = "+ String.valueOf(Id);
        db = databaseHelper.getReadableDatabase();

        Cursor cursor= null;
        if(db !=null)
        {
            cursor = db.rawQuery(query, null);
        }
        assert cursor != null;
        cursor.moveToFirst();

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                enter_tag_name_field.setText(cursor.getString(cursor.getColumnIndexOrThrow(TAG_COLUMN_TAG_NAME)));

                cursor.moveToNext();
            }

            cursor.close();
        }
    }

    public void validateTag(View v, Integer Id)
    {
        if (Objects.requireNonNull(enter_tag_name_field.getText()).toString().trim().isEmpty()) {
            CustomAlertDialogClass.showWarningOkDialog(v, applicationContext, R.string.please_enter_tag_name);
            // AppLogs.log(applicationContext, log_tag ,"Please enter tag name");
        } else {  updateTag(v, Id); }
    }

    public void updateTag(View v, Integer Id) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(TAG_COLUMN_TAG_NAME, Objects.requireNonNull(enter_tag_name_field.getText()).toString().trim());
            cv.put(TAG_COLUMN_UPDATED, DateFormatter.currentDate());

            Uri uri = ContentUris.withAppendedId(TAG_URI, Id);
            int rowCount = applicationContext.getContentResolver().update(uri, cv, null, null);

            AppLogs.log(applicationContext, log_tag ,getResources().getString(R.string.tag_updated));
            goHome();
        } catch (SQLiteConstraintException e){
            CustomAlertDialogClass.showWarningOkDialog(v, applicationContext, R.string.not_unique_tag_name);
        }
        catch (Exception e){
            Log.d(log_tag, "error: " + e.getMessage());
        }
    }
    public void deleteTag(Integer Id) {
        Uri uri = ContentUris.withAppendedId(TAG_URI, Id);
        int rowCount = applicationContext.getContentResolver().delete(uri, null, null);

        AppLogs.log(applicationContext, log_tag ,getResources().getString(R.string.tag_deleted));
        goHome();
    }
    public void goHome(){
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, new HomeFragment());
        fragmentTransaction.commit();
    }

    private void showWarningDialog(View view, Integer Id) {
        ConstraintLayout constraintLayout = view.findViewById(R.id.errorLayout);
        View v = LayoutInflater.from(applicationContext).inflate(R.layout.error_ok_cancel_dialog, constraintLayout);
        Button errorClose = v.findViewById(R.id.errorCloseButton);
        Button errorOkay = v.findViewById(R.id.errorOkayButton);

        TextView errorDescription = v.findViewById(R.id.errorDescription);
        errorDescription.setText(R.string.tag_delete);

        AlertDialog.Builder builder = new AlertDialog.Builder(applicationContext);
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        errorClose.findViewById(R.id.errorCloseButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        errorOkay.findViewById(R.id.errorOkayButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                deleteTag(Id);
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        tag_name = Objects.requireNonNull(enter_tag_name_field.getText()).toString().trim();

        outState.putString("tag_name", tag_name);
    }



}
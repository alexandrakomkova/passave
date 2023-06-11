package by.komkova.fit.bstu.passave.ui.fragments;

import static by.komkova.fit.bstu.passave.db.DatabaseHelper.NOTE_COLUMN_TAG_ID;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.NOTE_COLUMN_TEXT;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.NOTE_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.ui.activities.MainActivity.TAG_ID;
import static by.komkova.fit.bstu.passave.db.providers.NoteProvider.NOTE_URI;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import by.komkova.fit.bstu.passave.helpers.AppLogs;
import by.komkova.fit.bstu.passave.helpers.DateFormatter;
import by.komkova.fit.bstu.passave.R;
import by.komkova.fit.bstu.passave.db.DatabaseHelper;
import by.komkova.fit.bstu.passave.helpers.LocaleChanger;
import by.komkova.fit.bstu.passave.ui.custom_dialog.CustomAlertDialogClass;

public class DetailsNoteFragment extends Fragment {

    final String log_tag = getClass().getName();
    private TextInputEditText enter_note_text_field;
    private Context applicationContext;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    private SharedPreferences sharedPreferences = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_details_note, container, false);

        applicationContext = getActivity();
        databaseHelper = new DatabaseHelper(applicationContext);
        db = databaseHelper.getReadableDatabase();


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String languageValue = sharedPreferences.getString("language", "en");
        LocaleChanger.changeLocale(languageValue, applicationContext);


        enter_note_text_field = view.findViewById(R.id.enter_note_text_field);
        registerForContextMenu(enter_note_text_field);

        if (savedInstanceState != null) {
            String note_text = savedInstanceState.getString("note_text");
            enter_note_text_field.setText(note_text);
        }

        Bundle bundleArgument = getArguments();
        if (bundleArgument != null) {
            setNoteData(bundleArgument.getInt("note_id"));
        }

        Button update_note_btn = view.findViewById(R.id.update_note_btn);
        update_note_btn.setOnClickListener(view12 -> validateNote(view, bundleArgument.getInt("note_id")));

        Button delete_note_btn = view.findViewById(R.id.delete_note_btn);
        delete_note_btn.setOnClickListener(view1 -> showWarningDialog(view, Objects.requireNonNull(bundleArgument).getInt("note_id")));

        ImageButton backNotes_btn = view.findViewById(R.id.backNotes_btn);
        backNotes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout, new NotesFragment());
                fragmentTransaction.commit();

                // Log.d(log_tag, "TAP");
            }
        });


       return view;
    }

    private void showWarningDialog(View view, Integer Id) {
        ConstraintLayout constraintLayout = view.findViewById(R.id.errorLayout);
        View v = LayoutInflater.from(applicationContext).inflate(R.layout.error_ok_cancel_dialog, constraintLayout);
        Button errorClose = v.findViewById(R.id.errorCloseButton);
        Button errorOkay = v.findViewById(R.id.errorOkayButton);

        TextView errorDescription = v.findViewById(R.id.errorDescription);
        errorDescription.setText(R.string.note_delete);

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
                deleteNote(Id);
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void setNoteData(Integer Id) {
        String query = "select * from " + databaseHelper.NOTE_TABLE + " where "+databaseHelper.NOTE_COLUMN_ID + " = "+ String.valueOf(Id);
        db = databaseHelper.getReadableDatabase();

        Cursor cursor= null;
        if(db !=null) { cursor = db.rawQuery(query, null); }

        assert cursor != null;
        cursor.moveToFirst();

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                enter_note_text_field.setText(cursor.getString(cursor.getColumnIndexOrThrow(NOTE_COLUMN_TEXT)));

                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    public void validateNote(View v, Integer Id)
    {
        if (Objects.requireNonNull(enter_note_text_field.getText()).toString().trim().isEmpty()) {
            // AppLogs.log(applicationContext, log_tag ,"Please enter note text");
            CustomAlertDialogClass.showWarningOkDialog(v, applicationContext, R.string.please_enter_note_text);
        } else {  updateNote(Id); }
    }

    public void updateNote(Integer Id) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(NOTE_COLUMN_TEXT, Objects.requireNonNull(enter_note_text_field.getText()).toString().trim());

            cv.put(NOTE_COLUMN_UPDATED, DateFormatter.currentDate());
            cv.put(NOTE_COLUMN_TAG_ID, TAG_ID);

            Uri uri = ContentUris.withAppendedId(NOTE_URI, Id);
            int rowCount = applicationContext.getContentResolver().update(uri, cv, null, null);

            AppLogs.log(applicationContext, log_tag ,getResources().getString(R.string.note_updated));
            goNotes();
        } catch (Exception e){
            Log.d(log_tag, "error: " + e.getMessage());
        }
    }

    public void deleteNote(Integer Id) {
        Uri uri = ContentUris.withAppendedId(NOTE_URI, Id);
        int rowCount = applicationContext.getContentResolver().delete(uri, null, null);

        AppLogs.log(applicationContext, log_tag ,getResources().getString(R.string.note_deleted));
        goNotes();
    }

    public void goNotes(){
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, new NotesFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("note_text", Objects.requireNonNull(enter_note_text_field.getText()).toString().trim());
    }
}
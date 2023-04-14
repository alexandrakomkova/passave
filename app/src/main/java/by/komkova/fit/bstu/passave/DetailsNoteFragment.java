package by.komkova.fit.bstu.passave;

import static by.komkova.fit.bstu.passave.DatabaseHelper.FOLDER_COLUMN_FOLDER_NAME;
import static by.komkova.fit.bstu.passave.DatabaseHelper.FOLDER_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.DatabaseHelper.NOTE_COLUMN_TAG_ID;
import static by.komkova.fit.bstu.passave.DatabaseHelper.NOTE_COLUMN_TEXT;
import static by.komkova.fit.bstu.passave.DatabaseHelper.NOTE_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.FolderProvider.FOLDER_URI;
import static by.komkova.fit.bstu.passave.MainActivity.TAG_ID;
import static by.komkova.fit.bstu.passave.NoteProvider.NOTE_URI;

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

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DetailsNoteFragment extends Fragment {

    final String log_tag = getClass().getName();
    private Button update_note_btn, delete_note_btn;
    private TextInputEditText enter_note_text_field;
    private Context applicationContext;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    private String note_text = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_details_note, container, false);

        applicationContext = MainActivity.getContextOfApplication();
        databaseHelper = new DatabaseHelper(applicationContext);
        db = databaseHelper.getReadableDatabase();


        enter_note_text_field = view.findViewById(R.id.enter_note_text_field);

        if (savedInstanceState != null) {
            note_text = savedInstanceState.getString("note_text");
            enter_note_text_field.setText(note_text);
        }

        Bundle bundleArgument = getArguments();
        if (bundleArgument != null) {
            setNoteData(bundleArgument.getInt("note_id"));
        }

        update_note_btn = view.findViewById(R.id.update_note_btn);
        update_note_btn.setOnClickListener(view12 -> validateNote(bundleArgument.getInt("note_id")));

        delete_note_btn = view.findViewById(R.id.delete_note_btn);
        delete_note_btn.setOnClickListener(view1 -> deleteNote(bundleArgument.getInt("note_id")));


       return view;
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

    public void validateNote(Integer Id)
    {
        if (enter_note_text_field.getText().toString().trim().isEmpty()) {
            AppLogs.log(applicationContext, log_tag ,"Please enter note text");
        } else {  updateNote(Id); }
    }

    public void updateNote(Integer Id) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(NOTE_COLUMN_TEXT, enter_note_text_field.getText().toString().trim());

            Date currentDate = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            cv.put(NOTE_COLUMN_UPDATED, df.format(currentDate));
            cv.put(NOTE_COLUMN_TAG_ID, TAG_ID);

            Uri uri = ContentUris.withAppendedId(NOTE_URI, Id);
            int rowCount = applicationContext.getContentResolver().update(uri, cv, null, null);

            AppLogs.log(applicationContext, log_tag ,"Note updated");
            goNotes();
        } catch (Exception e){
            Log.d(log_tag, "error: " + e.getMessage());
        }
    }

    public void deleteNote(Integer Id) {
        Uri uri = ContentUris.withAppendedId(NOTE_URI, Id);
        int rowCount = applicationContext.getContentResolver().delete(uri, null, null);

        AppLogs.log(applicationContext, log_tag ,"Note deleted");
        goNotes();
    }

    public void goNotes(){
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, new NotesFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("note_text", enter_note_text_field.getText().toString().trim());
    }
}
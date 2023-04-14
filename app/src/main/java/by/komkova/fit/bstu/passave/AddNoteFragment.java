package by.komkova.fit.bstu.passave;

import static by.komkova.fit.bstu.passave.DatabaseHelper.NOTE_COLUMN_TAG_ID;
import static by.komkova.fit.bstu.passave.DatabaseHelper.NOTE_COLUMN_TEXT;
import static by.komkova.fit.bstu.passave.DatabaseHelper.NOTE_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_CREATED;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_DESCRIPTION;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_FAVOURITE;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_FOLDER_ID;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_LOGIN;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_PASSWORD;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_SERVICE_NAME;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.MainActivity.TAG_ID;
import static by.komkova.fit.bstu.passave.NoteProvider.NOTE_URI;
import static by.komkova.fit.bstu.passave.PasswordNoteProvider.PASSWORD_NOTE_URI;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddNoteFragment extends Fragment {

    final String log_tag = getClass().getName();
    private Button save_note_btn;
    private TextInputEditText enter_note_text_field;

    private Context applicationContext;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    private String note_text = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_note, container, false);

        applicationContext = MainActivity.getContextOfApplication();

        enter_note_text_field = view.findViewById(R.id.enter_note_text_field);

        if (savedInstanceState != null) {
            note_text = savedInstanceState.getString("note_text");
            enter_note_text_field.setText(note_text);
        }

        databaseHelper = new DatabaseHelper(getActivity());
        db = databaseHelper.getWritableDatabase();

        save_note_btn = view.findViewById(R.id.save_note_btn);
        save_note_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateNote(view);
            }
        });

        return view;
    }

    public void validateNote(View v) {

        if (Objects.requireNonNull(enter_note_text_field.getText()).toString().trim().isEmpty()) {
            AppLogs.log(applicationContext, log_tag, "Please enter any text");
        } else { addNote(v); }
    }

    public void addNote(View v) {
        ContentValues cv = new ContentValues();

        cv.put(NOTE_COLUMN_TEXT, Objects.requireNonNull(enter_note_text_field.getText()).toString().trim());
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        cv.put(NOTE_COLUMN_UPDATED, df.format(currentDate));
        cv.put(NOTE_COLUMN_TAG_ID, TAG_ID);

        Uri res =  applicationContext.getContentResolver().insert(NOTE_URI, cv);

        AppLogs.log(applicationContext, log_tag, "Note created");

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
        note_text = Objects.requireNonNull(enter_note_text_field.getText()).toString().trim();

        outState.putString("note_text", note_text);
    }
}
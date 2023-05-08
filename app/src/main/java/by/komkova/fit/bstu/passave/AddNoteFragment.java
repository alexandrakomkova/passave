package by.komkova.fit.bstu.passave;

import static by.komkova.fit.bstu.passave.DatabaseHelper.NOTE_COLUMN_TAG_ID;
import static by.komkova.fit.bstu.passave.DatabaseHelper.NOTE_COLUMN_TEXT;
import static by.komkova.fit.bstu.passave.DatabaseHelper.NOTE_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.MainActivity.TAG_ID;
import static by.komkova.fit.bstu.passave.NoteProvider.NOTE_URI;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class AddNoteFragment extends Fragment {

    final String log_tag = getClass().getName();
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

        applicationContext =  getActivity();

        enter_note_text_field = view.findViewById(R.id.enter_note_text_field);

        if (savedInstanceState != null) {
            note_text = savedInstanceState.getString("note_text");
            enter_note_text_field.setText(note_text);
        }

        databaseHelper = new DatabaseHelper(getActivity());
        db = databaseHelper.getWritableDatabase();

        Button save_note_btn = view.findViewById(R.id.save_note_btn);
        save_note_btn.setOnClickListener(this::validateNote);

        return view;
    }

    public void validateNote(View v) {

        if (Objects.requireNonNull(enter_note_text_field.getText()).toString().trim().isEmpty()) {
            CustomAlertDialogClass.showWarningOkDialog(v, applicationContext, R.string.please_enter_note_text);
            // AppLogs.log(applicationContext, log_tag, "Please enter any text");
        } else { addNote(); }
    }

    public void addNote() {
        ContentValues cv = new ContentValues();

        cv.put(NOTE_COLUMN_TEXT, Objects.requireNonNull(enter_note_text_field.getText()).toString().trim());

        cv.put(NOTE_COLUMN_UPDATED, DateFormatter.currentDate());
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
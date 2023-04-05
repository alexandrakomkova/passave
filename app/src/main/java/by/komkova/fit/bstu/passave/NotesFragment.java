package by.komkova.fit.bstu.passave;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.util.ArrayList;

public class NotesFragment extends Fragment {
    private String log_tag = getClass().getName();

    RecyclerView recyclerViewNote;
    private RecyclerView.LayoutManager layoutManagerNote;
    ArrayList<RCModelNote> noteArrayList;
    RCAdapterNote rcAdapterNote;

    private Context applicationContext;

    SQLiteDatabase db;
    DatabaseHelper databaseHelper;

    private FloatingActionButton add_floating_btn, add_note_floating_btn;
    private Animation rotateOpen, rotateClose, fromBottom, toBottom;
    private boolean clicked = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        applicationContext = MainActivity.getContextOfApplication();
        databaseHelper = new DatabaseHelper(applicationContext);
        db = databaseHelper.getReadableDatabase();
        noteArrayList = new ArrayList<RCModelNote>();

        recyclerViewNote = view.findViewById(R.id.recyclerViewNote);
        try {
            setInitialData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        rotateOpen = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(applicationContext, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(applicationContext, R.anim.to_bottom_anim);

        add_floating_btn = view.findViewById(R.id.add_floating_btn);
        add_floating_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddButtonClicked();
            }
        });

        add_note_floating_btn = view.findViewById(R.id.add_note_floating_btn);
        add_note_floating_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout, new AddNoteFragment());
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    private void setInitialData() throws ParseException {
        noteArrayList.clear();

        Cursor c1 = db.query(DatabaseHelper.NOTE_TABLE, null, null, null, null, null, null);
        if (c1 != null && c1.getCount() != 0) {
            noteArrayList.clear();
            while (c1.moveToNext()) {
                RCModelNote rcItem = new RCModelNote();
                // AppLogs.log(applicationContext, log_tag, c1.getString(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_LOGIN)) );

                rcItem.setId(c1.getInt(c1.getColumnIndexOrThrow(DatabaseHelper.NOTE_COLUMN_ID)));
                rcItem.setNoteText(c1.getString(c1.getColumnIndexOrThrow(DatabaseHelper.NOTE_COLUMN_TEXT)));
                noteArrayList.add(rcItem);
            }
        }
        c1.close();
        layoutManagerNote = new LinearLayoutManager(applicationContext);
        rcAdapterNote = new RCAdapterNote(applicationContext, noteArrayList);
        recyclerViewNote.setLayoutManager(layoutManagerNote);
        recyclerViewNote.setAdapter(rcAdapterNote);
    }

    private void onAddButtonClicked() {
        setVisibility(clicked);
        setAnimation(clicked);

        clicked = !clicked;
    }

    private void setVisibility(boolean clicked) {

        if(!clicked) {
            add_note_floating_btn.setVisibility(View.VISIBLE);
        } else {
            add_note_floating_btn.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(boolean clicked) {

        if(!clicked) {
            add_note_floating_btn.startAnimation(fromBottom);
            add_floating_btn.startAnimation(rotateOpen);
        } else {
            add_note_floating_btn.startAnimation(toBottom);
            add_floating_btn.startAnimation(rotateClose);
        }
    }

}
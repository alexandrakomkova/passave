package by.komkova.fit.bstu.passave.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import by.komkova.fit.bstu.passave.R;
import by.komkova.fit.bstu.passave.helpers.LocaleChanger;
import by.komkova.fit.bstu.passave.ui.adapters.RCAdapterTag;
import by.komkova.fit.bstu.passave.ui.models.RCModelTag;
import by.komkova.fit.bstu.passave.db.DatabaseHelper;

public class HomeFragment extends Fragment {

    private String log_tag = getClass().getName();

    RecyclerView recyclerViewOddId, recyclerViewEvenId;
    private RecyclerView.LayoutManager layoutManagerOddId, layoutManagerEvenId;
    ArrayList<RCModelTag> tagArrayListOdd, tagArrayListEven;
    RCAdapterTag rcAdapterEven, rcAdapterOdd;

    private Context applicationContext;
    SQLiteDatabase db;
    DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences = null;

    private FloatingActionButton add_tag_floating_btn, add_floating_btn;
    private Animation rotateOpen, rotateClose, fromBottom, toBottom;
    private boolean clicked = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        applicationContext = getActivity();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String languageValue = sharedPreferences.getString("language", "en");
        // AppLogs.log(this, log_tag, "Main: " + languageValue);
        LocaleChanger.changeLocale(languageValue, applicationContext);

        databaseHelper = new DatabaseHelper(applicationContext);
        db = databaseHelper.getReadableDatabase();
        tagArrayListOdd = new ArrayList<RCModelTag>();
        tagArrayListEven = new ArrayList<RCModelTag>();

        recyclerViewOddId = view.findViewById(R.id.recyclerViewOddId);
        recyclerViewEvenId = view.findViewById(R.id.recyclerViewEvenId);

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

        add_tag_floating_btn = view.findViewById(R.id.add_tag_floating_btn);
        add_tag_floating_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout, new AddTagFragment());
                fragmentTransaction.commit();
            }
        });

        return view;
    }


    private void setInitialData() throws ParseException {
        tagArrayListEven.clear();
        tagArrayListOdd.clear();

        db = databaseHelper.getReadableDatabase();

        String query = "select * from " + DatabaseHelper.TAG_TABLE + " where "+ DatabaseHelper.TAG_COLUMN_ID + " % 2 = 1";

        Cursor cursor = null;
        if(db !=null)
        {
            cursor = db.rawQuery(query, null);
        }
        assert cursor != null;
        cursor.moveToFirst();

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                RCModelTag rcItem = new RCModelTag();
                // AppLogs.log(applicationContext, log_tag, c1.getString(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_LOGIN)) );

                rcItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.TAG_COLUMN_ID)));
                rcItem.setTagName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TAG_COLUMN_TAG_NAME)));
                tagArrayListOdd.add(rcItem);

                cursor.moveToNext();
            }

            cursor.close();
        }

        layoutManagerOddId = new LinearLayoutManager(applicationContext);
        rcAdapterOdd = new RCAdapterTag(applicationContext, tagArrayListOdd);
        recyclerViewOddId.setLayoutManager(layoutManagerOddId);
        recyclerViewOddId.setAdapter(rcAdapterOdd);

        query = "select * from " + DatabaseHelper.TAG_TABLE + " where "+ DatabaseHelper.TAG_COLUMN_ID + " % 2 = 0";

        cursor = null;
        if(db !=null)
        {
            cursor = db.rawQuery(query, null);
        }
        assert cursor != null;
        cursor.moveToFirst();

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                RCModelTag rcItem = new RCModelTag();
                // AppLogs.log(applicationContext, log_tag, c1.getString(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_LOGIN)) );

                rcItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.TAG_COLUMN_ID)));
                rcItem.setTagName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TAG_COLUMN_TAG_NAME)));
                tagArrayListEven.add(rcItem);

                cursor.moveToNext();
            }

            cursor.close();
        }

        layoutManagerEvenId = new LinearLayoutManager(applicationContext);
        rcAdapterEven = new RCAdapterTag(applicationContext, tagArrayListEven);
        recyclerViewEvenId.setLayoutManager(layoutManagerEvenId);
        recyclerViewEvenId.setAdapter(rcAdapterEven);
    }

    private void onAddButtonClicked() {
        setVisibility(clicked);
        setAnimation(clicked);

        clicked = !clicked;
    }

    private void setVisibility(boolean clicked) {

        if(!clicked) {
            add_tag_floating_btn.setVisibility(View.VISIBLE);
        } else {
            add_tag_floating_btn.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(boolean clicked) {

        if(!clicked) {
            add_tag_floating_btn.startAnimation(fromBottom);
            add_floating_btn.startAnimation(rotateOpen);
        } else {
            add_tag_floating_btn.startAnimation(toBottom);
            add_floating_btn.startAnimation(rotateClose);
        }
    }
}
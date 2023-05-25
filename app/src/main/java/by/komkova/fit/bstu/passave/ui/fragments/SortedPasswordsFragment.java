package by.komkova.fit.bstu.passave.ui.fragments;

import static by.komkova.fit.bstu.passave.db.DatabaseHelper.PN_COLUMN_FAVOURITE;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.PN_COLUMN_FOLDER_ID;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.PN_COLUMN_TAG_ID;
import static by.komkova.fit.bstu.passave.ui.activities.MainActivity.TAG_ID;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import by.komkova.fit.bstu.passave.R;
import by.komkova.fit.bstu.passave.helpers.LocaleChanger;
import by.komkova.fit.bstu.passave.ui.adapters.RCAdapterPassword;
import by.komkova.fit.bstu.passave.ui.models.RCModelPassword;
import by.komkova.fit.bstu.passave.db.DatabaseHelper;

public class SortedPasswordsFragment extends Fragment {

    private final String log_tag = getClass().getName();
    private Context applicationContext;
    SQLiteDatabase db;
    DatabaseHelper dbHelper;

    private ArrayList<RCModelPassword> modelArrayList;
    private RCAdapterPassword rcAdapter;
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sorted_passwords, container, false);
        applicationContext = getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String languageValue = sharedPreferences.getString("language", "en");
        LocaleChanger.changeLocale(languageValue, applicationContext);

        dbHelper = new DatabaseHelper(applicationContext);
        db = dbHelper.getReadableDatabase();

        modelArrayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        rcAdapter = new RCAdapterPassword(applicationContext, modelArrayList);
        recyclerView.setAdapter(rcAdapter);

        ImageButton backSettings_ibtn = view.findViewById(R.id.backSettings_btn);
        backSettings_ibtn.setOnClickListener(view1 -> {
            FragmentTransaction fragmentTransaction = getActivity()
                    .getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_layout, new HomeFragment());
            fragmentTransaction.commit();
        });


        TextView sorted_passwords_label = view.findViewById(R.id.sorted_passwords_label);

        Bundle bundleArgument = getArguments();
        if (bundleArgument != null) {
            sorted_passwords_label.setText(bundleArgument.getString("folder_name"));

            if (bundleArgument.getString("folder_name").toLowerCase().equals("favourite")) {
                setFavouritePasswordData();
            } else {  setSortedPasswordData(bundleArgument.getInt("folder_id")); }
        }

        return view;
    }

    private void setSortedPasswordData(int folder_id) {
        modelArrayList.clear();
        String whereclause = PN_COLUMN_TAG_ID + "=? AND " + PN_COLUMN_FOLDER_ID + "=?";
        String[] whereargs = new String[]{ TAG_ID, String.valueOf(folder_id) };

        Cursor c1 = db.query(DatabaseHelper.PASSWORD_NOTE_TABLE,null, whereclause, whereargs,null,null,null);
        // AppLogs.log(applicationContext, log_tag, String.valueOf(c1.getCount()) + "/" + String.valueOf(folder_id) + "/" + TAG_ID);

        if (c1 != null && c1.getCount() != 0) {

            modelArrayList.clear();
            while (c1.moveToNext()) {
                RCModelPassword rcItem = new RCModelPassword();

                rcItem.setId(c1.getInt(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_ID)));
                rcItem.setTitle(c1.getString(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_SERVICE_NAME)));
                rcItem.setLogin(c1.getString(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_LOGIN)));
                rcItem.setLastUpdateDate(c1.getString(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_UPDATED)));
                rcItem.setFavourite(c1.getInt(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_FAVOURITE)));
                modelArrayList.add(rcItem);
            }
            c1.close();
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(applicationContext);
        rcAdapter = new RCAdapterPassword(applicationContext, modelArrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(rcAdapter);
    }

    private void setFavouritePasswordData() {
        modelArrayList.clear();
        String whereclause = PN_COLUMN_TAG_ID + "=? AND " + PN_COLUMN_FAVOURITE + "= 1";
        String[] whereargs = new String[]{ TAG_ID };

        Cursor c1 = db.query(DatabaseHelper.PASSWORD_NOTE_TABLE,null, whereclause, whereargs,null,null,null);
        // AppLogs.log(applicationContext, log_tag, String.valueOf(c1.getCount()) + "/" + String.valueOf(folder_id) + "/" + TAG_ID);

        if (c1 != null && c1.getCount() != 0) {

            modelArrayList.clear();
            while (c1.moveToNext()) {
                RCModelPassword rcItem = new RCModelPassword();

                rcItem.setId(c1.getInt(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_ID)));
                rcItem.setTitle(c1.getString(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_SERVICE_NAME)));
                rcItem.setLogin(c1.getString(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_LOGIN)));
                rcItem.setLastUpdateDate(c1.getString(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_UPDATED)));
                rcItem.setFavourite(c1.getInt(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_FAVOURITE)));
                modelArrayList.add(rcItem);
            }
            c1.close();
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(applicationContext);
        rcAdapter = new RCAdapterPassword(applicationContext, modelArrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(rcAdapter);
    }
}
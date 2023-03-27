package by.komkova.fit.bstu.passave;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<RCModel> modelArrayList;
    RCAdapter rcAdapter;
    private Context applicationContext;
    SQLiteDatabase db;
    DatabaseHelper dbHelper;
    SimpleDateFormat df;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        applicationContext = MainActivity.getContextOfApplication();
        dbHelper = new DatabaseHelper(applicationContext);
        db = dbHelper.getReadableDatabase();
        modelArrayList = new ArrayList<RCModel>();
        df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());


        recyclerView = view.findViewById(R.id.recyclerView);
        try {
            setInitialData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        rcAdapter = new RCAdapter(applicationContext, modelArrayList);
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(rcAdapter);

        return view;
    }

    private void setInitialData() throws ParseException {
        modelArrayList.clear();

        Cursor c1 = db.query(DatabaseHelper.PASSWORD_NOTE_TABLE, null, null, null, null, null, null);
        if (c1 != null && c1.getCount() != 0) {
            modelArrayList.clear();
            while (c1.moveToNext()) {
                RCModel rcItem = new RCModel();
                rcItem.setId(c1.getInt(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_ID)));
                rcItem.setTitle(c1.getString(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_SERVICE_NAME)));
                rcItem.setLastUpdateDate(c1.getString(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_UPDATED)));
                modelArrayList.add(rcItem);
            }
        }
        c1.close();
        layoutManager = new LinearLayoutManager(applicationContext);
        rcAdapter = new RCAdapter(applicationContext, modelArrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(rcAdapter);
    }

    private String formatDate(String date) throws ParseException {

        //current date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

        Date objDate = dateFormat.parse(date);

        //Expected date format
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

        return dateFormat2.format(objDate);
    }


}
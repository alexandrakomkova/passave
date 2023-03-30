package by.komkova.fit.bstu.passave;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.zip.Inflater;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<RCModel> modelArrayList;
    RCAdapter rcAdapter;
    private Context applicationContext;
    SQLiteDatabase db;
    DatabaseHelper dbHelper;
    SimpleDateFormat df;

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

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
                rcItem.setLogin(c1.getString(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_LOGIN)));
                rcItem.setLastUpdateDate(c1.getString(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_UPDATED)));
                rcItem.setFavourite(c1.getInt(c1.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_FAVOURITE)));
                modelArrayList.add(rcItem);
            }
        }
        c1.close();
        layoutManager = new LinearLayoutManager(applicationContext);
        rcAdapter = new RCAdapter(applicationContext, modelArrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(rcAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                rcAdapter.getFilter().filter(newText);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);

    }




}
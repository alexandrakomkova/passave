package by.komkova.fit.bstu.passave;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Animatable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.zip.Inflater;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView, recyclerViewFolder;
    private RecyclerView.LayoutManager layoutManager, layoutManagerFolder;
    ArrayList<RCModel> modelArrayList;
    ArrayList<RCModelFolder> folderArrayList;
    RCAdapter rcAdapter;
    RCAdapterFolder rcAdapterFolder;

    private Context applicationContext;

    SQLiteDatabase db;
    DatabaseHelper dbHelper;
    SimpleDateFormat df;

    private Button add_folder_btn;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private FloatingActionButton add_password_floating_btn, add_floating_btn, add_folder_floating_btn;

    private Animation rotateOpen, rotateClose, fromBottom, toBottom;
    private boolean clicked = false;

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
        folderArrayList = new ArrayList<RCModelFolder>();
        df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerViewFolder = view.findViewById(R.id.recyclerViewFolders);
        try {
            setInitialData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            setFolderInitialData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        rcAdapter = new RCAdapter(applicationContext, modelArrayList);
        recyclerView.setAdapter(rcAdapter);

        rcAdapterFolder = new RCAdapterFolder(applicationContext, folderArrayList);
        recyclerViewFolder.setAdapter(rcAdapterFolder);

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
        add_folder_floating_btn = view.findViewById(R.id.add_folder_floating_btn);
        add_folder_floating_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout, new AddFolderFragment());
                fragmentTransaction.commit();
            }
        });

        add_password_floating_btn = view.findViewById(R.id.add_password_floating_btn);
        add_password_floating_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout, new AddPasswordFragment());
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    private void onAddButtonClicked() {
        setVisibility(clicked);
        setAnimation(clicked);

        clicked = !clicked;
    }

    private void setVisibility(boolean clicked) {

        if(!clicked) {
            add_password_floating_btn.setVisibility(View.VISIBLE);
            add_folder_floating_btn.setVisibility(View.VISIBLE);
        } else {
            add_password_floating_btn.setVisibility(View.INVISIBLE);
            add_folder_floating_btn.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(boolean clicked) {

        if(!clicked) {
            add_password_floating_btn.startAnimation(fromBottom);
            add_folder_floating_btn.startAnimation(fromBottom);
            add_floating_btn.startAnimation(rotateOpen);
        } else {
            add_password_floating_btn.startAnimation(toBottom);
            add_folder_floating_btn.startAnimation(toBottom);
            add_floating_btn.startAnimation(rotateClose);
        }
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

    private void setFolderInitialData() throws ParseException {
        folderArrayList.clear();

        Cursor c1 = db.query(DatabaseHelper.FOLDER_TABLE, null, null, null, null, null, null);
        if (c1 != null && c1.getCount() != 0) {
            folderArrayList.clear();
            c1.moveToFirst();
            while (c1.moveToNext()) {
                RCModelFolder rcItem = new RCModelFolder();
                rcItem.setId(c1.getInt(c1.getColumnIndexOrThrow(DatabaseHelper.FOLDER_COLUMN_ID)));
                rcItem.setFolderTitle(c1.getString(c1.getColumnIndexOrThrow(DatabaseHelper.FOLDER_COLUMN_FOLDER_NAME)));
                folderArrayList.add(rcItem);
            }
        }
        c1.close();
        layoutManagerFolder = new LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false);
        rcAdapterFolder = new RCAdapterFolder(applicationContext, folderArrayList);
        recyclerViewFolder.setLayoutManager(layoutManagerFolder);
        recyclerViewFolder.setAdapter(rcAdapterFolder);
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
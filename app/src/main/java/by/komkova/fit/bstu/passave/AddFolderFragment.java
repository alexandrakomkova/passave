package by.komkova.fit.bstu.passave;

import static by.komkova.fit.bstu.passave.DatabaseHelper.FOLDER_COLUMN_FOLDER_NAME;
import static by.komkova.fit.bstu.passave.DatabaseHelper.FOLDER_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_CREATED;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_DESCRIPTION;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_FAVOURITE;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_LOGIN;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_PASSWORD;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_SERVICE_NAME;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.FolderProvider.FOLDER_URI;
import static by.komkova.fit.bstu.passave.PasswordNoteProvider.PASSWORD_NOTE_URI;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddFolderFragment extends Fragment {

    final String log_tag = getClass().getName();
    private Button save_folder_btn;
    private TextInputEditText enter_folder_title_tiet;
    private Context applicationContext;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    private String folder_title = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_folder, container, false);

        applicationContext = MainActivity.getContextOfApplication();

        enter_folder_title_tiet = view.findViewById(R.id.enter_folder_title_field);

        if (savedInstanceState != null) {
            folder_title = savedInstanceState.getString("folder_title");
            enter_folder_title_tiet.setText(folder_title);
        }

        save_folder_btn = view.findViewById(R.id.save_folder_btn);
        save_folder_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFolder(view);
            }
        });

        return view;
    }
    public void validateFolder(View v)
    {
        if (enter_folder_title_tiet.getText().toString().trim().isEmpty()) {
            AppLogs.log(applicationContext, log_tag ,"Please enter folder name");
        } else {  addFolder(v); }
    }

    public void addFolder(View v) {
        ContentValues cv = new ContentValues();

        cv.put(FOLDER_COLUMN_FOLDER_NAME, enter_folder_title_tiet.getText().toString().trim());

        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        cv.put(FOLDER_COLUMN_UPDATED, df.format(currentDate));

        Uri res =  applicationContext.getContentResolver().insert(FOLDER_URI, cv);

        goHome();
    }

    public void goHome(){
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, new HomeFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        folder_title = enter_folder_title_tiet.getText().toString().trim();

        outState.putString("folder_title", folder_title);
    }

}
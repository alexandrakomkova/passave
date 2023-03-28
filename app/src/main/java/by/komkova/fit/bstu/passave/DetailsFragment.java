package by.komkova.fit.bstu.passave;

import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_DESCRIPTION;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_LOGIN;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_PASSWORD;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_SERVICE_NAME;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.PasswordNoteProvider.PASSWORD_NOTE_URI;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

public class DetailsFragment extends Fragment {
    private String log_tag = DetailsFragment.class.getName();
    private Context applicationContext;

    private Integer Id;
    private TextInputEditText enter_service_title_tiet, enter_login_tiet, enter_details_tiet, enter_password_tiet;
    private Button update_password_btn, delete_password_btn, generate_password_btn;
    SQLiteDatabase db;
    DatabaseHelper databaseHelper;

    private String service_name = "";
    private String login = "";
    private String description = "";
    private String entered_password = "";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        service_name = enter_service_title_tiet.getText().toString().trim();
        login = enter_login_tiet.getText().toString().trim();
        description = enter_details_tiet.getText().toString().trim();
        entered_password = enter_password_tiet.getText().toString().trim();

        outState.putString("service_name", service_name);
        outState.putString("login", login);
        outState.putString("description", description);
        outState.putString("entered_password", entered_password);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        applicationContext = MainActivity.getContextOfApplication();
        databaseHelper = new DatabaseHelper(applicationContext);
        db = databaseHelper.getReadableDatabase();

        enter_service_title_tiet = view.findViewById(R.id.enter_service_title_field);
        enter_login_tiet = view.findViewById(R.id.enter_login_field);
        enter_details_tiet = view.findViewById(R.id.enter_details_field);
        enter_password_tiet = view.findViewById(R.id.enter_password_field);

        if (savedInstanceState != null) {
            service_name = savedInstanceState.getString("service_name");
            enter_service_title_tiet.setText(service_name);

            login = savedInstanceState.getString("login");
            enter_login_tiet.setText(login);

            description = savedInstanceState.getString("description");
            enter_details_tiet.setText(description);

            entered_password = savedInstanceState.getString("entered_password");
            enter_password_tiet.setText(entered_password);
        }

        Bundle bundleArgument = getArguments();
        if (bundleArgument != null) {
            if (bundleArgument.getInt("isEdit") > 0 ) {
                enter_service_title_tiet.setText(bundleArgument.getString("service_name"));
                enter_login_tiet.setText(bundleArgument.getString("login"));
                enter_details_tiet.setText(bundleArgument.getString("description"));
                enter_password_tiet.setText(bundleArgument.getString("generated_password"));
            } else {
                setPasswordNoteData(bundleArgument.getInt("password_note_id"));
            }
        }

        generate_password_btn = view.findViewById(R.id.generate_password_btn);
        generate_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneratePasswordFragment generatePasswordFragment = new GeneratePasswordFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("isEdit", 1);

                bundle.putString("service_name", String.valueOf(enter_service_title_tiet.getText()));
                bundle.putString("login", String.valueOf(enter_login_tiet.getText()));
                bundle.putString("description", String.valueOf(enter_details_tiet.getText()));
                bundle.putString("generated_password", String.valueOf(enter_password_tiet.getText()));

                generatePasswordFragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_layout,  generatePasswordFragment).commit();
            }
        });

        update_password_btn = view.findViewById(R.id.update_password_btn);
        update_password_btn.setOnClickListener(view1 -> updatePasswordNote(bundleArgument.getInt("password_note_id")));

        delete_password_btn = view.findViewById(R.id.delete_password_btn);
        delete_password_btn.setOnClickListener(view1 -> deletePasswordNote(bundleArgument.getInt("password_note_id")));

        return view;
    }

    private void setPasswordNoteData(Integer Id) {
        String query = "select * from " + databaseHelper.PASSWORD_NOTE_TABLE + " where "+databaseHelper.PN_COLUMN_ID + " = "+ String.valueOf(Id);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor= null;
        if(db !=null)
        {
            cursor = db.rawQuery(query, null);
        }
        cursor.moveToFirst();

        if(cursor!=null && cursor.getCount()!=0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                enter_service_title_tiet.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_SERVICE_NAME)));
                enter_login_tiet.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_LOGIN)));
                enter_details_tiet.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_DESCRIPTION)));
                enter_password_tiet.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_PASSWORD)));

                cursor.moveToNext();
            }

            cursor.close();
        }
    }

    public void updatePasswordNote(Integer Id) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(PN_COLUMN_SERVICE_NAME, enter_service_title_tiet.getText().toString().trim());
            cv.put(PN_COLUMN_LOGIN, enter_login_tiet.getText().toString().trim());
            cv.put(PN_COLUMN_PASSWORD, enter_password_tiet.getText().toString().trim());
            cv.put(PN_COLUMN_DESCRIPTION, enter_details_tiet.getText().toString().trim());

            Date currentDate = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            cv.put(PN_COLUMN_UPDATED, df.format(currentDate));

            Uri uri = ContentUris.withAppendedId(PASSWORD_NOTE_URI, Id);
            int rowCount = applicationContext.getContentResolver().update(uri, cv, null, null);

            Log.d(log_tag, "updated");
            goHome();
        } catch (Exception e){
            Log.d(log_tag, "error: " + e.getMessage());
        }
    }

    public void goHome(){
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, new HomeFragment());
        fragmentTransaction.commit();
    }

    public void deletePasswordNote(Integer Id) {
        Uri uri = ContentUris.withAppendedId(PASSWORD_NOTE_URI, Id);
        int rowCount = applicationContext.getContentResolver().delete(uri, null, null);

        goHome();
    }
}
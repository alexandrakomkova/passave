package by.komkova.fit.bstu.passave;

import static by.komkova.fit.bstu.passave.DatabaseHelper.FOLDER_COLUMN_FOLDER_NAME;
import static by.komkova.fit.bstu.passave.DatabaseHelper.FOLDER_COLUMN_ID;
import static by.komkova.fit.bstu.passave.DatabaseHelper.FOLDER_COLUMN_TAG_ID;
import static by.komkova.fit.bstu.passave.DatabaseHelper.FOLDER_TABLE;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PASSWORD_NOTE_TABLE;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_DESCRIPTION;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_FOLDER_ID;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_ID;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_LOGIN;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_PASSWORD;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_SERVICE_NAME;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_TAG_ID;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.MainActivity.TAG_ID;
import static by.komkova.fit.bstu.passave.PasswordNoteProvider.PASSWORD_NOTE_URI;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailsPasswordFragment extends Fragment {
    private final String log_tag = DetailsPasswordFragment.class.getName();
    private Context applicationContext;

    private TextView passwordDetailsTextView;
    private TextInputEditText enter_service_title_tiet, enter_login_tiet, enter_details_tiet, enter_password_tiet;
    private Spinner spinnerFolders;

    SQLiteDatabase db;
    DatabaseHelper databaseHelper;
    ArrayAdapter<String> spinnerAdapter;
    List<String> foldersList;
    Integer selectedFolderId = null;

    private String service_name = "";
    private String login = "";
    private String description = "";
    private String entered_password = "";

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        service_name = Objects.requireNonNull(enter_service_title_tiet.getText()).toString().trim();
        login = Objects.requireNonNull(enter_login_tiet.getText()).toString().trim();
        description = Objects.requireNonNull(enter_details_tiet.getText()).toString().trim();
        entered_password = Objects.requireNonNull(enter_password_tiet.getText()).toString().trim();

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
        applicationContext = getActivity();
        databaseHelper = new DatabaseHelper(applicationContext);
        db = databaseHelper.getReadableDatabase();

        enter_service_title_tiet = view.findViewById(R.id.enter_service_title_field);
        enter_login_tiet = view.findViewById(R.id.enter_login_field);
        enter_details_tiet = view.findViewById(R.id.enter_details_field);
        enter_password_tiet = view.findViewById(R.id.enter_password_field);
        passwordDetailsTextView = view.findViewById(R.id.passwordDetailsTextView);

        if (savedInstanceState != null) {
            service_name = savedInstanceState.getString("service_name");
            enter_service_title_tiet.setText(service_name);
            passwordDetailsTextView.setText(service_name);

            login = savedInstanceState.getString("login");
            enter_login_tiet.setText(login);

            description = savedInstanceState.getString("description");
            enter_details_tiet.setText(description);

            entered_password = savedInstanceState.getString("entered_password");
            enter_password_tiet.setText(entered_password);
        }

        spinnerFolders = view.findViewById(R.id.spinnerFolders);
        foldersList = new ArrayList<>();
        loadSpinnerData();

        spinnerFolders.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedFolderId = findFolderId(spinnerFolders.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        Bundle bundleArgument = getArguments();
        if (bundleArgument != null) {
            if (bundleArgument.getInt("isEdit") > 0 ) {
                enter_service_title_tiet.setText(bundleArgument.getString("service_name"));
                passwordDetailsTextView.setText(bundleArgument.getString("service_name"));
                enter_login_tiet.setText(bundleArgument.getString("login"));
                enter_details_tiet.setText(bundleArgument.getString("description"));
                enter_password_tiet.setText(bundleArgument.getString("generated_password"));
            } else {
                setPasswordNoteData(bundleArgument.getInt("password_note_id"));
            }
        }

        Button generate_password_btn = view.findViewById(R.id.generate_password_btn);
        generate_password_btn.setOnClickListener(view12 -> {
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
        });

        Button update_password_btn = view.findViewById(R.id.update_password_btn);
        update_password_btn.setOnClickListener(view1 -> updatePasswordNote(Objects.requireNonNull(bundleArgument).getInt("password_note_id")));

        Button delete_password_btn = view.findViewById(R.id.delete_password_btn);
        delete_password_btn.setOnClickListener(view1 -> deletePasswordNote(Objects.requireNonNull(bundleArgument).getInt("password_note_id")));

        ImageButton back_btn = view.findViewById(R.id.backHome_btn);
        back_btn.setOnClickListener(view13 -> {
            FragmentTransaction fragmentTransaction = getActivity()
                    .getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_layout, new PasswordNotesFragment());
            fragmentTransaction.commit();
        });

        return view;
    }

    public Integer findFolderId(String title) {
        String query = "select "+ DatabaseHelper.FOLDER_COLUMN_ID +" from " + DatabaseHelper.FOLDER_TABLE
                + " where " + DatabaseHelper.FOLDER_COLUMN_FOLDER_NAME + " = \""+title+"\"";

        int s = 0;
        Cursor cursor= null;
        if(db !=null)
        {
            cursor = db.rawQuery(query, null);
        }
        assert cursor != null;
        cursor.moveToFirst();

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){

                s = cursor.getInt(0);
                cursor.moveToNext();
            }

            cursor.close();
        }
        return s;
    }

    public void loadSpinnerData() {
        // String query = "select " + DatabaseHelper.FOLDER_COLUMN_FOLDER_NAME + " from " + DatabaseHelper.FOLDER_TABLE +
               // " where " + DatabaseHelper.FOLDER_COLUMN_FOLDER_NAME + " != \'Favourite\'";
        // SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String whereclause = FOLDER_COLUMN_TAG_ID + "=? AND " + FOLDER_COLUMN_FOLDER_NAME + " != 'Favourite'";
        String[] whereargs = new String[]{ TAG_ID };
        String [] columns = new String[] { FOLDER_COLUMN_FOLDER_NAME };
        // Cursor csr = db.query(DatabaseHelper.FOLDER_TABLE,null, whereclause, whereargs,null,null,null);

        Cursor cursor= null;
        if(db !=null)
        {
            // cursor = db.rawQuery(query, null);
            cursor = db.query(DatabaseHelper.FOLDER_TABLE, columns, whereclause, whereargs,null,null,null);

        }
        assert cursor != null;
        cursor.moveToFirst();


        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                String s = cursor.getString(0);

                foldersList.add(s);
                cursor.moveToNext();
            }

            cursor.close();

            spinnerAdapter = new ArrayAdapter<String>(applicationContext, R.layout.spinner_item_selected, foldersList);
            spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

            spinnerFolders.setAdapter(spinnerAdapter);
            spinnerAdapter.notifyDataSetChanged();
        }

    }

    private void setSelectedFolderSpinner(String folderName) {
        if (folderName != null) {
            int spinnerPosition = spinnerAdapter.getPosition(folderName);
            spinnerFolders.setSelection(spinnerPosition);
        }
    }

    private void setPasswordNoteData(Integer Id) {
         String query = "select "
                 + FOLDER_COLUMN_FOLDER_NAME + ", "
                 + PN_COLUMN_SERVICE_NAME + ", "
                 + PN_COLUMN_LOGIN + ", "
                 + PN_COLUMN_DESCRIPTION + ", "
                 + PN_COLUMN_PASSWORD
                 + " from " + PASSWORD_NOTE_TABLE + " join " + FOLDER_TABLE
                 + " on " + PASSWORD_NOTE_TABLE+ "." +PN_COLUMN_FOLDER_ID
                 + " = " + FOLDER_TABLE + "." + FOLDER_COLUMN_ID
                 + " where " + PASSWORD_NOTE_TABLE+ "." + PN_COLUMN_ID + " = "+ String.valueOf(Id);


        Cursor cursor= null;
        if(db !=null)
        {
            cursor = db.rawQuery(query, null);
            // cursor = db.query(DatabaseHelper.FOLDER_TABLE,null, whereclause, whereargs,null,null,null);
        }
        assert cursor != null;
        cursor.moveToFirst();

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                enter_service_title_tiet.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_SERVICE_NAME)));
                passwordDetailsTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_SERVICE_NAME)));
                enter_login_tiet.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_LOGIN)));
                enter_details_tiet.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_DESCRIPTION)));
                enter_password_tiet.setText(passwordDecrypt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_PASSWORD))));

                setSelectedFolderSpinner(cursor.getString(cursor.getColumnIndexOrThrow(FOLDER_COLUMN_FOLDER_NAME)));

                cursor.moveToNext();
            }

            cursor.close();
        }
    }

    private String passwordDecrypt(String str) {
        AES aes = new AES(getMasterKeyFromDatabase());
        return aes.decrypt(str);
    }

    private String getMasterKeyFromDatabase() {
        String mk = "";
        String query = "select " + DatabaseHelper.SETTINGS_COLUMN_MASTER_KEY+ " from " + DatabaseHelper.SETTINGS_TABLE;

        Cursor cursor = null;
        if (db !=null)
        {
            cursor = db.rawQuery(query, null);
        }

        assert cursor != null;
        cursor.moveToFirst();

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                mk = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SETTINGS_COLUMN_MASTER_KEY));

                cursor.moveToNext();
            }

            cursor.close();
        }

        return mk;
    }

    private String passwordEncrypt(String str) {
        AES aes = new AES(getMasterKeyFromDatabase());
        return aes.encrypt(str);
    }

    public void updatePasswordNote(Integer Id) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(PN_COLUMN_SERVICE_NAME, Objects.requireNonNull(enter_service_title_tiet.getText()).toString().trim());
            cv.put(PN_COLUMN_LOGIN, Objects.requireNonNull(enter_login_tiet.getText()).toString().trim());
            cv.put(PN_COLUMN_PASSWORD, passwordEncrypt(Objects.requireNonNull(enter_password_tiet.getText()).toString().trim()));
            cv.put(PN_COLUMN_DESCRIPTION, Objects.requireNonNull(enter_details_tiet.getText()).toString().trim());
            cv.put(PN_COLUMN_FOLDER_ID, AddPasswordFragment.findFolderId(spinnerFolders.getSelectedItem().toString(), db));

            cv.put(PN_COLUMN_UPDATED, DateFormatter.currentDate());
            cv.put(PN_COLUMN_TAG_ID, TAG_ID);

            Uri uri = ContentUris.withAppendedId(PASSWORD_NOTE_URI, Id);
            int rowCount = applicationContext.getContentResolver().update(uri, cv, null, null);
            AppLogs.log(applicationContext, log_tag, "Password updated");
            goHome();
        } catch (Exception e){
            Log.d(log_tag, "error: " + e.getMessage());
        }
    }

    public void goHome(){
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, new PasswordNotesFragment());
        fragmentTransaction.commit();
    }

    public void deletePasswordNote(Integer Id) {
        Uri uri = ContentUris.withAppendedId(PASSWORD_NOTE_URI, Id);
        int rowCount = applicationContext.getContentResolver().delete(uri, null, null);

        AppLogs.log(applicationContext, log_tag, "Password deleted");

        goHome();
    }
}
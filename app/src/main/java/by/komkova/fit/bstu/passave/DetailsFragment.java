package by.komkova.fit.bstu.passave;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;

public class DetailsFragment extends Fragment {
    private String log_tag = DetailsFragment.class.getName();
    private Context applicationContext;

    private Integer Id;
    private TextInputEditText enter_service_title_tiet, enter_login_tiet, enter_details_tiet, enter_password_tiet;
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
            setPasswordNoteData(bundleArgument.getInt("password_note_id"));
        }
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
}
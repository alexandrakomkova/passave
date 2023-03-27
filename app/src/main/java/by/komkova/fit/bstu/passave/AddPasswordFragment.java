package by.komkova.fit.bstu.passave;

import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_CREATED;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_DESCRIPTION;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_LOGIN;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_PASSWORD;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_SERVICE_NAME;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.PasswordNoteProvider.PASSWORD_NOTE_URI;

import android.content.ContentValues;
import android.content.Context;
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

public class AddPasswordFragment extends Fragment {

    final String log_tag = getClass().getName();
    private Button generate_password_btn, save_password_btn;
    private TextInputEditText enter_password_tiet, enter_login_tiet, enter_details_tiet, enter_service_title_tiet;
    private Context applicationContext;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    private String service_name = "";
    private String login = "";
    private String description = "";
    private String entered_password = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_password, container, false);

        applicationContext = MainActivity.getContextOfApplication();

        enter_service_title_tiet = view.findViewById(R.id.enter_service_title_field);
        enter_password_tiet = view.findViewById(R.id.enter_password_field);
        enter_login_tiet = view.findViewById(R.id.enter_login_field);
        enter_details_tiet = view.findViewById(R.id.enter_details_field);

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

        generate_password_btn = view.findViewById(R.id.generate_password_btn);
        generate_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneratePasswordFragment generatePasswordFragment = new GeneratePasswordFragment();
                Bundle bundle = new Bundle();
                bundle.putString("generated_password", String.valueOf(enter_password_tiet.getText()));

                generatePasswordFragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_layout,  generatePasswordFragment).commit();

//                FragmentTransaction fragmentTransaction = getActivity()
//                        .getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_layout, new GeneratePasswordFragment());
//                fragmentTransaction.commit();
            }
        });

        Bundle bundleArgument = getArguments();
        if (bundleArgument != null) {
            String recieveInfo = bundleArgument.getString("generated_password");
            enter_password_tiet.setText(recieveInfo);
        }

        databaseHelper = new DatabaseHelper(getActivity());
        db = databaseHelper.getWritableDatabase();

        save_password_btn = view.findViewById(R.id.save_password_btn);
        save_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePasswordNote(view);
            }
        });



        return view;
    }
    public void validatePasswordNote(View v) {
        addPasswordNote(v);
    }

    public void addPasswordNote(View v) {
        ContentValues cv = new ContentValues();

        cv.put(PN_COLUMN_SERVICE_NAME, enter_service_title_tiet.getText().toString().trim());
        cv.put(PN_COLUMN_LOGIN, enter_login_tiet.getText().toString().trim());
        cv.put(PN_COLUMN_PASSWORD, enter_password_tiet.getText().toString().trim());
        cv.put(PN_COLUMN_DESCRIPTION, enter_details_tiet.getText().toString().trim());

//        Date c = Calendar.getInstance().getTime();
//        Log.d(log_tag, "Current time => " + c); // Fri Mar 24 09:36:55 GMT+00:00 2023
//        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
//        Log.d(log_tag, df.format(c)); // 24-Mar-2023

        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        cv.put(PN_COLUMN_CREATED, df.format(currentDate));
        cv.put(PN_COLUMN_UPDATED, df.format(currentDate));

        // temporary(or not) without folder_id and favourite

        Uri res =  applicationContext.getContentResolver().insert(PASSWORD_NOTE_URI, cv);

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
        service_name = enter_service_title_tiet.getText().toString().trim();
        login = enter_login_tiet.getText().toString().trim();
        description = enter_details_tiet.getText().toString().trim();
        entered_password = enter_password_tiet.getText().toString().trim();

        outState.putString("service_name", service_name);
        outState.putString("login", login);
        outState.putString("description", description);
        outState.putString("entered_password", entered_password);

        Log.d(log_tag, service_name);
    }

}
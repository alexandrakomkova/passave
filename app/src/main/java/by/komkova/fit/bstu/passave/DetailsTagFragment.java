package by.komkova.fit.bstu.passave;

import static by.komkova.fit.bstu.passave.DatabaseHelper.TAG_COLUMN_TAG_NAME;
import static by.komkova.fit.bstu.passave.DatabaseHelper.TAG_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.FolderProvider.FOLDER_URI;
import static by.komkova.fit.bstu.passave.TagProvider.TAG_URI;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
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

public class DetailsTagFragment extends Fragment {
    final String log_tag = getClass().getName();
    private Button update_tag_btn, delete_tag_btn;
    private TextInputEditText enter_tag_name_field;
    private Context applicationContext;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    private String tag_name = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details_tag, container, false);

        applicationContext = getActivity();
        databaseHelper = new DatabaseHelper(applicationContext);
        db = databaseHelper.getReadableDatabase();

        enter_tag_name_field = view.findViewById(R.id.enter_tag_name_field);

        if (savedInstanceState != null) {
            tag_name = savedInstanceState.getString("tag_name");
            enter_tag_name_field.setText(tag_name);
        }

        Bundle bundleArgument = getArguments();
        if (bundleArgument != null) {
            // AppLogs.log(applicationContext, log_tag, String.valueOf(bundleArgument.getInt("tag_id")));
            setTagData(bundleArgument.getInt("tag_id"));
        }

        update_tag_btn = view.findViewById(R.id.update_tag_btn);
        update_tag_btn.setOnClickListener(view12 -> validateTag(bundleArgument.getInt("tag_id")));


        delete_tag_btn = view.findViewById(R.id.delete_tag_btn);
        delete_tag_btn.setOnClickListener(view1 -> deleteTag(bundleArgument.getInt("tag_id")));

        return view;
    }
    private void setTagData(Integer Id) {
        String query = "select * from " + DatabaseHelper.TAG_TABLE + " where "+ DatabaseHelper.TAG_COLUMN_ID + " = "+ String.valueOf(Id);
        db = databaseHelper.getReadableDatabase();

        Cursor cursor= null;
        if(db !=null)
        {
            cursor = db.rawQuery(query, null);
        }
        assert cursor != null;
        cursor.moveToFirst();

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                enter_tag_name_field.setText(cursor.getString(cursor.getColumnIndexOrThrow(TAG_COLUMN_TAG_NAME)));

                cursor.moveToNext();
            }

            cursor.close();
        }
    }

    public void validateTag(Integer Id)
    {
        if (enter_tag_name_field.getText().toString().trim().isEmpty()) {
            AppLogs.log(applicationContext, log_tag ,"Please enter tag name");
        } else {  updateTag(Id); }
    }

    public void updateTag(Integer Id) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(TAG_COLUMN_TAG_NAME, enter_tag_name_field.getText().toString().trim());
            cv.put(TAG_COLUMN_UPDATED, DateFormatter.currentDate());

            Uri uri = ContentUris.withAppendedId(TAG_URI, Id);
            int rowCount = applicationContext.getContentResolver().update(uri, cv, null, null);

            AppLogs.log(applicationContext, log_tag ,"Tag updated");
            goHome();
        } catch (Exception e){
            Log.d(log_tag, "error: " + e.getMessage());
        }
    }
    public void deleteTag(Integer Id) {
        Uri uri = ContentUris.withAppendedId(TAG_URI, Id);
        int rowCount = applicationContext.getContentResolver().delete(uri, null, null);

        AppLogs.log(applicationContext, log_tag ,"Tag deleted");
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
        tag_name = enter_tag_name_field.getText().toString().trim();

        outState.putString("tag_name", tag_name);
    }



}
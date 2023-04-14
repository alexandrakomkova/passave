package by.komkova.fit.bstu.passave;

import static by.komkova.fit.bstu.passave.DatabaseHelper.FOLDER_COLUMN_FOLDER_NAME;
import static by.komkova.fit.bstu.passave.DatabaseHelper.FOLDER_COLUMN_TAG_ID;
import static by.komkova.fit.bstu.passave.DatabaseHelper.FOLDER_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.FolderProvider.FOLDER_URI;
import static by.komkova.fit.bstu.passave.MainActivity.TAG_ID;

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

public class DetailsFolderFragment extends Fragment {
    final String log_tag = getClass().getName();
    private Button update_folder_btn, delete_folder_btn;
    private TextInputEditText enter_folder_title_tiet;
    private Context applicationContext;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    private String folder_title = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details_folder, container, false);
        applicationContext = MainActivity.getContextOfApplication();
        databaseHelper = new DatabaseHelper(applicationContext);
        db = databaseHelper.getReadableDatabase();


        enter_folder_title_tiet = view.findViewById(R.id.enter_folder_title_field);

        if (savedInstanceState != null) {
            folder_title = savedInstanceState.getString("folder_title");
            enter_folder_title_tiet.setText(folder_title);
        }

        Bundle bundleArgument = getArguments();
        if (bundleArgument != null) {
            setFolderData(bundleArgument.getInt("folder_id"));
        }

        update_folder_btn = view.findViewById(R.id.update_folder_btn);
        update_folder_btn.setOnClickListener(view12 -> validateFolder(bundleArgument.getInt("folder_id")));

        delete_folder_btn = view.findViewById(R.id.delete_folder_btn);
        delete_folder_btn.setOnClickListener(view1 -> deleteFolder(bundleArgument.getInt("folder_id")));

        return view;
    }
    private void setFolderData(Integer Id) {
        String query = "select * from " + databaseHelper.FOLDER_TABLE + " where "+databaseHelper.FOLDER_COLUMN_ID + " = "+ String.valueOf(Id);
        db = databaseHelper.getReadableDatabase();

        Cursor cursor= null;
        if(db !=null)
        {
            cursor = db.rawQuery(query, null);
        }
        cursor.moveToFirst();

        if(cursor!=null && cursor.getCount()!=0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                enter_folder_title_tiet.setText(cursor.getString(cursor.getColumnIndexOrThrow(FOLDER_COLUMN_FOLDER_NAME)));

                cursor.moveToNext();
            }

            cursor.close();
        }
    }

    public void validateFolder(Integer Id)
    {
        if (enter_folder_title_tiet.getText().toString().trim().isEmpty()) {
            AppLogs.log(applicationContext, log_tag ,"Please enter folder name");
        } else {  updateFolder(Id); }
    }

    public void updateFolder(Integer Id) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(FOLDER_COLUMN_FOLDER_NAME, enter_folder_title_tiet.getText().toString().trim());

            Date currentDate = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            cv.put(FOLDER_COLUMN_UPDATED, df.format(currentDate));
            cv.put(FOLDER_COLUMN_TAG_ID, TAG_ID);

            Uri uri = ContentUris.withAppendedId(FOLDER_URI, Id);
            int rowCount = applicationContext.getContentResolver().update(uri, cv, null, null);

            Log.d(log_tag, "folder updated");
            AppLogs.log(applicationContext, log_tag ,"Folder updated");
            goHome();
        } catch (Exception e){
            Log.d(log_tag, "error: " + e.getMessage());
        }
    }

    public void deleteFolder(Integer Id) {
        Uri uri = ContentUris.withAppendedId(FOLDER_URI, Id);
        int rowCount = applicationContext.getContentResolver().delete(uri, null, null);

        AppLogs.log(applicationContext, log_tag ,"Folder deleted");
        goHome();
    }

    public void goHome(){
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, new PasswordNotesFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("folder_title", enter_folder_title_tiet.getText().toString().trim());
    }
}
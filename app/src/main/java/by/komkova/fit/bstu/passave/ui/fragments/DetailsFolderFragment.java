package by.komkova.fit.bstu.passave.ui.fragments;

import static by.komkova.fit.bstu.passave.db.DatabaseHelper.FOLDER_COLUMN_FOLDER_NAME;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.FOLDER_COLUMN_ID;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.FOLDER_COLUMN_TAG_ID;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.FOLDER_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.db.providers.FolderProvider.FOLDER_URI;
import static by.komkova.fit.bstu.passave.ui.activities.MainActivity.TAG_ID;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import by.komkova.fit.bstu.passave.helpers.AppLogs;
import by.komkova.fit.bstu.passave.helpers.DateFormatter;
import by.komkova.fit.bstu.passave.R;
import by.komkova.fit.bstu.passave.db.DatabaseHelper;

public class DetailsFolderFragment extends Fragment {
    final String log_tag = getClass().getName();
    private TextInputEditText enter_folder_title_tiet;
    private Context applicationContext;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details_folder, container, false);
        applicationContext =  getActivity();
        databaseHelper = new DatabaseHelper(applicationContext);
        db = databaseHelper.getReadableDatabase();


        enter_folder_title_tiet = view.findViewById(R.id.enter_folder_title_field);

        if (savedInstanceState != null) {
            String folder_title = savedInstanceState.getString("folder_title");
            enter_folder_title_tiet.setText(folder_title);
        }

        Bundle bundleArgument = getArguments();
        if (bundleArgument != null) {
            setFolderData(bundleArgument.getInt("folder_id"));
        }

        Button update_folder_btn = view.findViewById(R.id.update_folder_btn);
        update_folder_btn.setOnClickListener(view12 -> validateFolder(bundleArgument.getInt("folder_id")));

        Button delete_folder_btn = view.findViewById(R.id.delete_folder_btn);
        delete_folder_btn.setOnClickListener(view1 -> showWarningDialog(view, bundleArgument.getInt("folder_id")));

        return view;
    }

    private void showWarningDialog(View view, Integer Id) {
        ConstraintLayout constraintLayout = view.findViewById(R.id.errorLayout);
        View v = LayoutInflater.from(applicationContext).inflate(R.layout.error_ok_cancel_dialog, constraintLayout);
        Button errorClose = v.findViewById(R.id.errorCloseButton);
        Button errorOkay = v.findViewById(R.id.errorOkayButton);

        TextView errorDescription = v.findViewById(R.id.errorDescription);
        errorDescription.setText(R.string.folder_delete);

        AlertDialog.Builder builder = new AlertDialog.Builder(applicationContext);
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        errorClose.findViewById(R.id.errorCloseButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        errorOkay.findViewById(R.id.errorOkayButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                deleteFolder(Id);
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void setFolderData(Integer Id) {
        String whereclause = FOLDER_COLUMN_ID + "=?";
        String[] whereargs = new String[]{ String.valueOf(Id) };
       //  Cursor c1 = db.query(DatabaseHelper.FOLDER_TABLE,null, whereclause, whereargs,null,null,null);

        // String query = "select * from " + databaseHelper.FOLDER_TABLE + " where "+databaseHelper.FOLDER_TABLE + " = "+ String.valueOf(Id);
        db = databaseHelper.getReadableDatabase();

        Cursor cursor= null;
        if(db !=null)
        {
            // cursor = db.rawQuery(query, null);
            cursor = db.query(DatabaseHelper.FOLDER_TABLE,null, whereclause, whereargs,null,null,null);
        }
        assert cursor != null;
        cursor.moveToFirst();

        if(cursor.getCount() != 0) {
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
        if (Objects.requireNonNull(enter_folder_title_tiet.getText()).toString().trim().isEmpty()) {
            AppLogs.log(applicationContext, log_tag ,"Please enter folder name");
        } else {  updateFolder(Id); }
    }

    public void updateFolder(Integer Id) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(FOLDER_COLUMN_FOLDER_NAME, enter_folder_title_tiet.getText().toString().trim());

            cv.put(FOLDER_COLUMN_UPDATED, DateFormatter.currentDate());
            cv.put(FOLDER_COLUMN_TAG_ID, TAG_ID);

            Uri uri = ContentUris.withAppendedId(FOLDER_URI, Id);
            int rowCount = applicationContext.getContentResolver().update(uri, cv, null, null);

            // Log.d(log_tag, "folder updated");
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("folder_title", Objects.requireNonNull(enter_folder_title_tiet.getText()).toString().trim());
    }
}
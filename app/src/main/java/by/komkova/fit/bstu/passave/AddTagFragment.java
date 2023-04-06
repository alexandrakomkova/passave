package by.komkova.fit.bstu.passave;

import static by.komkova.fit.bstu.passave.DatabaseHelper.FOLDER_COLUMN_FOLDER_NAME;
import static by.komkova.fit.bstu.passave.DatabaseHelper.FOLDER_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.DatabaseHelper.TAG_COLUMN_TAG_NAME;
import static by.komkova.fit.bstu.passave.FolderProvider.FOLDER_URI;
import static by.komkova.fit.bstu.passave.TagProvider.TAG_URI;

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

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTagFragment extends Fragment {

    final String log_tag = getClass().getName();
    private Button save_tag_btn;
    private TextInputEditText enter_tag_name_field;
    private Context applicationContext;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    private String tag_name = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_tag, container, false);

        applicationContext = MainActivity.getContextOfApplication();

        enter_tag_name_field = view.findViewById(R.id.enter_tag_name_field);

        if (savedInstanceState != null) {
            tag_name = savedInstanceState.getString("tag_name");
            enter_tag_name_field.setText(tag_name);
        }

        save_tag_btn = view.findViewById(R.id.save_tag_btn);
        save_tag_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateTag(view);
            }
        });

        return view;
    }

    public void validateTag(View v)
    {
        if (enter_tag_name_field.getText().toString().trim().isEmpty()) {
            AppLogs.log(applicationContext, log_tag ,"Please enter tag name");
        } else {  addTag(v); }
    }

    public void addTag(View v) {
        ContentValues cv = new ContentValues();

        cv.put(TAG_COLUMN_TAG_NAME, enter_tag_name_field.getText().toString().trim());

//        Date currentDate = Calendar.getInstance().getTime();
//        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
//        cv.put(TAG_COLUMN_UPDATED, df.format(currentDate));

        Uri res =  applicationContext.getContentResolver().insert(TAG_URI, cv);

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
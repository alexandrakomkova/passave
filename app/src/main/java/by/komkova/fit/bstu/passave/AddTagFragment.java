package by.komkova.fit.bstu.passave;

import static by.komkova.fit.bstu.passave.DatabaseHelper.TAG_COLUMN_TAG_NAME;
import static by.komkova.fit.bstu.passave.DatabaseHelper.TAG_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.TagProvider.TAG_URI;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class AddTagFragment extends Fragment {

    final String log_tag = getClass().getName();
    private TextInputEditText enter_tag_name_field;
    private Context applicationContext;

    private String tag_name = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_tag, container, false);

        applicationContext =  getActivity();

        enter_tag_name_field = view.findViewById(R.id.enter_tag_name_field);

        if (savedInstanceState != null) {
            tag_name = savedInstanceState.getString("tag_name");
            enter_tag_name_field.setText(tag_name);
        }

        Button save_tag_btn = view.findViewById(R.id.save_tag_btn);
        save_tag_btn.setOnClickListener(this::validateTag);

        return view;
    }

    public void validateTag(View v)
    {
        if (Objects.requireNonNull(enter_tag_name_field.getText()).toString().trim().isEmpty()) {
            AppLogs.log(applicationContext, log_tag ,"Please enter tag name");
        } else {  addTag(); }
    }

    public void addTag() {
        ContentValues cv = new ContentValues();

        cv.put(TAG_COLUMN_TAG_NAME, Objects.requireNonNull(enter_tag_name_field.getText()).toString().trim());
        cv.put(TAG_COLUMN_UPDATED, DateFormatter.currentDate());

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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        tag_name = Objects.requireNonNull(enter_tag_name_field.getText()).toString().trim();

        outState.putString("tag_name", tag_name);
    }
}
package by.komkova.fit.bstu.passave;

import android.database.sqlite.SQLiteDatabase;
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

public class AddPasswordFragment extends Fragment {

    final String log_tag = getClass().getName();
    private Button generate_password_btn;
    private TextInputEditText enter_password_tiet;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_password, container, false);

        enter_password_tiet = view.findViewById(R.id.enter_password_field);

        generate_password_btn = view.findViewById(R.id.generate_password_btn);
        generate_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout, new GeneratePasswordFragment());
                fragmentTransaction.commit();
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            String recieveInfo = bundle.getString("generated_password");
            enter_password_tiet.setText(recieveInfo);
        }

        databaseHelper = new DatabaseHelper(getActivity());
        db = databaseHelper.getWritableDatabase();


        return view;
    }
}
package by.komkova.fit.bstu.passave;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class SettingsLanguagesFragment extends Fragment{

    final String log_tag = getClass().getName();
    ImageButton backSettings_ibtn;
    private Button save_settings_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_languages, container, false);

        backSettings_ibtn = view.findViewById(R.id.backSettings_btn);
        backSettings_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout, new SettingsFragment());
                fragmentTransaction.commit();

                // Log.d(log_tag, "TAP");
            }
        });

        save_settings_btn = view.findViewById(R.id.save_languages_btn);
        save_settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguages();
            }
        });



        return view;
    }

    private void changeLanguages() {
    }

}
package by.komkova.fit.bstu.passave;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class GeneratePasswordFragment extends Fragment {

    private Button ok_btn, generate_password_btn;
    private TextInputEditText generated_password_tiet;
    private PasswordStrength passwordStrength;
    private TextView passwordStrengthTextView,password_length_value_tv;
    private SeekBar password_length_seekBar;
    private CheckBox caps_letters_checkbox, down_letters_checkbox, numbers_checkbox, special_symbols_checkbox;

    private String generated_password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_generate_password, container, false);

        generated_password_tiet = view.findViewById(R.id.generated_password_value);
        passwordStrengthTextView = view.findViewById(R.id.password_strength_label);
        password_length_value_tv = view.findViewById(R.id.password_length_value);

        if(savedInstanceState != null) {
            generated_password = savedInstanceState.getString("generated_password");
            generated_password_tiet.setText(generated_password);
        }

        Bundle bundleArgument = getArguments();
        if (bundleArgument != null) {
            String recieveInfo = bundleArgument.getString("generated_password");
            generated_password_tiet.setText(recieveInfo);
        }

        password_length_seekBar = view.findViewById(R.id.password_length_seekBar);
        password_length_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                password_length_value_tv.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ok_btn = view.findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(view1 -> {
            if (bundleArgument.getInt("isEdit") > 0 ) {
                DetailsPasswordFragment detailsFragment = new DetailsPasswordFragment();
                bundleArgument.putString("generated_password", String.valueOf(generated_password_tiet.getText()));
                bundleArgument.putInt("isEdit", 1);

                detailsFragment.setArguments(bundleArgument);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_layout,  detailsFragment).commit();
            } else {
                AddPasswordFragment addPasswordFragment = new AddPasswordFragment();
                bundleArgument.putString("generated_password", String.valueOf(generated_password_tiet.getText()));

                addPasswordFragment.setArguments(bundleArgument);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_layout,  addPasswordFragment).commit();
            }
        });

        generated_password_tiet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculatePasswordStrength(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        caps_letters_checkbox = view.findViewById(R.id.caps_letters_checkbox);
        down_letters_checkbox = view.findViewById(R.id.down_letters_checkbox);
        numbers_checkbox = view.findViewById(R.id.numbers_checkbox);
        special_symbols_checkbox = view.findViewById(R.id.special_symbols_checkbox);

        generate_password_btn = view.findViewById(R.id.generate_password_btn);
        generate_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                generatePassword(
                        Integer.valueOf((String) password_length_value_tv.getText()),
                        caps_letters_checkbox.isChecked(),
                        down_letters_checkbox.isChecked(),
                        numbers_checkbox.isChecked(),
                        special_symbols_checkbox.isChecked()
                );
            }
        });


        return view;
    }

    private void calculatePasswordStrength(String str) {
        passwordStrength = PasswordStrength.calculate(str);
        passwordStrengthTextView.setText(passwordStrength.msg);
        passwordStrengthTextView.setTextColor(getResources().getColor(passwordStrength.color));

    }

    private void generatePassword(Integer length, boolean includeUpperCaseLetters, boolean includeDownLetters, boolean includeSymbols, boolean includeNumbers) {
        PasswordGenerator obj = new PasswordGenerator(length,             // To specify password length
                includeUpperCaseLetters,                                         // To include upper case Letters
                includeDownLetters,                                       // To include lower case Letters
                includeNumbers,                                       // To include secial symbols
                includeSymbols);

        String generatedPassword = obj.generatePassword();
        generated_password_tiet.setText(generatedPassword);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("generated_password", generated_password);
    }
}
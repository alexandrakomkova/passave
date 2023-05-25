package by.komkova.fit.bstu.passave.ui.fragments;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import by.komkova.fit.bstu.passave.helpers.AppLogs;
import by.komkova.fit.bstu.passave.helpers.LocaleChanger;
import by.komkova.fit.bstu.passave.security.password_helpers.PasswordGenerator;
import by.komkova.fit.bstu.passave.security.password_helpers.PasswordStrength;
import by.komkova.fit.bstu.passave.R;
import by.komkova.fit.bstu.passave.ui.activities.MainActivity;

public class GeneratePasswordFragment extends Fragment {

    private TextInputEditText generated_password_tiet;
    private TextView passwordStrengthTextView,password_length_value_tv;
    private CheckBox caps_letters_checkbox, down_letters_checkbox, numbers_checkbox, special_symbols_checkbox;

    private String generated_password;
    private Context applicationContext;
    final String log_tag = getClass().getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_generate_password, container, false);

        applicationContext = getActivity();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String languageValue = sharedPreferences.getString("language", "en");
        LocaleChanger.changeLocale(languageValue, applicationContext);

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

        SeekBar password_length_seekBar = view.findViewById(R.id.password_length_seekBar);
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

        Button ok_btn = view.findViewById(R.id.ok_btn);
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

        Button generate_password_btn = view.findViewById(R.id.generate_password_btn);
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

        ImageButton copy_content_button = view.findViewById(R.id.copy_content_button);
        copy_content_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", generated_password_tiet.getText());
                manager.setPrimaryClip(clipData);

                AppLogs.log(applicationContext, log_tag, getResources().getString(R.string.text_copied));
            }
        });


        return view;
    }

    private void calculatePasswordStrength(String str) {
        PasswordStrength passwordStrength = PasswordStrength.calculate(str);
        passwordStrengthTextView.setText(passwordStrength.msg);
        passwordStrengthTextView.setTextColor(getResources().getColor(passwordStrength.color));

    }

    private void generatePassword(Integer length, boolean includeUpperCaseLetters, boolean includeDownLetters, boolean includeSymbols, boolean includeNumbers) {
        if (!includeUpperCaseLetters
                && !includeDownLetters
                && !includeSymbols
        && !includeNumbers) {
            includeUpperCaseLetters = true;
            includeDownLetters = true;
            includeSymbols = true;
            includeNumbers = true;

            caps_letters_checkbox.setChecked(true);
            down_letters_checkbox.setChecked(true);
            numbers_checkbox.setChecked(true);
            special_symbols_checkbox.setChecked(true);

            AppLogs.log(getActivity().getApplicationContext(), log_tag, getResources().getString(R.string.basic_conf_for_generate_password));
        }


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
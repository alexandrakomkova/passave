package by.komkova.fit.bstu.passave;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class GeneratePasswordFragment extends Fragment {

    private Button ok_btn;
    private TextInputEditText generated_password_tiet;
    private PasswordStrength passwordStrength;
    private TextView passwordStrengthTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_generate_password, container, false);

        generated_password_tiet = view.findViewById(R.id.generated_password_value);
        passwordStrengthTextView = view.findViewById(R.id.password_strength_label);

        ok_btn = view.findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPasswordFragment addPasswordFragment = new AddPasswordFragment();
                Bundle bundle = new Bundle();
                bundle.putString("generated_password", String.valueOf(generated_password_tiet.getText()));
                addPasswordFragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_layout,  addPasswordFragment).commit();

//                FragmentTransaction fragmentTransaction = getActivity()
//                        .getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_layout, new AddPasswordFragment());
//                fragmentTransaction.commit();
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



        return view;
    }

    private void calculatePasswordStrength(String str) {
        passwordStrength = PasswordStrength.calculate(str);
        passwordStrengthTextView.setText(passwordStrength.msg);
        passwordStrengthTextView.setTextColor(getResources().getColor(passwordStrength.color));

    }
}
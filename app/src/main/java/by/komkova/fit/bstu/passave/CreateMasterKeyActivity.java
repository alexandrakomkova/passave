package by.komkova.fit.bstu.passave;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class CreateMasterKeyActivity extends Activity {

    static final String log_tag = "CreateMasterKeyActivity";

    private TextInputEditText textMasterKey;
    private TextView passwordStrengthTextView;
    private ImageButton back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mk);

        textMasterKey = findViewById(R.id.enter_masterkey_field);
        passwordStrengthTextView = findViewById(R.id.password_strength_label);
        back_btn = findViewById(R.id.back_btn);

        textMasterKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculatePasswordStrength(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Log.d(log_tag, editable.toString());
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                goToLoginActivity(v);
            }
        });


        // Log.d(log_tag, text);

    }

    private void calculatePasswordStrength(String str) {
        PasswordStrength passwordStrength = PasswordStrength.calculate(str);
        passwordStrengthTextView.setText(passwordStrength.msg);
        passwordStrengthTextView.setTextColor(getResources().getColor(passwordStrength.color));

    }

    private void goToLoginActivity(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}

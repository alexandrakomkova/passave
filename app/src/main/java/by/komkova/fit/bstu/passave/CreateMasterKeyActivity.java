package by.komkova.fit.bstu.passave;

import static android.content.res.Resources.getSystem;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class CreateMasterKeyActivity extends Activity {

    final String log_tag = getClass().getName();

    private TextInputEditText enterMasterKey_tiet, repeatMasterKey_tiet;
    private TextView passwordStrengthTextView;
    private ImageButton back_btn;
    private Button createMk_btn;
    private PasswordStrength passwordStrength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mk);

        enterMasterKey_tiet = findViewById(R.id.enter_masterkey_field);
        repeatMasterKey_tiet = findViewById(R.id.repeat_masterkey_field);

        passwordStrengthTextView = findViewById(R.id.password_strength_label);
        back_btn = findViewById(R.id.back_btn);
        createMk_btn = findViewById(R.id.create_mk_btn);

        enterMasterKey_tiet.addTextChangedListener(new TextWatcher() {
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

        back_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                goToLoginActivity(v);
            }
        });

        createMk_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                validatePassword();
            }
        });
    }

    private void calculatePasswordStrength(String str) {
        passwordStrength = PasswordStrength.calculate(str);
        passwordStrengthTextView.setText(passwordStrength.msg);
        passwordStrengthTextView.setTextColor(getResources().getColor(passwordStrength.color));

    }

    private void goToLoginActivity(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private boolean eqlPasswords(String password1, String password2){
        if(password1.isEmpty() && password2.isEmpty()) { return false; }

        return password1.equals(password2);
    }

    private void validatePassword(){
        if (eqlPasswords(String.valueOf(enterMasterKey_tiet.getText()), String.valueOf(repeatMasterKey_tiet.getText()))){
            switch (passwordStrength.msg){
                case R.string.weak: AppLogs.log(CreateMasterKeyActivity.this, log_tag, "master key is too weak"); break;
                case R.string.medium: AppLogs.log(CreateMasterKeyActivity.this, log_tag, "master key is medium, please make it strong"); break;
                case R.string.strong:
                case R.string.very_strong: AppLogs.log(CreateMasterKeyActivity.this, log_tag, "master key created"); break;
                default:
            }
        } else {
            AppLogs.log(CreateMasterKeyActivity.this, log_tag, "passwords not matching");
        }
    }
}

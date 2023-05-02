package by.komkova.fit.bstu.passave;

import static by.komkova.fit.bstu.passave.DatabaseHelper.SETTINGS_COLUMN_CREATED;
import static by.komkova.fit.bstu.passave.DatabaseHelper.SETTINGS_COLUMN_MASTER_KEY;
import static by.komkova.fit.bstu.passave.DatabaseHelper.SETTINGS_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.DatabaseHelper.SETTINGS_TABLE;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class CreateMasterKeyActivity extends Activity {

    final String log_tag = getClass().getName();

    private TextInputEditText enterMasterKey_tiet, repeatMasterKey_tiet;
    private TextView passwordStrengthTextView;
    private PasswordStrength passwordStrength;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mk);

        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();

        enterMasterKey_tiet = findViewById(R.id.enter_masterkey_field);
        repeatMasterKey_tiet = findViewById(R.id.repeat_masterkey_field);

        passwordStrengthTextView = findViewById(R.id.password_strength_label);
        ImageButton back_btn = findViewById(R.id.back_btn);
        Button createMk_btn = findViewById(R.id.create_mk_btn);

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

        back_btn.setOnClickListener(v -> goToLoginActivity());

        createMk_btn.setOnClickListener(v -> validatePassword());
    }

    private void calculatePasswordStrength(String str) {
        passwordStrength = PasswordStrength.calculate(str);
        passwordStrengthTextView.setText(passwordStrength.msg);
        passwordStrengthTextView.setTextColor(getResources().getColor(passwordStrength.color));

    }

    private void goToLoginActivity() {
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
                case R.string.very_strong:
                    saveMkToDatabase(); break;
                default:
                    AppLogs.log(CreateMasterKeyActivity.this, log_tag, "something goes wrong");
            }
        } else {
            AppLogs.log(CreateMasterKeyActivity.this, log_tag, "passwords not matching");
        }
    }

    private void saveMkToDatabase() {
        String query = "select " + DatabaseHelper.SETTINGS_COLUMN_MASTER_KEY+ " from " + DatabaseHelper.SETTINGS_TABLE;

        int rowcount = 0;

        Cursor cursor = null;
        if (db !=null)
        {
            cursor = db.rawQuery(query, null);
            rowcount = cursor.getCount();
            // cursor.close();

            if (rowcount == 1) {
                AppLogs.log(CreateMasterKeyActivity.this, log_tag, "Master key already created");
                return;
            }
        }
        assert cursor != null;
        cursor.close();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SETTINGS_COLUMN_MASTER_KEY, MD5.md5Custom(Objects.requireNonNull(repeatMasterKey_tiet.getText()).toString().trim()));

        contentValues.put(SETTINGS_COLUMN_CREATED, DateFormatter.currentDate());
        contentValues.put(SETTINGS_COLUMN_UPDATED, DateFormatter.currentDate());

        long result = db.insert(SETTINGS_TABLE, null, contentValues);
        if (result == -1)
            AppLogs.log(CreateMasterKeyActivity.this, log_tag, "something went wrong");
        else
            AppLogs.log(CreateMasterKeyActivity.this, log_tag, "Master key created.");
            goLogin();
    }

    private void goLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}

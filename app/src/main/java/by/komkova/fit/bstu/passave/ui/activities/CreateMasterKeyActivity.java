package by.komkova.fit.bstu.passave.ui.activities;

import static by.komkova.fit.bstu.passave.db.DatabaseHelper.SETTINGS_COLUMN_CREATED;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.SETTINGS_COLUMN_MASTER_KEY;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.SETTINGS_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.SETTINGS_TABLE;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import by.komkova.fit.bstu.passave.helpers.AppLogs;
import by.komkova.fit.bstu.passave.helpers.LocaleChanger;
import by.komkova.fit.bstu.passave.ui.custom_dialog.CustomAlertDialogClass;
import by.komkova.fit.bstu.passave.helpers.DateFormatter;
import by.komkova.fit.bstu.passave.security.security_algorithms.SHA512;
import by.komkova.fit.bstu.passave.security.password_helpers.PasswordStrength;
import by.komkova.fit.bstu.passave.R;
import by.komkova.fit.bstu.passave.db.DatabaseHelper;

public class CreateMasterKeyActivity extends AppCompatActivity {

    final String log_tag = getClass().getName();

    private TextInputEditText enterMasterKey_tiet, repeatMasterKey_tiet;
    private TextView passwordStrengthTextView;
    private PasswordStrength passwordStrength;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    String mk = "";

    private SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mk);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightModeValue = sharedPreferences.getBoolean("night_mode", true);
        if (nightModeValue) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            AppLogs.log(this, log_tag, String.valueOf(true));
        }

        String languageValue = sharedPreferences.getString("language", "en");
        AppLogs.log(getApplicationContext(), log_tag, "language: " + String.valueOf(languageValue));
        LocaleChanger.changeLocale(languageValue, getApplicationContext());


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

        createMk_btn.setOnClickListener(v -> validatePassword(findViewById(android.R.id.content).getRootView()));
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

    private void validatePassword(View v){
        getMasterKeyFromDatabase();

        if(mk.isEmpty()) {
            if (eqlPasswords(String.valueOf(enterMasterKey_tiet.getText()), String.valueOf(repeatMasterKey_tiet.getText()))){
                switch (passwordStrength.msg){
                    case R.string.weak:
                        // AppLogs.log(CreateMasterKeyActivity.this, log_tag, "master key is too weak")
                        CustomAlertDialogClass.showWarningOkDialog(v, this, R.string.master_key_is_too_weak);
                        break;
                    case R.string.medium:
                        // AppLogs.log(CreateMasterKeyActivity.this, log_tag, "master key is medium, please make it strong");
                        CustomAlertDialogClass.showWarningOkDialog(v, this, R.string.master_key_is_medium);
                        break;
                    case R.string.strong:
                    case R.string.very_strong:
                        saveMkToDatabase(); break;
                    default:
                        // AppLogs.log(CreateMasterKeyActivity.this, log_tag, "something goes wrong");
                        CustomAlertDialogClass.showWarningOkDialog(v, this, R.string.error_details);
                        break;
                }
            } else {
                CustomAlertDialogClass.showWarningOkDialog(v, this, R.string.passwords_not_matching);
                // AppLogs.log(CreateMasterKeyActivity.this, log_tag, "passwords not matching");
            }
        } else {
            CustomAlertDialogClass.showWarningOkDialog(v, this, R.string.master_key_is_already_created);
        }
    }

    private void getMasterKeyFromDatabase() {
        String query = "select " + DatabaseHelper.SETTINGS_COLUMN_MASTER_KEY + " from " + DatabaseHelper.SETTINGS_TABLE;

        Cursor cursor = null;
        if (db !=null)
        {
            cursor = db.rawQuery(query, null);
        }

        assert cursor != null;
        cursor.moveToFirst();

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                mk = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SETTINGS_COLUMN_MASTER_KEY));

                cursor.moveToNext();
            }

            cursor.close();
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
                // AppLogs.log(CreateMasterKeyActivity.this, log_tag, "Master key already created");
                CustomAlertDialogClass.showWarningOkDialog(getCurrentFocus(), this, R.string.master_key_already_created);
                return;
            }
        }
        assert cursor != null;
        cursor.close();

        ContentValues contentValues = new ContentValues();
        // contentValues.put(SETTINGS_COLUMN_MASTER_KEY, MD5.md5Custom(Objects.requireNonNull(repeatMasterKey_tiet.getText()).toString().trim()));
        String mk = repeatMasterKey_tiet.getText().toString().trim() + DateFormatter.currentDate();
        // AppLogs.log(CreateMasterKeyActivity.this, log_tag, mk);

        contentValues.put(SETTINGS_COLUMN_MASTER_KEY, SHA512.sha512Custom(Objects.requireNonNull(mk)));
        contentValues.put(SETTINGS_COLUMN_CREATED, DateFormatter.currentDate());
        contentValues.put(SETTINGS_COLUMN_UPDATED, DateFormatter.currentDate());

        long result = db.insert(SETTINGS_TABLE, null, contentValues);
        if (result == -1)
            CustomAlertDialogClass.showWarningOkDialog(getCurrentFocus(), getApplicationContext(), R.string.error_details);
            // AppLogs.log(CreateMasterKeyActivity.this, log_tag, "something went wrong");
        else
            AppLogs.log(CreateMasterKeyActivity.this, log_tag, "Master key created.");
            goLogin();
    }

    private void goLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}

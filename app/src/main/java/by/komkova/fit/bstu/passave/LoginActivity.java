package by.komkova.fit.bstu.passave;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {
    final String log_tag = getClass().getName();

    private Button create_account_btn, login_btn;
    private TextView login_label;
    private TextInputEditText enter_masterkey_field;

    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;


    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    private String mk = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();

        // changeLocale();

        BiometricManager biometricManager = BiometricManager.from(this);
        switch(biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                AppLogs.log(getApplicationContext(), log_tag, "Device doesn't have fingerprint");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                AppLogs.log(getApplicationContext(), log_tag, "Not working");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                AppLogs.log(getApplicationContext(), log_tag, "No fingerprint assigned");
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                goTagActivity();
                AppLogs.log(LoginActivity.this, log_tag, "Login successful");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        };
        biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, callback);

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for Passave")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use master key")
                .build();

        if (getFingerprintFromDatabase() == 1) {
            biometricPrompt.authenticate(promptInfo);
        }

        create_account_btn = findViewById(R.id.create_account_btn);
        create_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goCreateMk();
            }
        });

        enter_masterkey_field = findViewById(R.id.enter_masterkey_field);
        login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enter_masterkey_field.getText().toString().trim().isEmpty()) {
                    AppLogs.log(LoginActivity.this, log_tag, "Please enter master key");
                } else {
                    getMasterKeyFromDatabase();
                    validatePassword();
                }
            }
        });
    }

    private void validatePassword() {
        if (mk.equals(MD5.md5Custom(enter_masterkey_field.getText().toString().trim()))) {
            goTagActivity();
            AppLogs.log(LoginActivity.this, log_tag, "Login successful");
        } else {
            AppLogs.log(LoginActivity.this, log_tag, "Wrong master key");
        }
    }

    private void getMasterKeyFromDatabase() {
        String query = "select " + DatabaseHelper.SETTINGS_COLUMN_MASTER_KEY+ " from " + DatabaseHelper.SETTINGS_TABLE;

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

    private int getFingerprintFromDatabase() {
        String query = "select " + DatabaseHelper.SETTINGS_COLUMN_FINGERPRINT + " from " + DatabaseHelper.SETTINGS_TABLE + " where " + DatabaseHelper.SETTINGS_COLUMN_ID + " = 1";

        Cursor cursor = null;
        if (db !=null)
        {
            cursor = db.rawQuery(query, null);
        }

        if (cursor == null){
            // AppLogs.log(getApplicationContext(), log_tag, "null");
            return 0;
        }

        cursor.moveToFirst();

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                // AppLogs.log(getApplicationContext(), log_tag, "f" + cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.SETTINGS_COLUMN_FINGERPRINT)));
                return cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.SETTINGS_COLUMN_FINGERPRINT));

                // cursor.moveToNext();
            }

            cursor.close();
        }
        return 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        changeLocale();
    }

    private void changeLocale() {
        Locale locale = new Locale(getLanguageFromDatabase());
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, null);
        // getApplicationContext().getResources().updateConfiguration(configuration, null);
    }

    private String getLanguageFromDatabase() {
        String query = "select " + DatabaseHelper.SETTINGS_COLUMN_LANGUAGE + " from " + DatabaseHelper.SETTINGS_TABLE + " where " + DatabaseHelper.SETTINGS_COLUMN_ID + " = 1";

        Cursor cursor = null;
        if (db !=null)
        {
            cursor = db.rawQuery(query, null);
        }

        if (cursor == null){
            return "en";
        }

        cursor.moveToFirst();

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                AppLogs.log(getApplicationContext(), log_tag, cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SETTINGS_COLUMN_LANGUAGE)));
                return cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SETTINGS_COLUMN_LANGUAGE));

                // cursor.moveToNext();
            }

            cursor.close();
        }
        return "en";
    }

    private void goCreateMk() {
        Intent intent = new Intent(this, CreateMasterKeyActivity.class);
        startActivity(intent);
    }

    private void goTagActivity() {
        Intent intent = new Intent(this, TagActivity.class);
        startActivity(intent);
    }
}

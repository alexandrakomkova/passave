package by.komkova.fit.bstu.passave.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.Executor;

import by.komkova.fit.bstu.passave.helpers.AppLogs;
import by.komkova.fit.bstu.passave.helpers.LocaleChanger;
import by.komkova.fit.bstu.passave.ui.custom_dialog.CustomAlertDialogClass;
import by.komkova.fit.bstu.passave.security.security_algorithms.SHA512;
import by.komkova.fit.bstu.passave.R;
import by.komkova.fit.bstu.passave.db.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {
    final String log_tag = getClass().getName();
    private static final int REQUEST_WRITE_STORAGE_REQUEST_CODE = 1111;
    private Button create_account_btn, login_btn;
    private TextView login_label;
    private TextInputEditText enter_masterkey_field;

    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;

    private SharedPreferences sharedPreferences = null;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    private String mk = "";
    private String mk_date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();

        // changeLocale();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightModeValue = sharedPreferences.getBoolean("night_mode", true);
        if (nightModeValue) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        String languageValue = sharedPreferences.getString("language", "en");
        // AppLogs.log(getApplicationContext(), log_tag, "language: " + String.valueOf(languageValue));
        LocaleChanger.changeLocale(languageValue, getApplicationContext());

        BiometricManager biometricManager = BiometricManager.from(this);
        switch(biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                // AppLogs.log(getApplicationContext(), log_tag, "Device doesn't have fingerprint");
                Log.d(log_tag, "Device doesn't have fingerprint");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                // AppLogs.log(getApplicationContext(), log_tag, "Not working");
                Log.d(log_tag, "Not working");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // AppLogs.log(getApplicationContext(), log_tag, "No fingerprint assigned");
                Log.d(log_tag, "No fingerprint assigned");
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
                AppLogs.log(LoginActivity.this, log_tag, getResources().getString(R.string.login_successful));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        };

        biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, callback);

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getResources().getString(R.string.biometric_dialog_title))
                .setSubtitle(getResources().getString(R.string.biometric_dialog_subtitle))
                .setNegativeButtonText(getResources().getString(R.string.biometric_dialog_negative_btn))
                .build();

//        promptInfo = new BiometricPrompt.PromptInfo.Builder()
//                .setTitle("Biometric authentication")
//                .setSubtitle("Log in using your fingerprint")
//                .setNegativeButtonText("Use master key")
//                .build();

        boolean fingerprintValue = sharedPreferences.getBoolean("fingerprint", true);
        // AppLogs.log(getApplicationContext(), log_tag, String.valueOf(fingerprintValue));
        if (fingerprintValue) {
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
                    // AppLogs.log(LoginActivity.this, log_tag, "Please enter master key");
                    CustomAlertDialogClass.showWarningOkDialog(getCurrentFocus(), LoginActivity.this, R.string.please_enter_master_key);
                } else {
                    getMasterKeyFromDatabase();
                    getMasterKeyDateFromDatabase();
                    validatePassword();
                }
            }
        });

        Button import_file_btn = findViewById(R.id.import_file_btn);
        import_file_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // showWarningDialog(getCurrentFocus());
                chooseFileToLoad(view);
            }
        });
    }

    private void validatePassword() {
//        if (mk.equals(MD5.md5Custom(enter_masterkey_field.getText().toString().trim()))) {
//            goTagActivity();
//            AppLogs.log(LoginActivity.this, log_tag, getResources().getString(R.string.login_successful));
//        } else {
//            CustomAlertDialogClass.showWarningOkDialog(getCurrentFocus(), LoginActivity.this, R.string.wrong_master_key);
//            // AppLogs.log(LoginActivity.this, log_tag, "Wrong master key");
//        }

        if (mk.isEmpty()) {
            CustomAlertDialogClass.showWarningOkDialog(getCurrentFocus(), LoginActivity.this, R.string.no_master_key);
           // AppLogs.log(LoginActivity.this, log_tag, getResources().getString(R.string.no_master_key));
        } else {

            // AppLogs.log(LoginActivity.this, log_tag, mk + "/" + SHA512.sha512Custom(enter_masterkey_field.getText().toString().trim() + mk_date));

            if (mk.equals(SHA512.sha512Custom(enter_masterkey_field.getText().toString().trim() + mk_date))) {
                goTagActivity();
                AppLogs.log(LoginActivity.this, log_tag, getResources().getString(R.string.login_successful));
            } else {
                CustomAlertDialogClass.showWarningOkDialog(getCurrentFocus(), LoginActivity.this, R.string.wrong_master_key);
                // AppLogs.log(LoginActivity.this, log_tag, "Wrong master key");
            }
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
    private void getMasterKeyDateFromDatabase() {
        String query = "select " + DatabaseHelper.SETTINGS_COLUMN_CREATED + " from " + DatabaseHelper.SETTINGS_TABLE;

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

                mk_date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SETTINGS_COLUMN_CREATED));

                cursor.moveToNext();
            }

            cursor.close();
        }
    }

//    private int getFingerprintFromDatabase() {
//        String query = "select " + DatabaseHelper.SETTINGS_COLUMN_FINGERPRINT + " from " + DatabaseHelper.SETTINGS_TABLE + " where " + DatabaseHelper.SETTINGS_COLUMN_ID + " = 1";
//
//        Cursor cursor = null;
//        if (db !=null)
//        {
//            cursor = db.rawQuery(query, null);
//        }
//
//        if (cursor == null){
//            // AppLogs.log(getApplicationContext(), log_tag, "null");
//            return 0;
//        }
//
//        cursor.moveToFirst();
//
//        if(cursor.getCount() != 0) {
//            cursor.moveToFirst();
//            while (!cursor.isAfterLast()) {
//                // AppLogs.log(getApplicationContext(), log_tag, "f" + cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.SETTINGS_COLUMN_FINGERPRINT)));
//                return cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.SETTINGS_COLUMN_FINGERPRINT));
//
//                // cursor.moveToNext();
//            }
//
//            cursor.close();
//        }
//        return 0;
//    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void goCreateMk() {
        Intent intent = new Intent(this, CreateMasterKeyActivity.class);
        startActivity(intent);
    }

    private void goTagActivity() {
        Intent intent = new Intent(this, TagActivity.class);
        startActivity(intent);
    }

    private void showWarningDialog(View view) {
        ConstraintLayout constraintLayout = view.findViewById(R.id.errorLayout);
        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.error_ok_cancel_dialog, constraintLayout);
        Button errorClose = v.findViewById(R.id.errorCloseButton);
        Button errorOkay = v.findViewById(R.id.errorOkayButton);

        TextView errorDescription = v.findViewById(R.id.errorDescription);
        errorDescription.setText(R.string.import_file_alert);

        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        errorClose.findViewById(R.id.errorCloseButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        errorOkay.findViewById(R.id.errorOkayButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                chooseFileToLoad(view);
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (hasReadPermissions() && hasWritePermissions()) {
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_WRITE_STORAGE_REQUEST_CODE); // your request code
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private void loadDBFileChooser(View view, String path){
        if (path.substring(path.lastIndexOf(".")).equals(".db")) {
            String dir= Environment.getExternalStorageDirectory().getAbsolutePath();
            String DatabaseName = "main_db.db";
            File sd = new File(dir);
            File data = Environment.getDataDirectory();
            FileChannel source = null;
            FileChannel destination = null;
            String backupDBPath = "/data/by.komkova.fit.bstu.passave/databases/" + DatabaseName;
            String currentDBPath = path;
            File currentDB = new File(sd, currentDBPath);
            File backupDB = new File(data, backupDBPath);

            try {
                source = new FileInputStream(currentDB).getChannel();
                destination = new FileOutputStream(backupDB).getChannel();
                destination.transferFrom(source, 0, source.size());
                source.close();
                destination.close();


                // Toast.makeText(getActivity(), "Your Database is Imported !!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                CustomAlertDialogClass.showWarningOkDialog(getCurrentFocus(), LoginActivity.this, R.string.error_details);
            }
        } else {
            CustomAlertDialogClass.showWarningOkDialog(getCurrentFocus(), LoginActivity.this, R.string.error_details);
        }
    }

    private void chooseFileToLoad(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_file)), 100);
        }
        catch (Exception e) {
            Log.d(log_tag, e.getMessage());
            CustomAlertDialogClass.showWarningOkDialog(getCurrentFocus(), LoginActivity.this, R.string.please_install_file_manager);
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode,
                                           @Nullable Intent data) {
        // AppLogs.log(applicationContext, "ImportExportFragment", "123");
        try {
            if(requestCode == 100 && resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                String path = uri.getPath();
                // File file = new File(path);

                // Log.i(log_tag, "Uri: " + path);

                String[] split = path.split("emulated/0");
                String firstSubString = split[0];
                String secondSubString = split[1];

                loadDBFileChooser(getCurrentFocus(), secondSubString);
                AppLogs.log(LoginActivity.this, log_tag, getResources().getString(R.string.db_imported));
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            Log.d(log_tag, e.getMessage());
            CustomAlertDialogClass.showWarningOkDialog(getCurrentFocus(), LoginActivity.this, R.string.error_details);
        }


    }
}

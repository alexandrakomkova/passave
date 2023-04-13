package by.komkova.fit.bstu.passave;

import static android.content.res.Resources.getSystem;

import static by.komkova.fit.bstu.passave.DatabaseHelper.FOLDER_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.DatabaseHelper.SETTINGS_COLUMN_CREATED;
import static by.komkova.fit.bstu.passave.DatabaseHelper.SETTINGS_COLUMN_MASTER_KEY;
import static by.komkova.fit.bstu.passave.DatabaseHelper.SETTINGS_TABLE;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateMasterKeyActivity extends Activity {

    final String log_tag = getClass().getName();

    private TextInputEditText enterMasterKey_tiet, repeatMasterKey_tiet;
    private TextView passwordStrengthTextView;
    private ImageButton back_btn;
    private Button createMk_btn;
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
        ContentValues contentValues = new ContentValues();
        contentValues.put(SETTINGS_COLUMN_MASTER_KEY, md5Custom(repeatMasterKey_tiet.getText().toString().trim()));

        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        contentValues.put(SETTINGS_COLUMN_CREATED, df.format(currentDate));

        long result = db.insert(SETTINGS_TABLE, null, contentValues);
        if (result == -1)
            AppLogs.log(CreateMasterKeyActivity.this, log_tag, "something goes wrong");
        else
            AppLogs.log(CreateMasterKeyActivity.this, log_tag, "Master key created.");
            goLogin();
    }

    private void goLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public static String md5Custom(String st) {
        if (st.isEmpty()){
            return "";
        }

        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(st.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while( md5Hex.length() < 32 ){
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }
}

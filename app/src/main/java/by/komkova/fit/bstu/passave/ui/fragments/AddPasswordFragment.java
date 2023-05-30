package by.komkova.fit.bstu.passave.ui.fragments;

import static android.content.Context.CLIPBOARD_SERVICE;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.FOLDER_COLUMN_TAG_ID;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.PN_COLUMN_CREATED;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.PN_COLUMN_DESCRIPTION;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.PN_COLUMN_FAVOURITE;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.PN_COLUMN_FOLDER_ID;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.PN_COLUMN_LOGIN;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.PN_COLUMN_PASSWORD;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.PN_COLUMN_SERVICE_NAME;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.PN_COLUMN_TAG_ID;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.PN_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.PN_SECURITY_ALGORITHM_ID;
import static by.komkova.fit.bstu.passave.ui.activities.MainActivity.TAG_ID;
import static by.komkova.fit.bstu.passave.db.providers.PasswordNoteProvider.PASSWORD_NOTE_URI;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import by.komkova.fit.bstu.passave.helpers.LocaleChanger;
import by.komkova.fit.bstu.passave.security.password_helpers.PasswordStrength;
import by.komkova.fit.bstu.passave.helpers.AppLogs;
import by.komkova.fit.bstu.passave.helpers.DateFormatter;
import by.komkova.fit.bstu.passave.R;
import by.komkova.fit.bstu.passave.db.DatabaseHelper;
import by.komkova.fit.bstu.passave.DeCryptor;
import by.komkova.fit.bstu.passave.EnCryptor;
import by.komkova.fit.bstu.passave.RSA;
import by.komkova.fit.bstu.passave.security.security_algorithms.AES;
import by.komkova.fit.bstu.passave.ui.custom_dialog.CustomAlertDialogClass;

//import org.springframework.security.crypto.encrypt.Encryptors;
//import org.springframework.security.crypto.encrypt.TextEncryptor;
//import org.springframework.security.crypto.keygen.KeyGenerators;

public class AddPasswordFragment extends Fragment {

    final String log_tag = getClass().getName();
    private Button generate_password_btn, save_password_btn;
    private TextInputEditText enter_password_tiet, enter_login_tiet, enter_details_tiet, enter_service_title_tiet;
    private TextView passwordStrengthTextView;
    private Spinner spinnerFolders;
    private Context applicationContext;
    private RadioGroup radioGroup;
    private RadioButton rsa_radio, aes_radio;
    private static final String SAMPLE_ALIAS = "MYALIAS";
    private EnCryptor encryptor;
    private DeCryptor decryptor;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    ArrayAdapter<String> spinnerAdapter;
    List<String> foldersList;
    Integer selectedFolderId = null;

    private String service_name = "";
    private String login = "";
    private String description = "";
    private String entered_password = "";
    private String crypto_pwd = "";
    private String decrypto_pwd = "";
    private String pk = "";

    private SharedPreferences sharedPreferences = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_password, container, false);
        encryptor = new EnCryptor();
        try {
            decryptor = new DeCryptor();
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException |
                IOException e) {
            e.printStackTrace();
        }

        applicationContext = getActivity();
        databaseHelper = new DatabaseHelper(getActivity());
        db = databaseHelper.getWritableDatabase();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String languageValue = sharedPreferences.getString("language", "en");
        LocaleChanger.changeLocale(languageValue, applicationContext);

//        radioGroup = view.findViewById(R.id.radios_algorithm_choice);
//        rsa_radio = view.findViewById(R.id.rsa_radio);
//        aes_radio = view.findViewById(R.id.aes_radio);

        enter_service_title_tiet = view.findViewById(R.id.enter_service_title_field);
        enter_password_tiet = view.findViewById(R.id.enter_password_field);
        enter_login_tiet = view.findViewById(R.id.enter_login_field);
        enter_details_tiet = view.findViewById(R.id.enter_details_field);
        passwordStrengthTextView = view.findViewById(R.id.password_strength_label);

        if (savedInstanceState != null) {
            service_name = savedInstanceState.getString("service_name");
            enter_service_title_tiet.setText(service_name);

            login = savedInstanceState.getString("login");
            enter_login_tiet.setText(login);

            description = savedInstanceState.getString("description");
            enter_details_tiet.setText(description);

            entered_password = savedInstanceState.getString("entered_password");
            enter_password_tiet.setText(entered_password);
        }

        generate_password_btn = view.findViewById(R.id.generate_password_btn);
        generate_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneratePasswordFragment generatePasswordFragment = new GeneratePasswordFragment();
                Bundle bundle = new Bundle();
                bundle.putString("service_name", String.valueOf(enter_service_title_tiet.getText()));
                bundle.putString("login", String.valueOf(enter_login_tiet.getText()));
                bundle.putString("description", String.valueOf(enter_details_tiet.getText()));
                bundle.putString("generated_password", String.valueOf(enter_password_tiet.getText()));

                generatePasswordFragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                assert fragmentManager != null;
                fragmentManager.beginTransaction().replace(R.id.fragment_layout,  generatePasswordFragment).commit();
            }
        });

        Bundle bundleArgument = getArguments();
        if (bundleArgument != null) {
            enter_service_title_tiet.setText(bundleArgument.getString("service_name"));
            enter_login_tiet.setText(bundleArgument.getString("login"));
            enter_details_tiet.setText(bundleArgument.getString("description"));
            enter_password_tiet.setText(bundleArgument.getString("generated_password"));
        }

        save_password_btn = view.findViewById(R.id.save_password_btn);
        save_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePasswordNote(view);
                // AppLogs.log(applicationContext, log_tag, aesCustom(enter_password_tiet.getText().toString().trim()));
                // rsaCipher();
                // passwordEncrypt(enter_password_tiet.getText().toString().trim());
            }
        });

        spinnerFolders = view.findViewById(R.id.spinnerFolders);
        foldersList = new ArrayList<String>();
        loadSpinnerData();
        spinnerFolders.setSelection(0); // 'no folder' id

        spinnerFolders.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedFolderId = findFolderId(spinnerFolders.getSelectedItem().toString(), db);
                // AppLogs.log(applicationContext, log_tag, spinnerFolders.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        enter_password_tiet.addTextChangedListener(new TextWatcher() {
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

        ImageButton copy_password_button = view.findViewById(R.id.copy_password_button);
        copy_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", enter_password_tiet.getText());
                manager.setPrimaryClip(clipData);

                AppLogs.log(applicationContext, log_tag, getResources().getString(R.string.text_copied));
            }
        });

        ImageButton copy_service_name_button = view.findViewById(R.id.copy_service_name_button);
        copy_service_name_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", enter_service_title_tiet.getText());
                manager.setPrimaryClip(clipData);

                AppLogs.log(applicationContext, log_tag, getResources().getString(R.string.text_copied));
            }
        });

        ImageButton copy_login_button = view.findViewById(R.id.copy_login_button);
        copy_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", enter_login_tiet.getText());
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

    public void loadSpinnerData() {
        String query = "select " + DatabaseHelper.FOLDER_COLUMN_FOLDER_NAME + " from " + DatabaseHelper.FOLDER_TABLE +
                " where " + DatabaseHelper.FOLDER_COLUMN_FOLDER_NAME + " != \'Favourite\' and " + FOLDER_COLUMN_TAG_ID + " = " + TAG_ID;
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor= null;
        if(db !=null)
        {
            cursor = db.rawQuery(query, null);
        }
        assert cursor != null;
        cursor.moveToFirst();


        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                String s = cursor.getString(0);

                foldersList.add(s);
                cursor.moveToNext();
            }

            cursor.close();

            spinnerAdapter = new ArrayAdapter<String>(applicationContext, R.layout.spinner_item_selected, foldersList);
            spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

            spinnerFolders.setAdapter(spinnerAdapter);
            spinnerAdapter.notifyDataSetChanged();

        }

    }

    public static Integer findFolderId(String title, SQLiteDatabase db) {
        String query = "select "+ DatabaseHelper.FOLDER_COLUMN_ID +" from " + DatabaseHelper.FOLDER_TABLE
                + " where " + DatabaseHelper.FOLDER_COLUMN_FOLDER_NAME + " = \""+title+"\"";

        int s = 0;
        Cursor cursor= null;
        if(db !=null)
        {
            cursor = db.rawQuery(query, null);
        }
        assert cursor != null;
        cursor.moveToFirst();

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){

                s = cursor.getInt(0);
                cursor.moveToNext();
            }

            cursor.close();
        }
        return s;
    }


    public void validatePasswordNote(View v) {
        boolean isValidated = true;
        if (enter_password_tiet.getText().toString().isEmpty() && enter_service_title_tiet.getText().toString().isEmpty()) {
            isValidated = false;
            // AppLogs.log(applicationContext, log_tag, getResources().getString(R.string.please_enter_password));
            CustomAlertDialogClass.showWarningOkDialog(v, applicationContext, R.string.please_enter_password_and_service);
        } else {
            if (enter_service_title_tiet.getText().toString().isEmpty()) {
                isValidated = false;
                // AppLogs.log(applicationContext, log_tag, getResources().getString(R.string.please_enter_service_title));
                CustomAlertDialogClass.showWarningOkDialog(v, applicationContext, R.string.please_enter_service_title);
            }

            if (enter_password_tiet.getText().toString().isEmpty()) {
                isValidated = false;
                // AppLogs.log(applicationContext, log_tag, getResources().getString(R.string.please_enter_password));
                CustomAlertDialogClass.showWarningOkDialog(v, applicationContext, R.string.please_enter_password);
            }

            if (passwordStrengthTextView.getText().equals(getResources().getString(R.string.weak))) {
                isValidated = false;
                showWarningDialog(v);
            }
        }

        if (isValidated) { addPasswordNote(v); }
    }

    private void showWarningDialog(View view) {
        ConstraintLayout constraintLayout = view.findViewById(R.id.errorLayout);
        View v = LayoutInflater.from(applicationContext).inflate(R.layout.error_ok_cancel_dialog, constraintLayout);
        Button errorClose = v.findViewById(R.id.errorCloseButton);
        Button errorOkay = v.findViewById(R.id.errorOkayButton);

        TextView errorDescription = v.findViewById(R.id.errorDescription);
        errorDescription.setText(R.string.weak_password_alert);

        AlertDialog.Builder builder = new AlertDialog.Builder(applicationContext);
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
                addPasswordNote(view);
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void addPasswordNote(View v) {
        ContentValues cv = new ContentValues();

        cv.put(PN_COLUMN_SERVICE_NAME, Objects.requireNonNull(enter_service_title_tiet.getText()).toString().trim());
        cv.put(PN_COLUMN_LOGIN, Objects.requireNonNull(enter_login_tiet.getText()).toString().trim());
        cv.put(PN_COLUMN_PASSWORD, Objects.requireNonNull(passwordEncryptAES(enter_password_tiet.getText().toString().trim())));
        cv.put(PN_COLUMN_DESCRIPTION, Objects.requireNonNull(enter_details_tiet.getText()).toString().trim());

        cv.put(PN_COLUMN_CREATED, DateFormatter.currentDate());
        cv.put(PN_COLUMN_UPDATED, DateFormatter.currentDate());

        cv.put(PN_COLUMN_FAVOURITE, 0);
        cv.put(PN_COLUMN_FOLDER_ID, selectedFolderId);
        cv.put(PN_COLUMN_TAG_ID, TAG_ID);

//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.aes_radio:
//                        cv.put(PN_SECURITY_ALGORITHM_ID, 1);
//                        break;
//                    case R.id.rsa_radio:
//                        cv.put(PN_SECURITY_ALGORITHM_ID, 2);
//                        cv.put(PN_COLUMN_KEY, pk);
//
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });

//        if (rsa_radio.isChecked()) {
//            cv.put(PN_SECURITY_ALGORITHM_ID, 2);
//            cv.put(PN_COLUMN_KEY, pk);
//        }
//
//        if (aes_radio.isChecked()) {
//            cv.put(PN_SECURITY_ALGORITHM_ID, 1);
//        }

        cv.put(PN_SECURITY_ALGORITHM_ID, 1);
        // temporary(or not) without favourite

        Uri res =  applicationContext.getContentResolver().insert(PASSWORD_NOTE_URI, cv);

        goHome();
    }

    public void goHome(){
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, new PasswordNotesFragment());
        fragmentTransaction.commit();
    }

    private String passwordEncryptAES(String str) {
        String mk = getMasterKeyFromDatabase();
        AES aes = new AES(mk);
        crypto_pwd = aes.encrypt(str);
        return crypto_pwd;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private String passwordEncrypt(String str) {
        // String mk = getMasterKeyFromDatabase();

//        if (rsa_radio.isChecked()) {
////            try {
////                RSA rsa = new RSA();
////                crypto_pwd = rsa.encrypt(str, rsa.stringToPublicKey(mk));
////                pk = rsa.privateKey.toString();
////                return crypto_pwd;
////            } catch (NoSuchAlgorithmException e) {
////                e.printStackTrace();
////            } catch (NoSuchPaddingException e) {
////                e.printStackTrace();
////            } catch (InvalidKeyException e) {
////                e.printStackTrace();
////            } catch (IllegalBlockSizeException e) {
////                e.printStackTrace();
////            } catch (BadPaddingException e) {
////                e.printStackTrace();
////            } catch (NoSuchAlgorithmException e) {
////                e.printStackTrace();
////            } catch (InvalidKeyException e) {
////                e.printStackTrace();
////            } catch (NoSuchPaddingException e) {
////                e.printStackTrace();
////            } catch (BadPaddingException e) {
////                e.printStackTrace();
////            } catch (IllegalBlockSizeException e) {
////                e.printStackTrace();
////            }
////        }
////
////        if (aes_radio.isChecked()) {
////            AES aes = new AES(mk);
////            crypto_pwd = aes.encrypt(str);
////            return crypto_pwd;
////        }
       //  AES aes = new AES(mk);

//        AES aes = new AES();
//        crypto_pwd = aes.encrypt(str);
//        AppLogs.log(applicationContext, log_tag, crypto_pwd);

        byte[] encryptedText = new byte[0];
        try {
            encryptedText = encryptor.encryptText(SAMPLE_ALIAS, str);
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        //AppLogs.log(applicationContext, log_tag, Base64.encodeToString(encryptedText, Base64.DEFAULT));
//        try {
//            AppLogs.log(applicationContext, log_tag, decryptor
//                    .decryptData(SAMPLE_ALIAS, encryptor.getEncryption(), encryptor.getIv()));
//        } catch (UnrecoverableEntryException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        }

        crypto_pwd = Base64.encodeToString(encryptedText, Base64.DEFAULT);

        return crypto_pwd;
    }

//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.aes_radio:
//                        AES aes = new AES(mk);
//                        crypto_pwd = aes.encrypt(str);
//                        Log.d(log_tag, "aes");
//                        break;
//                    case R.id.rsa_radio:
//                        try {
//                            RSA rsa = new RSA();
//                            crypto_pwd = rsa.encrypt(str, mk);
//                            pk = rsa.privateKey.toString();
//                            Log.d(log_tag, "rsa");
//                        } catch (NoSuchAlgorithmException e) {
//                            e.printStackTrace();
//                        } catch (NoSuchPaddingException e) {
//                            e.printStackTrace();
//                        } catch (InvalidKeyException e) {
//                            e.printStackTrace();
//                        } catch (IllegalBlockSizeException e) {
//                            e.printStackTrace();
//                        } catch (BadPaddingException e) {
//                            e.printStackTrace();
//                        }
//
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });

        // return crypto_pwd;

//        AES aes = new AES(getMasterKeyFromDatabase());
//        Log.d(log_tag, getMasterKeyFromDatabase());
//        Log.d(log_tag, aes.encrypt(str));
//        return aes.encrypt(str);
//        Log.d(log_tag, a);
//        Log.d(log_tag, aes.decrypt(a));


    private String getMasterKeyFromDatabase() {
        String mk = "";
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

        return mk;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        service_name = Objects.requireNonNull(enter_service_title_tiet.getText()).toString().trim();
        login = Objects.requireNonNull(enter_login_tiet.getText()).toString().trim();
        description = Objects.requireNonNull(enter_details_tiet.getText()).toString().trim();
        entered_password = Objects.requireNonNull(enter_password_tiet.getText()).toString().trim();

        outState.putString("service_name", service_name);
        outState.putString("login", login);
        outState.putString("description", description);
        outState.putString("entered_password", entered_password);
    }

    public void rsaCipher() {
        String mk = getMasterKeyFromDatabase();
//        String str = enter_password_tiet.getText().toString().trim();
        String str = "12345";


        mk = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyvgwEYZlJR5Xv6dPlFpg\n" +
                "hG7o9IaXxuLB+b/qsghkLG1iCF2RZebp699BjMkbJs1KQq3F3pOFHxqd1kBIl7zk\n" +
                "fXDzUIw475SWKQ2LWSiD+7WV04t8RNLIAi9JsJdfLl3Unfc+bTqgsSWHcLeP0lEu\n" +
                "GG+bvocbpx8NXPvizXyyOT+bGGmdqEhcqitYhM5B4C82cTY+XnQ4q/zJK4pm9bGr\n" +
                "ycJX4dzWjQdAPt65BKkXWsd7sgy0DI8nTSliJCdClEvysOVt8RDHQ6CoL70CA1LT\n" +
                "2aLD7x73G2h7uksTQ0ztHyxUdaX6G+slg+BwJgEn8p+o6qq4OQUJE0YISfMd99Ok\n" +
                "WwIDAQAB";

        RSA rsa = null;
        try {
            rsa = new RSA();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        try {
            // AppLogs.log(applicationContext, log_tag, String.valueOf(stringToPublicKey(mk)));
            crypto_pwd = rsa.encrypt(str);
            decrypto_pwd = rsa.decrypt(crypto_pwd);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        AppLogs.log(applicationContext, log_tag, crypto_pwd + "*********" + decrypto_pwd);
    }

}
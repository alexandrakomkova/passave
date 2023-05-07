package by.komkova.fit.bstu.passave;

import static by.komkova.fit.bstu.passave.DatabaseHelper.FOLDER_COLUMN_TAG_ID;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_CREATED;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_DESCRIPTION;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_FAVOURITE;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_FOLDER_ID;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_KEY;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_LOGIN;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_PASSWORD;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_SERVICE_NAME;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_TAG_ID;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_COLUMN_UPDATED;
import static by.komkova.fit.bstu.passave.DatabaseHelper.PN_SECURITY_ALGORITHM_ID;
import static by.komkova.fit.bstu.passave.MainActivity.TAG_ID;
import static by.komkova.fit.bstu.passave.PasswordNoteProvider.PASSWORD_NOTE_URI;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

//import org.springframework.security.crypto.encrypt.Encryptors;
//import org.springframework.security.crypto.encrypt.TextEncryptor;
//import org.springframework.security.crypto.keygen.KeyGenerators;

public class AddPasswordFragment extends Fragment {

    final String log_tag = getClass().getName();
    private Button generate_password_btn, save_password_btn;
    private TextInputEditText enter_password_tiet, enter_login_tiet, enter_details_tiet, enter_service_title_tiet;
    private Spinner spinnerFolders;
    private Context applicationContext;
    private RadioGroup radioGroup;
    private RadioButton rsa_radio, aes_radio;

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
    private String pk = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_password, container, false);

        applicationContext = getActivity();
        databaseHelper = new DatabaseHelper(getActivity());
        db = databaseHelper.getWritableDatabase();

        radioGroup = view.findViewById(R.id.radios_algorithm_choice);
        rsa_radio = view.findViewById(R.id.rsa_radio);
        aes_radio = view.findViewById(R.id.aes_radio);

        enter_service_title_tiet = view.findViewById(R.id.enter_service_title_field);
        enter_password_tiet = view.findViewById(R.id.enter_password_field);
        enter_login_tiet = view.findViewById(R.id.enter_login_field);
        enter_details_tiet = view.findViewById(R.id.enter_details_field);

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



        return view;
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
        if (enter_service_title_tiet.getText().toString().isEmpty()) {
            isValidated = false;
            AppLogs.log(applicationContext, log_tag, "Please enter service name");
        }

        if (enter_password_tiet.getText().toString().isEmpty()) {
            isValidated = false;
            AppLogs.log(applicationContext, log_tag, "Please enter password");
        }

        if (isValidated) { addPasswordNote(v); }
    }

    public void addPasswordNote(View v) {
        ContentValues cv = new ContentValues();

        cv.put(PN_COLUMN_SERVICE_NAME, Objects.requireNonNull(enter_service_title_tiet.getText()).toString().trim());
        cv.put(PN_COLUMN_LOGIN, Objects.requireNonNull(enter_login_tiet.getText()).toString().trim());
        cv.put(PN_COLUMN_PASSWORD, Objects.requireNonNull(passwordEncrypt(enter_password_tiet.getText().toString().trim())));
        cv.put(PN_COLUMN_DESCRIPTION, Objects.requireNonNull(enter_details_tiet.getText()).toString().trim());

//        Date currentDate = Calendar.getInstance().getTime();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
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

        if (rsa_radio.isChecked()) {
            cv.put(PN_SECURITY_ALGORITHM_ID, 2);
            cv.put(PN_COLUMN_KEY, pk);
        }

        if (aes_radio.isChecked()) {
            cv.put(PN_SECURITY_ALGORITHM_ID, 1);
        }

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

    private String passwordEncrypt(String str) {
        String mk = getMasterKeyFromDatabase();

        if (rsa_radio.isChecked()) {
            try {
                RSA rsa = new RSA();
                crypto_pwd = rsa.encrypt(str, rsa.stringToPublicKey(mk));
                pk = rsa.privateKey.toString();
                return crypto_pwd;
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
        }

        if (aes_radio.isChecked()) {
            AES aes = new AES(mk);
            crypto_pwd = aes.encrypt(str);
            return crypto_pwd;
        }

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

}
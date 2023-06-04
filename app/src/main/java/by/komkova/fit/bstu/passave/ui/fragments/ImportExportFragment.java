package by.komkova.fit.bstu.passave.ui.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.os.SystemClock.sleep;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import by.komkova.fit.bstu.passave.R;
import by.komkova.fit.bstu.passave.helpers.LocaleChanger;
import by.komkova.fit.bstu.passave.ui.activities.LoginActivity;
import by.komkova.fit.bstu.passave.ui.custom_dialog.CustomAlertDialogClass;

public class ImportExportFragment extends Fragment {

    private static final int REQUEST_WRITE_STORAGE_REQUEST_CODE = 1111;
    private final String log_tag = ImportExportFragment.class.getName();
    private SharedPreferences sharedPreferences = null;
    private Context applicationContext;
    private View viewForAlert;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_import_export, container, false);
        viewForAlert = view;
        requestAppPermissions();

        applicationContext = getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String languageValue = sharedPreferences.getString("language", "en");
        LocaleChanger.changeLocale(languageValue, applicationContext);

        Button import_file_btn = view.findViewById(R.id.import_file_btn);
        import_file_btn.setOnClickListener(this::showWarningDialog);
        // import_file_btn.setOnClickListener(this::importDB);

        Button export_file_btn = view.findViewById(R.id.export_file_btn);
        export_file_btn.setOnClickListener(this::exportDB);

        return view;
    }

    private void showWarningDialog(View view) {
        ConstraintLayout constraintLayout = view.findViewById(R.id.errorLayout);
        View v = LayoutInflater.from(applicationContext).inflate(R.layout.error_ok_cancel_dialog, constraintLayout);
        Button errorClose = v.findViewById(R.id.errorCloseButton);
        Button errorOkay = v.findViewById(R.id.errorOkayButton);

        TextView errorDescription = v.findViewById(R.id.errorDescription);
        errorDescription.setText(R.string.import_file_alert);

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

        ActivityCompat.requestPermissions(getActivity(),
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_WRITE_STORAGE_REQUEST_CODE); // your request code
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private void exportDB(View v){
        String DatabaseName = "main_db.db";
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ "by.komkova.fit.bstu.passave" +"/databases/"+DatabaseName ;
        String backupDBPath = "/Download/copy.db";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            backupDB.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(getActivity(), "Your Database is Exported !!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void importDB(View view){
        String dir=Environment.getExternalStorageDirectory().getAbsolutePath();
        String DatabaseName = "main_db.db";
        File sd = new File(dir);
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String backupDBPath = "/data/by.komkova.fit.bstu.passave/databases/" + DatabaseName;
        String currentDBPath = "/Download/copy.db";
        File currentDB = new File(sd, currentDBPath);
        File backupDB = new File(data, backupDBPath);

        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(getActivity(), "Your Database is Imported !!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDBFileChooser(View view, String path){
        if (path.substring(path.lastIndexOf(".")).equals(".db")) {
            String dir=Environment.getExternalStorageDirectory().getAbsolutePath();
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
                CustomAlertDialogClass.showWarningOkDialog(view, applicationContext, R.string.error_details);
            }
        } else {
            CustomAlertDialogClass.showWarningOkDialog(view, applicationContext, R.string.error_details);
        }

    }

    private void chooseFileToLoad(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            getActivity().startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_file)), 100);
        }
        catch (Exception e) {
            Log.d(log_tag, e.getMessage());
            CustomAlertDialogClass.showWarningOkDialog(view, applicationContext, R.string.please_install_file_manager);
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode,
                                              @Nullable Intent data) {
        try {
            if(requestCode == 100 && resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                String path = uri.getPath();
                // File file = new File(path);

                // Log.i(log_tag, "Uri: " + path);

                String[] split = path.split("emulated/0");
                String firstSubString = split[0];
                String secondSubString = split[1];

                loadDBFileChooser(viewForAlert, secondSubString);

            }
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            Log.d(log_tag, e.getMessage());
            CustomAlertDialogClass.showWarningOkDialog(viewForAlert, applicationContext, R.string.error_details);
        }
    }

}
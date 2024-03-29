package by.komkova.fit.bstu.passave.ui.activities;

import static android.os.SystemClock.sleep;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.PN_COLUMN_SERVICE_NAME;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.PN_COLUMN_UPDATED;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LabeledIntent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import by.komkova.fit.bstu.passave.helpers.AppLogs;
import by.komkova.fit.bstu.passave.helpers.LocaleChanger;
import by.komkova.fit.bstu.passave.ui.custom_dialog.CustomAlertDialogClass;
import by.komkova.fit.bstu.passave.ui.fragments.DetailsFolderFragment;
import by.komkova.fit.bstu.passave.ui.fragments.HomeFragment;
import by.komkova.fit.bstu.passave.R;
import by.komkova.fit.bstu.passave.ui.fragments.SettingsFragment;
import by.komkova.fit.bstu.passave.db.DatabaseHelper;
import by.komkova.fit.bstu.passave.ui.fragments.AboutFragment;
import by.komkova.fit.bstu.passave.ui.fragments.ImportExportFragment;

public class TagActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static Context contextOfApplication;
    final String log_tag = getClass().getName();
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }

    private NotificationManagerCompat notificationManagerCompat;
    private Notification notification;
    private SharedPreferences sharedPreferences = null;

    private DrawerLayout drawerLayout;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    String oldPasswords = null;
    String[] oldPasswordsArray = new String[] {  };
    List<String> oldPasswordsList = new ArrayList<String>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        contextOfApplication = getApplicationContext();
        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notificationsValue = sharedPreferences.getBoolean("notifications", true);

        if (notificationsValue) {
            Log.d("NOTIFICATION ON", "TRUE");
            selectOldPasswords();
        }

        // Log.d("BUILD NOTIF", "123");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("BUILD NOTIF", "1233");
            NotificationChannel notificationChannel = new NotificationChannel("passave", "Passave", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getContextOfApplication().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);

//            NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(this, "passave")
//                    .setSmallIcon(R.drawable.add_icon)
//                    .setContentTitle(getResources().getString(R.string.notification_content_title))
//                    .setContentText(getResources().getString(R.string.notification_content_text) + ": " + oldPasswords)
//                    .setAutoCancel(true);
            NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(this, "passave")
                    .setSmallIcon(R.drawable.add_icon)
                    .setContentTitle(getResources().getString(R.string.notification_content_text))
                    .setContentText(oldPasswords)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(oldPasswords))
                    .setAutoCancel(true);
            notification = notificationCompat.build();
            notificationManagerCompat = NotificationManagerCompat.from(this);
        }


        if (notificationsValue) {
            Log.d("NOTIFICATION ON", "TRUE");
           // selectOldPasswords();

            // Log.d("NOTIFICATION ON", String.valueOf(!oldPasswords.isEmpty()));

            if (!oldPasswords.isEmpty()) {
                Log.d("OLD_PASSWORDS", oldPasswords);
                showNotification();
            }
        }

        String languageValue = sharedPreferences.getString("language", "en");
        // AppLogs.log(this, log_tag, "Main: " + languageValue);
        // LocaleChanger.changeLocale(languageValue, getApplicationContext());
        LocaleChanger.changeLocale(languageValue, contextOfApplication);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new HomeFragment()).commit();
        }



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void selectOldPasswords() {
        // String whereclause = "date(" + PN_COLUMN_UPDATED + ", '365 days') >= ?";
        String whereclause = "date(" + PN_COLUMN_UPDATED + ", '1 days') <= ?";
        String[] whereargs = new String[]{ "DATE('now')" };
        String [] columns = new String[] { PN_COLUMN_SERVICE_NAME };
        // Cursor csr = db.query(DatabaseHelper.FOLDER_TABLE,null, whereclause, whereargs,null,null,null);

        Cursor cursor= null;
        if(db !=null)
        {
            Log.d("SELECT_OLD_PASSWORDS", "db != null");
            // cursor = db.rawQuery(query, null);
            cursor = db.query(DatabaseHelper.PASSWORD_NOTE_TABLE, columns, whereclause, whereargs,null,null,null);

        }
        assert cursor != null;
        cursor.moveToFirst();

        Log.d("SELECT_OLD_PASSWORDS", String.valueOf(cursor.getCount()));


        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                oldPasswordsList.add(cursor.getString(0));

                // oldPasswords = new StringBuilder().append(oldPasswords).append(", ").append(cursor.getString(0)).toString();

                cursor.moveToNext();
            }

            // oldPasswords = oldPasswordsList.toString();
            oldPasswords = String.join(", ", oldPasswordsList);

            // Log.d("SELECT_OLD_PASSWORDS", oldPasswords);

            cursor.close();
        }
    }

    private void showNotification() {
        notificationManagerCompat.notify(1, notification);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new HomeFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new SettingsFragment()).commit();
                break;
            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new AboutFragment()).commit();
                break;
            case R.id.nav_import_export:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new ImportExportFragment()).commit();
                break;
            default: break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment importExportFragment = new ImportExportFragment();

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            importExportFragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
        sleep(1000);

        // CustomAlertDialogClass.showWarningOkDialog(getCurrentFocus().getRootView(), this, R.string.weak);

        Intent intent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(intent);
    }


}

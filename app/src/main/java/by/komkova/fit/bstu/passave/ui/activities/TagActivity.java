package by.komkova.fit.bstu.passave.ui.activities;

import static by.komkova.fit.bstu.passave.db.DatabaseHelper.PN_COLUMN_SERVICE_NAME;
import static by.komkova.fit.bstu.passave.db.DatabaseHelper.PN_COLUMN_UPDATED;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

import by.komkova.fit.bstu.passave.helpers.LocaleChanger;
import by.komkova.fit.bstu.passave.ui.fragments.HomeFragment;
import by.komkova.fit.bstu.passave.R;
import by.komkova.fit.bstu.passave.ui.fragments.SettingsFragment;
import by.komkova.fit.bstu.passave.db.DatabaseHelper;
import by.komkova.fit.bstu.passave.ui.fragments.AboutFragment;
import by.komkova.fit.bstu.passave.ui.fragments.ImportExportFragment;

public class TagActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static Context contextOfApplication;
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

    String oldPasswords = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        contextOfApplication = getApplicationContext();
        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("passave", "Passave", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getContextOfApplication().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);

            NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(this, "passave")
                    .setSmallIcon(R.drawable.add_icon)
                    .setContentTitle(String.valueOf(R.string.notification_content_title))
                    .setContentText(String.valueOf(R.string.notification_content_text))
                    .setAutoCancel(true);
            notification = notificationCompat.build();
            notificationManagerCompat = NotificationManagerCompat.from(this);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notificationsValue = sharedPreferences.getBoolean("notifications", true);
        if (notificationsValue) {
            Log.d("NOTIFICATION ON", "TRUE");
            selectOldPasswords();

            if (!oldPasswords.isEmpty()) {
                Log.d("OLD_PASSWORDS", oldPasswords);
                showNotification();
            }
        }

        String languageValue = sharedPreferences.getString("language", "en");
        // AppLogs.log(this, log_tag, "Main: " + languageValue);
        LocaleChanger.changeLocale(languageValue, getApplicationContext());
        // AppLogs.log(this, "TagActivity", "Tag: " + languageValue);

//        if (checkNotificationsOption()) {
//            selectOldPasswords();
//
//            if (!oldPasswords.isEmpty()) {
//                Log.d("OLD_PASSWORDS", oldPasswords);
//                showNotification();
//            }
//        }
    }

//    private boolean checkNotificationsOption() {
//        String whereclause = SETTINGS_COLUMN_ID + "=?";
//        String[] whereargs = new String[]{ "1" };
//        String [] columns = new String[] { SETTINGS_COLUMN_NOTIFICATIONS };
//        Cursor cursor= null;
//        if(db !=null)
//        {
//            cursor = db.query(DatabaseHelper.SETTINGS_TABLE, columns, whereclause, whereargs,null,null,null);
//        }
//        assert cursor != null;
//        cursor.moveToFirst();
//
//        if(cursor.getCount() != 0) {
//            cursor.moveToFirst();
//            while (!cursor.isAfterLast()) {
//                if (cursor.getInt(0) == 1) {
//                    // AppLogs.log(getContextOfApplication(), "TagActivity", "true");
//                    return true;
//                }
//
//                cursor.moveToNext();
//            }
//
//            cursor.close();
//        }
//
//        return false;
//    }

    private void selectOldPasswords() {
        String whereclause = "date(" + PN_COLUMN_UPDATED + ", '365 days') >= ?";
        String[] whereargs = new String[]{ "DATE('now')" };
        String [] columns = new String[] { PN_COLUMN_SERVICE_NAME };
        // Cursor csr = db.query(DatabaseHelper.FOLDER_TABLE,null, whereclause, whereargs,null,null,null);

        Cursor cursor= null;
        if(db !=null)
        {
            // cursor = db.rawQuery(query, null);
            cursor = db.query(DatabaseHelper.PASSWORD_NOTE_TABLE, columns, whereclause, whereargs,null,null,null);

        }
        assert cursor != null;
        cursor.moveToFirst();


        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                oldPasswords = new StringBuilder().append(oldPasswords).append(", ").append(cursor.getString(0)).toString();

                cursor.moveToNext();
            }

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

}

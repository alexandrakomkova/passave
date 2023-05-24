package by.komkova.fit.bstu.passave.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

import by.komkova.fit.bstu.passave.ui.fragments.GeneratePasswordFragment;
import by.komkova.fit.bstu.passave.ui.fragments.ImportExportFragment;
import by.komkova.fit.bstu.passave.ui.fragments.NotesFragment;
import by.komkova.fit.bstu.passave.ui.fragments.PasswordNotesFragment;
import by.komkova.fit.bstu.passave.R;
import by.komkova.fit.bstu.passave.ui.fragments.SettingsFragment;
import by.komkova.fit.bstu.passave.ui.fragments.AboutFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final String log_tag = getClass().getName();
    public static String TAG_ID;

    private SharedPreferences sharedPreferences = null;

    private DrawerLayout drawerLayout;
    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contextOfApplication = getApplicationContext();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String languageValue = sharedPreferences.getString("language", "en");
        // AppLogs.log(this, log_tag, "Main: " + languageValue);
        // changeLocale(languageValue);

        Bundle arguments = getIntent().getExtras();
        TAG_ID = arguments.get("tag_id").toString();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new PasswordNotesFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_passwords);
            // AppLogs.log(getContextOfApplication(), log_tag, TAG_ID);

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_home:
                Intent intent = new Intent(this, TagActivity.class);
                startActivity(intent);
                // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new HomeFragment()).commit();
                break;
            case R.id.nav_passwords:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new PasswordNotesFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new SettingsFragment()).commit();
                break;
            case R.id.nav_password_generator:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new GeneratePasswordFragment()).commit();
                break;
            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new AboutFragment()).commit();
                break;
            case R.id.nav_notes:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new NotesFragment()).commit();
                break;
            case R.id.nav_import_export:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new ImportExportFragment()).commit();
                break;
            default: break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeLocale(String languageCode)
    {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getApplicationContext().getResources().updateConfiguration(configuration, null);
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
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        menu.add(0, v.getId(),0, "Copy");
        menu.setHeaderTitle("Copy text"); //setting header title for menu
        TextView textView = (TextView) v; // calling our textView
        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", textView.getText());
        manager.setPrimaryClip(clipData);
    }

}
package by.komkova.fit.bstu.passave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final String log_tag = getClass().getName();
    public static String TAG_ID;

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
//            case R.id.nav_add_password:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new AddPasswordFragment()).commit();
//                break;
//            case R.id.nav_add_folder:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new AddFolderFragment()).commit();
//                break;
            case R.id.nav_notes:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new NotesFragment()).commit();
                break;
//            case R.id.nav_edit_notes:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new AddNoteFragment()).commit();
//                break;
            default:
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
package by.komkova.fit.bstu.passave;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void goToLanguagesActivity(View view) {
        Intent intent = new Intent(this, SettingsLanguagesActivity.class);
        startActivity(intent);
    }

    public void goToMainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToSecurityActivity(View view) {
        Intent intent = new Intent(this, SettingsSecurityActivity.class);
        startActivity(intent);
    }
}

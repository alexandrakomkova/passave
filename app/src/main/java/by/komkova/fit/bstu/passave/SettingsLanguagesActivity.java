package by.komkova.fit.bstu.passave;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsLanguagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages);
    }

    public void goToSettingsActivity(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
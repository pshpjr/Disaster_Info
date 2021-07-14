package com.example.disaster_info;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this, CheckEmergencyService.class));

        Intent i = getIntent();
        if(i.getBooleanExtra("Emergency",false)){
            startActivity(new Intent(MainActivity.this,EmergencyActivity.class));
        }

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,EmergencyActivity.class));
            }
        });

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("main","getIntent");
        Intent i = getIntent();
        if(i.getExtras() == null) Log.d("main","empty intent");
        if(i.getBooleanExtra("Emergency",false)){
            startActivity(new Intent(MainActivity.this,EmergencyActivity.class));
        }
    }
}
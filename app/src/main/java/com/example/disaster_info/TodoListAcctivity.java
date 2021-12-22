package com.example.disaster_info;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class TodoListAcctivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_acctivity);

        findViewById(R.id.earthquake_button).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ShowImage.class);
            intent.putExtra("disaster_type",0);
            startActivity(intent);
        });
        findViewById(R.id.tsunami_button).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ShowImage.class);
            intent.putExtra("disaster_type",1);
            startActivity(intent);
        });
        findViewById(R.id.rain_button).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ShowImage.class);
            intent.putExtra("disaster_type",2);
            startActivity(intent);
        });
        findViewById(R.id.heat_button).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ShowImage.class);
            intent.putExtra("disaster_type",3);
            startActivity(intent);
        });
        findViewById(R.id.thunder_button).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ShowImage.class);
            intent.putExtra("disaster_type",4);
            startActivity(intent);
        });
        findViewById(R.id.typhoon_button).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ShowImage.class);
            intent.putExtra("disaster_type",5);
            startActivity(intent);
        });
    }

}
package com.example.disaster_info;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class ShowImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        ImageView img = findViewById(R.id.reactImage);
        Intent i = getIntent();
        int inum = i.getIntExtra("disaster_type",0);
        switch (inum){
            case 0:
                img.setImageResource(R.drawable.earthquake);
                break;
            case 1:
                img.setImageResource(R.drawable.tsunami);
                break;
            case 2:
                img.setImageResource(R.drawable.typhoon);
                break;
            case 3:
                img.setImageResource(R.drawable.heat);
                break;
            case 4:
                img.setImageResource(R.drawable.thunder);
                break;
            case 5:
                img.setImageResource(R.drawable.cold);
        }
    }
}
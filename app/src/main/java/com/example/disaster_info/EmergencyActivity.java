package com.example.disaster_info;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;

/*
* 문제 1. 사용자가 속보 확인 안 하면 갱신이 안 될지도
* 문제 2. 앱을 실행하면 바로 알림이 뜸
* */
public class EmergencyActivity extends AppCompatActivity {
    String disasterType;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        btn = findViewById(R.id.react_button);
    }

    //push 알림에 붙어있는 인텐트에 값 넣어서 넘기는건 계속 null이 나와서 DB에서 따로 찾는걸로 변경
    @Override
    protected void onResume() {
        super.onResume();
        getType();
        setTitle();

        setFragment();
        setReact();

    }

    private void setReact() {
        int in = -1;
        switch (disasterType){
            case "지진":
                in = 0;
                break;
            case "해일":
                in = 1;
                break;
            case "호우":
                in = 2;
                break;
            case "폭염":
                in = 3;
                break;
            case "낙뢰":
                in = 4;
                break;
            case "한파":
                in = 5;
                break;

        }
        int finalIn = in;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShowImage.class);
                intent.putExtra("disaster_type", finalIn);
                startActivity(intent);
            }
        });
    }


    //mapFragment 창에 지도 띄움
    private void setFragment(){
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.mapFragmentLayout);


        if(current == null){
            Fragment fragment = new MapsFragment();
            Bundle b = new Bundle();
            b.putString("disasterType",disasterType);
            fragment.setArguments(b);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.mapFragmentLayout,fragment)
                    .commit();
        }
    }

    private void getType(){
        disasterType = getIntent().getStringExtra("disasterType");
    }
    private void setTitle(){
        ((TextView)findViewById(R.id.disasterType)).setText(disasterType +" 대피소");
    }

}

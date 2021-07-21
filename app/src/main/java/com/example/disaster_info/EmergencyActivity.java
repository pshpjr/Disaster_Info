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
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;

/*
* 문제 1. 사용자가 속보 확인 안 하면 갱신이 안 될지도
* 문제 2. 앱을 실행하면 바로 알림이 뜸
* */
public class EmergencyActivity extends AppCompatActivity {
    private boolean isGPSRun;
    int nCurrentPermission = 0;

    Boolean isCheckServiceRun = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);


    }

    //push 알림에 붙어있는 인텐트에 값 넣어서 넘기는건 계속 null이 나와서 DB에서 따로 찾는걸로 변경
    @Override
    protected void onResume() {
        super.onResume();
        setFragment();

        setTitle();

    }





    //mapFragment 창에 지도 띄움
    private void setFragment(){
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.mapFragmentLayout);


        if(current == null){
            Fragment fragment = new MapsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.mapFragmentLayout,fragment)
                    .commit();
        }
    }

    private void setTitle(){
        ResultSet rs = new DBConnection().getData("select * from dbo.기상특보");

        if(rs != null) {
            while (true) {
                try {
                    if (!rs.next() || rs == null) break;
                    rs.next();
                    String disasterType = rs.getString(2).substring(2);
                    ((TextView)findViewById(R.id.disasterType)).setText(disasterType);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

}

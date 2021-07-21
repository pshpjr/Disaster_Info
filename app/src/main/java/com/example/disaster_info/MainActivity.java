package com.example.disaster_info;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {
    static final int PERMISSIONS_REQUEST = 0x0000001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this, CheckEmergencyService.class));
        OnCheckPermission();




        findViewById(R.id.disaster_info_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("메인","속보보기");
                startActivity(new Intent(MainActivity.this,EmergencyActivity.class));

            }
        });

        findViewById(R.id.Newscard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,NewsActivity.class));
            }
        });
    }


    //권한 확인을 위한 함수
    public void OnCheckPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED

                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                Toast.makeText(this, "앱 실행을 위해서는 권한을 설정해야 합니다", Toast.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(this,

                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},

                        PERMISSIONS_REQUEST);

            } else {

                ActivityCompat.requestPermissions(this,

                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},

                        PERMISSIONS_REQUEST);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0

                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "앱 실행을 위한 권한이 설정 되었습니다", Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(this, "앱 실행을 위한 권한이 취소 되었습니다", Toast.LENGTH_LONG).show();

            }
        }
    }
}
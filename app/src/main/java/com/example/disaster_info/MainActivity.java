package com.example.disaster_info;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int PERMISSIONS_REQUEST = 0x0000001;
    ImageView dustIcon ;
    TextView dustText;
    int dustLevel = 0;
    DBConnection newsConnection;
    DBConnection dustConnection;
    // 뉴스 리사이클 어뎁터 생성
    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isServiceRunning()) {
            startService(new Intent(MainActivity.this, CheckEmergencyService.class));
        }

        dustIcon = findViewById(R.id.dustIcon);
        dustText = findViewById(R.id.dustText);

        OnCheckPermission();

        initConnection();

        newsConnection.getData();
        dustConnection.getData();
        setDustIconOnLevel();

        setOnClickListeners();


    }

    private void initConnection() {
        initNews();
        initDust();
    }

    private void setOnClickListeners(){
        findViewById(R.id.disaster_info_card).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("메인","속보보기");
            startActivity(new Intent(MainActivity.this,EmergencyListActivity.class));

        }
    });

        findViewById(R.id.Newscard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=%EB%B6%80%EC%82%B0+%EB%82%A0%EC%94%A8")));
            }
        });

        findViewById(R.id.todo_button).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), TodoListAcctivity.class);
            startActivity(intent);
        });}


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

    public Boolean isServiceRunning(){

        // 시스템 내부의 액티비티 상태를 파악하는 ActivityManager객체를 생성한다.

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);


        //  manager.getRunningServices(가져올 서비스 목록 개수) - 현재 시스템에서 동작 중인 모든 서비스 목록을 얻을 수 있다.



        // 리턴값은 List<ActivityManager.RunningServiceInfo>이다. (ActivityManager.RunningServiceInfo의 객체를 담은 List)


        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {



            // ActivityManager.RunningServiceInfo의 객체를 통해 현재 실행중인 서비스의 정보를 가져올 수 있다.
            if (CheckEmergencyService.class.getName().equals(service.service.getClassName())) {

                return true;
            }

        }
        return  false;

    }

    // 리사이클 뷰 초기화 함수
    private void initNews(){
        RecyclerView recyclerView = findViewById(R.id.recycle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter =  new NewsAdapter();
        recyclerView.setAdapter(adapter);

        newsConnection = new DBConnection().setQuery("SELECT * FROM dbo.태풍_뉴스");
        newsConnection.connectListener = new DBConnection.ConnectListener() {
            @Override
            public void onConnectionSuccess(ResultSet rs) {
                try {
                    while (rs.next()) {
                        NewsData data = new NewsData();
                        data.setText1(rs.getString(1));
                        data.setImageUrl(rs.getString(3));
                        data.setUrl1(rs.getString(2));
                        adapter.additem(data);
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception throwables) {
                    throwables.printStackTrace();
                }
            }

            @Override
            public void onConnectionsFalse() {

            }
        };
    }



    private void initDust(){
        dustConnection = new DBConnection().setQuery("SELECT * FROM dbo.부산_미세먼지");
        dustConnection.connectListener = new DBConnection.ConnectListener() {
            @Override
            public void onConnectionSuccess(ResultSet rs) {
                try {
                    rs.next();
                    dustLevel = Integer.parseInt(rs.getString(2).replaceAll("[^\\d]", ""));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    dustLevel = -1;
                }
            }

            @Override
            public void onConnectionsFalse() {

            }
        };

    }

    private void setDustIconOnLevel(){
        if(0<=dustLevel&&dustLevel<=30){
            dustIcon.setImageResource(R.drawable.happiness);
            dustText.setText("좋음");
        }else if(30<dustLevel&&dustLevel<=80){
            dustIcon.setImageResource(R.drawable.neutral);
            dustText.setText("보통");
        }else if(80<dustLevel&&dustLevel<=150){
            dustIcon.setImageResource(R.drawable.sad);
            dustText.setText("나쁨");
        }else if(150<dustLevel){
            dustIcon.setImageResource(R.drawable.stunned);
            dustText.setText("매우 나쁨");
        }
        else
            dustText.setText("네트워크 오류");
    }

}
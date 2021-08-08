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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int PERMISSIONS_REQUEST = 0x0000001;

    // 뉴스 리사이클 어뎁터 생성
    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isServiceRunning()) {
            startService(new Intent(MainActivity.this, CheckEmergencyService.class));
        }
        OnCheckPermission();




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
                startActivity(new Intent(MainActivity.this,NewsActivity.class));
            }
        });

        // 뉴스 뷰 생성
        init();

        adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });
        // 뉴스 데이터 가져오기
        getData();
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
    private void init(){
        RecyclerView recyclerView = findViewById(R.id.recycle);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter =  new NewsAdapter();
        recyclerView.setAdapter(adapter);
    }

    // 리사이클 뷰 데이터 가져오기 함수
    private void getData(){
        List<String> list_text = Arrays.asList("1","2","3");
        List<Integer> list_imageid = Arrays.asList(R.drawable.ic_launcher_foreground,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_foreground);
        List<String> list_url = Arrays.asList("https://www.naver.com/",
                "https://www.google.com/",
                "https://www.youtube.com/");
        for(int i=0;i<1;i++)
        {
            NewsData data = new NewsData();
            data.setText1(list_text.get(i));
            data.setImageid(list_imageid.get(i));
            data.setUrl1(list_url.get(i));
            adapter.additem(data);
        }

        adapter.notifyDataSetChanged();
    }
}
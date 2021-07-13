package com.example.disaster_info;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class EmergencyActivity extends AppCompatActivity {
    private boolean isGPSRun;
    int nCurrentPermission = 0;
    static final int PERMISSIONS_REQUEST = 0x0000001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        OnCheckPermission();

        Fragment current = getSupportFragmentManager().findFragmentById(R.id.mapFragmentLayout);


        if(current == null){
            Fragment fragment = new MapsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.mapFragmentLayout,fragment)
                    .commit();
        }
    }
    private BroadcastReceiver GPSLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            Log.d("receiver", "Got message: " + intent.toString());
            double latitude = intent.getDoubleExtra("latitude",35);
            double longitude = intent.getDoubleExtra("longitude",35);
            Location location = new Location("myLocation");
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            Log.d("receiver", "Got message: " + location.toString());


        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver( GPSLocationReceiver, new IntentFilter("LocationChange"));

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver( GPSLocationReceiver);

    }

    private void setStartService(){
        startService(new Intent(EmergencyActivity.this, GPSManager.class));
//        bindService(new Intent(this, GPSManager.class), mConnection, Context.BIND_AUTO_CREATE);
        isGPSRun = true;
    }

    private void setStopService() {
        if (isGPSRun) {
            stopService(new Intent(EmergencyActivity.this, GPSManager.class));
//            unbindService(mConnection);
//            isGPSRun = false;
        }

    }

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

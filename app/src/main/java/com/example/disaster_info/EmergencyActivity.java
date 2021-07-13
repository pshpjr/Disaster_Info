package com.example.disaster_info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class EmergencyActivity extends AppCompatActivity {
    private boolean isGPSRun;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

//      ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE);
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
}

package com.example.disaster_info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class EmergencyActivity extends AppCompatActivity {
    private boolean isGPSRun;

    private BroadcastReceiver GPSLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            Log.d("receiver", "Got message: " + intent.toString());
            Location location = (Location) intent.getSerializableExtra("Location");
            Log.d("receiver", "Got message: " + location.toString());

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        Fragment current = getSupportFragmentManager().findFragmentById(R.id.mapFragmentLayout);


        if(current == null){
            Fragment fragment = new MapsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.mapFragmentLayout,fragment)
                    .commit();
        }
        setStartService();

    }

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

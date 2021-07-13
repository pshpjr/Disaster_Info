package com.example.disaster_info;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;


import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.sql.ResultSet;
import java.sql.SQLException;


public class CheckEmergencyService extends Service {
    int Interval = 1;
    CheckDb checkDB;
    Context context = this;
    public CheckEmergencyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("check","서비스 시작");
        checkDB = new CheckDb();
        Thread check = new Thread(checkDB);
        check.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        checkDB.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class CheckDb implements Runnable {
        String disasterType = "";
        String disasterDate = "";
        Boolean isRun = true;

        @Override
        public void run() {
            Log.d("check","스레드 시작");
            while (isRun) {
                try {
                    ResultSet rs = new DBConnection().getData("select * from dbo.기상특보");
                    if (!rs.next()) break;
                    else if (!rs.getString(1).equals(disasterDate)) {
                        Log.d("check","속보 발견");
                        disasterDate = rs.getString(1);
                        rs.next();
                        disasterType = rs.getString(2);
                        Intent intent = new Intent("New Emergency");
                        intent.putExtra("disasterType",disasterType);
                        intent.putExtra("disasterDate",disasterDate);

                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public void stop () {
            isRun = false;
        }
    }
}
package com.example.disaster_info;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class CheckEmergencyService extends Service {
    int Interval = 1;
    CheckDb checkDB;
    public CheckEmergencyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
        private Handler handler;

        @Override
        public void run() {
            ResultSet rs = new DBConnection().getData("select * from dbo.기상특보");
            while (isRun) {
                try {
                    if (rs.next()) break;
                    else if (!rs.getString(1).equals(disasterDate)) {
                        disasterDate = rs.getString(1);
                        rs.next();
                        disasterType = rs.getString(2);
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    try {
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        public void stop () {
            isRun = false;
        }
    }
}
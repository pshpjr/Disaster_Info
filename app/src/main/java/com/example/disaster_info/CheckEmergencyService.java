package com.example.disaster_info;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.sql.ResultSet;
import java.sql.SQLException;


public class CheckEmergencyService extends Service {
    final int INTERVAL = 1;
    CheckDb checkDB;
    Context context = this;

    public CheckEmergencyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Log.d("check","서비스 시작");
        checkDB = new CheckDb();
        Thread check = new Thread(checkDB);
        check.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("check","서비스 종료");
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
                    if (!rs.next() || rs == null) continue;
                    else if (!rs.getString(1).equals(disasterDate)) {

                        Log.d("check","속보 발견");
                        disasterDate = rs.getString(1);
                        rs.next();
                        disasterType = rs.getString(2).substring(2);
                        Log.d("check",disasterDate +" " + disasterType);


                        NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(getApplicationContext(), "Disaster_Info")
                            .setContentTitle("속보 발생")
                            .setContentText(disasterDate +" "+disasterType)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentIntent(PendingIntent.getActivity(getApplicationContext(),0,
                                    new Intent(getApplicationContext(),MainActivity.class).putExtra("Emergency",true).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK),PendingIntent.FLAG_UPDATE_CURRENT))
                            .setAutoCancel(true);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                        notificationManager.notify(119, builder.build());
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                try {
                    Thread.sleep(INTERVAL *60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public void stop () {
            isRun = false;
        }
    }
    //우리 버전 안드로이드에선 이렇게 등록해줘야 알림 사용 가능
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Disaster_Info";
            String description = "Disaster_Info";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Disaster_Info", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
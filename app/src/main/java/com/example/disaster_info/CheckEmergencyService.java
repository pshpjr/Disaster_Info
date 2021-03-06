package com.example.disaster_info;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.sql.ResultSet;
import java.sql.SQLException;


public class CheckEmergencyService extends Service {
    final int INTERVAL = 1;
    CheckDb checkDB;
    Context context = this;
    DBConnection connection;
    boolean isRun;
    String disasterType = "";
    String disasterDate = "";
    public CheckEmergencyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRun = true;
        createNotificationChannel();
        initConnection();
        startDbCheck();
    }

    private void startDbCheck() {

        checkDB = new CheckDb();
        Thread check = new Thread(checkDB);
        check.start();
    }

    private class CheckDb implements Runnable {
        @Override
        public void run() {
            Log.d("service","서비스 시작");
            while (isRun) {
                connection.getData();
                try {
                    Thread.sleep(INTERVAL *60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.stop();
        Log.d("service","서비스 종료");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("service","unBInd ");
        return super.onUnbind(intent);
    }


    //클래스마다 반복되는데 줄일 수 없나
    void initConnection(){

        connection = new DBConnection();
        connection.setQuery("select * from dbo.기상특보");

        connection.connectListener = new DBConnection.ConnectListener() {
            @Override
            public void onConnectionSuccess(ResultSet rs) {
                try {
                    rs.next();
                    if (!rs.getString(1).equals(disasterDate)) {
                        Log.d("check", "속보 발견");
                        disasterDate = rs.getString(1);
                        rs.next();
                        disasterType = rs.getString(2).substring(2);
                        Log.d("check", disasterDate + " " + disasterType);
                        makeNoti(disasterDate, disasterType);
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            @Override
            public void onConnectionsFalse() {

            }
        };
    }

    public void stop () {
        isRun = false;
    }//외부에서 종료

    private void makeNoti(String disasterDate,String disasterType){
        Intent i = new Intent(getApplicationContext(),EmergencyListActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(i);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), "Disaster_Info")
                        .setContentTitle("속보 발생")
                        .setContentText(disasterDate +" "+disasterType)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(119, builder.build());
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
    }     // isServiceRunning 메소드에 들어가는 파라미터 값


    //테스트용
    private void makeTestNoti(){
        Log.d("check","속보 발견");
        String disasterDate = "2021년 8월 20일";
        String disasterType = "폭염";
        Log.d("check",disasterDate +" " + disasterType);

        Intent i = new Intent(getApplicationContext(),EmergencyListActivity.class).putExtra("disasterType",disasterType);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(i);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), "Disaster_Info")
                        .setContentTitle("속보 발생")
                        .setContentText(disasterDate +" "+disasterType)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(119, builder.build());
    }
}
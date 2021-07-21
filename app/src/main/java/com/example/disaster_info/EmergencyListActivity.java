package com.example.disaster_info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmergencyListActivity extends AppCompatActivity {
    EmergencyListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_list);
        Thread t = new Thread(new CheckDb());
        adapter = new EmergencyListAdapter() ;
        ListView listview = (ListView) findViewById(R.id.list) ;

        t.start();
        try {
            t.join();
            listview.setAdapter(adapter);
        }
        catch(InterruptedException e) {

            e.printStackTrace();
        }


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String disasterType = (String)parent.getItemAtPosition(position);
                Intent intent = new Intent(EmergencyListActivity.this, EmergencyActivity.class);
                intent.putExtra("disasterType",disasterType);
                startActivity(intent);
            }
        });

    }

    private class CheckDb implements Runnable {

        @Override
        public void run() {
            Log.d("check","스레드 시작");
                try {
                    ResultSet rs = new DBConnection().getData("select * from dbo.기상특보");
                    if (!rs.next() || rs == null);
                    else {
                        rs.next();
                        do{
                            String s = rs.getString(2).substring(2);
                            adapter.addItem(s);
                        }while(rs.next());
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
        }
    }
}
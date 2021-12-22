package com.example.disaster_info;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmergencyListActivity extends AppCompatActivity {
    EmergencyListAdapter adapter;
    DBConnection connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_list);

        initListView();
        initConnection();

        connection.getData();
    }

    private void initListView() {
        adapter = new EmergencyListAdapter() ;
        ListView listview = (ListView) findViewById(R.id.list) ;
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String disasterType = (String)parent.getItemAtPosition(position);
                disasterType = disasterType.substring(0,2);
                Intent intent = new Intent(EmergencyListActivity.this, EmergencyActivity.class);
                intent.putExtra("disasterType",disasterType);
                startActivity(intent);
            }
        });
    }

    private void initConnection() {
        connection = new DBConnection().setQuery("select * from dbo.기상특보");
        connection.connectListener = new DBConnection.ConnectListener() {
            @Override
            public void onConnectionSuccess(ResultSet rs) {
                try {
                    rs.next();
                    while (rs.next()){
                        String s = rs.getString(2).substring(2);
                        adapter.addItem(s);
                    }
                } catch (Exception throwables) {
                    adapter.addItem("db에 데이터 없음");
                    throwables.printStackTrace();
                }
            }
            @Override
            public void onConnectionsFalse() {

            }
        };
    }
}
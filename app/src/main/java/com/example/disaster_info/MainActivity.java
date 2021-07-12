package com.example.disaster_info;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ResultSet rs = new DBConnection().getData("select * from [dbo].[재난별_행동요령]");

        while (true) {
            try {
                if (!rs.next()) break;
                else {
                    Toast.makeText(this, "연결성공", Toast.LENGTH_SHORT).show();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
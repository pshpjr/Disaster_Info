/*
* db에서 데이터 받아오는 클래스
*
* 예시
        ResultSet rs = new DBConnection().getData("select * from [dbo].[SELECT 지진_해일_대피소명,위도,경도 FROM dbo.부산_대피소]");

        while (true) {
            try {
                if (!rs.next()) break;
                else{
                    Toast.setText
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
*
*
*
*
* */


package com.example.disaster_info;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {

    private Connection conn;
    public ConnectListener connectListener;
    private String query;

    public void getData() {
        if (tryConnect(true)) {
            ResultSet rs = executeQuery(query);
            if (rs != null)
                connectListener.onConnectionSuccess(rs);
            else
                connectListener.onConnectionsFalse();
        } else {
            connectListener.onConnectionsFalse();
        }
    }
    public DBConnection setQuery(String query){
        this.query = query; return this;
    }
    private boolean tryConnect(boolean showMessage) {
        try {
            if (conn != null && !conn.isClosed()) {
                Log.d("DBConnection", "connection closed");
                return true;
            }

            String dbIp = "";
            String dbName = "";
            String dbUser = "";
            String dbUserPass = "";
            ConnectionClass connClass = new ConnectionClass();
            conn = connClass.getConnection(dbUser, dbUserPass, dbName, dbIp);

            if (conn == null) {
                Log.d("DBConnection", "db연결실패");
                return false;
            } else {
                if (conn.isClosed()) {
                    Log.d("DBConnection", "db isclosed");
                    return false;
                } else {
                    Log.d("DBConnection", "연결성공");
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private ResultSet executeQuery(String query) {
        try {
                return conn.prepareStatement(query).executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static class ConnectionClass {
        private Connection connection = null;
        private String ConnectionURL = null;
        private String errMsg = "";

        @SuppressLint("NewApi")
        public Connection getConnection(String user, String password, String database, String server) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                DriverManager.registerDriver((Driver) Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance());
                ConnectionURL = "jdbc:jtds:sqlserver://" + server + "/" + database + ";user=" + user + ";password=" + password + ";";
                connection = DriverManager.getConnection(ConnectionURL);
                Log.d("#DB", "after connection");
            } catch (Exception e) {
                Log.e("DBConnection", e.getMessage());
                errMsg = e.getMessage();
            }
            return connection;
        }

        public String getLastErrMsg() {
            return errMsg;
        }

    }
    public interface ConnectListener{
        public void onConnectionSuccess( ResultSet  rs);
        public void onConnectionsFalse();

    }
}

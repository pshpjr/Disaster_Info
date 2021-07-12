/*
* db에서 데이터 받아오는 클래스
*
* 예시
        ResultSet rs = new DBConnection().getData("select * from [dbo].[재난별_행동요령]");

        while (true) {
            try {
                if (!rs.next()) break;
                else{
                    하고싶은 거거                }
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
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {

    private Connection conn;

    public ResultSet getData(String query){
        if (tryConnect(true)) {
            try {
                return executeQuery(query);
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null;
            }
        }
        else{
            return null;
        }
    }

    private boolean tryConnect(boolean showMessage) {
        try {
            if (conn != null && !conn.isClosed())
                return true;
            String dbIp = "rpa.9bon.org";
            String dbName = "Disaster_Info";
            String dbUser = "user01";
            String dbUserPass = "qkqh106!";
            ConnectionClass connClass = new ConnectionClass();
            conn = connClass.getConnection(dbUser, dbUserPass, dbName, dbIp);
            if (conn == null) {
                Log.d("DBConnection","db연결실패");
                return false;
            } else {
                if (conn.isClosed()) {
                    Log.d("DBConnection","db isclosed");
                    return false;
                } else {
                    Log.d("DBConnection","에러");
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
            if (tryConnect(true)) {
                return conn.prepareStatement(query).executeQuery();
            } else
                return null;
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
        public Connection getConnection(String user, String password, String database, String server)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try
            {
                DriverManager.registerDriver((Driver)Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance());
                ConnectionURL = "jdbc:jtds:sqlserver://" + server + "/" + database + ";user=" + user+ ";password=" + password + ";";
                connection = DriverManager.getConnection(ConnectionURL);
                Log.d("#DB", "after connection");
            }
            catch (SQLException se)
            {
                Log.e("error here 1 : ", se.getMessage());
                errMsg = se.getMessage();
            }
            catch (ClassNotFoundException e)
            {
                Log.e("error here 2 : ", e.getMessage());
                errMsg = e.getMessage();
            }
            catch (Exception e)
            {
                Log.e("error here 3 : ", e.getMessage());
                errMsg = e.getMessage();
            }
            return connection;
        }

        public String getLastErrMsg(){
            return errMsg;
        }
    }
}

package com.example.disaster_info;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapsFragment extends Fragment {

    ArrayList<MarkerOptions> data = new ArrayList<>();
    private Location myLocation = null;
    String disasterType;
    Context context;
    GoogleMap GMap;
    ProgressDialog progressDialog;
    LocationRequest mLocationRequest = null;
    LocationCallback mLocationCallback = null;
    Boolean isGetData = false;
    DBConnection mapConnection;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            GMap = googleMap;
            Log.d("MapsFragment","gmap ready");

            Log.d("gps","startRequset");
            LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        disasterType = getArguments().getString("disasterType").substring(0,2);

        setLocationCallback();
        initMapConnection();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        showProgressDialog();

    }

    private void setLocationCallback() {
        mLocationRequest= LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback= new LocationCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                myLocation = locationResult.getLastLocation();
                GMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 13));
                GMap.setMyLocationEnabled(true);
                LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(this);

                progressDialog.cancel();

                Runnable progressRunnable = new Runnable() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {

                        mapConnection.getData();

                    }
                };

                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 300);

            }

        };
    }

    private void initMapConnection() {
        mapConnection = new DBConnection().setQuery("SELECT 쉼터명,위도,경도 FROM dbo.폭염_쉼터");
        mapConnection.connectListener = new DBConnection.ConnectListener() {
            @Override
            public void onConnectionSuccess(ResultSet rs) {
                isGetData = true;
                setPinsInResultSet(rs);
                addPinOnMap();
            }

            @Override
            public void onConnectionsFalse() {

            }
        };
    }

    void setPinsInResultSet(ResultSet Pindata){
        while (true){
            try {
                if (!Pindata.next()) break;
                else{
                    String name = Pindata.getString(1);
                    String latitude = Pindata.getString(2);
                    String longitude = Pindata.getString(3);

                    if(latitude.equals("-") || longitude.equals("-"))
                        continue;

                    Location pinLocation = new Location("");
                    pinLocation.setLatitude(Float.parseFloat(latitude));
                    pinLocation.setLongitude(Float.parseFloat(longitude));
                    Float distance = myLocation.distanceTo(pinLocation);

                    Log.d("location",name + " " + pinLocation.toString() +" "+ distance);
                    if(distance<=5000)
                        data.add(new MarkerOptions().position(new LatLng(Float.parseFloat(Pindata.getString(2)),Float.parseFloat(Pindata.getString(3)))).title(Pindata.getString(1)));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
    }

    public void addPinOnMap(){
        for(int i =0;i<data.size();i++) {
            GMap.addMarker(data.get(i));
        }
    }

    public void showProgressDialog() {
        isGetData = false;
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        ((TextView) progressDialog.findViewById(R.id.loading_text)).setText("위치 확인중...");
        progressDialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                progressDialog.cancel();
                if(!isGetData)
                    Toast.makeText(context, "인터넷이 느립니다", Toast.LENGTH_SHORT).show();
            }
        };
        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 8000);
    }





}

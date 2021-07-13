package com.example.disaster_info;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
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

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {
    ArrayList<MarkerOptions> data = new ArrayList<>();
    private Location myLocation = new Location(String.valueOf(new LatLng(35,127)));
    LocationRequest myLocationRequest;
    String division = "";
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         *
         * 지진_해일_대피소명	위도	경도
         * 신암마을 뒤 신도로2(신규)	35.219069	129.224961
         * 문동마을 뒷산	35.307811	129.2567075
         * 칠암초등학교	35.29875141	129.2559105
         * 부경대수산과학연구소 앞 야산	35.285577	129.2567909
         * 고리스포츠문화센터	35.329446	129.2877944
         * 기장문화예절학교	35.32549534	129.2682777
         */


        @Override
        public void onMapReady(GoogleMap googleMap) {

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }


            myLocation = new GPSManager(getContext()).getLocation();

            final Geocoder geocoder = new Geocoder(getActivity());

            //내 위치를 geocoder 이용해서 어떤 구에 있는지 확인함
            try {

                List<Address> a = geocoder.getFromLocation(myLocation.getLatitude(),myLocation.getLongitude(),20);
                for(Address add: a){
                    if(add.getSubLocality() != null) {
                        division = add.getSubLocality();
                        break;
                    }
                }
                Log.d("location",a.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }


            //행정구역을 가지고 올 수 있다면 DB에서 해당 구의 대피소 받아옴
            assert division != null;
            division = division.substring(0,division.length()-1);//~구에서 구 제거
            ResultSet Pindata = new DBConnection().getData("SELECT 지진_해일_대피소명,위도,경도 FROM dbo.부산_대피소 where 소재지_도로명_주소 like N'%"
                    +division+"%' or 소재지_지번_주소 like N'%"+division+"%'");

            while (true){
                try {
                    if (!Pindata.next()) break;
                    else{
                        Log.d("data",Pindata.getString(1));
                        data.add(new MarkerOptions().position(new LatLng(Float.parseFloat(Pindata.getString(2)),Float.parseFloat(Pindata.getString(3)))).title(Pindata.getString(1)));
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
            //대피소들을 지도에 추가
            for(int i =0;i<data.size();i++) {
                googleMap.addMarker(data.get(i));
            }

            googleMap.setMyLocationEnabled(true);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()),14));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

}
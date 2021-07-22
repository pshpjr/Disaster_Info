package com.example.disaster_info;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {
    ArrayList<MarkerOptions> data = new ArrayList<>();
    private Location myLocation = new Location(String.valueOf(new LatLng(35,127)));
    LocationRequest myLocationRequest;
    String disasterType;
    RequestQueue requestQueue;
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


            if(disasterType.equals("지진")||disasterType.equals("해일")) {
                ResultSet Pindata = new DBConnection().getData("SELECT 지진_해일_대피소명,위도,경도 FROM dbo.부산_대피소");
                setPinsResultSet(Pindata);
            }
            else if(disasterType.equals("호우")){
                setRianPin();
            }
            else if(disasterType.equals("폭염")){
                ResultSet Pindata = new DBConnection().getData("SELECT ,위도,경도 FROM dbo.폭염_대피소");
                setPinsResultSet(Pindata);
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
        //disasterType = getArguments().getString("disasterType");
        requestQueue = Volley.newRequestQueue(getContext());
        disasterType = "호우";
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

    void setPinsResultSet(ResultSet Pindata){
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

                    if(distance<=5000)
                        data.add(new MarkerOptions().position(new LatLng(Float.parseFloat(Pindata.getString(2)),Float.parseFloat(Pindata.getString(3)))).title(Pindata.getString(1)));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
    }

    public void setRianPin() {

        data.add(new MarkerOptions().position(new LatLng(35.28989040004841, 129.2549768028447)).title("동백천"));
        data.add(new MarkerOptions().position(new LatLng(35.2727016869513, 129.16133503519814)).title("이곡천"));
        data.add(new MarkerOptions().position(new LatLng(35.33843641413546, 129.25663783875385)).title("용소천"));
        data.add(new MarkerOptions().position(new LatLng(35.33771633907129, 129.17315211771563)).title("고래골천"));
        data.add(new MarkerOptions().position(new LatLng(35.36164092574747, 129.27607806759758)).title("효암천"));
        data.add(new MarkerOptions().position(new LatLng(35.16781512597354, 128.97751917259333)).title("삼락교"));
        data.add(new MarkerOptions().position(new LatLng(35.3200397916244, 129.11650246228209)).title("임기천"));
        data.add(new MarkerOptions().position(new LatLng(35.23988782574658, 129.0149526150221)).title("대천교"));
        data.add(new MarkerOptions().position(new LatLng(35.22123877741128, 129.0868916950927)).title("온천장역"));
        data.add(new MarkerOptions().position(new LatLng(35.1905265009569, 129.10792610000098)).title("온천천 하류"));

        String Url = "http://apis.data.go.kr/6260000/BusanRvrwtLevelInfoService/getRvrwtLevelInfo?serviceKey=Le2aBd97INBIfgvNSSRX1sh8fiX9UgAPtYK7sotAaug8CrkhE28LFQ9eXR4J%2B%2FBUHu03KzBy5Gro3Hdw2PRxxA%3D%3D&resultType=json";

        //Fetching the API from URL
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Url, null, response -> {
                Log.d("pin","onResponse");
                //Since the objects of JSON are in an Array we need to define the array from which we can fetch objects

        }, error -> error.printStackTrace());
        requestQueue.add(jsonObjectRequest);
    }

}
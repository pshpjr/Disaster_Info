package com.example.disaster_info;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsFragment extends Fragment {
    ArrayList <MarkerOptions> data = new ArrayList<>();
    private OnMapReadyCallback callback = new OnMapReadyCallback() {
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


            data.add(new MarkerOptions().title("신암마을 뒤 신도로2(신규)").position(new LatLng(35.219069, 129.224961)));
            data.add(new MarkerOptions().title("문동마을 뒷산").position(new LatLng(35.307811, 129.2567075)));
            data.add(new MarkerOptions().title("칠암초등학교").position(new LatLng(35.29875141, 129.2559105)));
            data.add(new MarkerOptions().title("부경대수산과학연구소").position(new LatLng(35.285577, 129.2567909)));

            for(int i =0;i<data.size();i++) {
                googleMap.addMarker(data.get(i));
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(data.get(1).getPosition(),13));
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
package com.verizon.location.platformdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.verizon.location.commonmodels.Coordinate;
import com.verizon.location.maps.MapInitializationError;
import com.verizon.location.maps.MapView;
import com.verizon.location.maps.OnMapReadyCallback;
import com.verizon.location.maps.VltMap;
import com.verizon.location.maps.VltMapOptions;

import org.jetbrains.annotations.NotNull;

public class UserLocationDemo extends AppCompatActivity {

    private Coordinate defaultCoordinates = new Coordinate(39.755700f,-104.994201f);
    private VltMapOptions options = new VltMapOptions();
    private static final int REQUEST_CODE = 1487;

    private boolean mapIsVisible;
    private VltMap vltMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location_demo);
        MapView mapView = findViewById(R.id.map_view);

        options.setZoom(6);
        options.setTarget(defaultCoordinates);

        mapView.onCreate(savedInstanceState);
        mapView.attachMapReadyCallback(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NotNull VltMap map) {
                UserLocationDemo.this.vltMap = map;
                mapIsVisible = true;
            }

            @Override
            public void onMapFailedToLoad(@NotNull MapInitializationError mapError) {
                Log.e("onCreate", "map failed to load "+mapError.getErrorDescription());
                Toast.makeText(UserLocationDemo.this, "" + mapError.getErrorDescription(), Toast.LENGTH_SHORT).show();
            }
        });

        if (!mapIsVisible) {
            loadMap();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (mapIsVisible) {
                showUserLocation();
            }
        });
    }

    private void showUserLocation() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            showCurrentLocation();
        } else {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                UserLocationDemo.this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_CODE
        );
    }

    private void showCurrentLocation() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            vltMap.setMyLocationEnabled(true);
        }
    }

    void loadMap() {
        mapIsVisible = true;
        MapView mv = findViewById(R.id.map_view);
        mv.initialize(getResources().getString(R.string.map_key), options, new OnMapReadyCallback() {
            @Override
            public void onMapFailedToLoad(@NotNull MapInitializationError mapError) {
                Log.e("loadMap", "MAP ERROR : "+mapError);
                mapIsVisible = false;
                Toast.makeText(UserLocationDemo.this, "" + mapError.getErrorDescription(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMapReady(@NotNull VltMap map) {
                UserLocationDemo.this.vltMap = map;
                showUserLocation();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        this.finish();
        return true;
    }
}

package com.verizon.location.platformdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.verizon.location.commonmodels.Coordinate;
import com.verizon.location.maps.MapGestures;
import com.verizon.location.maps.MapInitializationError;
import com.verizon.location.maps.MapView;
import com.verizon.location.maps.OnMapReadyCallback;
import com.verizon.location.maps.VltMap;
import com.verizon.location.maps.VltMapOptions;

import org.jetbrains.annotations.NotNull;

public class MapGesturesDemo extends AppCompatActivity {

    VltMapOptions options = new VltMapOptions();
    Toast mapRotatingToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_gestures_demo);

        MapView mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.initialize(getResources().getString(R.string.map_key), options, new OnMapReadyCallback() {
            @Override
            public void onMapFailedToLoad(@NotNull MapInitializationError mapError) {
                Log.e("MAP ERROR", ""+mapError);
                Toast.makeText(MapGesturesDemo.this, "" + mapError.getErrorDescription(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMapReady(@NotNull VltMap map) {

                map.getGestures().addOnMapClickListener(new MapGestures.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NotNull Coordinate coordinate, @NotNull Point point) {
                        showSnackbar("Map click.");
                    }
                });

                map.getGestures().addOnMapLongClickListener(new MapGestures.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NotNull Coordinate coordinate) {
                        showSnackbar("Map long click.");
                    }
                });

                map.getGestures().addOnMapRotateListener(new MapGestures.OnMapRotateListener() {
                    @Override
                    public void onMapRotateBegin() {
                        showSnackbar("Begin map rotation.");
                    }

                    @Override
                    public void onMapRotate(double v) {
                        if (mapRotatingToast == null) {
                            mapRotatingToast = Toast.makeText(MapGesturesDemo.this, "Map rotating...", Toast.LENGTH_SHORT);
                        }
                        mapRotatingToast.show();
                    }

                    @Override
                    public void onMapRotateEnd(double v) {
                        showSnackbar("End map rotation.");
                        mapRotatingToast.cancel();
                    }
                });

                map.getGestures().addOnMapFlingListener(new MapGestures.OnMapFlingListener() {
                    @Override
                    public void onMapFling() {
                        showSnackbar("Map fling.");
                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        finish();
        return true;
    }

    private void showSnackbar(String text) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getResources().getString(R.string.dismiss_label), view -> snackbar.dismiss());
        snackbar.show();
    }
}

package com.verizon.location.platformdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;

import com.verizon.location.commonmodels.BoundingBox;
import com.verizon.location.commonmodels.Coordinate;
import com.verizon.location.maps.MapInitializationError;
import com.verizon.location.maps.MapView;
import com.verizon.location.maps.OnMapReadyCallback;
import com.verizon.location.maps.VltMap;
import com.verizon.location.maps.VltMapOptions;

import org.jetbrains.annotations.NotNull;

public class BoundingBoxDemo extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private VltMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bounding_box_demo);

        Button button = findViewById(R.id.btn);
        button.setOnClickListener(view -> {
            PopupMenu popup =
                    new PopupMenu(BoundingBoxDemo.this, view, Gravity.END, 0, R.style.PopupMenuPosition);
            popup.setOnMenuItemClickListener(BoundingBoxDemo.this);
            popup.inflate(R.menu.popup_menu);
            popup.show();
        });

        MapView mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.initialize(getResources().getString(R.string.map_key), new VltMapOptions(), new OnMapReadyCallback() {
            @Override
            public void onMapFailedToLoad(@NotNull MapInitializationError mapError) {
                Log.e("MAP ERROR", ""+mapError);
            }

            @Override
            public void onMapReady(@NotNull VltMap map) {
                BoundingBoxDemo.this.map = map;
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.co:
                moveToColoradoBounds();
                return true;
            case R.id.tx:
                moveToTexasBounds();
                return true;
            case R.id.ny:
                moveToNewYorkBounds();
                return true;
            case R.id.ut:
                moveToUtahBounds();
                return true;
            default:
                return false;
        }
    }

    private void moveToColoradoBounds() {
        BoundingBox bbColorado = new BoundingBox(new Coordinate(36.99403f, -109.336205f),
                new Coordinate(41.4281f, -101.6619f));
        moveToBoundingBox(bbColorado);
    }

    private void moveToTexasBounds() {
        BoundingBox bbTexas = new BoundingBox(new Coordinate(36.5584f, -95.07161f),
                new Coordinate(28.5479f, -104.982185f));
        moveToBoundingBox(bbTexas);
    }

    private void moveToNewYorkBounds() {
        BoundingBox bbNewYork = new BoundingBox(new Coordinate(40.2307f, -74.5358f),
                new Coordinate(41.2496f, -72.60234f));
        moveToBoundingBox(bbNewYork);
    }

    private void moveToUtahBounds() {
        BoundingBox bbUtah = new BoundingBox(new Coordinate(42.40689f, -109.01411f),
                new Coordinate(37.1935f, -114.141815f));
        moveToBoundingBox(bbUtah);
    }

    private void moveToBoundingBox(BoundingBox boundingBox) {
        map.getCamera().update(
                boundingBox, 0, 0, 0, false
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        finish();
        return true;
    }
}
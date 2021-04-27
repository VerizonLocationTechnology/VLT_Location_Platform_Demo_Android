package com.verizon.location.platformdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.verizon.location.commonmodels.Coordinate;
import com.verizon.location.maps.MapFeature;
import com.verizon.location.maps.MapInitializationError;
import com.verizon.location.maps.MapView;
import com.verizon.location.maps.OnMapReadyCallback;
import com.verizon.location.maps.VltMap;
import com.verizon.location.maps.VltMapOptions;

import org.jetbrains.annotations.NotNull;

public class MapTrafficDemo extends AppCompatActivity {

    private boolean mapIsVisible;
    private VltMapOptions options = new VltMapOptions();
    private VltMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_demo);

        options.setZoom(12);
        options.setTarget(new Coordinate(39.7557f,-104.9942f));

        MapView mv = findViewById(R.id.map_view);
        mv.onCreate(savedInstanceState);
        mv.initialize(getResources().getString(R.string.map_key), options, new OnMapReadyCallback() {
            @Override
            public void onMapFailedToLoad(@NotNull MapInitializationError mapError) {
                Log.e("MAP ERROR", ""+mapError);
                Toast.makeText(MapTrafficDemo.this, "" + mapError.getErrorDescription(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMapReady(@NotNull VltMap map) {
                MapTrafficDemo.this.map = map;
                mapIsVisible = true;
            }
        });

        CheckBox trafficFlowCheckBox = findViewById(R.id.feature_switch_traffic_flow);
        trafficFlowCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mapIsVisible) {
                    map.setFeatureVisible(MapFeature.TRAFFIC_FLOW, b);
                    if (b) {
                        trafficFlowCheckBox.setText(getResources().getString(R.string.traffic_flow_on_label));
                    } else {
                        trafficFlowCheckBox.setText(getResources().getString(R.string.traffic_flow_off_label));
                    }
                }
            }
        });

        CheckBox trafficIncidentsCheckBox = findViewById(R.id.feature_switch_traffic_incidents);
        trafficIncidentsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mapIsVisible) {
                    map.setFeatureVisible(MapFeature.TRAFFIC_INCIDENTS, b);
                    if (b) {
                        trafficIncidentsCheckBox.setText(getResources().getString(R.string.traffic_incident_on_label));
                    } else {
                        trafficIncidentsCheckBox.setText(getResources().getString(R.string.traffic_incident_off_label));
                    }
                }
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

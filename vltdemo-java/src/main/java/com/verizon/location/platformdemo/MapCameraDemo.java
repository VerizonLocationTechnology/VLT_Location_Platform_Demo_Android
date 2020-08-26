package com.verizon.location.platformdemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.verizon.location.commonmodels.Coordinate;
import com.verizon.location.maps.Camera;
import com.verizon.location.maps.MapInitializationError;
import com.verizon.location.maps.MapView;
import com.verizon.location.maps.OnMapReadyCallback;
import com.verizon.location.maps.VltMap;
import com.verizon.location.maps.VltMapOptions;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class MapCameraDemo extends AppCompatActivity {

    private boolean mapIsVisible = false;
    private VltMap vltMap;

    private TextView zoomField, bearingField, tiltField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_camera_demo);

        bearingField = findViewById(R.id.bearing_value);
        zoomField = findViewById(R.id.zoom_value);
        tiltField = findViewById(R.id.tilt_value);

        VltMapOptions vltMapOptions = new VltMapOptions();
        vltMapOptions.setTarget(new Coordinate(39.7557f,-104.9942f));

        MapView mv = findViewById(R.id.map_view);
        mv.onCreate(savedInstanceState);
        mv.initialize(
                getResources().getString(R.string.map_key),
                vltMapOptions,
                new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NotNull VltMap map) {
                        mapIsVisible = true;
                        Timber.e("map is ready");
                        vltMap = map;
                        vltMap.getCamera().addCameraListener(new Camera.CameraListener() {
                            @Override
                            public void onCameraUpdated(@NotNull Coordinate coordinate, double zoom, double bearing, double tilt) {
                                updateFieldValues(zoom, bearing, tilt);
                            }
                        });
                        updateFieldValues(vltMap.getCamera().getZoom(),
                                vltMap.getCamera().getBearing(),
                                vltMap.getCamera().getTilt()
                        );
                    }

                    @Override
                    public void onMapFailedToLoad(@NotNull MapInitializationError mapError) {
                        Timber.e("map failed to load %s", mapError.getErrorDescription());
                        Toast.makeText(MapCameraDemo.this, "" + mapError.getErrorDescription(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void adjustCamera(View view) {
        if (mapIsVisible) {
            showMetricsDialog();
        }
    }

    private void showMetricsDialog() {
        if (mapIsVisible) {
            AlertDialog.Builder metricsDialogBuilder = new AlertDialog.Builder(this);
            final View metricsDialogView = this.getLayoutInflater().inflate(R.layout.metrics_dialog, null);
            metricsDialogBuilder.setView(metricsDialogView);

            final Button updateButton = metricsDialogView.findViewById(R.id.update_button);
            final EditText bearingText = metricsDialogView.findViewById(R.id.bearing);
            bearingText.setText(getDoubleAsString(vltMap.getCamera().getBearing()));
            final EditText zoomText = metricsDialogView.findViewById(R.id.zoom);
            zoomText.setText(getDoubleAsString(vltMap.getCamera().getZoom()));
            final EditText tiltText = metricsDialogView.findViewById(R.id.tilt);
            tiltText.setText(getDoubleAsString(vltMap.getCamera().getTilt()));

            AlertDialog metricsDialog = metricsDialogBuilder.create();
            metricsDialog.setTitle(getResources().getString(R.string.map_camera_dialog_label));

            updateButton.setOnClickListener(v -> {
                try {
                    Double zoom = Double.valueOf(zoomText.getText().toString());
                    Double bearing = Double.valueOf(bearingText.getText().toString());
                    Double tilt = Double.valueOf(tiltText.getText().toString());

                    try {
                        Coordinate currentPosition = vltMap.getCamera().getTarget();
                        vltMap.getCamera().update(currentPosition, zoom, bearing, tilt, false);
                        metricsDialog.dismiss();
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, R.string.camera_invalid_number_message, Toast.LENGTH_SHORT).show();
                }
            });
            metricsDialog.show();
        }
    }

    private void updateFieldValues(double zoom, double bearing, double tilt) {
        zoomField.setText( getResources().getString(R.string.camera_metric_zoom_label, getDoubleAsString(zoom)) );
        MapCameraDemo.this.zoomField.setVisibility(View.VISIBLE);
        bearingField.setText( getResources().getString(R.string.camera_metric_bearing_label, getDoubleAsString(bearing)) );
        MapCameraDemo.this.bearingField.setVisibility(View.VISIBLE);
        tiltField.setText( getResources().getString(R.string.camera_metric_tilt_label, getDoubleAsString(tilt)) );
        MapCameraDemo.this.tiltField.setVisibility(View.VISIBLE);
    }

    private String getDoubleAsString(Double value) {
        String str = String.format("%.2f", value);
        if (str.equals("-0.00")) {
            return "0.00";
        } else {
            return str;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        this.finish();
        return true;
    }

}

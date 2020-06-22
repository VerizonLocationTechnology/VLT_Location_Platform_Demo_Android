package com.verizon.location.platformdemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.verizon.location.commonmodels.Coordinate;
import com.verizon.location.maps.MapInitializationError;
import com.verizon.location.maps.MapView;
import com.verizon.location.maps.OnMapReadyCallback;
import com.verizon.location.maps.VltMap;
import com.verizon.location.maps.VltMapOptions;
import com.verizon.location.maps.model.Circle;
import com.verizon.location.maps.model.Marker;
import com.verizon.location.maps.model.MarkerImage;
import com.verizon.location.maps.model.Polygon;
import com.verizon.location.maps.model.Polyline;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MapShapesDemo extends AppCompatActivity {

    private boolean mapIsVisible;
    VltMapOptions options = new VltMapOptions();
    private VltMap vltMap;
    private List<Marker> markers;
    private List<Circle> circles;
    private List<Polygon> polygons;
    private List<Polyline> polylines;

    private TextView shapeOptions;
    private int[] state = {0, 0, 0, 0};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_shapes_demo);

        shapeOptions = findViewById(R.id.shape_options);
        initializeShapes();

        options.setTarget(new Coordinate(39.743159f, -104.994932f));
        options.setZoom(10.5);

        ((MapView) findViewById(R.id.map_view)).onCreate(savedInstanceState);
        ((MapView) findViewById(R.id.map_view)).attachMapReadyCallback(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NotNull VltMap map) {
                Timber.e("onMapReady: %s", map);
                MapShapesDemo.this.vltMap = map;
                mapIsVisible = true;
                initializeShapes();
            }

            @Override
            public void onMapFailedToLoad(@NotNull MapInitializationError mapError) {
                Timber.e("onMapFailedToLoad error %s", mapError.getErrorDescription());
            }
        });
        if (!mapIsVisible) {
            loadMap();
        }
    }

    public void shapeListDialog(View view) {
        if (mapIsVisible) {
            AlertDialog.Builder shapesDialogBuilder = new AlertDialog.Builder(this);
            final View shapesDialogView = this.getLayoutInflater().inflate(R.layout.shapes_dialog, null);
            shapesDialogBuilder.setView(shapesDialogView);

            final CheckBox polylineCheckBox = shapesDialogView.findViewById(R.id.polyline);
            polylineCheckBox.setChecked(state[0] == 1);
            final CheckBox polygonCheckBox = shapesDialogView.findViewById(R.id.polygon);
            polygonCheckBox.setChecked(state[1] == 1);
            final CheckBox circleCheckBox = shapesDialogView.findViewById(R.id.circle);
            circleCheckBox.setChecked(state[2] == 1);
            final CheckBox markerCheckBox = shapesDialogView.findViewById(R.id.marker);
            markerCheckBox.setChecked(state[3] == 1);

            AlertDialog shapesDialog = shapesDialogBuilder.create();
            shapesDialog.setTitle(getResources().getString(R.string.map_shapes_dialog_label));

            polylineCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
                showPolyline(b);
            });
            polygonCheckBox.setOnCheckedChangeListener((compoundButton, b) -> showPolygon(b));
            circleCheckBox.setOnCheckedChangeListener((compoundButton, b) -> showCircles(b));
            markerCheckBox.setOnCheckedChangeListener((compoundButton, b) -> showMarkers(b));
            shapesDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.dismiss_label), (dialogInterface, i) -> shapesDialog.dismiss());
            shapesDialog.show();
        }
    }

    private void loadMap() {
        mapIsVisible = true;
        MapView mv = findViewById(R.id.map_view);
        mv.initialize(getResources().getString(R.string.map_key), options, new OnMapReadyCallback() {
            @Override
            public void onMapFailedToLoad(@NotNull MapInitializationError mapError) {
                mapIsVisible = false;
                Timber.e("onMapFailedToLoad error %s", mapError.getErrorDescription());
                Toast.makeText(MapShapesDemo.this, "" + mapError.getErrorDescription(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMapReady(@NotNull VltMap map) {
                MapShapesDemo.this.vltMap = map;
                Timber.e("onMapReady: %s", map);
            }
        });
    }

    private void showPolyline(boolean show) {
        if (show) {
            for (Polyline p : polylines) {
                vltMap.add(p);
            }
            state[0] = 1;
            findViewById(R.id.polyline).setVisibility(View.VISIBLE);
        } else {
            for (Polyline p : polylines) {
                vltMap.remove(p);
            }
            state[0] = 0;
            findViewById(R.id.polyline).setVisibility(View.GONE);
        }
        updateShapeOptionsPrompt();
    }

    private void showPolygon(boolean show) {
        if (show) {
            for (Polygon p : polygons) {
                vltMap.add(p);
            }
            state[1] = 1;
            findViewById(R.id.polygon).setVisibility(View.VISIBLE);
        } else {
            for (Polygon p : polygons) {
                vltMap.remove(p);
            }
            state[1] = 0;
            findViewById(R.id.polygon).setVisibility(View.GONE);
        }
        updateShapeOptionsPrompt();
    }

    private void showCircles(boolean show) {
        if (show) {
            for (Circle c : circles) {
                vltMap.add(c);
            }
            state[2] = 1;
            findViewById(R.id.circle).setVisibility(View.VISIBLE);
        } else {
            for (Circle c : circles) {
                vltMap.remove(c);
            }
            state[2] = 0;
            findViewById(R.id.circle).setVisibility(View.GONE);
        }
        updateShapeOptionsPrompt();
    }

    private void showMarkers(boolean show) {
        if (show) {
            for (Marker m : markers) {
                vltMap.add(m);
            }
            state[3] = 1;
            findViewById(R.id.marker).setVisibility(View.VISIBLE);
        } else {
            for (Marker m : markers) {
                vltMap.remove(m);
            }
            state[3] = 0;
            findViewById(R.id.marker).setVisibility(View.GONE);
        }
        updateShapeOptionsPrompt();
    }

    private void initializeShapes() {
        initializeMarkers();
        initializeCircles();
        initializePolygon();
        initializePolyline();
    }

    private void initializePolyline() {
        polylines = new ArrayList<>();
        List<Coordinate> polylinePoints = new ArrayList<>();
        polylinePoints.add(new Coordinate(39.752153694980215f, -104.99883502721786f));
        polylinePoints.add(new Coordinate(39.75169383845034f, -104.99942511320114f));
        polylinePoints.add(new Coordinate(39.75121954374626f, -105.00003665685654f));
        polylinePoints.add(new Coordinate(39.75086691382805f, -104.99960750341415f));
        polylinePoints.add(new Coordinate(39.750536966071145f, -104.99917834997176f));
        polylinePoints.add(new Coordinate(39.75018020828083f, -104.9987331032753f));
        polylinePoints.add(new Coordinate(39.74986056822902f, -104.99830931425095f));
        polylinePoints.add(new Coordinate(39.74940688302845f, -104.99889135360718f));
        polylinePoints.add(new Coordinate(39.74896144374296f, -104.99947875738144f));
        polylinePoints.add(new Coordinate(39.74862117568179f, -104.99905496835709f));
        polylinePoints.add(new Coordinate(39.74826647021683f, -104.99861240386963f));
        Polyline polyline = new Polyline(polylinePoints,
                getColorWithAlpha(Color.parseColor("#0077B4"), 0.8f),
                4
        );
        polylines.add(polyline);
    }

    private void initializePolygon() {
        polygons = new ArrayList<>();
        List<Coordinate> polygonPoints = new ArrayList<>();
        polygonPoints.add(new Coordinate(39.75123191669306f, -104.9966248869896f));
        polygonPoints.add(new Coordinate(39.749943056126305f, -104.9967160820961f));
        polygonPoints.add(new Coordinate(39.74953474006916f, -104.99523282051085f));
        polygonPoints.add(new Coordinate(39.750842167801096f, -104.99475806951523f));
        polygonPoints.add(new Coordinate(39.75170414916851f, -104.99532133340836f));

        Polygon polygon = new Polygon(polygonPoints, Color.parseColor("#ED7000"));
        polygon.setShowCallout(true);
        polygons.add(polygon);
    }

    private void initializeCircles() {
        circles = new ArrayList<>();
        Circle circle1 = new Circle(
                new Coordinate(39.743159f, -104.994932f),
                250f,
                Color.parseColor("#008330")
        );
        circle1.setShowCallout(true);
        circles.add(circle1);
        Circle circle2 = new Circle(
                new Coordinate(39.7399f, -104.9954f),
                250f,
                getColorWithAlpha(Color.parseColor("#ED7000"), 0.80f)
        );
        circle2.setShowCallout(true);
        circles.add(circle2);
    }

    private void initializeMarkers() {
        markers = new ArrayList<>();
        Marker marker1 = new Marker(new Coordinate(39.7559f, -104.9949f), null);
        markers.add(marker1);
        Marker marker2 = new Marker(new Coordinate(39.7393f, -104.9848f), null);
        markers.add(marker2);
        Marker marker3 = new Marker(new Coordinate(39.752153694980215f, -104.99883502721786f),
                MarkerImage.createFromResource(this, R.drawable.star_marker, "pink-star"));
        markers.add(marker3);
        Marker marker4 = new Marker(new Coordinate(39.74826647021683f, -104.99861240386963f),
                MarkerImage.createFromResource(this, R.drawable.custom_marker, "blue-flame"));
        markers.add(marker4);
    }

    private void updateShapeOptionsPrompt() {
        if ( (state[0] == 0) && (state[1] == 0) && (state[2] == 0) && (state[3] == 0) ) {
            findViewById(R.id.shape_options).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.shape_options).setVisibility(View.GONE);
        }
    }

    private int getColorWithAlpha(int color, float ratio) {
        int newColor;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        newColor = Color.argb(alpha, r, g, b);
        return newColor;
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        this.finish();
        return true;
    }

}

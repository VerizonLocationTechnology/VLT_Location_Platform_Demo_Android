package com.verizon.location.platformdemo;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.verizon.location.commonmodels.Coordinate;
import com.verizon.location.maps.MapInitializationError;
import com.verizon.location.maps.MapView;
import com.verizon.location.maps.OnMapReadyCallback;
import com.verizon.location.maps.VltMap;
import com.verizon.location.maps.VltMapOptions;
import com.verizon.location.maps.model.MarkerImage;
import com.verizon.location.maps.model.layer.GeoJsonLayer;
import com.verizon.location.maps.model.layer.GeoJsonStyle;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;

public class GeojsonDemo extends AppCompatActivity {

    private boolean mapIsVisible;
    private VltMapOptions options = new VltMapOptions();
    private VltMap vltMap;

    private GeoJsonLayer polygonLayer;
    private GeoJsonLayer multiPolygonLayer;
    private GeoJsonLayer pointLayer;
    private GeoJsonLayer lineStringLayer;
    private GeoJsonLayer multiLineStringLayer;
    private GeoJsonLayer multiPointLayer;

    private int[] state = {0, 0, 0, 0, 0, 0};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geojson_demo);

        options.setTarget(new Coordinate(39.753159f, -104.954932f));
        options.setZoom(9.5);

        ((MapView) findViewById(R.id.map_view)).onCreate(savedInstanceState);
        ((MapView) findViewById(R.id.map_view)).attachMapReadyCallback(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NotNull VltMap map) {
                Timber.e("onMapReady: %s", map);
                GeojsonDemo.this.vltMap = map;
                mapIsVisible = true;
                initializeImages();
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

    public void geojsonOptionsDialog(View view) {
        if (mapIsVisible) {
            AlertDialog.Builder shapesDialogBuilder = new AlertDialog.Builder(this);
            final View shapesDialogView = getLayoutInflater().inflate(R.layout.geojson_dialog, null);
            shapesDialogBuilder.setView(shapesDialogView);

            final CheckBox multiPolygonCheckBox = shapesDialogView.findViewById(R.id.multi_polygon_cb);
            multiPolygonCheckBox.setChecked(state[0] == 1);
            multiPolygonCheckBox.setOnCheckedChangeListener((compoundButton, b) -> showMultiPolygon(b));

            final CheckBox polygonCheckBox = shapesDialogView.findViewById(R.id.polygon_cb);
            polygonCheckBox.setChecked(state[1] == 1);
            polygonCheckBox.setOnCheckedChangeListener((compoundButton, b) -> showPolygon(b));

            final CheckBox pointCheckBox = shapesDialogView.findViewById(R.id.point_cb);
            pointCheckBox.setChecked(state[2] == 1);
            pointCheckBox.setOnCheckedChangeListener((compoundButton, b) -> showPoints(b));

            final CheckBox linestringCheckBox = shapesDialogView.findViewById(R.id.linestring_cb);
            linestringCheckBox.setChecked(state[3] == 1);
            linestringCheckBox.setOnCheckedChangeListener((compoundButton, b) -> showLineString(b));

            final CheckBox multiPointCheckBox = shapesDialogView.findViewById(R.id.multi_point_cb);
            multiPointCheckBox.setChecked(state[4] == 1);
            multiPointCheckBox.setOnCheckedChangeListener((compoundButton, b) -> showMultiPoints(b));

            final CheckBox multiLinestringCheckBox = shapesDialogView.findViewById(R.id.multi_linestring_cb);
            multiLinestringCheckBox.setChecked(state[5] == 1);
            multiLinestringCheckBox.setOnCheckedChangeListener((compoundButton, b) -> showMultiLineString(b));

            AlertDialog shapesDialog = shapesDialogBuilder.create();
            shapesDialog.setTitle(getResources().getString(R.string.geojson_dialog_label));

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
                Toast.makeText(GeojsonDemo.this, "" + mapError.getErrorDescription(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMapReady(@NotNull VltMap map) {
                GeojsonDemo.this.vltMap = map;
                Timber.e("onMapReady: %s", map);
            }
        });
    }

    private void showMultiPolygon(boolean show) {
        if (show) {
            loadFeature(R.raw.multi_polygon);
            state[0] = 1;
            findViewById(R.id.multi_polygon).setVisibility(View.VISIBLE);
        } else {
            if (multiPolygonLayer != null) {
                vltMap.removeLayer(multiPolygonLayer.getId());
                multiPolygonLayer = null;
            }
            state[0] = 0;
            findViewById(R.id.multi_polygon).setVisibility(View.GONE);
        }
        updateShapeOptionsPrompt();
    }

    private void showPolygon(boolean show) {
        if (show) {
            loadFeature(R.raw.polygon);
            state[1] = 1;
            findViewById(R.id.polygon).setVisibility(View.VISIBLE);
        } else {
            if (polygonLayer != null) {
                vltMap.removeLayer(polygonLayer.getId());
                polygonLayer = null;
            }
            state[1] = 0;
            findViewById(R.id.polygon).setVisibility(View.GONE);
        }
        updateShapeOptionsPrompt();
    }

    private void showPoints(boolean show) {
        if (show) {
            loadFeature(R.raw.point);
            state[2] = 1;
            findViewById(R.id.point).setVisibility(View.VISIBLE);
        } else {
            if (pointLayer != null) {
                vltMap.removeLayer(pointLayer.getId());
                pointLayer = null;
            }
            state[2] = 0;
            findViewById(R.id.point).setVisibility(View.GONE);
        }
        updateShapeOptionsPrompt();
    }

    private void showLineString(boolean show) {
        if (show) {
            loadFeature(R.raw.linestring);
            state[3] = 1;
            findViewById(R.id.linestring).setVisibility(View.VISIBLE);
        } else {
            if (lineStringLayer != null) {
                vltMap.removeLayer(lineStringLayer.getId());
                lineStringLayer = null;
            }
            state[3] = 0;
            findViewById(R.id.linestring).setVisibility(View.GONE);
        }
        updateShapeOptionsPrompt();
    }

    private void showMultiPoints(boolean show) {
        if (show) {
            loadFeature(R.raw.multi_point);
            state[4] = 1;
            findViewById(R.id.multi_point).setVisibility(View.VISIBLE);
        } else {
            if (multiPointLayer != null) {
                vltMap.removeLayer(multiPointLayer.getId());
                multiPointLayer = null;
            }
            state[4] = 0;
            findViewById(R.id.multi_point).setVisibility(View.GONE);
        }
        updateShapeOptionsPrompt();
    }

    private void showMultiLineString(boolean show) {
        if (show) {
            loadFeature(R.raw.multi_linestring);
            state[5] = 1;
            findViewById(R.id.multi_linestring).setVisibility(View.VISIBLE);
        } else {
            if (multiLineStringLayer != null) {
                vltMap.removeLayer(multiLineStringLayer.getId());
                multiLineStringLayer = null;
            }
            state[5] = 0;
            findViewById(R.id.multi_linestring).setVisibility(View.GONE);
        }
        updateShapeOptionsPrompt();
    }

    private void initializeImages() {
        MarkerImage defaultMarker = MarkerImage.createFromResource(GeojsonDemo.this, R.drawable.default_marker_icon, "default");
        vltMap.addImage(defaultMarker);
        MarkerImage blueFlameMarker = MarkerImage.createFromResource(GeojsonDemo.this, R.drawable.custom_marker, "blueflame");
        vltMap.addImage(blueFlameMarker);
        MarkerImage pinkStarMarker = MarkerImage.createFromResource(GeojsonDemo.this, R.drawable.star_marker, "pinkstar");
        vltMap.addImage(pinkStarMarker);
    }


    void loadFeature(int fileResource) {
        InputStream is = getResources().openRawResource(fileResource);
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            StringBuilder data = new StringBuilder();
            while((line = br.readLine()) != null) {
                data.append(line);
            }
            br.close();
            JSONObject jsonData = new JSONObject(data.toString());
            parseFeature(jsonData);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch(JSONException je) {
            je.printStackTrace();
        }
    }

    void parseFeature(JSONObject featureCollection) {
        String type = "";
        try {
            JSONArray features = featureCollection.getJSONArray("features");
            JSONObject geometry = features.getJSONObject(0).getJSONObject("geometry");
            type = geometry.get("type").toString();
        } catch (JSONException je) {
            je.printStackTrace();
        }
        switch(type) {
            case "MultiPolygon":
                addMultiPolygonFeature(featureCollection);
                break;
            case "Polygon":
                addPolygonFeature(featureCollection);
                break;
            case "Point":
                addPointFeature(featureCollection);
                break;
            case "LineString":
                addLineFeature(featureCollection);
                break;
            case "MultiLineString":
                addMultiLineFeature(featureCollection);
                break;
            case "MultiPoint":
                addMultiPointFeature(featureCollection);
                break;
            default:
                Log.wtf("", "unsupported type : "+type);
                break;
        }
    }

    void addLineFeature(JSONObject lineFeatures) {
        lineStringLayer = new GeoJsonLayer("LineString");
        vltMap.addLayer(lineStringLayer);

        GeoJsonStyle lineStringStyle = new GeoJsonStyle();
        lineStringStyle.setLineStringColor(Color.parseColor("#7851A9"));
        lineStringStyle.setLineStringWidth(4f);
        lineStringLayer.setDefaultStyle(lineStringStyle);

        lineStringLayer.setGeoJson(lineFeatures);
    }

    void addPointFeature(JSONObject pointFeatures) {
        pointLayer = new GeoJsonLayer("Point");
        vltMap.addLayer(pointLayer);

        GeoJsonStyle defaultStyle = new GeoJsonStyle();
        defaultStyle.setPointIconImage("default");
        pointLayer.setDefaultStyle(defaultStyle);

        pointLayer.setGeoJson(pointFeatures);
    }

    void addPolygonFeature(JSONObject polygonFeatures) {
        polygonLayer = new GeoJsonLayer("Polygon");
        vltMap.addLayer(polygonLayer);

        GeoJsonStyle polygonStyle = new GeoJsonStyle();
        polygonStyle.setPolygonFillColor(Color.parseColor("#0077B4"));
        polygonLayer.setDefaultStyle(polygonStyle);

        polygonLayer.setGeoJson(polygonFeatures);
    }

    void addMultiLineFeature(JSONObject multiLineFeatures) {
        multiLineStringLayer = new GeoJsonLayer("MultiLineString");
        vltMap.addLayer(multiLineStringLayer);

        GeoJsonStyle lineStringStyle = new GeoJsonStyle();
        lineStringStyle.setLineStringColor(Color.parseColor("#0028C7"));
        lineStringStyle.setLineStringWidth(4f);
        multiLineStringLayer.setDefaultStyle(lineStringStyle);

        multiLineStringLayer.setGeoJson(multiLineFeatures);
    }

    void addMultiPointFeature(JSONObject multiPointFeatures) {
        multiPointLayer = new GeoJsonLayer("MultiPoint");
        vltMap.addLayer(multiPointLayer);

        GeoJsonStyle defaultStyle = new GeoJsonStyle();
        defaultStyle.setPointIconImage("blueflame");
        multiPointLayer.setDefaultStyle(defaultStyle);

        multiPointLayer.setGeoJson(multiPointFeatures);
    }

    void addMultiPolygonFeature(JSONObject multiPolygonFeatures) {
        multiPolygonLayer = new GeoJsonLayer("MultiPolygon");
        vltMap.addLayer(multiPolygonLayer);

        GeoJsonStyle polygonStyle = new GeoJsonStyle();
        polygonStyle.setPolygonFillColor(getColorWithAlpha(Color.parseColor("#ED7000"), 0.8f));
        multiPolygonLayer.setDefaultStyle(polygonStyle);

        multiPolygonLayer.setGeoJson(multiPolygonFeatures);
    }

    private void updateShapeOptionsPrompt() {
        if ( (state[0] == 0) && (state[1] == 0) && (state[2] == 0) && (state[3] == 0) && (state[4] == 0) && (state[5] == 0) ) {
            findViewById(R.id.geojson_options).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.geojson_options).setVisibility(View.GONE);
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
        finish();
        return true;
    }

}
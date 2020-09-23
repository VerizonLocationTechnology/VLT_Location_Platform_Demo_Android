package com.verizon.location.platformdemo.layers;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.verizon.location.commonmodels.Coordinate;
import com.verizon.location.maps.MapInitializationError;
import com.verizon.location.maps.MapView;
import com.verizon.location.maps.OnMapReadyCallback;
import com.verizon.location.maps.VltMap;
import com.verizon.location.maps.VltMapOptions;
import com.verizon.location.maps.model.layer.GeoJsonLayer;
import com.verizon.location.maps.model.layer.GeoJsonStyle;
import com.verizon.location.maps.model.layer.Layer;
import com.verizon.location.platformdemo.R;
import com.verizon.location.platformdemo.layers.ReorderLayersDialogFragment.LayersUpdateListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import timber.log.Timber;

public class LayersDemo extends AppCompatActivity implements OnMapReadyCallback,
        LayersUpdateListener, OnClickListener {

    MapView mapView;
    VltMap vltMap;
    GeoJsonLayer layerA, layerB, layerC;
    GeoJsonStyle styleA, styleB, styleC;

    Button editButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_layers);

        editButton = findViewById(R.id.edit_layers_button);

        VltMapOptions options = new VltMapOptions();
        options.setTarget(new Coordinate(42.3637f, -71.060604f));
        options.setZoom(9.5);

        mapView = findViewById(R.id.map_view);

        mapView.onCreate(savedInstanceState);
        mapView.initialize(getResources().getString(R.string.map_key), options, this);
    }

    @Override
    public void onMapFailedToLoad(@NotNull MapInitializationError error) {
        Timber.e("onMapFailedToLoad error %s", error.getErrorDescription());
    }

    @Override
    public void onMapReady(@NotNull VltMap map) {
        this.vltMap = map;

        initGeoJsonData();

        addLayer("A");
        addLayer("B");
        addLayer("C");
        editButton.setOnClickListener(this);
    }

    private void initGeoJsonData() {
        layerA = new GeoJsonLayer("A");
        layerA.setGeoJson(loadJSONFromFile(R.raw.layer_a));

        layerB = new GeoJsonLayer("B");
        layerB.setGeoJson(loadJSONFromFile(R.raw.layer_b));

        layerC = new GeoJsonLayer("C");
        layerC.setGeoJson(loadJSONFromFile(R.raw.layer_c));

        styleA = new GeoJsonStyle();
        styleA.setLineStringWidth(2f);
        styleA.setLineStringColor(Color.parseColor("#7e0170"));
        styleA.setPolygonFillColor(Color.parseColor("#ff00f7"));
        styleA.setPolygonFillOpacity(0.75f);

        styleB = new GeoJsonStyle();
        styleB.setLineStringWidth(2f);
        styleB.setLineStringColor(Color.parseColor("#8a7300"));
        styleB.setPolygonFillColor(Color.parseColor("#ffdd00"));
        styleB.setPolygonFillOpacity(0.75f);

        styleC = new GeoJsonStyle();
        styleC.setLineStringWidth(2f);
        styleC.setLineStringColor(Color.parseColor("#3e8901"));
        styleC.setPolygonFillColor(Color.parseColor("#51ff00"));
        styleC.setPolygonFillOpacity(0.75f);
    }

    private JSONObject loadJSONFromFile(int resId) {
        JSONObject jsonData = new JSONObject();
        InputStream is = getResources().openRawResource(resId);
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            StringBuilder data = new StringBuilder();
            while((line = br.readLine()) != null) {
                data.append(line);
            }
            br.close();
            jsonData = new JSONObject(data.toString());
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch(JSONException je) {
            je.printStackTrace();
        }
        return jsonData;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.edit_layers_button) {
            List<Layer> layers = vltMap.getLayers();
            ReorderLayersDialogFragment editLayersFrag = ReorderLayersDialogFragment.newInstance(layers);
            editLayersFrag.show(getSupportFragmentManager(), "edit_layers_frag");
        }
    }

    @Override
    public void onLayersUpdated(@NotNull List<String> newLayerOrder) {
        vltMap.removeLayer(newLayerOrder.get(0));
        vltMap.removeLayer(newLayerOrder.get(1));
        vltMap.removeLayer(newLayerOrder.get(2));

        addLayer(newLayerOrder.get(0));
        addLayer(newLayerOrder.get(1));
        addLayer(newLayerOrder.get(2));
    }

    private void addLayer(String id) {
        if (id.equals("A")) {
            vltMap.addLayer(layerA);
            layerA.setDefaultStyle(styleA);
        } else if (id.equals("B")) {
            vltMap.addLayer(layerB);
            layerB.setDefaultStyle(styleB);
        } else if (id.equals("C")) {
            vltMap.addLayer(layerC);
            layerC.setDefaultStyle(styleC);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        this.finish();
        return true;
    }
}

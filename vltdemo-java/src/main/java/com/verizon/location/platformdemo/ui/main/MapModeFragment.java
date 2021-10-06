package com.verizon.location.platformdemo.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.verizon.location.commonmodels.Coordinate;
import com.verizon.location.maps.Camera;
import com.verizon.location.maps.MapInitializationError;
import com.verizon.location.maps.MapMode;
import com.verizon.location.maps.MapView;
import com.verizon.location.maps.OnMapReadyCallback;
import com.verizon.location.maps.VltMap;
import com.verizon.location.maps.VltMapOptions;
import com.verizon.location.platformdemo.R;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapModeFragment extends Fragment implements Camera.CameraListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_MAP_MODE = "map_mode";

    private MapModePageViewModel mapModePageViewModel;
    private MapMode mode;

    View viewRoot;
    ViewGroup viewGroupContainer;

    public static MapModeFragment newInstance(int index) {
        MapModeFragment mapModeFragment = new MapModeFragment();
        Bundle bundle = new Bundle();
        switch(index) {
            case 1:
                bundle.putSerializable(ARG_MAP_MODE, MapMode.VERIZON_DAY);
                break;
            case 2:
                bundle.putSerializable(ARG_MAP_MODE, MapMode.VERIZON_DAY_3D);
                break;
            case 3:
                bundle.putSerializable(ARG_MAP_MODE, MapMode.VERIZON_NIGHT);
                break;
            case 4:
                bundle.putSerializable(ARG_MAP_MODE, MapMode.VERIZON_NIGHT_3D);
                break;
            case 5:
                bundle.putSerializable(ARG_MAP_MODE, MapMode.VERIZON_SAT);
                break;
        }
        bundle.putInt(ARG_SECTION_NUMBER, index);
        mapModeFragment.setArguments(bundle);
        return mapModeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapModePageViewModel = ViewModelProviders.of(this).get(MapModePageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
            mode = (MapMode)getArguments().getSerializable(ARG_MAP_MODE);
        }
        mapModePageViewModel.setIndex(index);
        mapModePageViewModel.setMapMode(mode);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map_mode_demo, container, false);
        viewRoot = root;
        viewGroupContainer = container;
        final TextView textView = root.findViewById(R.id.section_label);
        mapModePageViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));

        ((MapView) root.findViewById(R.id.map_view)).onCreate(savedInstanceState);
        ((MapView) root.findViewById(R.id.map_view)).attachMapReadyCallback(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NotNull VltMap map) {
                Timber.e("onMapReady: %s", map);
                map.getCamera().addCameraListener(MapModeFragment.this);
            }

            @Override
            public void onMapFailedToLoad(@NotNull MapInitializationError mapError) {
                Timber.e("onMapFailedToLoad: error %s", mapError);
                Toast.makeText(getActivity(), "" + mapError.getErrorDescription(), Toast.LENGTH_SHORT).show();
            }
        });

        return viewRoot;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMap();
    }

    void loadMap() {
        MapView mv = viewRoot.findViewById(R.id.map_view);
        VltMapOptions options = mapModePageViewModel.getOptions().getValue();
        if (options == null) {
            options = new VltMapOptions();
            options.setZoom(12.5);
            options.setTilt(25);
            options.setTarget(new Coordinate(39.7500f, -104.99540f));
            mode = mapModePageViewModel.getMode().getValue();
        }

        options.setMode( (mode==null) ? MapMode.VERIZON_DAY : mode );
        mv.initialize(getResources().getString(R.string.map_key), options, new OnMapReadyCallback() {
            @Override
            public void onMapFailedToLoad(@NotNull MapInitializationError mapError) {
                Timber.e("onMapFailedToLoad: error %s", mapError);
                Toast.makeText(getActivity(), "" + mapError.getErrorDescription(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMapReady(@NotNull VltMap map) {
                Timber.e("onMapReady: %s", map);
            }
        });
    }

    @Override
    public void onCameraUpdated(@NotNull Coordinate coordinate, double zoom, double bearing, double tilt) {
        VltMapOptions options = new VltMapOptions();
        options.setTarget(coordinate);
        options.setZoom(zoom);
        options.setBearing(bearing);
        options.setTilt(tilt);
        this.mapModePageViewModel.setOptions(options);
    }
}
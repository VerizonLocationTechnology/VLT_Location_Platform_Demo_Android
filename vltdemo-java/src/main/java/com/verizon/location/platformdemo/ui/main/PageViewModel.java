package com.verizon.location.platformdemo.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.verizon.location.maps.MapMode;
import com.verizon.location.maps.VltMapOptions;

public class PageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private MutableLiveData<MapMode> mapMode = new MutableLiveData<>();
    private MutableLiveData<VltMapOptions> mapOptions = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, input -> "loading...");

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        return mText;
    }
    public void setMapMode(MapMode mode) {
        mapMode.setValue(mode);
    }
    public LiveData<MapMode> getMode() {
        return mapMode;
    }
    public void setOptions(VltMapOptions options) {
        mapOptions.setValue(options);
    }
    public LiveData<VltMapOptions> getOptions() {
        return mapOptions;
    }
}
package com.verizon.location.platformdemo.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.verizon.location.maps.MapMode
import com.verizon.location.maps.VltMapOptions

class MapModePageViewModel : ViewModel() {

    private val _index = MutableLiveData<Int>()
    private val mapMode = MutableLiveData<MapMode>()
    private val mapOptions = MutableLiveData<VltMapOptions>()
    val text: LiveData<String> = Transformations.map(_index) {
        "loading..."
    }

    fun setIndex(index: Int) {
        _index.value = index
    }

    fun setMapMode(mode: MapMode?) {
        mode?.let {
            mapMode.value = it
        }
    }

    fun getMode(): LiveData<MapMode?>? {
        return mapMode
    }

    fun setOptions(options: VltMapOptions?) {
        options?.let {
            mapOptions.value = it
        }
    }

    fun getOptions(): LiveData<VltMapOptions?>? {
        return mapOptions
    }

}
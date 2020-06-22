package com.verizon.location.platformdemo.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.verizon.location.maps.MapMode
import com.verizon.location.maps.VltMapOptions

class PageViewModel : ViewModel() {

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
        mapMode.value = mode
    }

    fun getMode(): LiveData<MapMode?>? {
        return mapMode
    }

    fun setOptions(options: VltMapOptions?) {
        mapOptions.value = options
    }

    fun getOptions(): LiveData<VltMapOptions?>? {
        return mapOptions
    }

}
package com.verizon.location.navigation.location

import android.location.Location

interface InitialPositionListener {
    fun onInitialPositionReceived(location: Location)
}
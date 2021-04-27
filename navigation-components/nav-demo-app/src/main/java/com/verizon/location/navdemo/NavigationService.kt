package com.verizon.location.navdemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.verizon.location.common.NavEventListener
import com.verizon.location.common.NavEventListener.NavigationEndReason
import com.verizon.location.common.model.ManeuverPrompt
import com.verizon.location.common.model.Route
import com.verizon.location.common.model.RouteStop
import com.verizon.location.navigation.NavigationManager
import com.verizon.location.navigation.ui.util.NavatarUtil

class NavigationService : Service() {

    lateinit var navManager: NavigationManager
    lateinit var nm: NotificationManager

    private val binder: IBinder = NavigationServiceBinder()
    private lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
            nm.createNotificationChannel(channel)
        }
        notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setOngoing(true)

        navManager = NavigationManager(this)
        navManager.addEventListener(object : NavEventListener {
            override fun onNavigationStarted(route: Route) {
                updateNotification("Started", R.drawable.navatar_continue)
            }

            override fun onRouteProgress(nextManeuver: ManeuverPrompt, distanceRemaining: Double) {
                updateNotification(nextManeuver)
            }

            override fun onDestinationReached() {
                updateNotification("You have reached your destination", R.drawable.ic_finish)
            }

            override fun onNavigationEnded(reason: NavigationEndReason, destination: RouteStop?) {
                nm.cancel(NOTIFICATION_ID)
                stopForeground(true)
            }
        })
    }

    override fun onDestroy() {
        stopForeground(true)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY;
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return false
    }

    private fun updateNotification(text: String, iconResourceId: Int) {
        notificationBuilder
            .setContentTitle("Navigating")
            .setContentText(text)
            .setSmallIcon(iconResourceId)
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun updateNotification(nextManeuver: ManeuverPrompt) {
        notificationBuilder
            .setContentTitle("Navigating")
            .setContentText(nextManeuver.text)
            .setSmallIcon(NavatarUtil.getNavatar(nextManeuver.type))
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    inner class NavigationServiceBinder : Binder() {
        val service: NavigationService
            get() = this@NavigationService
    }

    private companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "nav_channel_id"
        const val CHANNEL_NAME = "nav_channel"
    }
}
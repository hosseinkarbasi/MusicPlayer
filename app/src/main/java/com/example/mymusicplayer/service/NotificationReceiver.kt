package com.example.mymusicplayer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.mymusicplayer.App

class NotificationReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context?, intent: Intent?) {
        val actionName = intent?.action
        val serviceIntent = Intent(context, MusicService::class.java)
        if (actionName != null) {
            when (actionName) {
                App.PLAYPause -> {
                    serviceIntent.putExtra("ActionName", "playPause")
                    context?.startService(serviceIntent)
                }
                App.NEXT -> {
                    serviceIntent.putExtra("ActionName", "next")
                    context?.startService(serviceIntent)
                }
                App.PREVIOUS -> {
                    serviceIntent.putExtra("ActionName", "PREVIOUS")
                    context?.startService(serviceIntent)
                }
                App.EXIT -> {
                    serviceIntent.putExtra("ActionName", "exit")
                    context?.startService(serviceIntent)
                }
            }
        }
    }
}
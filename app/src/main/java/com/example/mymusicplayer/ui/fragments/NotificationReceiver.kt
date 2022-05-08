package com.example.mymusicplayer.ui.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

class NotificationReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context?, intent: Intent?) {
        val actionName = intent?.action
        val serviceIntent = Intent(context, MusicService::class.java)
        if (actionName != null) {
            when (actionName) {
                ApplicationClass.PLAYPause -> {
                    serviceIntent.putExtra("ActionName", "playPause")
                    context?.startService(serviceIntent)
                }
                ApplicationClass.NEXT -> {
                    serviceIntent.putExtra("ActionName", "next")
                    context?.startService(serviceIntent)
                }
                ApplicationClass.PREVIOUS -> {
                    serviceIntent.putExtra("ActionName", "PREVIOUS")
                    context?.startService(serviceIntent)
                }
                ApplicationClass.EXIT -> {
                    serviceIntent.putExtra("ActionName", "exit")
                    context?.startService(serviceIntent)
                }
            }
        }
    }
}
package com.example.mymusicplayer.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.session.MediaSessionCompat
import com.example.mymusicplayer.utils.IMediaControl
import kotlin.system.exitProcess

class MusicService : Service() {

    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaControl: IMediaControl
    lateinit var mediaSession: MediaSessionCompat

    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val actionName = intent?.getStringExtra("ActionName")
        if (actionName != null) {
            when (actionName) {
                "playPause" -> {
                    mediaControl.playPauseMusic()
                }
                "next" -> {
                    mediaControl.prevNextSong(true)
                }
                "PREVIOUS" -> {
                    mediaControl.prevNextSong(false)
                }
                "exit" -> {
                    stopForeground(true)
                    exitProcess(1)
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun setCallBack(mediaController: IMediaControl) {
        mediaControl = mediaController
    }
}

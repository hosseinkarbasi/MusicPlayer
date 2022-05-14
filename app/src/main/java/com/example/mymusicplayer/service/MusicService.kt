package com.example.mymusicplayer.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.mymusicplayer.R
import com.example.mymusicplayer.utils.IMediaControl
import com.example.mymusicplayer.App
import com.example.mymusicplayer.ui.fragments.player.PlayerFragment.Companion.musicList
import com.example.mymusicplayer.ui.fragments.player.PlayerFragment.Companion.songPosition
import kotlin.system.exitProcess

class MusicService : Service() {

    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaControl: IMediaControl
    private lateinit var mediaSession: MediaSessionCompat

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

    fun showNotification(playPauseBtn: Int) {

        val prevIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(App.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(App.PLAYPause)
        val playPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(App.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val exitIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(App.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val imgArt = getImgArt(musicList[songPosition].path)
        val image = if (imgArt != null) {
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background)
        }

        val notification = NotificationCompat.Builder(
            baseContext,
            App.CHANNEL_ID
        )
            .setContentTitle(musicList[songPosition].title)
            .setContentText(musicList[songPosition].artist)
            .setSilent(true)
            .setSmallIcon(R.drawable.ic_album)
            .setLargeIcon(image)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.previous, "Previous", prevPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.next, "Next", nextPendingIntent)
            .addAction(R.drawable.close, "Exit", exitPendingIntent)
            .build()

        startForeground(13, notification)
    }

    private fun getImgArt(path: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        return retriever.embeddedPicture
    }
}

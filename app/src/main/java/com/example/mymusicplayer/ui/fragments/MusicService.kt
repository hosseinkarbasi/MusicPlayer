package com.example.mymusicplayer.ui.fragments

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.mymusicplayer.R
import com.example.mymusicplayer.ui.IMediaControl
import com.example.mymusicplayer.ui.fragments.nowplaying.NowPlaying
import com.example.mymusicplayer.ui.fragments.player.PlayerFragment
import com.example.mymusicplayer.ui.fragments.player.PlayerFragment.Companion.songPosition
import com.example.mymusicplayer.ui.fragments.songs.SongsFragment.Companion.musicList
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class MusicService : Service() {

    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    private lateinit var mediaControl: IMediaControl

    override fun onBind(intent: Intent?): IBinder? {
        mediaSession = MediaSessionCompat(this, "My Music")
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
                    mediaControl.prevNextSong(true, baseContext)
                }
                "PREVIOUS" -> {
                    mediaControl.prevNextSong(false, baseContext)
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

    fun showNotification(playPauseBtn: Int) {

        val prevIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            prevIntent,
           PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(ApplicationClass.PLAYPause)
        val playPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val exitIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(ApplicationClass.EXIT)
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
            ApplicationClass.CHANNEL_ID
        )
            .setContentTitle(musicList[songPosition].title)
            .setContentText(musicList[songPosition].artist)
            .setSilent(true)
            .setSmallIcon(R.drawable.ic_album)
            .setLargeIcon(image)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(NowPlaying.musicService?.mediaSession?.sessionToken)
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

    @RequiresApi(Build.VERSION_CODES.M)
    fun createMediaPlayer() {
        try {
            if (NowPlaying.musicService?.mediaPlayer == null) NowPlaying.musicService?.mediaPlayer =
                MediaPlayer()
            NowPlaying.musicService?.mediaPlayer?.reset()
            NowPlaying.musicService?.mediaPlayer?.setDataSource(musicList[songPosition].path)
            NowPlaying.musicService?.mediaPlayer?.prepare()
            NowPlaying.musicService?.mediaPlayer?.start()
            NowPlaying.isPlaying = true
//            PlayerF.binding.btnPlay.setImageResource(R.drawable.ic_pause)
//            NowPlaying.binding.tvStart.text =
//                formatDuration(mediaPlayer!!.currentPosition.toLong())
//            NowPlaying.binding.tvEnd.text = formatDuration(mediaPlayer!!.duration.toLong())
//            NowPlaying.binding.seekBar.progress = 0
//            NowPlaying.binding.seekBar.max = mediaPlayer!!.duration
        } catch (e: Exception) {
            return
        }
    } //TODO

    private fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)
                - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun seekBarSetup() {
        runnable = Runnable {
            PlayerFragment.binding.tvStart.text =
                formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerFragment.binding.seekBar.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    } //TODO

    fun setCallBack(mediaController: IMediaControl) {
        mediaControl = mediaController
    }
}

package com.example.mymusicplayer.ui.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.mymusicplayer.R
import com.example.mymusicplayer.ui.fragments.nowplaying.NowPlaying
import com.example.mymusicplayer.ui.fragments.songs.SongsFragment
import com.example.mymusicplayer.ui.fragments.songs.SongsFragment.Companion.songPosition
import com.google.android.material.button.MaterialButton
import kotlin.system.exitProcess

class NotificationReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ApplicationClass.PREVIOUS -> prevNextSong(false, context!!)
            ApplicationClass.PLAY -> if (SongsFragment.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> prevNextSong(true, context!!)
            ApplicationClass.EXIT -> {
                SongsFragment.musicService?.stopForeground(true)
                SongsFragment.musicService = null
                exitProcess(1)
            }
        }
    }


    private fun playMusic() {
        SongsFragment.isPlaying = true
        SongsFragment.musicService?.mediaPlayer?.start()
        SongsFragment.musicService?.showNotification(R.drawable.ic_pause)
//      NowPlaying.binding.btnPlay.setImageResource(R.drawable.ic_pause)
        NowPlaying.binding.btnPlayNow.setBackgroundResource(R.drawable.ic_pause)
    }

    private fun pauseMusic() {
        SongsFragment.isPlaying = false
        SongsFragment.musicService?.mediaPlayer?.pause()
        SongsFragment.musicService?.showNotification(R.drawable.ic_play)
//      NowPlaying.binding.btnPlay.setImageResource(R.drawable.ic_play)
        NowPlaying.binding.btnPlayNow.setBackgroundResource(R.drawable.ic_play)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun prevNextSong(increment: Boolean, context: Context) {
        setSongPosition(increment)
        SongsFragment.musicService?.createMediaPlayer()

//        Glide.with(context)
//            .load(NowPlaying.musicList[NowPlaying.asghar].imgUri)
//            .apply(RequestOptions().placeholder(R.drawable.ic_launcher_background))
//            .into(NowPlaying.binding.imgSong)
//        NowPlaying.binding.tvSongName.text =
//            PlayerFragment.musicList[PlayerFragment.songPosition].title

        Glide.with(context)
            .load(SongsFragment.musicList[songPosition].imgUri)
            .into(NowPlaying.binding.songImageNow)
        NowPlaying.binding.tvSongNameNow.text = SongsFragment.musicList[songPosition].title

        playMusic()
    }

    private fun setSongPosition(increment: Boolean) {
        if (SongsFragment.repeat) {
            if (increment) {
                if (SongsFragment.musicList.size - 1 == songPosition) songPosition =
                    0
                else songPosition++
            } else {
                if (songPosition == 0) songPosition =
                    SongsFragment.musicList.size - 1
                else songPosition--
            }
        }
    }
}
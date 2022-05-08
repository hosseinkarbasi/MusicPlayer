package com.example.mymusicplayer.ui

import android.content.Context

interface IMediaControl {

    fun playPauseMusic()
    fun prevNextSong(increment: Boolean, context: Context)

}
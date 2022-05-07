package com.example.mymusicplayer.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.concurrent.TimeUnit

@Parcelize
data class Music(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long = 0,
    val path: String,
    val imgUri: String
) : Parcelable {
    fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)
                - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%02d:%02d", minutes, seconds)
    }
}
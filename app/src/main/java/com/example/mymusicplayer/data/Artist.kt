package com.example.mymusicplayer.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Artist(
    val title: String?,
    val music: MutableList<Music>?
) : Parcelable

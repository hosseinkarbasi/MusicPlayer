package com.example.mymusicplayer.ui.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymusicplayer.data.Album
import com.example.mymusicplayer.data.Artist
import com.example.mymusicplayer.data.Music
import com.example.mymusicplayer.utils.Helper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class SongsViewModel(application: Application) : AndroidViewModel(application) {

    private var deviceMusicList = mutableListOf<Music>()

    private val _deviceMusic: MutableStateFlow<List<Music>> = MutableStateFlow(emptyList())
    val deviceMusic = _deviceMusic.asStateFlow()

    private val _artists: MutableStateFlow<List<Artist>> = MutableStateFlow(emptyList())
    val artists = _artists.asStateFlow()

    private val _albums: MutableStateFlow<List<Album>> = MutableStateFlow(emptyList())
    val albums = _albums.asStateFlow()

    init {
        getMusics()
        getAlbums()
        getArtists()
    }

    @SuppressLint("Range")
    fun getAllAudio(context: Application): MutableList<Music> {

        val selection = MediaStore.Audio.Media.IS_MUSIC + " !=0"

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC",
            null
        )

        if (cursor != null) {
            if (cursor.moveToFirst())
                do {
                    val title =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val id =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val album =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artist =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val path =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val duration =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumId =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()

                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUri = Uri.withAppendedPath(uri, albumId).toString()

                    val music = Music(id, title, artist, album, duration, path, artUri)
                    val file = File(music.path)
                    if (file.exists()) deviceMusicList.add(music)

                } while (cursor.moveToNext())
            cursor.close()
        }
        return deviceMusicList
    }

    private fun getMusics() {
        viewModelScope.launch {
            getAllAudio(getApplication()).let {
                _deviceMusic.emit(it)
            }
        }
    }

    private fun getAlbums() {
        Helper.buildSortedAlbums(deviceMusicList).let {
            viewModelScope.launch {
                _albums.emit(it)
            }
        }
    }

    private fun getArtists() {
        Helper.buildSortedArtist(deviceMusicList).let {
            viewModelScope.launch {
                _artists.emit(it)
            }
        }
    }
}
package com.example.mymusicplayer.ui.fragments.songs

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mymusicplayer.R
import com.example.mymusicplayer.data.Music
import com.example.mymusicplayer.databinding.SongsFragmentBinding
import com.example.mymusicplayer.ui.fragments.nowplaying.NowPlayingDirections
import java.io.File
import java.util.*

class SongsFragment : Fragment(R.layout.songs_fragment) {

    private var _binding: SongsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var myAdapter: MusicAdapter

    companion object {
        val musicList = mutableListOf<Music>()
//        var songPosition: Int = 0
        val searchList = mutableListOf<Music>()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = SongsFragmentBinding.bind(view)

        initRecyclerView()
        getAllAudio()
        init()
        search()

    }

    private fun initRecyclerView() {
        myAdapter = MusicAdapter()
        binding.rvMusic.adapter = myAdapter
    }

    private fun init() {
        myAdapter.onItemPosition {
//            songPosition = it
            findNavController()
                .navigate(
                    NowPlayingDirections.actionGlobalPlayerFragment(it)
                )
        }

        searchList.clear()
        searchList.addAll(musicList)
        myAdapter.submitList(searchList)
    }

    private fun search() {
        binding.searchSongs.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchList.clear()
                val search = newText?.lowercase(Locale.getDefault())
                if (search != null) {
                    musicList.forEach {
                        if (it.title.lowercase(Locale.getDefault()).contains(search) ||
                            it.artist.lowercase(Locale.getDefault()).contains(search)
                        ) {
                            searchList.add(it)
                        }
                    }
                    myAdapter.submitList(searchList)
//                    myAdapter.notifyDataSetChanged()
                }
                return true
            }
        })
    }

    @SuppressLint("Range")
    fun getAllAudio() {
        musicList.clear()
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
            MediaStore.Audio.Media.ALBUM_ID,
        )

        val cursor =
            context?.contentResolver?.query(
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
                    val id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val album =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artist =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val duration =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumId =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUri = Uri.withAppendedPath(uri, albumId).toString()

                    val music = Music(id, title, artist, album, duration, path, artUri)
                    val file = File(music.path)
                    if (file.exists())
                        musicList.add(music)

                } while (cursor.moveToNext())
            cursor.close()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvMusic.adapter = null
        _binding = null
    }
}
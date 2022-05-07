package com.example.mymusicplayer.ui.fragments.songs

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mymusicplayer.R
import com.example.mymusicplayer.data.Music
import com.example.mymusicplayer.databinding.SongsFragmentBinding
import com.example.mymusicplayer.ui.fragments.MusicService
import com.example.mymusicplayer.ui.fragments.nowplaying.NowPlaying
import java.io.File
import java.util.*

class SongsFragment : Fragment(R.layout.songs_fragment), ServiceConnection,
    MediaPlayer.OnCompletionListener {

    private var _binding: SongsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var myAdapter: MusicAdapter

    companion object {
        var musicService: MusicService? = null
        val musicList = mutableListOf<Music>()
        var isPlaying: Boolean = false
        var repeat: Boolean = false
        var songPosition: Int = 0
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

        val intent = Intent(requireContext(), MusicService::class.java)
        activity?.bindService(intent, this, Context.BIND_AUTO_CREATE)
        activity?.startService(intent)

        NowPlaying.binding.btnPlayNow.setOnClickListener {
            if (isPlaying) pauseMusic() else playMusic()
        }

        NowPlaying.binding.btnNextNow.setOnClickListener {
            nextMusic()
        }
    }


    private fun initRecyclerView() {
        myAdapter = MusicAdapter()
        binding.rvMusic.adapter = myAdapter
    }

    private fun init() {
        myAdapter.onItemPosition {
            songPosition = it
            createMediaPlayer()
            setLayoutNowPlaying()
            musicService!!.showNotification(R.drawable.ic_pause)
            playMusic()
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

    private fun createMediaPlayer() {
        try {
            if (musicService?.mediaPlayer == null) musicService?.mediaPlayer = MediaPlayer()
            musicService?.mediaPlayer?.reset()
            musicService?.mediaPlayer?.setDataSource(musicList[songPosition].path)
            musicService?.mediaPlayer?.prepare()
            musicService?.mediaPlayer?.start()
            isPlaying = true
            musicService?.mediaPlayer?.setOnCompletionListener(this)
        } catch (e: Exception) {
            return
        }
    }

    private fun playMusic() {
        isPlaying = true
        musicService?.mediaPlayer?.start()
        NowPlaying.binding.btnPlayNow.setBackgroundResource(R.drawable.ic_pause)
        musicService?.showNotification(R.drawable.ic_pause)
    }

    private fun pauseMusic() {
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
        NowPlaying.binding.btnPlayNow.setBackgroundResource(R.drawable.ic_play)
        musicService!!.showNotification(R.drawable.ic_play)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun nextMusic() {
        setSongPosition(increment = true)
        musicService!!.createMediaPlayer()
        Glide.with(requireContext())
            .load(musicList[songPosition].imgUri)
            .into(NowPlaying.binding.songImageNow)
        NowPlaying.binding.tvSongNameNow.text = musicList[songPosition].title
        musicService!!.showNotification(R.drawable.ic_pause)
        playMusic()
    }

    private fun setSongPosition(increment: Boolean) {
        if (increment) {
            if (musicList.size - 1 == songPosition) songPosition =
                0
            else songPosition++
        } else {
            if (songPosition == 0) songPosition =
                musicList.size - 1
            else songPosition--
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        musicService?.showNotification(R.drawable.ic_pause)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(p0: MediaPlayer?) {
        setSongPosition(true)
        createMediaPlayer()
        try {
            setLayoutNowPlaying()
        } catch (e: Exception) {
            return
        }
    }

    private fun setLayoutNowPlaying() {
        if (musicService != null) {
            Glide.with(requireContext())
                .load(musicList[songPosition].imgUri)
                .into(NowPlaying.binding.songImageNow)

            NowPlaying.binding.tvSongNameNow.text = musicList[songPosition].title
            if (isPlaying) NowPlaying.binding.btnPlayNow.setBackgroundResource(R.drawable.ic_pause)
            else NowPlaying.binding.btnPlayNow.setBackgroundResource(R.drawable.ic_play)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvMusic.adapter = null
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        setLayoutNowPlaying()
    }
}
package com.example.mymusicplayer.ui.fragments.nowplaying

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.mymusicplayer.R
import com.example.mymusicplayer.databinding.NowplayingFragmentBinding
import com.example.mymusicplayer.ui.IMediaControl
import com.example.mymusicplayer.ui.activity.ViewPagerAdapter
import com.example.mymusicplayer.ui.fragments.MusicService
import com.example.mymusicplayer.ui.fragments.NotificationReceiver
import com.example.mymusicplayer.ui.fragments.songs.MusicAdapter
import com.example.mymusicplayer.ui.fragments.songs.SongsFragment
import com.google.android.material.tabs.TabLayoutMediator

class NowPlaying : Fragment(R.layout.nowplaying_fragment) {

    private val tabTitle = arrayOf("Songs", "Artists", "Albums")
    private var songPosition: Int = -1

    companion object {
        private var _binding: NowplayingFragmentBinding? = null
        val binding get() = _binding!!
        var musicService: MusicService? = null
        var isPlaying: Boolean = false
        var repeat: Boolean = false
        var musicReceiver: NotificationReceiver? = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = NowplayingFragmentBinding.bind(view)

//        val intent = Intent(requireContext(), MusicService::class.java)
//        activity?.bindService(intent, this, Context.BIND_AUTO_CREATE)
//        activity?.startService(intent)

        viewPager()

//        binding.root.setOnClickListener {
//            val action =
//                NowPlayingDirections.actionNowPlayingToPlayerFragment(songPosition)
//            findNavController().navigate(action)
//        }

        binding.btnPlayNow.setOnClickListener {
//            if (isPlaying) pauseMusic() else playMusic()
        }

        binding.btnNextNow.setOnClickListener {
//            nextMusic()
        }
    }

    private fun createMediaPlayer() {
        try {
            if (musicService?.mediaPlayer == null) musicService?.mediaPlayer =
                MediaPlayer()
            musicService?.mediaPlayer?.reset()
            musicService?.mediaPlayer?.setDataSource(SongsFragment.musicList[songPosition].path)
            musicService?.mediaPlayer?.prepare()
            musicService?.mediaPlayer?.start()
            isPlaying = true
//            musicService?.mediaPlayer?.setOnCompletionListener(this)
        } catch (e: Exception) {
            return
        }
    }

//    override fun playMusic() {
//        isPlaying = true
//        musicService?.mediaPlayer?.start()
//        binding.btnPlayNow.setBackgroundResource(R.drawable.ic_pause)
//        musicService?.showNotification(R.drawable.ic_pause)
//    }

//    override fun pauseMusic() {
//        isPlaying = false
//        musicService!!.mediaPlayer!!.pause()
//        binding.btnPlayNow.setBackgroundResource(R.drawable.ic_play)
//        musicService!!.showNotification(R.drawable.ic_play)
//    }

//    @RequiresApi(Build.VERSION_CODES.M)
//    override fun nextMusic() {
//        setSongPosition(increment = true)
//        musicService!!.createMediaPlayer()
//        Glide.with(requireContext())
//            .load(SongsFragment.musicList[songPosition].imgUri)
//            .into(binding.songImageNow)
//        binding.tvSongNameNow.text =
//            SongsFragment.musicList[songPosition].title
//        musicService!!.showNotification(R.drawable.ic_pause)
//        playMusic()
//    }

    private fun setSongPosition(increment: Boolean) {
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

//    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//        val binder = service as MusicService.MyBinder
//        musicService = binder.currentService()
//        musicService?.showNotification(R.drawable.ic_pause)
//        musicReceiver?.setCallBack(this)
//    }
//
//    override fun onServiceDisconnected(p0: ComponentName?) {
//        musicService = null
//    }
//
//    override fun onCompletion(p0: MediaPlayer?) {
//        setSongPosition(true)
//        createMediaPlayer()
//        try {
//            setLayoutNowPlaying()
//        } catch (e: Exception) {
//            return
//        }
//    }

    private fun setLayoutNowPlaying() {
        if (musicService != null) {
            Glide.with(requireContext())
                .load(SongsFragment.musicList[songPosition].imgUri)
                .into(binding.songImageNow)

            binding.tvSongNameNow.text =
                SongsFragment.musicList[songPosition].title
            if (isPlaying) binding.btnPlayNow.setBackgroundResource(R.drawable.ic_pause)
            else binding.btnPlayNow.setBackgroundResource(R.drawable.ic_play)
        }
    }

    private fun viewPager() {
        binding.viewPager.adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        TabLayoutMediator(binding.Tabs, binding.viewPager) { tab, position ->
            tab.text = tabTitle[position]
        }.attach()
    }

    override fun onResume() {
        super.onResume()
        setLayoutNowPlaying()
    }
}
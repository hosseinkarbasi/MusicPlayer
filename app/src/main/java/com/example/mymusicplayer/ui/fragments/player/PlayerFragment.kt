package com.example.mymusicplayer.ui.fragments.player


import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mymusicplayer.R
import com.example.mymusicplayer.data.Music
import com.example.mymusicplayer.databinding.PlayerFragmentBinding
import com.example.mymusicplayer.ui.fragments.MusicService
import com.example.mymusicplayer.ui.fragments.songs.SongsFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PlayerFragment : Fragment(R.layout.player_fragment),
    MediaPlayer.OnCompletionListener {

    private val args by navArgs<PlayerFragmentArgs>()

    companion object {
        var isPlaying: Boolean = false
        var musicService: MusicService? = null
        lateinit var binding: PlayerFragmentBinding
        var songPosition: Int = 0
        val musicList = mutableListOf<Music>()
        var repeat: Boolean = false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = PlayerFragmentBinding.bind(view)

//        val intent = Intent(requireContext(), MusicService::class.java)
//        activity?.bindService(intent, this, Context.BIND_AUTO_CREATE)
//        activity?.startService(intent)

        musicList.addAll(SongsFragment.searchList)
        songPosition = args.musicPosition

        lifecycleScope.launch {
            delay(100L)
            setLayout()
        }

        binding.btnPlay.setOnClickListener {
            if (isPlaying) pauseMusic()
            else playMusic()
        }

        binding.btnNext.setOnClickListener { prevNextSong(true) }
        binding.btnBack.setOnClickListener { prevNextSong(false) }
        binding.btnShuffle.setOnClickListener { shuffle() }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService?.mediaPlayer?.seekTo(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit
            override fun onStopTrackingTouch(p0: SeekBar?) = Unit

        })
        binding.btnRepeat.setOnClickListener {
            if (!repeat) {
                repeat = true
                binding.btnRepeat.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_500
                    )
                )
            } else {
                repeat = false
                binding.btnRepeat.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.teal_200
                    )
                )
            }
        }
    }

    private fun setLayout() {
        binding.tvSongName.text = musicList[songPosition].title
        Glide.with(requireContext())
            .load(musicList[songPosition].imgUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_launcher_background))
            .into(binding.imgSong)
        if (repeat) binding.btnRepeat.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.purple_500
            )
        )
    }

    private fun playMusic() {
        binding.btnPlay.setImageResource(R.drawable.ic_pause)
        musicService?.showNotification(R.drawable.ic_pause)
        isPlaying = true
        musicService?.mediaPlayer?.start()
    }

    private fun pauseMusic() {
        binding.btnPlay.setImageResource(R.drawable.ic_play)
        musicService?.showNotification(R.drawable.ic_play)
        isPlaying = false
        musicService?.mediaPlayer?.pause()
    }

    private fun createMediaPlayer() {
        try {
            if (musicService?.mediaPlayer == null) musicService?.mediaPlayer =
                MediaPlayer()
            musicService?.mediaPlayer?.reset()
            musicService?.mediaPlayer?.setDataSource(musicList[songPosition].path)
            musicService?.mediaPlayer?.prepare()
            musicService?.mediaPlayer?.start()
            isPlaying = true
            binding.btnPlay.setImageResource(R.drawable.ic_pause)
            binding.tvStart.text =
                formatDuration(musicService?.mediaPlayer!!.currentPosition.toLong())
            binding.tvEnd.text = formatDuration(musicService?.mediaPlayer!!.duration.toLong())
            binding.seekBar.progress = 0
            binding.seekBar.max = musicService?.mediaPlayer!!.duration
            musicService?.mediaPlayer?.setOnCompletionListener(this)
        } catch (e: Exception) {
            return
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun prevNextSong(increment: Boolean) {
        if (increment) {
            setSongPosition(true)
            setLayout()
            createMediaPlayer()
        } else {
            setSongPosition(false)
            setLayout()
            createMediaPlayer()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun shuffle() {
        musicList.shuffle()
        setLayout()
    }

    private fun setSongPosition(increment: Boolean) {
        if (repeat) {
            if (increment) {
                if (musicList.size - 1 == songPosition) songPosition = 0
                else songPosition++
            } else {
                if (songPosition == 0) songPosition = musicList.size - 1
                else songPosition--
            }
        }
    }

    private fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)
                - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%02d:%02d", minutes, seconds)
    }

//    @RequiresApi(Build.VERSION_CODES.M)
//    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//        val binder = service as MusicService.MyBinder
//        musicService = binder.currentService()
//        createMediaPlayer()
//        musicService?.showNotification(R.drawable.ic_pause)
//        musicService?.seekBarSetup()
//    }
//
//    override fun onServiceDisconnected(p0: ComponentName?) {
//        musicService = null
//    }

    override fun onCompletion(p0: MediaPlayer?) {
        setSongPosition(true)
        createMediaPlayer()
        try {
            setLayout()
        } catch (e: Exception) {
            return
        }
    }
}
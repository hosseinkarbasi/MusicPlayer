package com.example.mymusicplayer.ui.fragments.player

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.mymusicplayer.App
import com.example.mymusicplayer.R
import com.example.mymusicplayer.data.Music
import com.example.mymusicplayer.databinding.PlayerFragmentBinding
import com.example.mymusicplayer.utils.IMediaControl
import com.example.mymusicplayer.service.MusicService
import com.example.mymusicplayer.service.NotificationReceiver
import com.example.mymusicplayer.ui.viewModel.SongsViewModel
import com.example.mymusicplayer.utils.collectWithRepeatOnLifecycle
import java.util.concurrent.TimeUnit

class PlayerFragment : Fragment(R.layout.player_fragment),
    ServiceConnection,
    MediaPlayer.OnCompletionListener,
    IMediaControl {

    private var _binding: PlayerFragmentBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<PlayerFragmentArgs>()
    private lateinit var runnable: Runnable
    var musicService: MusicService? = null
    private var repeat: Boolean = false
    private val viewModel by activityViewModels<SongsViewModel>()
    private var songPosition: Int = 0
    private val musicList = mutableListOf<Music>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = PlayerFragmentBinding.bind(view)

        val intent = Intent(requireContext(), MusicService::class.java)
        activity?.bindService(intent, this, Context.BIND_AUTO_CREATE)
        activity?.startService(intent)

        initMusicList()
        updateSeekBar()

        binding.btnPlay.setOnClickListener { playPauseMusic() }
        binding.btnNext.setOnClickListener { prevNextSong(true) }
        binding.btnBack.setOnClickListener { prevNextSong(false) }
        binding.btnShuffle.setOnClickListener { shuffle() }
        binding.btnRepeat.setOnClickListener { repeat() }

    }

    private fun updateSeekBar() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService?.mediaPlayer?.seekTo(progress)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) = Unit
            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        })
    }

    private fun initMusicList() {
        songPosition = args.musicPosition
        viewModel.deviceMusic.collectWithRepeatOnLifecycle(viewLifecycleOwner) {
            musicList.clear()
            musicList.addAll(it)
            setLayout()
        }
    }

    private fun repeat() {
        if (!repeat) {
            repeat = true
            binding.btnRepeat.setImageResource(R.drawable.ic_repeat_select)
        } else {
            repeat = false
            binding.btnRepeat.setImageResource(R.drawable.ic_repeat)
        }
    }

    private fun setLayout() = binding.apply {
        tvSongName.text = musicList[songPosition].title
        Glide.with(binding.imgSong)
            .load(musicList[songPosition].imgUri)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(RequestOptions().placeholder(R.drawable.ic_launcher_background))
            .into(imgSong)
        if (repeat) binding.btnRepeat.setImageResource(R.drawable.ic_repeat_select)
    }

    private fun createMediaPlayer() = binding.apply {
        try {
            if (musicService?.mediaPlayer == null) musicService?.mediaPlayer = MediaPlayer()
            musicService?.mediaPlayer?.reset()
            musicService?.mediaPlayer?.setDataSource(musicList[songPosition].path)
            musicService?.mediaPlayer?.prepare()
            musicService?.mediaPlayer?.start()
            btnPlay.setImageResource(R.drawable.ic_pause)
            tvStart.text = formatDuration(musicService?.mediaPlayer!!.currentPosition.toLong())
            tvEnd.text = formatDuration(musicService?.mediaPlayer!!.duration.toLong())
            seekBar.progress = 0
            seekBar.max = musicService?.mediaPlayer!!.duration
            musicService?.mediaPlayer?.setOnCompletionListener(this@PlayerFragment)
        } catch (e: Exception) {
            return@apply
        }
    }

    override fun playPauseMusic() {
        if (musicService?.mediaPlayer?.isPlaying == true) {
            binding.btnPlay.setImageResource(R.drawable.ic_play)
            showNotification(R.drawable.ic_play)
            musicService?.mediaPlayer?.pause()
        } else {
            binding.btnPlay.setImageResource(R.drawable.ic_pause)
            showNotification(R.drawable.ic_pause)
            musicService?.mediaPlayer?.start()
        }
    }

    override fun prevNextSong(increment: Boolean) {
        if (increment) {
            setSongPosition(true)
            createMediaPlayer()
            setLayout()
            showNotification(R.drawable.ic_pause)
        } else {
            setSongPosition(false)
            setLayout()
            createMediaPlayer()
            showNotification(R.drawable.ic_pause)
        }
    }

    private fun shuffle() {
        musicList.shuffle()
        setLayout()
        createMediaPlayer()
    }

    private fun setSongPosition(increment: Boolean) {
        if (!repeat) {
            if (increment) {
                if (musicList.size - 1 == songPosition) songPosition = 0
                else songPosition++
                showNotification(R.drawable.ic_pause)
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

    private fun seekBarSetup() {
        runnable = Runnable {
            binding.tvStart.text =
                formatDuration(musicService?.mediaPlayer!!.currentPosition.toLong())
            binding.seekBar.progress = musicService?.mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    private fun showNotification(playPauseBtn: Int) {

        val prevIntent = Intent(
            context,
            NotificationReceiver::class.java
        ).setAction(App.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playIntent = Intent(
            context,
            NotificationReceiver::class.java
        ).setAction(App.PLAYPause)
        val playPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextIntent = Intent(
            context,
            NotificationReceiver::class.java
        ).setAction(App.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val exitIntent = Intent(
            context,
            NotificationReceiver::class.java
        ).setAction(App.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(
            context,
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
            requireContext(),
            App.CHANNEL_ID
        )
            .setContentTitle(musicList[songPosition].title)
            .setContentText(musicList[songPosition].artist)
            .setSilent(true)
            .setSmallIcon(R.drawable.ic_album)
            .setLargeIcon(image)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(musicService?.mediaSession?.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.previous, "Previous", prevPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.next, "Next", nextPendingIntent)
            .addAction(R.drawable.close, "Exit", exitPendingIntent)
            .build()

        musicService?.startForeground(13, notification)
    }

    private fun getImgArt(path: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        return retriever.embeddedPicture
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        musicService?.setCallBack(this)
        createMediaPlayer()
        showNotification(R.drawable.ic_pause)
        seekBarSetup()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(p0: MediaPlayer?) {
        setSongPosition(true)
        createMediaPlayer()
        try {
            setLayout()
        } catch (e: Exception) {
            return
        }
    }

    override fun onResume() {
        super.onResume()
        setLayout()
    }
}
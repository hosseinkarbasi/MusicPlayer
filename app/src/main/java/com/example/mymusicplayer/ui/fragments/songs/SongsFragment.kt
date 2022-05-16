package com.example.mymusicplayer.ui.fragments.songs

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mymusicplayer.R
import com.example.mymusicplayer.data.Music
import com.example.mymusicplayer.databinding.SongsFragmentBinding
import com.example.mymusicplayer.ui.fragments.nowplaying.NowPlayingFragmentDirections
import com.example.mymusicplayer.ui.viewModel.SongsViewModel
import com.example.mymusicplayer.utils.collectWithRepeatOnLifecycle
import kotlin.random.Random

class SongsFragment : Fragment(R.layout.songs_fragment) {

    private var _binding: SongsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var myAdapter: SongAdapter
    private val viewModel by activityViewModels<SongsViewModel>()
    private val musicList = mutableListOf<Music>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = SongsFragmentBinding.bind(view)

        initMusicList()
        initRecyclerView()
        shuffle()
    }

    private fun initRecyclerView() {
        myAdapter = SongAdapter()
        binding.rvMusic.adapter = myAdapter
        myAdapter.submitList(musicList)
        myAdapter.onItemPosition {
            val action = NowPlayingFragmentDirections.actionGlobalPlayerFragment(it,"")
            findNavController().navigate(action)
        }
    }

    private fun initMusicList() {
        viewModel.deviceMusic.collectWithRepeatOnLifecycle(viewLifecycleOwner) {
            musicList.clear()
            musicList.addAll(it)
            binding.shuffle.text = musicList.count().toString()
        }
    }

    private fun shuffle() {
        binding.shuffle.setOnClickListener {
            val randomIndex = (0..musicList.size).random(Random(System.currentTimeMillis()))
            val action = NowPlayingFragmentDirections.actionGlobalPlayerFragment(randomIndex,"")
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvMusic.adapter = null
        _binding = null
    }
}
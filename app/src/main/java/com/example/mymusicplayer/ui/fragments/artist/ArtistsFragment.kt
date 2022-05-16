package com.example.mymusicplayer.ui.fragments.artist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mymusicplayer.R
import com.example.mymusicplayer.databinding.ArtistsFragmentBinding
import com.example.mymusicplayer.ui.fragments.nowplaying.NowPlayingFragmentDirections
import com.example.mymusicplayer.ui.viewModel.SongsViewModel
import com.example.mymusicplayer.utils.collectWithRepeatOnLifecycle

class ArtistsFragment : Fragment(R.layout.artists_fragment) {

    private var _binding: ArtistsFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<SongsViewModel>()
    private lateinit var artisAdapter: ArtistAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = ArtistsFragmentBinding.bind(view)

        getArtists()
        initRecyclerView()

    }

    private fun getArtists() {
        viewModel.artists.collectWithRepeatOnLifecycle(viewLifecycleOwner) {
            artisAdapter.submitList(it)
        }
    }

    private fun initRecyclerView() {
        artisAdapter = ArtistAdapter()
        binding.rvArtist.adapter = artisAdapter
        artisAdapter.onItemPosition {
            val action = NowPlayingFragmentDirections.actionNowPlayingToArtistDetailsFragment(it)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvArtist.adapter = null
        _binding = null
    }
}
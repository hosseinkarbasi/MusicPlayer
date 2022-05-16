package com.example.mymusicplayer.ui.fragments.artist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mymusicplayer.R
import com.example.mymusicplayer.databinding.ArtistDetailsFragmentBinding

class ArtistDetailsFragment : Fragment(R.layout.artist_details_fragment) {

    private var _binding: ArtistDetailsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ArtistDetailsAdapter
    private val args by navArgs<ArtistDetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ArtistDetailsFragmentBinding.bind(view)

        initRecyclerView()

    }

    private fun initRecyclerView() {
        adapter = ArtistDetailsAdapter()
        binding.rvArtist.adapter = adapter
        adapter.submitList(args.artist.music)
        adapter.onItemPosition { position, artistKey ->
            val action =
                ArtistDetailsFragmentDirections.actionGlobalPlayerFragment(position, artistKey)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvArtist.adapter = null
        _binding = null
    }
}
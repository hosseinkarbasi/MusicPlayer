package com.example.mymusicplayer.ui.fragments.album

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mymusicplayer.R
import com.example.mymusicplayer.databinding.AlbumDetailsFragmentBinding

class AlbumDetailsFragment : Fragment(R.layout.album_details_fragment) {

    private var _binding: AlbumDetailsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AlbumDetailsAdapter
    private val args by navArgs<AlbumDetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = AlbumDetailsFragmentBinding.bind(view)

        initRecyclerView()

    }

    private fun initRecyclerView() {
        adapter = AlbumDetailsAdapter()
        binding.rvAlbum.adapter = adapter
        adapter.submitList(args.album.music)
        adapter.onItemPosition { position, albumKey ->
            val action =
                AlbumDetailsFragmentDirections.actionGlobalPlayerFragment(position, albumKey)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvAlbum.adapter = null
        _binding = null
    }
}
package com.example.mymusicplayer.ui.fragments.album

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mymusicplayer.R
import com.example.mymusicplayer.databinding.AlbumsFragmentBinding
import com.example.mymusicplayer.ui.fragments.nowplaying.NowPlayingFragmentDirections
import com.example.mymusicplayer.ui.viewModel.SongsViewModel
import com.example.mymusicplayer.utils.collectWithRepeatOnLifecycle

class AlbumsFragment : Fragment(R.layout.albums_fragment) {

    private var _binding: AlbumsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var albumAdapter: AlbumAdapter
    private val viewModel by activityViewModels<SongsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = AlbumsFragmentBinding.bind(view)

        getAlbums()
        initRecyclerView()
    }

    private fun getAlbums() {
        viewModel.albums.collectWithRepeatOnLifecycle(viewLifecycleOwner) {
           albumAdapter.submitList(it)
        }
    }

    private fun initRecyclerView() {
        albumAdapter = AlbumAdapter()
        binding.rvAlbum.adapter = albumAdapter
        albumAdapter.onItemPosition {
            val action = NowPlayingFragmentDirections.actionNowPlayingToAlbumDetailsFragment(it)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvAlbum.adapter = null
        _binding = null
    }
}
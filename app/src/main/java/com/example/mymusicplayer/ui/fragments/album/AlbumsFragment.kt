package com.example.mymusicplayer.ui.fragments.album

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.mymusicplayer.R
import com.example.mymusicplayer.data.Music
import com.example.mymusicplayer.databinding.AlbumsFragmentBinding
import com.example.mymusicplayer.ui.fragments.songs.SongsFragment

class AlbumsFragment : Fragment(R.layout.albums_fragment) {

    private var _binding: AlbumsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var albumAdapter: AAdapter
    private val musicList = mutableListOf<Music>()
    private var albumList = mutableMapOf<String, List<Music>>()
    private var albumList2 = ArrayList<MutableMap<String, List<Music>>>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = AlbumsFragmentBinding.bind(view)

////        musicList.addAll(SongsFragment.musicList)
//        albumList = musicList.groupBy { it.album } as MutableMap<String, List<Music>>
//        albumList2.addAll(arrayOf(albumList))
//        initRecyclerView()
    }

    private fun initRecyclerView() {
        albumAdapter = AAdapter(albumList)
        binding.rvAlbum.adapter = albumAdapter
//        albumAdapter.submitList(albumList.keys.toMutableList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvAlbum.adapter = null
        _binding = null
    }
}
package com.example.mymusicplayer.ui.fragments.nowplaying

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mymusicplayer.R
import com.example.mymusicplayer.databinding.NowplayingFragmentBinding
import com.example.mymusicplayer.ui.activity.ViewPagerAdapter
import com.example.mymusicplayer.ui.fragments.songs.SongsFragment
import com.google.android.material.tabs.TabLayoutMediator

class NowPlaying : Fragment(R.layout.nowplaying_fragment) {

    private val tabTitle = arrayOf("Songs", "Artists", "Albums")

    companion object {
        private var _binding: NowplayingFragmentBinding? = null
        val binding get() = _binding!!
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = NowplayingFragmentBinding.bind(view)

        viewPager()
        binding.root.setOnClickListener {
            val action =
                NowPlayingDirections.actionNowPlayingToPlayerFragment(SongsFragment.songPosition)
            findNavController().navigate(action)
        }
    }

    private fun viewPager() {
        binding.viewPager.adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        TabLayoutMediator(binding.Tabs, binding.viewPager) { tab, position ->
            tab.text = tabTitle[position]
        }.attach()
    }
}
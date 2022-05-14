package com.example.mymusicplayer.ui.fragments.nowplaying

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mymusicplayer.R
import com.example.mymusicplayer.databinding.NowplayingFragmentBinding
import com.example.mymusicplayer.ui.activity.ViewPagerAdapter
import com.example.mymusicplayer.service.MusicService
import com.example.mymusicplayer.ui.fragments.songs.SongsFragment
import com.google.android.material.tabs.TabLayoutMediator

class NowPlayingFragment : Fragment(R.layout.nowplaying_fragment) {

    private var songPosition: Int = -1
    private var _binding: NowplayingFragmentBinding? = null
    private val binding get() = _binding!!
    var musicService: MusicService? = null
    var isPlaying: Boolean = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = NowplayingFragmentBinding.bind(view)

        viewPager()
    }

//    private fun setLayoutNowPlaying() {
//        if (musicService != null) {
//            Glide.with(requireContext())
//                .load(SongsFragment.musicList[songPosition].imgUri)
//                .into(binding.songImageNow)
//            binding.tvSongNameNow.text =
//                SongsFragment.musicList[songPosition].title
//            if (isPlaying) binding.btnPlayNow.setBackgroundResource(R.drawable.ic_pause)
//            else binding.btnPlayNow.setBackgroundResource(R.drawable.ic_play)
//        }
//    }

    private fun viewPager() {
        val tabTitle = arrayOf("Songs", "Albums","Artists")
        val tabIcon = arrayOf(R.drawable.ic_music, R.drawable.ic_album ,R.drawable.ic_artist)
        binding.viewPager.adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        TabLayoutMediator(binding.Tabs, binding.viewPager) { tab, position ->
            tab.text = tabTitle[position]
            tab.setIcon(tabIcon[position])
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
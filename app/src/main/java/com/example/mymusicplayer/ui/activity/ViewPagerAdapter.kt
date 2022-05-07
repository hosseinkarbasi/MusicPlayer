package com.example.mymusicplayer.ui.activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mymusicplayer.ui.fragments.album.AlbumsFragment
import com.example.mymusicplayer.ui.fragments.artist.ArtistsFragment
import com.example.mymusicplayer.ui.fragments.songs.SongsFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SongsFragment()
            1 -> AlbumsFragment()
            2 -> ArtistsFragment()
            else -> SongsFragment()
        }
    }
}

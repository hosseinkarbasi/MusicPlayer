package com.example.mymusicplayer.ui.fragments.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mymusicplayer.data.Music
import com.example.mymusicplayer.databinding.AlbumItemBinding

class AAdapter(private val albumList: MutableMap<String, List<Music>>) :
    RecyclerView.Adapter<AAdapter.AlbumHolder>() {

    inner class AlbumHolder(private val binding: AlbumItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: String) {
            binding.albumNameTv.text = albumList[position].toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumHolder {
        val inflate = LayoutInflater.from(parent.context)
        val view = AlbumItemBinding.inflate(inflate, parent, false)
        return AlbumHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumHolder, position: Int) {
        holder.bind(position.toString())
    }

    override fun getItemCount() = albumList.size
}
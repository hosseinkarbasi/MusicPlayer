package com.example.mymusicplayer.ui.fragments.artist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mymusicplayer.data.Music
import com.example.mymusicplayer.databinding.SongItemMaterialBinding

class ArtistDetailsAdapter : ListAdapter<Music, ArtistDetailsAdapter.CustomViewHolder>(DiffCallBack()) {

    private var itemClick: ((position: Int, String) -> Unit)? = null

    inner class CustomViewHolder(private var binding: SongItemMaterialBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Music) {
            binding.tvSongArtisItem.text = item.artist
            binding.tvSongNameItem.text = item.title
            binding.tvSongTimeItem.text = item.formatDuration(item.duration)

            binding.root.setOnClickListener {
                itemClick?.let {
                    it(absoluteAdapterPosition, item.artist)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder =
        CustomViewHolder(
            SongItemMaterialBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun onItemPosition(clickListener: (Int,String) -> Unit) {
        itemClick = clickListener
    }

    class DiffCallBack : DiffUtil.ItemCallback<Music>() {
        override fun areItemsTheSame(oldItem: Music, newItem: Music): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Music, newItem: Music): Boolean {
            return oldItem == newItem
        }
    }
}


package com.example.mymusicplayer.ui.fragments.album

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.mymusicplayer.R
import com.example.mymusicplayer.data.Album
import com.example.mymusicplayer.databinding.AlbumItemBinding

class AlbumAdapter : ListAdapter<Album, AlbumAdapter.CustomViewHolder>(DiffCallBack()) {

    private var itemClick: ((album: Album) -> Unit)? = null

    inner class CustomViewHolder(private var binding: AlbumItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: Album) {

            binding.albumNameTv.text = item.title
            binding.albumSongCountTv.text = "${item.music?.size} song"

            Glide.with(binding.albumImg)
                .load(item.music?.get(0)?.imgUri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions().placeholder(R.drawable.ic_launcher_background))
                .into(binding.albumImg)

            binding.root.setOnClickListener {
                itemClick?.let {
                    it(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder =
        CustomViewHolder(
            AlbumItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    fun onItemPosition(clickListener: (Album) -> Unit) {
        itemClick = clickListener
    }

    class DiffCallBack : DiffUtil.ItemCallback<Album>() {

        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem == newItem
        }
    }
}


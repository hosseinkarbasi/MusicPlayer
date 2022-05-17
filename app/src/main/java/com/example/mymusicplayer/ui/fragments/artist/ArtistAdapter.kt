package com.example.mymusicplayer.ui.fragments.artist

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
import com.example.mymusicplayer.data.Artist
import com.example.mymusicplayer.databinding.ArtistItemBinding

class ArtistAdapter : ListAdapter<Artist, ArtistAdapter.CustomViewHolder>(DiffCallBack()) {

    private var itemClick: ((album: Artist) -> Unit)? = null

    inner class CustomViewHolder(private var binding: ArtistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: Artist) {

            binding.artistNameTv.text = item.title
            binding.artistSongCountTv.text = "${item.music?.size} song"

            Glide.with(binding.artistImg)
                .load(item.music?.get(0)?.imgUri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions().placeholder(R.drawable.ic_launcher_background))
                .into(binding.artistImg)

            binding.root.setOnClickListener {
                itemClick?.let {
                    it(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder =
        CustomViewHolder(
            ArtistItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    fun onItemPosition(clickListener: (Artist) -> Unit) {
        itemClick = clickListener
    }

    class DiffCallBack : DiffUtil.ItemCallback<Artist>() {

        override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem == newItem
        }
    }
}


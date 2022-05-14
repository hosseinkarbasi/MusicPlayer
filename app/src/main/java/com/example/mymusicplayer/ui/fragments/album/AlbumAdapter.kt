package com.example.mymusicplayer.ui.fragments.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mymusicplayer.data.Music
import com.example.mymusicplayer.databinding.AlbumItemBinding

class AlbumAdapter : ListAdapter<String,
        AlbumAdapter.CustomViewHolder>(DiffCallBack()) {

    private var itemClick: ((position: Int) -> Unit)? = null

    inner class CustomViewHolder(private var binding: AlbumItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {

            binding.albumNameTv.text = item
//            binding.albumSongCountTv.text = item.size.toString()

//            Glide.with(binding.albumImg)
//                .load(item.values[])
//                .apply(RequestOptions().placeholder(R.drawable.ic_launcher_background))
//                .into(binding.albumImg)

            binding.root.setOnClickListener {
                itemClick?.let {
                    it(absoluteAdapterPosition)
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


    fun onItemPosition(clickListener: (Int) -> Unit) {
        itemClick = clickListener
    }

    class DiffCallBack : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}


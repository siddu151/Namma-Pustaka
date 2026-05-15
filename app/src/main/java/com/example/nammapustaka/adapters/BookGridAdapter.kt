package com.example.nammapustaka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nammapustaka.R
import com.example.nammapustaka.database.entity.BookEntity
import com.example.nammapustaka.databinding.ItemBookBinding

class BookGridAdapter(
    private val onClick: (BookEntity) -> Unit
) : ListAdapter<BookEntity, BookGridAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inf = LayoutInflater.from(parent.context)
        val binding = ItemBookBinding.inflate(inf, parent, false)
        return VH(binding, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        private val binding: ItemBookBinding,
        private val onClick: (BookEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(b: BookEntity) {
            binding.tvTitle.text = b.title
            binding.tvAuthor.text = b.author
            binding.chipCategory.text = b.category
            binding.tvAvailability.text = if (b.available) {
                binding.root.context.getString(R.string.available)
            } else {
                binding.root.context.getString(R.string.unavailable)
            }
            binding.tvAvailability.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    if (b.available) R.color.np_primary else R.color.np_overdue
                )
            )
            val uri = b.imageUri
            if (!uri.isNullOrBlank()) {
                Glide.with(binding.ivCover).load(uri).placeholder(R.drawable.ic_book_24).into(binding.ivCover)
            } else {
                binding.ivCover.setImageResource(R.drawable.ic_book_24)
            }
            binding.root.setOnClickListener { onClick(b) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<BookEntity>() {
            override fun areItemsTheSame(oldItem: BookEntity, newItem: BookEntity): Boolean =
                oldItem.bookId == newItem.bookId

            override fun areContentsTheSame(oldItem: BookEntity, newItem: BookEntity): Boolean =
                oldItem == newItem
        }
    }
}

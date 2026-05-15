package com.example.nammapustaka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nammapustaka.R
import com.example.nammapustaka.databinding.ItemLeaderboardBinding
import com.example.nammapustaka.models.LeaderboardUiRow

class LeaderboardRowsAdapter : RecyclerView.Adapter<LeaderboardRowsAdapter.VH>() {

    private val items = mutableListOf<LeaderboardUiRow>()

    fun submit(list: List<LeaderboardUiRow>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position], position + 1)
    }

    class VH(private val binding: ItemLeaderboardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(row: LeaderboardUiRow, rank: Int) {
            binding.tvRank.text = "#$rank"
            binding.tvName.text = row.name
            binding.tvCount.text = "${row.pagesRead} pages • ${row.booksCompleted} books"
            val ctx = binding.root.context
            if (rank <= 3) {
                val c = when (rank) {
                    1 -> R.color.np_secondary
                    2 -> R.color.np_primary
                    else -> R.color.np_primary
                }
                binding.root.setCardBackgroundColor(ContextCompat.getColor(ctx, c))
                binding.tvName.setTextColor(ContextCompat.getColor(ctx, R.color.np_on_primary))
                binding.tvRank.setTextColor(ContextCompat.getColor(ctx, R.color.np_on_primary))
                binding.tvCount.setTextColor(ContextCompat.getColor(ctx, R.color.np_on_primary))
            } else {
                binding.root.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.np_surface))
                binding.tvName.setTextColor(ContextCompat.getColor(ctx, R.color.np_primary))
                binding.tvRank.setTextColor(ContextCompat.getColor(ctx, R.color.np_primary))
                binding.tvCount.setTextColor(ContextCompat.getColor(ctx, R.color.np_primary))
            }
            val uri = row.imageUri
            if (!uri.isNullOrBlank()) {
                Glide.with(binding.ivAvatar).load(uri).circleCrop().into(binding.ivAvatar)
            } else {
                binding.ivAvatar.setImageResource(R.drawable.ic_book_24)
            }
        }
    }
}

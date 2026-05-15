package com.example.nammapustaka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nammapustaka.databinding.ItemReviewBinding
import com.example.nammapustaka.models.ReviewListRow

class ReviewRowsAdapter : RecyclerView.Adapter<ReviewRowsAdapter.VH>() {

    private val items = mutableListOf<ReviewListRow>()

    fun submit(list: List<ReviewListRow>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    class VH(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(row: ReviewListRow) {
            binding.tvStudent.text = row.studentName
            binding.tvRating.text = "★ ${row.rating}"
            binding.tvText.text = row.reviewText
        }
    }
}

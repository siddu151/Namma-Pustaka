package com.example.nammapustaka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nammapustaka.databinding.ItemReservedRowBinding
import com.example.nammapustaka.models.ReservationListRow
import com.example.nammapustaka.utils.DateUtils

class ReservedRowsAdapter : RecyclerView.Adapter<ReservedRowsAdapter.VH>() {

    private val items = mutableListOf<ReservationListRow>()

    fun submit(list: List<ReservationListRow>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemReservedRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    class VH(private val binding: ItemReservedRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(row: ReservationListRow) {
            binding.tvTitle.text = "${row.bookTitle}\nQueued on ${DateUtils.format(row.createdAt)}"
        }
    }
}

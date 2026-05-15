package com.example.nammapustaka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nammapustaka.databinding.ItemDashboardCardBinding
import com.example.nammapustaka.models.DashboardCard

class DashboardAdapter(
    private val onClick: (DashboardCard) -> Unit
) : RecyclerView.Adapter<DashboardAdapter.VH>() {

    private val items = mutableListOf<DashboardCard>()

    fun submit(list: List<DashboardCard>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemDashboardCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b, onClick)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    class VH(
        private val binding: ItemDashboardCardBinding,
        private val onClick: (DashboardCard) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(card: DashboardCard) {
            binding.tvTitle.text = card.title
            binding.tvSubtitle.text = card.subtitle
            binding.root.setOnClickListener { onClick(card) }
        }
    }
}

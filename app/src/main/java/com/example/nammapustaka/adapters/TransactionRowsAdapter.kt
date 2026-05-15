package com.example.nammapustaka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nammapustaka.databinding.ItemTransactionBinding
import com.example.nammapustaka.models.RecentTransactionRow
import com.example.nammapustaka.utils.DateUtils

class TransactionRowsAdapter : RecyclerView.Adapter<TransactionRowsAdapter.VH>() {

    private val items = mutableListOf<RecentTransactionRow>()

    fun submit(list: List<RecentTransactionRow>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    class VH(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(row: RecentTransactionRow) {
            binding.tvBook.text = row.bookTitle
            binding.tvStudent.text = row.studentName
            val ret = row.returnDate?.let { DateUtils.format(it) } ?: "—"
            binding.tvMeta.text = "${DateUtils.format(row.issueDate)} → due ${DateUtils.format(row.dueDate)} | returned: $ret | ${row.status}"
        }
    }
}

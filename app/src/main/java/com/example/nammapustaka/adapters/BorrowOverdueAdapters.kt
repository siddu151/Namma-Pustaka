package com.example.nammapustaka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.nammapustaka.R
import com.example.nammapustaka.databinding.ItemBorrowedBinding
import com.example.nammapustaka.models.BorrowListRow
import com.example.nammapustaka.models.OverdueRow
import com.example.nammapustaka.utils.DateUtils

class OverdueRowsAdapter : RecyclerView.Adapter<OverdueRowsAdapter.VH>() {

    private val items = mutableListOf<OverdueRow>()

    fun submit(list: List<OverdueRow>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemBorrowedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    class VH(private val binding: ItemBorrowedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(row: OverdueRow) {
            binding.tvBook.text = row.bookTitle
            binding.tvStudent.text = row.studentName
            binding.tvDates.text =
                "Issued ${DateUtils.format(row.issueDate)} • Due ${DateUtils.format(row.dueDate)}"
            binding.tvOverdue.visibility = android.view.View.VISIBLE
            binding.tvOverdue.text = binding.root.context.getString(
                R.string.overdue_days,
                row.overdueDays.toInt().coerceAtLeast(0)
            )
            binding.tvOverdue.setTextColor(ContextCompat.getColor(binding.root.context, R.color.np_overdue))
        }
    }
}

class BorrowRowsAdapter : RecyclerView.Adapter<BorrowRowsAdapter.VH>() {

    private val items = mutableListOf<BorrowListRow>()

    fun submit(list: List<BorrowListRow>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemBorrowedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    class VH(private val binding: ItemBorrowedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(row: BorrowListRow) {
            binding.tvBook.text = row.bookTitle
            binding.tvStudent.text = row.studentName
            binding.tvDates.text = "Due ${DateUtils.format(row.dueDate)}"
            binding.tvOverdue.visibility = android.view.View.GONE
        }
    }
}

package com.example.nammapustaka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.nammapustaka.NammaPustakaApp
import com.example.nammapustaka.R
import com.example.nammapustaka.adapters.ReviewRowsAdapter
import com.example.nammapustaka.databinding.FragmentBookDetailBinding
import com.example.nammapustaka.viewmodel.AppViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class BookDetailFragment : Fragment() {

    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!

    private val vm: BookDetailViewModel by viewModels {
        AppViewModelFactory.from(requireActivity().application as NammaPustakaApp)
    }

    private val bookId: Long by lazy { arguments?.getLong("bookId", -1L) ?: -1L }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (bookId <= 0) {
            Toast.makeText(requireContext(), "Invalid book", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }
        vm.setBookId(bookId)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        val reviewAdapter = ReviewRowsAdapter()
        binding.rvReviews.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReviews.adapter = reviewAdapter

        vm.book.observe(viewLifecycleOwner) { b ->
            if (b == null) return@observe
            binding.tvTitle.text = b.title
            binding.tvAuthor.text = b.author
            binding.tvCategory.text = b.category
            binding.tvDescription.text = b.description
            binding.tvAvailability.text = if (b.available) getString(R.string.available) else getString(R.string.unavailable)
            binding.tvAvailability.setTextColor(
                ContextCompat.getColor(requireContext(), if (b.available) R.color.np_primary else R.color.np_overdue)
            )
            if (!b.imageUri.isNullOrBlank()) {
                Glide.with(this).load(b.imageUri).into(binding.ivCover)
            }
            binding.tvSummary.text = b.kannadaSummary.orEmpty().ifBlank { getString(R.string.kannada_summary) + ": —" }
            binding.tvDifficulty.text = getString(R.string.reading_level) + ": " + (b.readingDifficulty ?: "—")
        }

        vm.averageRating.observe(viewLifecycleOwner) { avg ->
            binding.tvAvgRating.text = if (avg == null) "—" else String.format("%.1f ★", avg)
        }

        vm.reviews.observe(viewLifecycleOwner) { list ->
            reviewAdapter.submit(list)
        }

        binding.btnReserve.visibility = if (vm.isAdmin()) View.GONE else View.VISIBLE

        binding.btnGenerateSummary.setOnClickListener {
            binding.progress.visibility = View.VISIBLE
            lifecycleScope.launch {
                val r = vm.generateSummary()
                binding.progress.visibility = View.GONE
                r.onSuccess { vm.refreshBook() }
                Toast.makeText(
                    requireContext(),
                    r.fold(onSuccess = { "Summary updated" }, onFailure = { it.message ?: "Error" }),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.btnReserve.setOnClickListener {
            lifecycleScope.launch {
                val r = vm.reserve()
                Toast.makeText(requireContext(), r.fold(onSuccess = { "Reserved" }, onFailure = { it.message ?: "Error" }), Toast.LENGTH_LONG).show()
            }
        }

        binding.btnReview.setOnClickListener {
            val ratingBar = RatingBar(requireContext()).apply {
                numStars = 5
                stepSize = 1f
                rating = 4f
            }
            val edit = TextInputEditText(requireContext()).apply { hint = getString(R.string.review) }
            val box = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(48, 16, 48, 0)
                addView(ratingBar)
                addView(edit)
            }
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.review_book))
                .setView(box)
                .setPositiveButton(R.string.save) { _, _ ->
                    lifecycleScope.launch {
                        val r = vm.addReview(ratingBar.rating, edit.text?.toString().orEmpty())
                        Toast.makeText(requireContext(), r.fold(onSuccess = { "Thanks!" }, onFailure = { it.message ?: "Error" }), Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        binding.btnDelete.visibility = if (vm.isAdmin()) View.VISIBLE else View.GONE
        binding.btnEdit.visibility = if (vm.isAdmin()) View.VISIBLE else View.GONE
        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.addEditBookFragment, bundleOf("bookId" to bookId))
        }

        binding.btnDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_book)
                .setMessage("This cannot be undone.")
                .setPositiveButton(R.string.delete_book) { _, _ ->
                    lifecycleScope.launch {
                        val r = vm.deleteBook()
                        Toast.makeText(requireContext(), r.fold(onSuccess = { "Deleted" }, onFailure = { it.message ?: "Error" }), Toast.LENGTH_LONG).show()
                        if (r.isSuccess) findNavController().popBackStack()
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

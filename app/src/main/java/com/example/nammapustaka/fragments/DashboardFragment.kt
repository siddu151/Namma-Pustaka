package com.example.nammapustaka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nammapustaka.NammaPustakaApp
import com.example.nammapustaka.R
import com.example.nammapustaka.adapters.DashboardAdapter
import com.example.nammapustaka.databinding.FragmentDashboardBinding
import com.example.nammapustaka.models.DashboardCard
import com.example.nammapustaka.models.UserRole

/**
 * Role-aware dashboard shortcuts (CardView grid).
 */
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val session = (requireActivity().application as NammaPustakaApp).sessionManager
        val adapter = DashboardAdapter { card ->
            findNavController().navigate(card.destinationId)
        }
        binding.rvCards.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCards.adapter = adapter

        val cards = if (session.role == UserRole.ADMIN) {
            listOf(
                DashboardCard(getString(R.string.add_book), "Register new titles", R.id.addEditBookFragment),
                DashboardCard(getString(R.string.book_catalog), "Browse & edit", R.id.bookCatalogFragment),
                DashboardCard(getString(R.string.scan_qr), "Issue / return with QR", R.id.issueReturnFragment),
                DashboardCard(getString(R.string.transaction_history), "All issues & returns", R.id.transactionsFragment),
                DashboardCard(getString(R.string.overdue_books), "Red alerts & days late", R.id.overdueFragment),
                DashboardCard(getString(R.string.student_reports), "Counts & health", R.id.reportsFragment),
                DashboardCard(getString(R.string.leaderboard), "Top readers", R.id.leaderboardFragment),
                DashboardCard(getString(R.string.profile), "Admin overview", R.id.profileFragment)
            )
        } else {
            listOf(
                DashboardCard(getString(R.string.book_catalog), "Browse & search", R.id.bookCatalogFragment),
                DashboardCard(getString(R.string.reserved_books), "Your wait-list", R.id.reservedFragment),
                DashboardCard(getString(R.string.leaderboard), "School reading stars", R.id.leaderboardFragment),
                DashboardCard("AI summary", "Open any book → Generate Kannada summary", R.id.bookCatalogFragment),
                DashboardCard("Reviews", "Rate books from book details", R.id.bookCatalogFragment),
                DashboardCard(getString(R.string.profile), "Your reading stats", R.id.profileFragment)
            )
        }
        adapter.submit(cards)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

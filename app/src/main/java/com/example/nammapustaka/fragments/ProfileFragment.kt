package com.example.nammapustaka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nammapustaka.NammaPustakaApp
import com.example.nammapustaka.adapters.BorrowRowsAdapter
import com.example.nammapustaka.databinding.FragmentProfileBinding
import com.example.nammapustaka.viewmodel.AppViewModelFactory
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val vm: ProfileViewModel by viewModels {
        AppViewModelFactory.from(requireActivity().application as NammaPustakaApp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm.refreshSessionUser()
        val borrowAdapter = BorrowRowsAdapter()
        binding.rvBorrows.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBorrows.adapter = borrowAdapter

        vm.me.observe(viewLifecycleOwner) { u ->
            if (u == null) return@observe
            binding.tvName.text = u.name
            binding.tvEmail.text = u.email
            binding.tvClass.text = u.className?.let { "Class: $it" }.orEmpty()
        }

        vm.myBorrows.observe(viewLifecycleOwner) { borrowAdapter.submit(it) }

        if (vm.isAdmin()) {
            lifecycleScope.launch {
                val t = vm.adminStats()
                binding.tvStats.text =
                    "Books in library: ${t.first}\nStudents: ${t.second}\nTransactions: ${t.third}"
            }
        } else {
            val uid = (requireActivity().application as NammaPustakaApp).sessionManager.userId
            vm.leaderboard.observe(viewLifecycleOwner) { rows ->
                val row = rows.firstOrNull { it.studentId == uid }
                binding.tvStats.text =
                    "Pages read (leaderboard): ${row?.pagesRead ?: 0}\nBooks completed: ${row?.booksCompleted ?: 0}"
            }
        }

        binding.btnLogout.setOnClickListener {
            (requireActivity().application as NammaPustakaApp).sessionManager.clear()
            requireActivity().recreate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

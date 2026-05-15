package com.example.nammapustaka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nammapustaka.NammaPustakaApp
import com.example.nammapustaka.databinding.FragmentReportsBinding
import com.example.nammapustaka.viewmodel.AppViewModelFactory

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private val vm: ReportsViewModel by viewModels {
        AppViewModelFactory.from(requireActivity().application as NammaPustakaApp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        vm.stats.observe(viewLifecycleOwner) { t ->
            if (t != null) {
                binding.tvStats.text =
                    "Total books: ${t.first}\nTotal students: ${t.second}\nTotal transactions: ${t.third}"
            }
        }
        binding.btnRefresh.setOnClickListener { vm.reload() }
        vm.reload()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

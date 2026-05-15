package com.example.nammapustaka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nammapustaka.NammaPustakaApp
import com.example.nammapustaka.adapters.ReservedRowsAdapter
import com.example.nammapustaka.databinding.FragmentReservedBinding
import com.example.nammapustaka.viewmodel.AppViewModelFactory

class ReservedFragment : Fragment() {

    private var _binding: FragmentReservedBinding? = null
    private val binding get() = _binding!!

    private val vm: ReservedViewModel by viewModels {
        AppViewModelFactory.from(requireActivity().application as NammaPustakaApp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReservedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        val adapter = ReservedRowsAdapter()
        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        binding.rv.adapter = adapter
        vm.rows.observe(viewLifecycleOwner) { list ->
            adapter.submit(list)
            binding.tvEmpty.visibility = if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.nammapustaka.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nammapustaka.NammaPustakaApp
import com.example.nammapustaka.R
import com.example.nammapustaka.databinding.FragmentRegisterBinding
import com.example.nammapustaka.models.UserRole
import com.example.nammapustaka.viewmodel.AppViewModelFactory

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val vm: AuthViewModel by viewModels {
        AppViewModelFactory.from(requireActivity().application as NammaPustakaApp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val roles = listOf("Student", "Admin / Teacher")
        binding.spRole.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text?.toString().orEmpty()
            val email = binding.etEmail.text?.toString().orEmpty()
            val pass = binding.etPassword.text?.toString().orEmpty()
            val cls = binding.etClass.text?.toString()
            val role = if (binding.spRole.selectedItemPosition == 0) UserRole.STUDENT else UserRole.ADMIN
            vm.register(name, email, pass, role, cls).observe(viewLifecycleOwner) { result ->
                result.fold(
                    onSuccess = {
                        Toast.makeText(requireContext(), "Registered successfully", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_register_to_login)
                    },
                    onFailure = { e ->
                        Toast.makeText(requireContext(), e.message ?: "Error", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
        binding.btnToLogin.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

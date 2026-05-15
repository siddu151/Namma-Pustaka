package com.example.nammapustaka.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nammapustaka.NammaPustakaApp
import com.example.nammapustaka.R
import com.example.nammapustaka.activities.MainActivity
import com.example.nammapustaka.databinding.FragmentLoginBinding
import com.example.nammapustaka.viewmodel.AppViewModelFactory

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val vm: AuthViewModel by viewModels {
        AppViewModelFactory.from(requireActivity().application as NammaPustakaApp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString().orEmpty()
            val pass = binding.etPassword.text?.toString().orEmpty()
            vm.login(email, pass).observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    (activity as? MainActivity)?.refreshBottomNavigationMenu()
                    findNavController().navigate(R.id.action_login_to_dashboard)
                }
            }
        }
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
        binding.btnForgot.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_forgot)
        }
        vm.loginError.observe(viewLifecycleOwner) { err ->
            if (!err.isNullOrBlank()) Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

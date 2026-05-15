package com.example.nammapustaka.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nammapustaka.NammaPustakaApp
import com.example.nammapustaka.R
import com.example.nammapustaka.databinding.FragmentSplashBinding

/**
 * Branded splash with simple fade animation; routes to login or main dashboard.
 */
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ObjectAnimator.ofFloat(binding.logo, View.ALPHA, 0.2f, 1f).setDuration(900).start()
        Handler(Looper.getMainLooper()).postDelayed({
            val session = (requireActivity().application as NammaPustakaApp).sessionManager
            if (session.isLoggedIn) {
                findNavController().navigate(R.id.action_splash_to_dashboard)
            } else {
                findNavController().navigate(R.id.action_splash_to_login)
            }
        }, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

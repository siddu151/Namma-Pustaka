package com.example.nammapustaka.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.nammapustaka.NammaPustakaApp
import com.example.nammapustaka.R
import com.example.nammapustaka.databinding.ActivityMainBinding
import com.example.nammapustaka.models.UserRole

/**
 * Single-activity host: Navigation Component + role-aware bottom navigation.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* optional follow-up */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        maybeRequestNotificationPermission()

        val navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHost.navController

        refreshBottomNavigationMenu()

        NavigationUI.setupWithNavController(binding.bottomNav, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hide = when (destination.id) {
                R.id.splashFragment,
                R.id.loginFragment,
                R.id.registerFragment,
                R.id.forgotPasswordFragment -> true
                else -> false
            }
            binding.bottomNav.isVisible = !hide && session().isLoggedIn
        }
    }

    /** Call after login / logout so the correct tabs appear. */
    fun refreshBottomNavigationMenu() {
        binding.bottomNav.menu.clear()
        if (!session().isLoggedIn) return
        when (session().role) {
            UserRole.ADMIN -> binding.bottomNav.inflateMenu(R.menu.bottom_nav_admin)
            UserRole.STUDENT -> binding.bottomNav.inflateMenu(R.menu.bottom_nav_student)
        }
    }

    private fun session() = (application as NammaPustakaApp).sessionManager

    private fun maybeRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val ok = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!ok) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

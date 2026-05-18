package com.namma.homestay.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.namma.homestay.R
import com.namma.homestay.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // ✅ NEW: Dashboard tab added — shows profile details + photos
                R.id.nav_dashboard -> switchFragment(DashboardFragment())
                R.id.nav_profile -> switchFragment(ProfileFragment())
                R.id.nav_menu -> switchFragment(MenuFragment())
                R.id.nav_inquiries -> switchFragment(InquiryFragment())
                R.id.nav_guide -> switchFragment(GuideFragment())
            }
            true
        }

        if (savedInstanceState == null) {
            // ✅ App now opens on Dashboard tab by default
            binding.bottomNavigation.selectedItemId = R.id.nav_dashboard
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}
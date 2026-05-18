package com.namma.homestay.ui

import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.namma.homestay.databinding.FragmentDashboardBinding
import com.namma.homestay.viewmodel.MainViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            if (profile == null || profile.homeName.isEmpty()) {
                binding.layoutContent.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
                return@observe
            }

            binding.layoutContent.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE

            binding.tvHomeName.text = profile.homeName
            binding.tvLocation.text = profile.location
            binding.tvDescription.text = profile.description.ifEmpty { "No description" }
            binding.tvPrice.text = if (profile.pricePerNight.isNotEmpty())
                "₹${profile.pricePerNight} / night" else "Price not set"

            binding.cbCleanRoom.isChecked = profile.cleanRoom
            binding.cbCleanToilet.isChecked = profile.cleanToilet
            binding.cbSafeWater.isChecked = profile.safeDrinkingWater
            binding.cbFood.isChecked = profile.foodAvailable

            // ✅ Load Base64 images
            if (profile.roomImageUrl.isNotEmpty()) {
                binding.ivRoom.visibility = View.VISIBLE
                loadBase64Image(profile.roomImageUrl, binding.ivRoom)
            } else {
                binding.ivRoom.visibility = View.GONE
            }

            if (profile.toiletImageUrl.isNotEmpty()) {
                binding.ivToilet.visibility = View.VISIBLE
                loadBase64Image(profile.toiletImageUrl, binding.ivToilet)
            } else {
                binding.ivToilet.visibility = View.GONE
            }

            if (profile.farmImageUrl.isNotEmpty()) {
                binding.ivFarm.visibility = View.VISIBLE
                loadBase64Image(profile.farmImageUrl, binding.ivFarm)
            } else {
                binding.ivFarm.visibility = View.GONE
            }
        }
    }

    private fun loadBase64Image(base64: String, imageView: ImageView) {
        if (base64.isEmpty()) return
        lifecycleScope.launch {
            try {
                val bytes = withContext(Dispatchers.IO) {
                    Base64.decode(base64, Base64.DEFAULT)
                }
                if (_binding != null) {
                    Glide.with(this@DashboardFragment).load(bytes).into(imageView)
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
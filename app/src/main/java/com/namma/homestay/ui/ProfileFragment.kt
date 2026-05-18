package com.namma.homestay.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.namma.homestay.data.model.HomeProfile
import com.namma.homestay.databinding.FragmentProfileBinding
import com.namma.homestay.viewmodel.MainViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    private var roomImageUri: Uri? = null
    private var toiletImageUri: Uri? = null
    private var farmImageUri: Uri? = null

    private var currentImageType = ""

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                when (currentImageType) {
                    "room" -> {
                        roomImageUri = uri
                        binding.ivRoom.setImageURI(uri)
                    }
                    "toilet" -> {
                        toiletImageUri = uri
                        binding.ivToilet.setImageURI(uri)
                    }
                    "farm" -> {
                        farmImageUri = uri
                        binding.ivFarm.setImageURI(uri)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.cardRoom.setOnClickListener { openImagePicker("room") }
        binding.cardToilet.setOnClickListener { openImagePicker("toilet") }
        binding.cardFarm.setOnClickListener { openImagePicker("farm") }

        binding.btnSave.setOnClickListener {
            // ✅ Preserve existing image data if no new image was picked
            val existing = viewModel.profile.value
            val profile = HomeProfile(
                homeName = binding.etHomeName.text.toString(),
                location = binding.etLocation.text.toString(),
                description = binding.etDescription.text.toString(),
                pricePerNight = binding.etPrice.text.toString(),
                cleanRoom = binding.cbCleanRoom.isChecked,
                cleanToilet = binding.cbCleanToilet.isChecked,
                safeDrinkingWater = binding.cbSafeWater.isChecked,
                foodAvailable = binding.cbFood.isChecked,
                roomImageUrl = existing?.roomImageUrl ?: "",
                toiletImageUrl = existing?.toiletImageUrl ?: "",
                farmImageUrl = existing?.farmImageUrl ?: ""
            )
            viewModel.saveProfile(profile, roomImageUri, toiletImageUri, farmImageUri)
        }
    }

    private fun openImagePicker(type: String) {
        currentImageType = type
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun observeViewModel() {
        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                binding.etHomeName.setText(profile.homeName)
                binding.etLocation.setText(profile.location)
                binding.etDescription.setText(profile.description)
                binding.etPrice.setText(profile.pricePerNight)
                binding.cbCleanRoom.isChecked = profile.cleanRoom
                binding.cbCleanToilet.isChecked = profile.cleanToilet
                binding.cbSafeWater.isChecked = profile.safeDrinkingWater
                binding.cbFood.isChecked = profile.foodAvailable

                // ✅ Load Base64 image: decode the string back to bytes and display
                loadBase64Image(profile.roomImageUrl, binding.ivRoom)
                loadBase64Image(profile.toiletImageUrl, binding.ivToilet)
                loadBase64Image(profile.farmImageUrl, binding.ivFarm)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !isLoading
        }

        viewModel.successMessage.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                viewModel.clearMessages()
            }
        }
    }

    // ✅ Helper: convert Base64 string → show in ImageView using Glide
    private fun loadBase64Image(base64: String, imageView: android.widget.ImageView) {
        if (base64.isEmpty()) return
        lifecycleScope.launch {
            try {
                val bytes = withContext(Dispatchers.IO) {
                    Base64.decode(base64, Base64.DEFAULT)
                }
                if (_binding != null) {
                    Glide.with(this@ProfileFragment).load(bytes).into(imageView)
                }
            } catch (e: Exception) {
                // image data corrupted — ignore
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
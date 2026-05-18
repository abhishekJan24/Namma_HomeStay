package com.namma.homestay.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.namma.homestay.databinding.FragmentGuideBinding
import com.namma.homestay.viewmodel.MainViewModel

class GuideFragment : Fragment() {

    private var _binding: FragmentGuideBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: GuideAdapter

    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                selectedImageUri = uri
                binding.ivSelectedImage.setImageURI(uri)
                binding.ivSelectedImage.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = GuideAdapter()
        binding.rvGuides.layoutManager = LinearLayoutManager(context)
        binding.rvGuides.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }

        binding.btnAddPlace.setOnClickListener {
            val name = binding.etPlaceName.text.toString().trim()
            val desc = binding.etPlaceDesc.text.toString().trim()
            if (name.isNotEmpty() && desc.isNotEmpty()) {
                viewModel.addGuide(name, desc, selectedImageUri)
                binding.etPlaceName.text?.clear()
                binding.etPlaceDesc.text?.clear()
                selectedImageUri = null
                binding.ivSelectedImage.visibility = View.GONE
            } else {
                Toast.makeText(context, "Please enter name and description", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.guides.observe(viewLifecycleOwner) { guides ->
            adapter.submitList(guides)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
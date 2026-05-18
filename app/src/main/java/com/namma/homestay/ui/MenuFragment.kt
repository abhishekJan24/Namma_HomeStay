package com.namma.homestay.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.namma.homestay.databinding.FragmentMenuBinding
import com.namma.homestay.viewmodel.MainViewModel

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: MenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
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
        adapter = MenuAdapter()
        binding.rvMenu.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMenu.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnAddMenu.setOnClickListener {

            val itemName = binding.etMenuItem.text?.toString()?.trim() ?: ""
            val price = binding.etPrice.text?.toString()?.trim() ?: ""

            if (itemName.isNotEmpty() && price.isNotEmpty()) {

                viewModel.addMenuItem(itemName, price)

                // Clear inputs
                binding.etMenuItem.text?.clear()
                binding.etPrice.text?.clear()

            } else {
                Toast.makeText(requireContext(), "Enter item and price", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.menuItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }

        // Optional: observe messages (better UX)
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
            }
        }

        viewModel.successMessage.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

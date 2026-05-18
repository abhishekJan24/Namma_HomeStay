package com.namma.homestay.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.namma.homestay.databinding.FragmentInquiriesBinding
import com.namma.homestay.viewmodel.MainViewModel

class InquiryFragment : Fragment() {

    private var _binding: FragmentInquiriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: InquiryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInquiriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = InquiryAdapter()
        binding.rvInquiries.layoutManager = LinearLayoutManager(context)
        binding.rvInquiries.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.inquiries.observe(viewLifecycleOwner) { inquiries ->
            adapter.submitList(inquiries)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

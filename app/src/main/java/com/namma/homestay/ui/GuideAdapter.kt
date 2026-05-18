package com.namma.homestay.ui

import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.namma.homestay.data.model.LocalGuide
import com.namma.homestay.databinding.ItemGuideBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GuideAdapter : ListAdapter<LocalGuide, GuideAdapter.GuideViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LocalGuide>() {
            override fun areItemsTheSame(oldItem: LocalGuide, newItem: LocalGuide) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: LocalGuide, newItem: LocalGuide) =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val binding = ItemGuideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GuideViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: GuideViewHolder) {
        super.onViewRecycled(holder)
        holder.cancelImageLoad()
    }

    inner class GuideViewHolder(private val binding: ItemGuideBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var imageJob: Job? = null

        fun bind(item: LocalGuide) {
            binding.tvGuideName.text = item.name
            binding.tvGuideDesc.text = item.description
            loadBase64ImageAsync(item.imageUrl, binding.ivGuideImage)
        }

        fun cancelImageLoad() {
            imageJob?.cancel()
        }

        private fun loadBase64ImageAsync(base64: String, imageView: ImageView) {
            imageJob?.cancel()
            if (base64.isEmpty()) {
                imageView.setImageDrawable(null)
                return
            }
            imageJob = CoroutineScope(Dispatchers.Main).launch {
                try {
                    val bytes = withContext(Dispatchers.IO) {
                        Base64.decode(base64, Base64.DEFAULT)
                    }
                    Glide.with(imageView.context).load(bytes).into(imageView)
                } catch (e: Exception) {
                    // corrupted image data — ignore
                }
            }
        }
    }
}
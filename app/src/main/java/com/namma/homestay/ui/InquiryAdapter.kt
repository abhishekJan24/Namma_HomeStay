package com.namma.homestay.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namma.homestay.data.model.Inquiry
import com.namma.homestay.databinding.ItemInquiryBinding

class InquiryAdapter : RecyclerView.Adapter<InquiryAdapter.InquiryViewHolder>() {

    private var items = listOf<Inquiry>()

    fun submitList(newItems: List<Inquiry>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InquiryViewHolder {
        val binding = ItemInquiryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InquiryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InquiryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class InquiryViewHolder(private val binding: ItemInquiryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Inquiry) {
            binding.tvName.text = item.name
            binding.tvMessage.text = item.message
            
            binding.btnCall.setOnClickListener {
                if (item.phone.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${item.phone}")
                    itemView.context.startActivity(intent)
                }
            }
        }
    }
}

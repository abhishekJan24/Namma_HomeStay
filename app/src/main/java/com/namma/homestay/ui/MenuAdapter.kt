package com.namma.homestay.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namma.homestay.data.model.DailyMenu
import com.namma.homestay.databinding.ItemMenuBinding

class MenuAdapter : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private var items = listOf<DailyMenu>()

    fun submitList(newItems: List<DailyMenu>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class MenuViewHolder(private val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DailyMenu) {

            // Item Name
            binding.tvItemName.text = item.itemName

            // Price (IMPORTANT)
            binding.tvPrice.text = "₹${item.price}"
        }
    }
}

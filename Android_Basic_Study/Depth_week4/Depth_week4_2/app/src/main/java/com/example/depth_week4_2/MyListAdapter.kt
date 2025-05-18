package com.example.depth_week4_2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.depth_week4_2.databinding.ItemNumberBinding

class MyListAdapter : ListAdapter<Int, MyListAdapter.ViewHolder>(NumberDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNumberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(private val binding: ItemNumberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Int) {
            binding.tvItem.text = item.toString()
        }
    }
}
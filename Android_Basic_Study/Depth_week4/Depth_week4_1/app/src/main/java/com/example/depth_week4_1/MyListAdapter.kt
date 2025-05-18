package com.example.depth_week4_1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.depth_week4_1.databinding.ItemNumberBinding

class MyListAdapter(private val items: List<Int>): BaseAdapter() {
    override fun getCount(): Int = items.size

    override fun getItem(pos: Int): Int = items[pos]

    override fun getItemId(pos: Int): Long = pos.toLong()

    override fun getView(pos: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: ItemNumberBinding
        val view: View

        if (convertView == null) {
            binding = ItemNumberBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent,
                false
            )
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemNumberBinding
        }

        binding.tvItem.text = getItem(pos).toString()
        return view
    }
}


package com.example.depth_shoppingapp.ui.myBag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.depth_shoppingapp.databinding.FragmentMybagBinding


class MyBagFragment : Fragment() {

    private var _binding: FragmentMybagBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val myBagViewModel =
            ViewModelProvider(this).get(MyBagViewModel::class.java)

        _binding = FragmentMybagBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textMybag
        myBagViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
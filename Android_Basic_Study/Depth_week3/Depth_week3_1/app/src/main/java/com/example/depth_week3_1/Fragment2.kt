package com.example.depth_week3_1

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.depth_week3_1.databinding.Fragment2Binding

class Fragment2: Fragment() {
    private lateinit var binding: Fragment2Binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = Fragment2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("Fragment2", "onAttach: Fragment2가 Activity에 연결됨")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Fragment2", "onCreate: Fragment2 생성됨")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Fragment2", "onViewCreated: Fragment2의 View 생성 완료 및 초기화")
    }

    override fun onStart() {
        super.onStart()
        Log.d("Fragment2", "onStart: Fragment2가 화면에 표시될 준비됨")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Fragment2", "onResume: Fragment2가 활성화되어 사용자와 상호작용 가능")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Fragment2", "onPause: Fragment2가 비활성화됨")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Fragment2", "onStop: Fragment2가 화면에서 사라짐")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Fragment2", "onDestroyView: Fragment2의 View가 파괴됨")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Fragment2", "onDestroy: Fragment2가 파괴됨")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("Fragment2", "onDetach: Fragment2가 Activity와 연결 해제됨")
    }
}
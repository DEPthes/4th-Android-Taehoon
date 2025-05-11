package com.example.depth_week3_1

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.depth_week3_1.databinding.Fragment1Binding

class Fragment1 : Fragment() {
    private lateinit var binding: Fragment1Binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = Fragment1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("Fragment1","onAttach: Fragment1이 Activity에 연결됨")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Fragment1", "onCreate: Fragment1 생성됨")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Fragment1", "onViewCreated: Fragment1의 View 생성 완료 및 초기화")
    }

    override fun onStart() {
        super.onStart()
        Log.d("Fragment1", "onStart: Fragment1이 화면에 표시될 준비됨")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Fragment1", "onResume: Fragment1이 활성화되어 사용자와 상호작용 가능")
    }
    override fun onPause() {
        super.onPause()
        Log.d("Fragment1", "onPause: Fragment1이 비활성화됨")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Fragment1", "onStop: Fragment1이 화면에서 사라짐")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Fragment1", "onDestroyView: Fragment1의 View가 파괴됨")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Fragment1", "onDestroy: Fragment1이 파괴됨")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("Fragment1", "onDetach: Fragment1이 Activity와 연결 해제됨")
    }

}
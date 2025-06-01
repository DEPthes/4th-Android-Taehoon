package com.example.depth_practice_5

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.depth_practice_5.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var postRepositoryImpl: PostRepositoryImpl


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postRepositoryImpl = PostRepositoryImpl()

        with(binding) {
            btnPosts.setOnClickListener{
                tvResult.text = postRepositoryImpl.getPosts(tvResult).text
            }
            //editText는 하나의 Scanner Input같은 것
            btnPost.setOnClickListener{
                if(etText.text != null) {
                    val id = Integer.parseInt(etText.text.toString())
                    tvResult.text = postRepositoryImpl.getPost(tvResult, id).text
                }
            }
        }
    }
}
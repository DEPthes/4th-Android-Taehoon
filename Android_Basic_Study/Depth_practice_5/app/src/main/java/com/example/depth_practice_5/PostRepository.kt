package com.example.depth_practice_5

import android.widget.TextView
import retrofit2.Call

interface PostRepository {
    fun getPosts(tvResult : TextView): TextView

    fun getPost(tvResult : TextView, id : Int) : TextView

    fun setPost(post: Post) : Call<Post>

    fun updatePost(
        id : Int,
        userId: Int,
        title: String,
        body: String,
    ): Call<Post>

    fun deletePost(id: Int): Call<Unit>
}
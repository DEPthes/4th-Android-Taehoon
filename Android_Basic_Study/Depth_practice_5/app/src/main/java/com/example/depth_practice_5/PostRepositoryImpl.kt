package com.example.depth_practice_5

import android.util.Log
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class PostRepositoryImpl: PostRepository {
    private var retrofit : Retrofit = RetrofitClient.getInstance()
    private var service: PostService = retrofit.create(PostService::class.java)

    val tag = "RETROFIT"
    override fun getPosts(tvResult: TextView): TextView {
        service.getPosts().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                Log.d(tag, response.body().toString())
                val res = response.body()
                tvResult.text = null

                if (res != null) {
                    for(data in response.body()!!) {
                        tvResult.append("UserId: " + data.userId + "\n")
                        tvResult.append("Id: " + data.id + "\n")
                        tvResult.append("Title: " + data.title + "\n")
                        tvResult.append("Body: " + data.body + "\n\n")
                    }
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.d(tag, t.message.toString())
            }
        })
        return tvResult
    }

    override fun getPost(tvResult: TextView, id: Int): TextView {
        service.getPost(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d(tag, response.body().toString())
                val res = response.body()
                tvResult.text = null

                if (res != null) {
                    tvResult.append("UserId: " + res.userId + "\n")
                    tvResult.append("Id: " + res.id + "\n")
                    tvResult.append("Title: " + res.title + "\n")
                    tvResult.append("Body: " + res.body + "\n\n")
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d(tag, t.message.toString())
            }
        })
        return tvResult
    }


    //사이트에서 나머지 세개의 fun은 API 지원하지 않기에 implements할 필요 X
    override fun setPost(post: Post): Call<Post> {
        TODO("Not yet implemented")
    }

    override fun updatePost(id: Int, userId: Int, title: String, body: String): Call<Post> {
        TODO("Not yet implemented")
    }

    override fun deletePost(id: Int): Call<Unit> {
        TODO("Not yet implemented")
    }

}
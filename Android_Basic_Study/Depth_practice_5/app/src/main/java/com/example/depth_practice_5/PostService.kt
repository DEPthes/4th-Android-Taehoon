package com.example.depth_practice_5

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface PostService {
    @GET("posts")
    fun getPosts(): Call<List<Post>>

    @GET("posts/{id}")
    fun getPost(@Path("id") id : Int) : Call<Post>

    @POST("posts")
    fun setPost(@Body post: Post) : Call<Post>

    @Multipart
    @PUT("posts/{id}")
    fun updatePost(
        @Path("id") id : Int,
        @Part("userId") userId : Int,
        @Part("title") title : String,
        @Part("body") body : String
    ) : Call<Post>

    @DELETE("posts/{id}")
    fun deletePost(@Path("id") id : Int) : Call<Unit>
}
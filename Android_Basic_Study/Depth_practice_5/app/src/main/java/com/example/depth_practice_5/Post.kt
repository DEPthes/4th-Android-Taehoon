package com.example.depth_practice_5

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("userId")
    val userId : Int,

    @SerializedName("id")
    val id : Int,

    @SerializedName("title")
    val title : String,

    @SerializedName("body")
    val body: String
)

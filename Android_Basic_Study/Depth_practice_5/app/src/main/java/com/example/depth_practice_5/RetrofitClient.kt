package com.example.depth_practice_5

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//앱 전역에서 중복호출되거나 할때 단 하나만의 Client만 만들어 사용하기 위해 (중복방지)
object RetrofitClient {
    private var instance : Retrofit? = null
    //gson = 직렬화 라이브러리, json <-> data class(외 다른 클래스) 형식 변환 라이브러리
    private val gson = GsonBuilder().setLenient().create()

    fun getInstance() : Retrofit {
        if(instance == null) {
            //builder는 retrofit안에서의 구현된 json 형식으로, creation 패턴 메서드
            instance = Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        //물음표 = null인 경우를 처리하는 것 / 물음표. 일때는 let함수처럼 null일때 처리
        // 느낌표 두개 = nullable한 변수들에 대해서 확실히 null이 아닐때 컴파일러에게 null이 아님을 명시
        return instance!!
    }

    private val logging = HttpLoggingInterceptor().apply {
        // 요청과 응답의 본문 내용까지 로그에 포함
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()
}
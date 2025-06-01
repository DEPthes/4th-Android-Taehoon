package com.example.depth_week5

import kr.or.kobis.kobisopenapi.consumer.rest.KobisOpenAPIRestService
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface KoficBoxOfficeService {


    @GET("searchDailyBoxOfficeList.json")
    fun getDailyBoxOffice(
        @Query("key") key: String,
        @Query("targetDt") targetDt: String,
        @Query("itemPerPage") itemPerPage: String = "10",
        @Query("multiMovieYn") multiMovieYn: String = "N",
        @Query("repNationCd") repNationCd: String = "",
        @Query("wideAreaCd") wideAreaCd: String = ""
    ): Call<SearchDailyBoxOfficeDTO>

}



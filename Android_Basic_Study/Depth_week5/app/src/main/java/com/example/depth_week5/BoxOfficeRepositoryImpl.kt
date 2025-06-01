package com.example.depth_week5

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoxOfficeRepositoryImpl(
    private val apiService: KoficBoxOfficeService
) : BoxOfficeRepository {

    override fun getDailyBoxOffice(
        key: String,
        targetDt: String,
        onSuccess: (List<DailyBoxOfficeList>) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.getDailyBoxOffice(key, targetDt).enqueue(object : Callback<SearchDailyBoxOfficeDTO> {
            override fun onResponse(call: Call<SearchDailyBoxOfficeDTO>, response: Response<SearchDailyBoxOfficeDTO>) {
                if (response.isSuccessful) {
                    val movieList = response.body()?.boxOfficeResult?.dailyBoxOfficeList ?: emptyList()
                    onSuccess(movieList)
                } else {
                    onError("API 호출 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SearchDailyBoxOfficeDTO>, t: Throwable) {
                onError("네트워크 오류: ${t.message}")
            }
        })
    }
}

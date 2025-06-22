package com.example.depth_week5


interface BoxOfficeRepository {
    fun getDailyBoxOffice(
        key : String,
        targetDt : String,
        onSuccess : (List<DailyBoxOfficeList>) -> Unit,
        onError: (String) -> Unit)
}
package com.example.depth_week5

import android.widget.TextView

interface BoxOfficeRepository {
    fun getDailyBoxOffice(
        key : String,
        targetDt : String,
        onSuccess : (List<DailyBoxOfficeList>) -> Unit,
        onError: (String) -> Unit)
}
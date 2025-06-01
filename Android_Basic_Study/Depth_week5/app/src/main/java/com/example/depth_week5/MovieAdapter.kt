package com.example.depth_week5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.depth_week5.MainActivity.MovieItem

class MovieAdapter(private val movieList: MutableList<MovieItem>) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rankText: TextView = itemView.findViewById(R.id.tv_rank)
        val movieNameText: TextView = itemView.findViewById(R.id.tv_movie_name)
        val openDateText: TextView = itemView.findViewById(R.id.tv_open_date)
        val audienceText: TextView = itemView.findViewById(R.id.tv_audience)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.rankText.text = "${movie.rank}위"
        holder.movieNameText.text = movie.movieName
        holder.openDateText.text = "개봉일: ${movie.openDate}"
        holder.audienceText.text = "누적 관객수: ${movie.audienceCount}명"
    }

    override fun getItemCount(): Int = movieList.size

    // 새 영화 추가 메서드
    fun addMovie(movie: MovieItem) {
        movieList.add(movie)
        notifyItemInserted(movieList.size - 1)
    }

    // DailyBoxOfficeList를 MovieItem으로 변환해서 추가 (수정됨)
    fun addMoviesFromBoxOffice(boxOfficeList: List<DailyBoxOfficeList>) {
        val startPosition = movieList.size
        boxOfficeList.forEach { dailyMovie ->
            val movieItem = MovieItem(
                rank = dailyMovie.rank,
                movieName = dailyMovie.movieNm,
                openDate = dailyMovie.openDt,
                audienceCount = dailyMovie.audiAcc
            )
            movieList.add(movieItem)
        }
        notifyItemRangeInserted(startPosition, boxOfficeList.size)
    }

    // 박스오피스 데이터로 전체 리스트 교체
    fun replaceMoviesFromBoxOffice(boxOfficeList: List<DailyBoxOfficeList>) {
        movieList.clear()
        boxOfficeList.forEach { dailyMovie ->
            val movieItem = MovieItem(
                rank = dailyMovie.rank,
                movieName = dailyMovie.movieNm,
                openDate = dailyMovie.openDt,
                audienceCount = dailyMovie.audiAcc
            )
            movieList.add(movieItem)
        }
        notifyDataSetChanged()
    }

    // 전체 리스트 초기화
    fun clearAllMovies() {
        movieList.clear()
        notifyDataSetChanged()
    }
}
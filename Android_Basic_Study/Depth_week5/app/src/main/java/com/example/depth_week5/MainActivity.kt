package com.example.depth_week5

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    data class MovieItem(
        val rank: String,
        val movieName: String,
        val openDate: String,
        val audienceCount: String
    )

    private lateinit var boxOfficeRepository: BoxOfficeRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieAdapter
    private val movieList = mutableListOf<MovieItem>()

    // 수동 입력을 위한 UI 요소들
    private lateinit var etRank: EditText
    private lateinit var etMovieName: EditText
    private lateinit var etOpenDate: EditText
    private lateinit var etAudienceCount: EditText
    private lateinit var etTargetDate: EditText
    private lateinit var btnAddMovie: Button
    private lateinit var btnFetchBoxOffice: Button
    private lateinit var btnClearList: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupRecyclerView()
        setupRepository()
        setupClickListeners()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        etRank = findViewById(R.id.et_rank)
        etMovieName = findViewById(R.id.et_movie_name)
        etOpenDate = findViewById(R.id.et_open_date)
        etAudienceCount = findViewById(R.id.et_audience_count)
        etTargetDate = findViewById(R.id.et_target_date)
        btnAddMovie = findViewById(R.id.btn_add_movie)
        btnFetchBoxOffice = findViewById(R.id.btn_fetch_box_office)
        btnClearList = findViewById(R.id.btn_clear_list)

        // 기본 날짜 설정 (오늘로부터 1일 전)
        val currentDate = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
            .format(java.util.Date(System.currentTimeMillis() - 86400000)) // 1일 전
        etTargetDate.setText(currentDate)
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter(movieList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupRepository() {
        val apiService = RetrofitClient.getInstance().create(KoficBoxOfficeService::class.java)
        boxOfficeRepository = BoxOfficeRepositoryImpl(apiService)
    }

    private fun setupClickListeners() {
        btnAddMovie.setOnClickListener {
            addMovieManually()
        }

        btnFetchBoxOffice.setOnClickListener {
            fetchBoxOfficeData()
        }

        btnClearList.setOnClickListener {
            clearMovieList()
        }
    }

    private fun addMovieManually() {
        val rank = etRank.text.toString().trim()
        val movieName = etMovieName.text.toString().trim()
        val openDate = etOpenDate.text.toString().trim()
        val audienceCount = etAudienceCount.text.toString().trim()

        if (rank.isEmpty() || movieName.isEmpty() || openDate.isEmpty() || audienceCount.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val movieItem = MovieItem(rank, movieName, openDate, audienceCount)
        adapter.addMovie(movieItem)

        // 입력 필드 초기화
        etRank.text.clear()
        etMovieName.text.clear()
        etOpenDate.text.clear()
        etAudienceCount.text.clear()

        Toast.makeText(this, "영화가 추가되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun fetchBoxOfficeData() {
        val key = "03b423974e4f6e5cc943eb4d5ffffcc8" // 발급받은 API 키
        val targetDate = etTargetDate.text.toString().trim()

        // 날짜 입력 검증
        if (targetDate.isEmpty() || targetDate.length != 8) {
            Toast.makeText(this, "올바른 날짜를 입력해주세요 (YYYYMMDD 형식)", Toast.LENGTH_SHORT).show()
            return
        }

        // 날짜 유효성 검사
        try {
            val dateFormat = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
            dateFormat.isLenient = false
            dateFormat.parse(targetDate)
        } catch (e: Exception) {
            Toast.makeText(this, "유효하지 않은 날짜입니다", Toast.LENGTH_SHORT).show()
            return
        }

        boxOfficeRepository.getDailyBoxOffice(
            key = key,
            targetDt = targetDate,
            onSuccess = { boxOfficeList ->
                Log.d("BoxOffice", "성공: ${boxOfficeList.size}개의 영화 (조회날짜: $targetDate)")

                // 5순위까지만 로그 출력
                val top5Movies = boxOfficeList.take(5)
                top5Movies.forEach { movie ->
                    Log.d("BoxOffice", "순위: ${movie.rank}위")
                    Log.d("BoxOffice", "영화 이름: ${movie.movieNm}")
                    Log.d("BoxOffice", "개봉일: ${movie.openDt}")
                    Log.d("BoxOffice", "누적 관객 수: ${movie.audiAcc}명")
                    Log.d("BoxOffice", "------------------------")
                }

                // 기존 리스트를 갱신(교체)
                adapter.replaceMoviesFromBoxOffice(top5Movies)
                Toast.makeText(this, "${targetDate} 박스오피스 데이터를 가져왔습니다.", Toast.LENGTH_SHORT).show()
            },
            onError = { errorMessage ->
                Log.e("BoxOffice", "에러: $errorMessage")
                Toast.makeText(this, "데이터를 가져오는데 실패했습니다: $errorMessage", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun clearMovieList() {
        adapter.clearAllMovies()
        Toast.makeText(this, "목록을 초기화했습니다.", Toast.LENGTH_SHORT).show()
    }
}
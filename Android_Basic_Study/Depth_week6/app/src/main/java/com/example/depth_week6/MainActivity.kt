package com.example.depth_week6

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.depth_week6.AppDatabase
import com.example.depth_week6.FavoriteRepositoryImpl

class MainActivity : AppCompatActivity() {

    private lateinit var productRepository: ProductRepository
    private lateinit var favoriteRepository: FavoriteRepositoryImpl
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private val productList = mutableListOf<Product>()

    private lateinit var etSearchQuery: EditText
    private lateinit var btnSearch: Button
    private lateinit var btnLoadAll: Button
    private lateinit var btnClear: Button
    private lateinit var btnProfile: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRepositories()
        initViews()
        setupRecyclerView()
        setupClickListeners()

        loadAllProducts()
    }

    private fun initRepositories() {
        val apiService = RetrofitClient.getInstance().create(ProductApiService::class.java)
        val database = AppDatabase.getDatabase(this)
        favoriteRepository = FavoriteRepositoryImpl(database.favoriteDao())
        productRepository = ProductRepositoryImpl(apiService, favoriteRepository)
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        etSearchQuery = findViewById(R.id.et_search_query)
        btnSearch = findViewById(R.id.btn_search)
        btnLoadAll = findViewById(R.id.btn_load_all)
        btnClear = findViewById(R.id.btn_clear)
        btnProfile = findViewById(R.id.btn_profile)
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(productList) { product ->
            handleFavoriteClick(product)
        }
        recyclerView.adapter = adapter
        // Grid 레이아웃으로 2열 표시
        recyclerView.layoutManager = GridLayoutManager(this, 2)
    }

    private fun setupClickListeners() {
        btnSearch.setOnClickListener {
            searchProducts()
        }

        btnLoadAll.setOnClickListener {
            loadAllProducts()
        }

        btnClear.setOnClickListener {
            clearProductList()
        }

        // 프로필 버튼 클릭 시 찜 목록으로 이동
        btnProfile.setOnClickListener {
            val intent = Intent(this, FavoriteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun searchProducts() {
        val query = etSearchQuery.text.toString().trim()

        if (query.isEmpty()) {
            Toast.makeText(this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        productRepository.searchProducts(
            query = query,
            onSuccess = { products ->
                Log.d("ProductSearch", "검색 성공: ${products.size}개의 상품")
                adapter.updateProducts(products)
                Toast.makeText(this, "${products.size}개 상품을 찾았습니다.", Toast.LENGTH_SHORT).show()
            },
            onError = { errorMessage ->
                Log.e("ProductSearch", "검색 실패: $errorMessage")
                Toast.makeText(this, "검색 실패: $errorMessage", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun loadAllProducts() {
        productRepository.getAllProducts(
            onSuccess = { products ->
                Log.d("ProductSearch", "전체 상품 로드 성공: ${products.size}개")
                adapter.updateProducts(products)
                Toast.makeText(this, "전체 상품을 불러왔습니다.", Toast.LENGTH_SHORT).show()
            },
            onError = { errorMessage ->
                Log.e("ProductSearch", "상품 로드 실패: $errorMessage")
                Toast.makeText(this, "상품 로드 실패: $errorMessage", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun handleFavoriteClick(product: Product) {
        if (!product.isFavorite) {
            // 찜 해제
            favoriteRepository.removeFromFavorite(product.id)
            Toast.makeText(this, "${product.title} 찜을 해제했습니다.", Toast.LENGTH_SHORT).show()
        } else {
            // 찜 추가
            favoriteRepository.addToFavorite(product)
            Toast.makeText(this, "${product.title}을(를) 찜했습니다.", Toast.LENGTH_SHORT).show()
        }
        Log.d("Favorite", "찜 상태 변경: ${product.title} - ${!product.isFavorite}")
    }

    private fun clearProductList() {
        adapter.clearProducts()
        etSearchQuery.text.clear()
        Toast.makeText(this, "목록을 초기화했습니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        // 찜 목록에서 돌아왔을 때 상태 업데이트
        if (productList.isNotEmpty()) {
            val favoriteIds = favoriteRepository.getFavoriteProductIds()
            productList.forEach { product ->
                product.isFavorite = favoriteIds.contains(product.id)
            }
            adapter.notifyDataSetChanged()
        }
    }
}
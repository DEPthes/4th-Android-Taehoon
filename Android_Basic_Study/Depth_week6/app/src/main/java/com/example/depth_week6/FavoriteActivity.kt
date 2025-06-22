package com.example.depth_week6

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.depth_week6.AppDatabase
import com.example.depth_week6.FavoriteRepositoryImpl
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity() {

    private lateinit var favoriteRepository: FavoriteRepositoryImpl
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteAdapter
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var btnBack: ImageButton
    private lateinit var btnClearAll: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        initRepository()
        initViews()
        setupRecyclerView()
        setupClickListeners()
        loadFavorites()
    }

    private fun initRepository() {
        val database = AppDatabase.getDatabase(this)
        favoriteRepository = FavoriteRepositoryImpl(database.favoriteDao())
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recycler_view_favorites)
        emptyStateLayout = findViewById(R.id.layout_empty_state)
        btnBack = findViewById(R.id.btn_back)
        btnClearAll = findViewById(R.id.btn_clear_all_favorites)
    }

    private fun setupRecyclerView() {
        adapter = FavoriteAdapter(mutableListOf()) { product ->
            removeFavorite(product)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnClearAll.setOnClickListener {
            clearAllFavorites()
        }
    }

    private fun loadFavorites() {
        lifecycleScope.launch {
            favoriteRepository.getAllFavorites().collect { favorites ->
                if (favorites.isEmpty()) {
                    showEmptyState()
                } else {
                    hideEmptyState()
                    adapter.updateFavorites(favorites)
                }
            }
        }
    }

    private fun removeFavorite(product: Product) {
        favoriteRepository.removeFromFavorite(product.id)
        adapter.removeFavorite(product)
        Toast.makeText(this, "${product.title} 찜을 해제했습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun clearAllFavorites() {
        favoriteRepository.clearAllFavorites()
        adapter.clearAll()
        showEmptyState()
        Toast.makeText(this, "모든 찜을 해제했습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun showEmptyState() {
        emptyStateLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun hideEmptyState() {
        emptyStateLayout.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }
}
package com.example.depth_shoppingapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.depth_shoppingapp.databinding.ActivityMainBinding
import com.example.depth_shoppingapp.product.productRetrofit.ProductGetApiService
import com.example.depth_shoppingapp.product.productRetrofit.ProductRepository
import com.example.depth_shoppingapp.product.productRetrofit.ProductRepositoryImpl
import com.example.depth_shoppingapp.product.productRetrofit.RetrofitClient
import com.example.depth_shoppingapp.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_search, R.id.nav_mybag
            )
        )



        navView.setupWithNavController(navController)
    }



}

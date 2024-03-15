package com.example.swipekart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.swipekart.databinding.ActivityMainBinding
import com.example.swipekart.ui.ProductListFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.appbar.backBtn.setOnClickListener {
            finish()
        }
        binding.appbar.tvTitle.text=getString(R.string.product_list)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ProductListFragment())
                .commit()
        }
    }
}
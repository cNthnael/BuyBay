package com.sentraboga.buybay.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.sentraboga.buybay.R
import com.sentraboga.buybay.databinding.ActivityShoppingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        val navController = findNavController(R.id.shoppingHostFrag)
        binding.bottomNav.setupWithNavController(navController)
    }
}
package com.sentraboga.buybay.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sentraboga.buybay.R
import com.sentraboga.buybay.databinding.ActivityShoppingBinding
import com.sentraboga.buybay.util.Resource
import com.sentraboga.buybay.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    val viewModel by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        val navController = findNavController(R.id.shoppingHostFrag)
        binding.bottomNav.setupWithNavController(navController)

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when(it) {
                    is Resource.Success -> {
                        val count = it.data?.size ?: 0
                        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
                        bottomNav.getOrCreateBadge(R.id.cartFragment).apply {
                            number = count
                            backgroundColor = resources.getColor(R.color.g_blue)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}
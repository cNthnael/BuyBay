package com.sentraboga.buybay.util

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sentraboga.buybay.activities.ShoppingActivity

fun Fragment.hideBottomNavView(){
    val bottomNavView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(com.sentraboga.buybay.R.id.bottomNav)
    bottomNavView.visibility = android.view.View.GONE
}

fun Fragment.showBottomNavView(){
    val bottomNavView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(com.sentraboga.buybay.R.id.bottomNav)
    bottomNavView.visibility = android.view.View.VISIBLE
}
package com.secondslot.coursework.features.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.secondslot.coursework.R

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.container, MainFragment.newInstance())
//                .commitAllowingStateLoss()
//        }
//
//        // Initialize navController
//        val navHostFragment = supportFragmentManager
//            .findFragmentById(R.id.container) as NavHostFragment
//        val navController = navHostFragment.navController
//
//        // Hook up navigation button (up/drawer) on toolbar
//        val appBarConfiguration = AppBarConfiguration(navController.graph, null)
    }
}

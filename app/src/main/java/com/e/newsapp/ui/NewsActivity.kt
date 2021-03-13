 package com.e.newsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyProperties
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.e.newsapp.R
import com.e.newsapp.databinding.ActivityNewsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.security.AlgorithmConstraints
import javax.crypto.KeyGenerator

 @AndroidEntryPoint
class NewsActivity : AppCompatActivity() {
    private var _binding : ActivityNewsBinding? = null
    private val binding get() = _binding!!

     init {
         System.loadLibrary("Keys")
     }

     //To declare a function that is implemented in native (C or C++) code, you need to mark it with the external modifier:
     private external fun getApiKey():String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
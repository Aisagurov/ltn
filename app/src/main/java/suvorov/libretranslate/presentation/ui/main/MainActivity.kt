package suvorov.libretranslate.presentation.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import suvorov.libretranslate.R

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = ContextCompat.getColor(this,R.color.gray)

        if (supportFragmentManager.findFragmentById(R.id.mainNavHostFragment) !is Fragment) return
        val navController = findNavController(R.id.mainNavHostFragment)

        val mainBottomNavigationView = findViewById<BottomNavigationView>(R.id.mainBottomNavigationView)
        mainBottomNavigationView.setupWithNavController(navController)
    }
}
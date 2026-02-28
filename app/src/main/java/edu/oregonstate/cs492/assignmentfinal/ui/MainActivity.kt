package edu.oregonstate.cs492.assignmentfinal.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.appbar.MaterialToolbar
import edu.oregonstate.cs492.assignmentfinal.R

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfig: AppBarConfiguration
    private val tag = "MainActivity"
    private val handler = Handler(Looper.getMainLooper())
    private var timeoutRunnable: Runnable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val darkModeEnabled = prefs.getBoolean("dark_mode_enabled", false)
        if (darkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfig = AppBarConfiguration(navController.graph)

        val topAppBar: MaterialToolbar = findViewById(R.id.top_app_bar)
        setSupportActionBar(topAppBar)
        setupActionBarWithNavController(navController, appBarConfig)
    }

    override fun onResume() {
        super.onResume()
        applyScreenTimeout()
    }
    override fun onPause() {
        super.onPause()
        timeoutRunnable?.let { handler.removeCallbacks(it) }
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    fun applyScreenTimeout(timeoutSeconds: Long? = null){
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val timeoutSeconds = prefs.getString("screen_timeout", "30")?.toLong() ?: 30L
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        Handler(Looper.getMainLooper()).postDelayed({
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }, timeoutSeconds * 1000)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }
}
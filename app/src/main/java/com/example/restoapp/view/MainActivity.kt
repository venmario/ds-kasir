package com.example.restoapp.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cloudinary.android.MediaManager
import com.example.restoapp.R
import com.example.restoapp.databinding.ActivityMainBinding
import com.example.restoapp.global.Secret
import com.example.restoapp.util.setFcmTokens
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object{
        val OLD_FCM_TOKEN = "OLD_FCM_TOKEN"
        val CURRENT_FCM_TOKEN = "CURRENT_FCM_TOKEN"
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                this,
                "Van't post notifications without notifications permission",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    private fun cloudinaryConfig(){
        val config = HashMap<String,String>()
        config["cloud_name"] = Secret.cloudinaryCloudName
        config["api_key"] = Secret.cloudinaryApiKey
        config["api_secret"] = Secret.cloudinaryApiSecret
        MediaManager.init(this, config)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cloudinaryConfig()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result

            Log.d("TAG", token)
            setFcmTokens(this,token)
        })
        askNotificationPermission()

        val navController = (supportFragmentManager.findFragmentById(R.id.hostFragment) as NavHostFragment).navController
        val drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.productManagementFragment, R.id.logoutFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener{_, destination,_ ->
            if (destination.id == R.id.loginFragment ||
                destination.id == R.id.logoutFragment ||
                destination.id == R.id.productFormFragment) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false) // Hide back button
                supportActionBar?.setDisplayShowHomeEnabled(false) // Hide app icon
                supportActionBar?.setDisplayShowTitleEnabled(false) // Hide title
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                binding.toolbar.visibility = View.GONE
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show back button
                supportActionBar?.setDisplayShowHomeEnabled(true) // Show app icon
                supportActionBar?.setDisplayShowTitleEnabled(true) // Show title
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                binding.toolbar.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = (supportFragmentManager.findFragmentById(R.id.hostFragment) as NavHostFragment).navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun askNotificationPermission() {
        // This is only necessary for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d("itemId", item.itemId.toString())
        when (item.itemId) {
            R.id.homeFragment -> supportFragmentManager.beginTransaction()
                .replace(R.id.homeFragment, HomeFragment()).commit()
            R.id.productManagementFragment -> supportFragmentManager.beginTransaction()
                .replace(R.id.productManagementFragment, ProductManagementFragment()).commit()
            R.id.logoutFragment -> {
                Log.d("logout","logout")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.logoutFragment, LogoutFragment()).commit()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
package com.example.autocaravanas

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.autocaravanas.databinding.ActivityMainBinding
import com.example.autocaravanas.viewmodel.ReservaViewModel
import android.view.View
import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var reservaViewModel: ReservaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // NavController desde el fragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Mostrar la Toolbar solo en HomeFragment
        val fragmentsConToolbar = setOf(R.id.homeFragment, R.id.editReservaFragment /*, otros IDs*/)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("ToolbarDebug", "Destino actual: ${destination.id} (${resources.getResourceEntryName(destination.id)})")
            binding.toolbar.visibility =
                if (destination.id in fragmentsConToolbar) View.VISIBLE else View.GONE
        }

        reservaViewModel = ViewModelProvider(this)[ReservaViewModel::class.java]
    }

    // Para que funcione el botón "Up"
    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }
}

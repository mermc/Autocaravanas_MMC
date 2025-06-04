package com.example.autocaravanas

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.autocaravanas.databinding.ActivityMainBinding
import com.example.autocaravanas.repository.ReservaRepository
import com.example.autocaravanas.viewmodel.ReservaViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var reservaViewModel: ReservaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
    }

    private fun setupViewModel(){
        // Inicializamos el ViewModel y el repositorio
        val reservaRepository = ReservaRepository(ReservaDatabase(this))
        val viewModelProviderFactory = ReservaViewModelFactory(application, reservaRepository)
        reservaViewModel = ViewModelProvider(this, viewModelProviderFactory)[ReservaViewModel::class.java]
    }
}
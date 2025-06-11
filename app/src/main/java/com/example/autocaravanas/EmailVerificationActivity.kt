package com.example.autocaravanas

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.autocaravanas.api.ApiAdapter
import com.example.autocaravanas.api.ApiHelper
import com.example.autocaravanas.databinding.ActivityEmailVerificationBinding
import com.example.autocaravanas.repository.ReservaRepository
import kotlinx.coroutines.launch

class EmailVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmailVerificationBinding
    private var repository: ReservaRepository = ReservaRepository(ApiHelper(ApiAdapter.apiService))

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("EmailVerificationActivity", "Ha entrado en esta actividad de verificacion de correo")
        super.onCreate(savedInstanceState)
        binding = ActivityEmailVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialmente comprobamos si ya está verificado
        lifecycleScope.launch {
            checkEmailVerification()
        }

        // Botón para reenviar correo de verificación
        binding.btnReenviar.setOnClickListener {
            lifecycleScope.launch {
                reenviarCorreoVerificacion()
            }
        }

        // Nuevo botón por si ya lo ha verificado manualmente
        binding.btnYaVerificado.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Swipe-to-refresh para volver a verificar
        binding.swipeRefresh.setOnRefreshListener {
            lifecycleScope.launch {
                checkEmailVerification()
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private suspend fun checkEmailVerification() {
        try {
            val user = repository.getUser()
            if (user == null) {
                binding.tvEstado.text = "Error al obtener usuario. Intenta refrescar o verifica manualmente."
                // Muestra el botón "Ya he verificado mi correo"
                return
            }
            if (user.emailVerifiedAt != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                binding.tvEstado.text = "Se te ha enviado un correo de verificación, accede a tu bandeja de entrada o spam"
            }
        } catch (e: Exception) {
            Log.e("EmailVerification", "Error al comprobar verificación", e)
            Toast.makeText(this, "Error al comprobar verificación", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun reenviarCorreoVerificacion() {
        try {
            val response = repository.reenviarEmailVerificacion()
            if (response.success) {
                Toast.makeText(this, "Correo de verificación reenviado", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Error: ${response.message}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e("EmailVerification", "Error al reenviar correo", e)
            Toast.makeText(this, "No se pudo reenviar el correo", Toast.LENGTH_LONG).show()
        }
    }
}
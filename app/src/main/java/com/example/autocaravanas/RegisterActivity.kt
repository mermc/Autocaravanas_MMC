package com.example.autocaravanas

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.autocaravanas.api.ApiAdapter
import com.example.autocaravanas.api.ApiHelper
import com.example.autocaravanas.databinding.ActivityRegisterBinding
import com.example.autocaravanas.repository.ReservaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var repository: ReservaRepository = ReservaRepository(ApiHelper(ApiAdapter.instance!!))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btRegistro.setOnClickListener {
            binding.btRegistro.isEnabled = false

            lifecycleScope.launch {
                val name = binding.etTitle.text.toString()
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                val confirmPassword = binding.etConfirmPassword.text.toString()

                // Validamos que todos los datos estén correctos
                if (validateRegister(name, email, password, confirmPassword)) {
                    val response = repository.register(
                        name,
                        email,
                        password,
                        confirmPassword
                    )

                    if (response.success) {
                        withContext(Dispatchers.Main) {
                            binding.btRegistro.isEnabled = true
                        }

                        val mainActivityIntent = Intent(this@RegisterActivity, MainActivity::class.java)
                        mainActivityIntent.putExtra("API_TOKEN", response.login.token)
                        startActivity(mainActivityIntent)
                        finish()
                    } else {
                        withContext(Dispatchers.Main) {
                            val alertDialog: AlertDialog =
                                AlertDialog.Builder(this@RegisterActivity).create().apply {
                                    setTitle("Error")
                                    setMessage("Error al registrarse")
                                    setButton(
                                        AlertDialog.BUTTON_POSITIVE,
                                        "Aceptar"
                                    ) { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                }

                            alertDialog.show()

                            binding.btRegistro.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    private fun validateRegister(
        name: String,
        email: String,
        password: String,
        confirmationPassword: String
    ): Boolean {
        var registerValidated = true

        // Comprobamos que todos los campos estén rellenos
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmationPassword.isEmpty()) {
            registerValidated = false

            if (name.isEmpty()) binding.etTitle.error = "El nombre de usuario no puede estar vacio"
            if (email.isEmpty()) binding.etEmail.error = "El email no puede estar vacio"
            if (password.isEmpty()) binding.etPassword.error = "La contraseña no puede estar vacia"
            if (confirmationPassword.isEmpty()) binding.etConfirmPassword.error =
                "La confirmación de contraseña no puede estar vacia"

            showMessage( "No se puede registrar. Hay campos sin rellenar")

            binding.btRegistro.isEnabled = true
        }

        if (registerValidated && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            registerValidated = false

            showMessage("El email no es válido")

            binding.etEmail.error = "El email no es válido"
            binding.btRegistro.isEnabled = true
        }

        if (registerValidated && password != confirmationPassword) {
            registerValidated = false

            showMessage("Las contraseñas no coinciden")

            binding.etConfirmPassword.error = "Las contraseñas no coinciden"
            binding.btRegistro.isEnabled = true
        }

        return registerValidated
    }

    private fun showMessage(message: String) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }
}
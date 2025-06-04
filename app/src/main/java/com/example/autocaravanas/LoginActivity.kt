package com.example.autocaravanas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.autocaravanas.api.ApiAdapter
import com.example.autocaravanas.api.ApiHelper
import com.example.autocaravanas.databinding.ActivityLoginBinding
import com.example.autocaravanas.repository.ReservaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.text.orEmpty

val Context.dataStore by preferencesDataStore(name = "USER_PREFERENCES")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var repository: ReservaRepository = ReservaRepository(ApiHelper(ApiAdapter.instance!!))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cargamos los datos de usuario
        getLogin()

        binding.btLogin.setOnClickListener {
            toggleUI(false)

            lifecycleScope.launch {
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()

                // Validamos que todos los datos del login estén correctos
                if (validateLogin(email, password)) {
                    val response = repository.login(email, password)

                    if (response.success) {
                        withContext(Dispatchers.Main) {
                            toggleUI(true)
                        }

                        // Guardamos los datos del usuario
                        saveLogin(email, password)

                        // Lanzamos la actividad principal
                        val mainActivityIntent = Intent(this@LoginActivity, MainActivity::class.java)
                        mainActivityIntent.putExtra("API_TOKEN", response.login.token)
                        startActivity(mainActivityIntent)
                        finish()
                    } else {
                        withContext(Dispatchers.Main) {
                            val alertDialog: AlertDialog =
                                AlertDialog.Builder(this@LoginActivity).create().apply {
                                    setTitle("Error")
                                    setMessage("Error al iniciar sesión: " + response.message.ifEmpty { "No se ha podido iniciar sesión" })
                                    setButton(
                                        AlertDialog.BUTTON_POSITIVE,
                                        "Aceptar"
                                    ) { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                }

                            alertDialog.show()

                            toggleUI(true)
                        }
                    }
                }
            }
        }

        binding.btRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }

    private fun toggleUI(state: Boolean) {
        binding.btLogin.isEnabled = state
        binding.btRegister.isEnabled = state
    }

    private suspend fun saveLogin(email: String, password: String) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey("rememberLogin")] = binding.cbRememberLogin.isChecked

            if (binding.cbRememberLogin.isChecked) {
                preferences[stringPreferencesKey("email")] = email
                preferences[stringPreferencesKey("password")] = password
            } else {
                preferences.remove(stringPreferencesKey("email"))
                preferences.remove(stringPreferencesKey("password"))
            }
        }
    }

    private fun getLogin() {
        runBlocking {
            dataStore.data.map { preferences ->
                binding.cbRememberLogin.isChecked =
                    preferences[booleanPreferencesKey("rememberLogin")] ?: false

                if (binding.cbRememberLogin.isChecked) {
                    binding.etEmail.setText(preferences[stringPreferencesKey("email")].orEmpty())
                    binding.etPassword.setText(preferences[stringPreferencesKey("password")].orEmpty())
                }
            }.first()
        }
    }

    private fun validateLogin(email: String, password: String): Boolean {
        var loginValidated = true

        // Comprobamos si hay campos vacios
        if (email.isEmpty() || password.isEmpty()) {
            loginValidated = false

            showMessage("No puedes dejar campos sin rellenar")

            if (email.isEmpty())
                binding.etEmail.error = "El email no puede estar vacio"
            if (password.isEmpty())
                binding.etPassword.error = "La contraseña no puede estar vacia"

            toggleUI(true)
        }

        // Comprobamos si el email cumple el estandar en regex
        if (loginValidated && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginValidated = false

            showMessage("El email no es válido")

            binding.etEmail.error = "El email no es válido"
            toggleUI(true)
        }

        return loginValidated
    }

    private fun showMessage(message: String) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
    }
}
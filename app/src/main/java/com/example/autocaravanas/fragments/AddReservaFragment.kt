package com.example.autocaravanas.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import java.time.LocalDate
import java.util.Calendar

class AddReservaFragment : Fragment(R.layout.fragment_add_reserva), MenuProvider {

    private var addReservaBinding: FragmentAddReservaBinding? = null
    private val binding get() = addReservaBinding!!

    private lateinit var reservasViewModel: ReservaViewModel
    private lateinit var addReservaView: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addReservaBinding = FragmentAddReservaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        reservasViewModel = (activity as MainActivity).reservaViewModel
        addReservaView = view

        iniciarSpinnerCaballo()
        iniciarSpinnerHora()

        binding.addReservaFecha.setOnClickListener {
            mostrarDatePickerDialog()
        }
    }

    private fun mostrarDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = datePickerDialogCustom(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                binding.addReservaFecha.text = "$dayOfMonth/${month + 1}/$year"
            }
        )
        datePickerDialog.show()
    }

    private fun saveReserva(view: View) {
        val reservaJinete = binding.addReservaJinete.text.toString().trim()
        val reservaTelefono = binding.addReservaTelefono.text.toString().trim()
        val reservaCaballo = binding.spinnerReservaCaballo.selectedItem?.toString()?.trim()
        val fechaTexto = binding.addReservaFecha.text.toString().trim()
        val reservaHora = binding.addReservaHora.selectedItem?.toString()?.trim()
        val reservaComentario = binding.addReservaComentario.text.toString().trim()

        // Validamos primero campos obligatorios para que no de error si se pulsa en guardar o cierre la aplicación
        if (reservaJinete.isEmpty() || reservaTelefono.isEmpty() || reservaCaballo.isNullOrEmpty() ||
            fechaTexto.isEmpty() || reservaHora.isNullOrEmpty()) {
            Toast.makeText(addReservaView.context, "Por favor completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar número de teléfono
        val telefonoRegex = Regex("^\\d{9}$")
        if (!telefonoRegex.matches(reservaTelefono)) {
            Toast.makeText(addReservaView.context, "El número de teléfono debe tener 9 dígitos", Toast.LENGTH_SHORT).show()
            return
        }
        // Procesar la fecha después de los otros campos porque es la que más puede dar error
        val (day, month, year) = fechaTexto.split("/").map { it.toInt() }
        val reservaFecha = LocalDate.of(year, month, day).toString()

        // Crea y guarda la reserva
        val reserva = Reserva(
            0,
            reservaJinete,
            reservaTelefono,
            reservaCaballo,
            reservaFecha,
            reservaHora,
            reservaComentario
        )
        reservasViewModel.addReserva(reserva)

        Toast.makeText(addReservaView.context, "Reserva Realizada con Éxito", Toast.LENGTH_SHORT).show()
        view.findNavController().popBackStack(R.id.homeFragment, false)

        // Enviar mensaje
        enviarMensaje(reserva)

    }

    private fun iniciarSpinnerCaballo() {
        val caballos = listOf("Moreno", "Canela", "Soberano", "Viviana", "Luz", "Blanca Paloma") // Lista de nombres de caballos
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, caballos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerReservaCaballo.adapter = adapter
    }
    private fun iniciarSpinnerHora() {
        val horas = listOf("10:00", "11:00") // Lista de horas disponibles
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, horas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.addReservaHora.adapter = adapter
    }

    private fun enviarMensaje(reserva: Reserva) {
        val mensaje = "¡Gracias por realizar tu reserva con nosotras!\n\n" +
                "Detalles de la reserva:\n" +
                "Nombre del caballo: ${reserva.nombreCaballo}\n" +
                "Fecha del paseo: ${reserva.fechaReserva}\n" +
                "Hora del paseo: ${reserva.horaReserva}\n" +
                "Cualquier duda puedes contestar a este mensaje o enviar un email a info@reservashipica.com y te la aclararemos.\n"


        val numeroTelefono = "34${reserva.telefono}" // Agregamos el código de España

        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://api.whatsapp.com/send?phone=$numeroTelefono&text=${Uri.encode(mensaje)}")
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("EnviarMensaje", "Error al abrir WhatsApp: ${e.message}", e)
            Toast.makeText(addReservaView.context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_add_reserva, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.saveMenu -> {
                saveReserva(addReservaView)
                true
            }
            else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        addReservaBinding = null
    }
}
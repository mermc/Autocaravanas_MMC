package com.example.autocaravanas.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.autocaravanas.MainActivity
import com.example.autocaravanas.R
import com.example.autocaravanas.databinding.FragmentEditReservaBinding
import com.example.autocaravanas.model.Caravana
import com.example.autocaravanas.model.Reserva
import com.example.autocaravanas.viewmodel.ReservaViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditReservaFragment : Fragment(R.layout.fragment_edit_reserva), MenuProvider {

    private var _binding: FragmentEditReservaBinding? = null
    private val binding get() = _binding!!

    private lateinit var reservasViewModel: ReservaViewModel
    private lateinit var currentReserva: Reserva


    private var fechaInicio: String = ""
    private var fechaFin: String = ""

    private val args: EditReservaFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditReservaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
/*
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
*/
        reservasViewModel = (activity as MainActivity).reservaViewModel
        currentReserva = args.reserva!!

        val menuHost: MenuHost = requireActivity()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                //Log.d("MenuDebugEdit", "addMenuProvider ejecutado")
                menuHost.addMenuProvider(this@EditReservaFragment)
            }
        }

        fechaInicio = currentReserva.fechaInicio
        fechaFin = currentReserva.fechaFin

        binding.editReservaFechaInicio.text = fechaInicio
        binding.editReservaFechaFin.text = fechaFin

        setupDatePickers()

        binding.editReservaFab.setOnClickListener {
            //Log.d("EditReserva", "Botón FAB pulsado. Fechas: $fechaInicio a $fechaFin")
            editarReserva()
        }

        reservasViewModel.updateResult.observe(viewLifecycleOwner) { reservaActualizada ->
            Log.d("UpdateDebug1", "Observer activado. Mensaje: $reservaActualizada")
            reservaActualizada?.let {
                mostrarDialogResumen(it)
                Log.d("UpdateDebug1", "Reserva actualizada: $it")
                reservasViewModel.clearUpdateResult()
            }
        }

        reservasViewModel.deleteMessage.observe(viewLifecycleOwner) { msg ->
            //Log.d("DeleteDebug", "Observer activado. Mensaje: $msg")
            msg?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                //Log.d("DeleteDebug", "Toast mostrado con mensaje: $it")
                reservasViewModel.clearDeleteMessage()
                //Log.d("DeleteDebug", "Mensaje de borrado limpiado")
                findNavController().popBackStack(R.id.homeFragment, false)
                //Log.d("DeleteDebug", "Navegación de vuelta al Home ejecutada")
            }
        }

        reservasViewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            Log.d("UpdateDebug2", "Observer activado. Mensaje: $errorMsg")
            errorMsg?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                reservasViewModel.clearError()
            }
        }
    }

    private fun setupDatePickers() {
        binding.editReservaFechaInicio.setOnClickListener {
            showDatePicker { date ->
                fechaInicio = date
                binding.editReservaFechaInicio.text = date
                // Limpiar fecha fin y caravana al cambiar fecha inicio
                fechaFin = ""
                binding.editReservaFechaFin.text = ""
            }
        }
        binding.editReservaFechaFin.setOnClickListener {
            if (fechaInicio.isEmpty()) {
                Toast.makeText(context, "Selecciona primero la fecha de inicio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showDatePicker { date ->
                fechaFin = date
                binding.editReservaFechaFin.text = date
            }
        }
    }

    private fun showDatePicker(onDateSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(requireContext(), { _, year, month, day ->
            val fecha = String.format("%04d-%02d-%02d", year, month + 1, day)
            onDateSet(fecha)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }


    private fun editarReserva() {
        if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            Toast.makeText(context, "Rellena todos los campos y selecciona caravana", Toast.LENGTH_SHORT).show()
            return
        }
        val reservaEditada = currentReserva.copy(
            fechaInicio = fechaInicio,
            fechaFin = fechaFin
        )
        Log.d("EditReserva", "Entrando en editarReserva con fechas: $fechaInicio / $fechaFin")
        reservasViewModel.updateReserva(currentReserva, reservaEditada)

    }

    private fun mostrarDialogResumen(reserva: Reserva) {
        Log.d("EditReserva", "Mostrando diálogo resumen: $reserva")
        fun soloFecha(fecha: String): String {
            return fecha.split("T", " ").firstOrNull() ?: fecha
        }

        val resumen = """
            Caravana: ${reserva.caravana?.nombre ?: "N/A"}
            Fechas: ${soloFecha(reserva.fechaInicio)} a ${soloFecha(reserva.fechaFin)}
            Precio total: ${reserva.precioTotal} €
            Precio pagado: ${reserva.precioPagado} €
        """.trimIndent()

        AlertDialog.Builder(requireContext())

            .setTitle("Resumen de cambios")
            .setMessage(resumen)
            .setCancelable(false)
            .setPositiveButton("Confirmar") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(context, "Reserva actualizada", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack(R.id.homeFragment, false)
            }
            .show()
    }

    private fun deleteReserva() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance().time
        val fechaInicioDate = try {
            dateFormat.parse(currentReserva.fechaInicio)
        } catch (e: Exception) {
            null
        }

        if (fechaInicioDate == null) {
            Toast.makeText(context, "Fecha de inicio no válida", Toast.LENGTH_SHORT).show()
            return
        }

        // Calcular días hasta el inicio
        val diffMillis = fechaInicioDate.time - today.time
        val dias = (diffMillis / (1000 * 60 * 60 * 24)).toInt()

        when {
            dias < 0 -> {
                // Ya ha empezado
                AlertDialog.Builder(requireContext())
                    .setTitle("No permitido")
                    .setMessage("No se puede cancelar una reserva que ya ha comenzado o finalizado.")
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
            dias < 15 -> {
                // Menos de 15 días, mostrar advertencia especial
                AlertDialog.Builder(requireContext())
                    .setTitle("Borrar Reserva")
                    .setMessage("¿Está seguro de que quiere borrar esta reserva? Quedan menos de 15 días para el comienzo y se hará el cargo total.")
                    .setPositiveButton("Eliminar") { _, _ ->
                        Log.d("DeleteDebugMenos15", "Llamando a borrar reserva con id=${currentReserva.id}")
                        reservasViewModel.deleteReserva(currentReserva)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            else -> {
                // Más de 15 días, diálogo normal
                AlertDialog.Builder(requireContext())
                    .setTitle("Borrar Reserva")
                    .setMessage("¿Está seguro de que quiere borrar esta reserva?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        Log.d("DeleteDebugMas15", "Llamando a borrar reserva con id=${currentReserva.id}")
                        reservasViewModel.deleteReserva(currentReserva)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        Log.d("MenuDebugEdit", "onCreateMenu en ${this::class.java.simpleName}")
        Log.d("MenuDebugEdit", "Menu Correcto")
        menu.clear()
        menuInflater.inflate(R.menu.menu_edit_reserva, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        Log.d("MenuDebugEdit", "Item seleccionado: ${menuItem.itemId}")
        return when (menuItem.itemId) {
            R.id.deleteMenu -> {
                Log.d("MenuDebugEdit", "Acción de borrar ejecutada")
                deleteReserva()
                true
            }
            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

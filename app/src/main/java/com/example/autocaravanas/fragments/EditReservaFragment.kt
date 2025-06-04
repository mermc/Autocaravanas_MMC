package com.example.autocaravanas.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
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
import androidx.navigation.fragment.navArgs
import com.example.autocaravanas.MainActivity
import com.example.autocaravanas.R
import com.example.autocaravanas.model.Reserva
import com.example.autocaravanas.viewmodel.ReservaViewModel
import java.time.LocalDate
import java.util.Calendar

class EditReservaFragment : Fragment(R.layout.fragment_edit_reserva), MenuProvider {

    private var editReservaBinding: FragmentEditReservaBinding? = null
    private val binding get() = editReservaBinding!!

    private lateinit var reservasViewModel: ReservaViewModel
    private lateinit var currentReserva: Reserva

    private val args: EditReservaFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        editReservaBinding = FragmentEditReservaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        reservasViewModel = (activity as MainActivity).reservaViewModel
        currentReserva = args.reserva!!

        iniciarSpinnerHora()

        binding.editReservaJinete.setText(currentReserva.nombreJinete)
        binding.editReservaTelefono.setText(currentReserva.telefono)
        binding.spinnerEditReservaCaballo.setSelection(
            (binding.spinnerEditReservaCaballo.adapter as ArrayAdapter<String>)
                .getPosition(currentReserva.nombreCaballo)
        )
        binding.editReservaFecha.setOnClickListener {
            mostrarDatePickerDialog()

        }
        binding.editReservaHora.setSelection(
            (binding.editReservaHora.adapter as ArrayAdapter<String>)
                .getPosition(currentReserva.horaReserva)
        )
        binding.editReservaComentario.setText(currentReserva.comentario)

        binding.editReservaFab.setOnClickListener {
            edicionReserva()
        }

    }

    private fun edicionReserva(){
        val reservaJinete = binding.editReservaJinete.text.toString().trim()
        val reservaTelefono = binding.editReservaTelefono.text.toString().trim()
        val reservaCaballo = binding.spinnerEditReservaCaballo.selectedItem.toString().trim()
        val fechaTexto = binding.editReservaFecha.text.toString()

        val reservaHora = binding.editReservaHora.selectedItem.toString().trim()
        val reservaComentario = binding.editReservaComentario.text.toString().trim()

        if (reservaJinete.isEmpty() || reservaTelefono.isEmpty() || reservaCaballo.isNullOrEmpty() ||
            fechaTexto.isEmpty() || reservaHora.isNullOrEmpty()) {
            Toast.makeText(context, "Por favor completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar número de teléfono
        val telefonoRegex = Regex("^\\d{9}$")
        if (!telefonoRegex.matches(reservaTelefono)) {
            Toast.makeText(context, "El número de teléfono debe tener 9 dígitos", Toast.LENGTH_SHORT).show()
            return
        }

        val (day, month, year) = fechaTexto.split("/").map { it.toInt() }
        val reservaFecha = LocalDate.of(year, month, day).toString()

        if (reservaJinete.isNotEmpty()){
            val reserva = Reserva (
                currentReserva.id,
                reservaJinete,
                reservaTelefono,
                reservaCaballo,
                reservaFecha,
                reservaHora,
                reservaComentario
            )
            reservasViewModel.updateReserva(reserva)
            view?.findNavController()?.popBackStack(R.id.homeFragment, false)
        } else {
            Toast.makeText(context, "Por favor introduce el nombre del jinete", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteReserva(){
        AlertDialog.Builder(activity).apply {
            setTitle("Borrar Reserva")
            setMessage("¿Está seguro de que quiere borrar esta reserva?")
            setPositiveButton("Eliminar"){_,_ ->
                reservasViewModel.deleteReserva(currentReserva)
                Toast.makeText(context, " Reserva eliminada", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
            }
            setNegativeButton("Cancelar", null)
        }.create().show()
    }

    private fun mostrarDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = datePickerDialogCustom(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                binding.editReservaFecha.text = "$dayOfMonth/${month + 1}/$year"
            }
        )
        datePickerDialog.show()
    }

    private fun iniciarSpinnerHora() {
        val horas = listOf("10:00", "11:00")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, horas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.editReservaHora.adapter = adapter
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_edit_reserva, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.deleteMenu -> {
                deleteReserva()
                true
            } else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        editReservaBinding = null
    }
}
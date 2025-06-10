package com.example.autocaravanas.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autocaravanas.MainActivity
import com.example.autocaravanas.R
import com.example.autocaravanas.adapter.CaravanaDisponibleAdapter
import com.example.autocaravanas.databinding.DialogResumenReservaBinding
import com.example.autocaravanas.databinding.FragmentAddReservaBinding
import com.example.autocaravanas.model.Caravana
import com.example.autocaravanas.model.Reserva
import com.example.autocaravanas.viewmodel.ReservaViewModel
import java.util.Calendar


class AddReservaFragment : Fragment(R.layout.fragment_add_reserva) {

    private var _binding: FragmentAddReservaBinding? = null
    private val binding get() = _binding!!
    private lateinit var reservasViewModel: ReservaViewModel

    private var caravanaList: List<Caravana> = emptyList()
    private lateinit var caravanaAdapter: CaravanaDisponibleAdapter
    private var selectedCaravana: Caravana? = null
    private var fechaInicio: String = ""
    private var fechaFin: String = ""
    private var reservasPrevias: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddReservaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reservasViewModel = (activity as MainActivity).reservaViewModel

        setupDatePickers()

        setupHomeRecyclerView()

        // Observa los cambios para mostrar mensajes
        reservasViewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                reservasViewModel.clearError()
            }
        }

        reservasPrevias = reservasViewModel.reservas.value?.size ?: 0

        reservasViewModel.reservas.observe(viewLifecycleOwner) { reservas ->
            if (reservas.size > reservasPrevias) {
                findNavController().navigate(R.id.action_addReservaFragment_to_homeFragment)
            }
            reservasPrevias = reservas.size
        }
    }

    private fun setupDatePickers() {
        Log.d("AddReservaFragment", "Fechas Bien")
        binding.fechaInicio.setOnClickListener {
            showDatePicker { date ->
                fechaInicio = date
                binding.fechaInicio.text = date
                // Reset fechaFin y caravana al cambiar fechaInicio
                binding.fechaFin.text = ""
                fechaFin = ""
                caravanaList = emptyList()
                selectedCaravana = null
            }
        }
        binding.fechaFin.setOnClickListener {
            if (fechaInicio.isEmpty()) {
                Toast.makeText(context, "Selecciona primero la fecha de inicio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showDatePicker { date ->
                fechaFin = date
                binding.fechaFin.text = date
                Log.d("AddReservaFragment", "Enviando fechas: inicio=$fechaInicio, fin=$fechaFin")
                fetchCaravanasDisponibles()
            }
        }
    }

    private fun showDatePicker(onDateSet: (String) -> Unit) {
        Log.d("AddReservaFragment", "ShowBien")
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(requireContext(), { _, year, month, day ->
            val fecha = String.format("%04d-%02d-%02d", year, month + 1, day)
            onDateSet(fecha)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }

    private fun setupHomeRecyclerView() {
        Log.d("AddReservaFragment", "Ha entrado en recyclerview")
        caravanaAdapter = CaravanaDisponibleAdapter( caravanaList) { caravana ->
            selectedCaravana = caravana
            mostrarDialogResumen(caravana)
        }
        binding.rvCaravanas.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = caravanaAdapter
        }
    }

    private fun mostrarDialogResumen(caravana: Caravana) {
        val binding = DialogResumenReservaBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setCancelable(false)
            .create()

        binding.tvCaravana.text = "Caravana: ${caravana.nombre} (${caravana.modelo})"
        binding.tvFechas.text = "Del $fechaInicio al $fechaFin"
        binding.tvCapacidad.text = "Capacidad: ${caravana.capacidad}"
        binding.tvPrecio.text = "Precio por día: ${caravana.precioDia} €"


        binding.btnCancelar.setOnClickListener { dialog.dismiss() }
        binding.btnConfirmar.setOnClickListener {
            val reserva = Reserva(
                id = 0,
                userId =0,
                usuario = null,
                caravanaId = caravana.id,
                caravana = caravana,
                fechaInicio = fechaInicio,
                fechaFin = fechaFin,
                precioTotal = "",
                precioPagado = "",
                fianza = "",
                createdAt = null,
                updatedAt = null
            )
            Log.d("AddReservaFragment", "Reserva: $reserva")
            reservasViewModel.crearReserva(reserva)
            dialog.dismiss()
            findNavController().navigate(R.id.action_addReservaFragment_to_homeFragment)
        }

        dialog.show()
    }




    private fun fetchCaravanasDisponibles() {
        Log.d("AddReservaFragment", "FetchBien")
        if (fechaInicio.isEmpty() || fechaFin.isEmpty()) return
        reservasViewModel.getCaravanasDisponibles(fechaInicio, fechaFin) { disponibles ->
            caravanaList = disponibles
            updateCaravanaRecycler()
            Log.d("AddReservaFragment", "Caravanas disponibles: $caravanaList")
        }
    }

    private fun updateCaravanaRecycler() {
        Log.d("AddReservaFragment", "UpdateBien")
        caravanaAdapter = CaravanaDisponibleAdapter(caravanaList) { caravana ->
            mostrarDialogResumen(caravana)
        }
        binding.rvCaravanas.adapter = caravanaAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
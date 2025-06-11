package com.example.autocaravanas.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autocaravanas.LoginActivity
import com.example.autocaravanas.MainActivity
import com.example.autocaravanas.R
import com.example.autocaravanas.adapter.ReservaAdapter
import com.example.autocaravanas.databinding.FragmentHomeBinding
import com.example.autocaravanas.fragments.EditReservaFragment
import com.example.autocaravanas.model.Reserva
import com.example.autocaravanas.viewmodel.ReservaViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener,
    MenuProvider {

    private var homeBinding: FragmentHomeBinding? = null
    private val binding get() = homeBinding!!

    private lateinit var reservasViewModel : ReservaViewModel
    private lateinit var reservaAdapter: ReservaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // layout de este fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                Log.d("MenuDebugEdit", "addMenuProvider ejecutado")
                menuHost.addMenuProvider(this@HomeFragment)
            }
        }

        reservasViewModel = (activity as MainActivity).reservaViewModel
        Log.d("HomeFragment", "onViewCreated: reservasViewModel = $reservasViewModel")

        reservasViewModel.getReservas()
        Log.d("HomeFragment", "onViewCreated: ${reservasViewModel.reservas.value}")

        setupHomeRecyclerView()

        reservasViewModel.reservas.observe(viewLifecycleOwner) { reservas ->
            updateUI(reservas)
        }

        reservasViewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                reservasViewModel.clearError()
            }
        }

        reservasViewModel.reservas.observe(viewLifecycleOwner) { reservas ->
            updateUI(reservas)
        }

        binding.addReservaFab.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_addReservaFragment)
            Log.d("HomeFragment", "Entrado en añadir reserva: ${reservasViewModel.reservas.value}")
        }

        reservasViewModel.reservas.observe(viewLifecycleOwner) { reservas ->
            updateUI(reservas)
        }
    }

    private fun updateUI(reservas: List<Reserva>?) {
        if (reservas != null) {
            val hoy = java.time.LocalDate.now()
            val reservasFuturas = reservas.filter {
                try {
                    val fechaFin = java.time.LocalDate.parse(it.fechaFin)
                    !fechaFin.isBefore(hoy)
                } catch (e: Exception) {
                    false
                }
            }
            if (reservasFuturas.isNotEmpty()) {
                binding.homeRecyclerView.visibility = View.VISIBLE
                binding.emptyReservasImage.visibility = View.GONE
                reservaAdapter.submitList(reservasFuturas)
            } else {
                binding.homeRecyclerView.visibility = View.GONE
                binding.emptyReservasImage.visibility = View.VISIBLE
            }
        }
    }

    private fun setupHomeRecyclerView() {
        reservaAdapter = ReservaAdapter()
        binding.homeRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = reservaAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeBinding = null
    }

    // Métodos de SearchView.OnQueryTextListener
    override fun onQueryTextSubmit(query: String?): Boolean {
        // Implementa la búsqueda si lo necesitas
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        // Implementa la búsqueda si lo necesitas
        return false
    }

    // Métodos de MenuProvider
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        Log.d("MenuDebugHome", "onCreateMenu en ${this::class.java.simpleName}")
        Log.d("MenuDebugHome", "Menu Correcto")
        menu.clear()
        menuInflater.inflate(R.menu.home_menu, menu)
    }

    // Menu options

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_refresh -> {
                Log.d("MenuDebugHome", "Item seleccionado: ${menuItem.itemId}")
                reservasViewModel.getReservas()
                Toast.makeText(requireContext(), "Reservas actualizadas", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_logout -> {
                Log.d("MenuDebugHome", "Item seleccionado: ${menuItem.itemId}")
                logout()
                true
            }
            R.id.action_contact -> {
                // Mostrar datos de contacto (puedes mostrar un AlertDialog, Fragment, actividad, etc.)
                mostrarDialogoContacto()
                true
            }
            else -> false
        }
    }

    private fun logout() {
        reservasViewModel.logout { success ->
            if (success) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "Error al cerrar la sesión", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        reservasViewModel.getReservas()
    }

    private fun mostrarDialogoContacto() {
        AlertDialog.Builder(requireContext())
            .setTitle("Contacto Autocaravanas Milan")
            .setMessage(
                """
            Autocaravanas Milan
            Teléfono: 999 888 777
            Móvil: 666 555 444
            Email: administracion@milanmc.me
            """.trimIndent()
            )
            .setPositiveButton("Cerrar", null)
            .show()
    }


}
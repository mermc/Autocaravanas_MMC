package com.example.autocaravanas.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.autocaravanas.databinding.ItemCaravanaDisponibleBinding
import com.example.autocaravanas.model.Caravana

class CaravanaDisponibleAdapter(
    private val caravanas: List<Caravana>,
    private val onReservarClick: (Caravana) -> Unit
) : RecyclerView.Adapter<CaravanaDisponibleAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCaravanaDisponibleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCaravanaDisponibleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = caravanas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val caravana = caravanas[position]
        holder.binding.tvNombre.text = caravana.nombre
        holder.binding.tvModelo.text = caravana.modelo
        holder.binding.tvCapacidad.text = "Capacidad: ${caravana.capacidad}"
        holder.binding.tvPrecio.text = "Precio/día: ${caravana.precioDia}"

        // Cargar la foto con Glide
        val url = "https://caravanas.milanmc.me/storage/${caravana.foto ?: ""}"
        Glide.with(holder.itemView.context)
            .load(url)
            .into(holder.binding.ivFoto)

        holder.binding.btnReservar.setOnClickListener { onReservarClick(caravana) }
    }


}
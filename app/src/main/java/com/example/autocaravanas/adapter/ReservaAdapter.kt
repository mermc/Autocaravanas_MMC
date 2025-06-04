package com.example.autocaravanas.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.autocaravanas.model.Reserva

class ReservaAdapter() : RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder>() {

    var reservasList: List<Reserva> = emptyList()

    var onItemClick: ((Reserva) -> Unit)? = null
    var onLongItemClick: ((Reserva) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val itemBinding =
            ReservaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReservaViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = reservasList.size

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val item = reservasList[position]
        holder.render(item)
    }

    fun getItem(position: Int): Reserva {
        return reservasList.get(position)
    }

    inner class ReservaViewHolder(binding: ReservaItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val caravana = binding.tvCaravana
        val fecha_inicio = binding.tvFechaInicio
        val fecha_fin = binding.tvFechaFin
        val precio_total = binding.tvPrecioTotal
        val precio_pagado = binding.tvPrecioPagado
        val fianza = binding.tvFianza

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(reservasList[layoutPosition])
            }

            itemView.setOnLongClickListener {
                onLongItemClick?.invoke(reservasList[layoutPosition])
                false
            }
        }

        fun render(reserva: Reserva) {
            caravana.text = reserva.caravana
            fecha_inicio.text = reserva.fechaInicio
            fecha_fin.text = reserva.fechaFin
            precio_total.text = reserva.precioTotal
            precio_pagado.text = reserva.precioPagado
            fianza.text = reserva.fianza

        }
    }
}
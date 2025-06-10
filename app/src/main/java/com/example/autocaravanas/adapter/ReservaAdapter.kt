package com.example.autocaravanas.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.autocaravanas.databinding.ReservaLayoutBinding
import com.example.autocaravanas.model.Reserva
import com.example.autocaravanas.fragments.HomeFragmentDirections

class ReservaAdapter() : RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder>() {

    class ReservaViewHolder(val itemBinding: ReservaLayoutBinding): RecyclerView.ViewHolder(itemBinding.root)

    // Diferenciador para comparar los elementos de la lista
    private val differCallback = object : DiffUtil.ItemCallback<Reserva>(){
        override fun areItemsTheSame(oldItem: Reserva, newItem: Reserva): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.caravana == newItem.caravana &&
                    oldItem.fechaInicio == newItem.fechaInicio &&
                    oldItem.fechaFin == newItem.fechaFin &&
                    oldItem.precioTotal == newItem.precioTotal &&
                    oldItem.precioPagado == newItem.precioPagado

        }

        override fun areContentsTheSame(oldItem: Reserva, newItem:Reserva): Boolean {
            return oldItem == newItem
        }
    }
    private val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<Reserva>) {
        differ.submitList(list)
    }

    // Inflamos el layout de cada elemento de la lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        return ReservaViewHolder(
            ReservaLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // Enlazamos los datos del modelo a la vista mostrando los detalles de la reserva según el ViewHolder
    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val currentReserva = differ.currentList[position]

        val url = "https://caravanas.milanmc.me/storage/" + (currentReserva.caravana?.foto ?: "")
        Log.d("ReservaAdapter", "URL de la imagen: $url")
        Glide.with(holder.itemView.context)
            .load(url)
            .into(holder.itemBinding.ivCaravana)

        holder.itemBinding.tvCaravanaNombre.text = "Caravana: ${currentReserva.caravana?.nombre ?: ""}"
        holder.itemBinding.tvCaravanaModelo.text = "Modelo:  ${currentReserva.caravana?.modelo ?: ""}"
        holder.itemBinding.tvCaravanaCapacidad.text = "Capacidad: ${currentReserva.caravana?.capacidad ?: ""}"
        holder.itemBinding.tvFechaInicio.text = "Fecha Inicio: ${currentReserva.fechaInicio}"
        holder.itemBinding.tvFechaFin.text = "Fecha Fin: ${currentReserva.fechaFin}"
        holder.itemBinding.tvPrecioTotal.text = "Precio Total: ${currentReserva.precioTotal.toString()}"
        holder.itemBinding.tvPrecioPagado.text = "Precio Pagado: ${currentReserva.precioPagado.toString()}"

        holder.itemView.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToEditReservaFragment(currentReserva)
            it.findNavController().navigate(direction)
        }
    }

}
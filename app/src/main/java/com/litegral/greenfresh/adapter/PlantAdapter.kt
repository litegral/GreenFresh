package com.litegral.greenfresh.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.litegral.greenfresh.R
import com.litegral.greenfresh.data.Plant
import com.litegral.greenfresh.DetailItemActivity

class PlantAdapter(
    private val plants: MutableList<Plant>,
    private val onDeleteClick: (Plant) -> Unit
) : RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        val viewHolder = PlantViewHolder(view)
        viewHolder.itemView.findViewById<Button>(R.id.btnHapus).setOnClickListener {
            val position = viewHolder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onDeleteClick(plants[position])
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plants[position]
        holder.bind(plant)
    }

    override fun getItemCount(): Int = plants.size

    fun addPlants(newPlants: List<Plant>) {
        plants.clear()
        plants.addAll(newPlants)
        notifyDataSetChanged()
    }

    class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val btnDetail: Button = itemView.findViewById(R.id.btnDetail)
        private val btnHapus: Button = itemView.findViewById(R.id.btnHapus)

        fun bind(plant: Plant) {
            tvTitle.text = plant.plant_name
            tvPrice.text = "Rp ${plant.price}"
            // The API does not provide an image, so we can load a placeholder or hide the ImageView
            // For now, I'll just use the placeholder from the layout.

            btnDetail.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, DetailItemActivity::class.java).apply {
                    putExtra("PLANT_NAME", plant.plant_name)
                }
                context.startActivity(intent)
            }
        }
    }
} 
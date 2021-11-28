package com.example.fall

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fall.data.PlayerData
import com.example.fall.databinding.ItemPlayerBinding

class DeadPlayerAdapter(private val listener: OnPlayerSelectedListener) :
    RecyclerView.Adapter<DeadPlayerAdapter.DeadPlayerViewHolder>() {

    private val items = mutableListOf<PlayerData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : DeadPlayerViewHolder {
        return DeadPlayerViewHolder(
            ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: DeadPlayerViewHolder, position: Int) {
        val item = items[position]

        holder.binding.charName.text =
            when (item.modelResourceId) {
                R.drawable.pistol_moving_sprite -> "Denzel"
                R.drawable.shotgun_player -> "Justin"
                else -> "Denzel"
            }

        val resourceId = when (item.modelResourceId) {
            R.drawable.pistol_moving_sprite -> R.drawable.pistolie
            R.drawable.shotgun_player -> R.drawable.shottie
            else -> R.drawable.pistolie
        }

        holder.binding.score.text = item.score.toString()
        holder.binding.imageView.setImageResource(resourceId)
    }

    fun addItem(item: PlayerData) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun update(itemList: List<PlayerData>) {
        items.clear()
        items.addAll(itemList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    interface OnPlayerSelectedListener {
        fun onItemChanged(item: PlayerData)
    }

    inner class DeadPlayerViewHolder(val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root)
}

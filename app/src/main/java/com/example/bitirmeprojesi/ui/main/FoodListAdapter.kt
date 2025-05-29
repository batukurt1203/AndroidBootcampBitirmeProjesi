package com.example.bitirmeprojesi.ui.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bitirmeprojesi.data.model.Food
import com.example.bitirmeprojesi.databinding.ItemFoodBinding

class FoodListAdapter(
    private var foods: List<Food>,
    private val onItemClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodListAdapter.FoodViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateFoods(newFoods: List<Food>?) {
        if (newFoods != null) {
            foods = newFoods
        }
        notifyDataSetChanged()
    }

    inner class FoodViewHolder(val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foods[position]
        holder.binding.foodName.text = food.yemek_adi
        holder.binding.foodPrice.text = "${food.yemek_fiyat} ₺"
        Glide.with(holder.itemView.context)
            .load("http://kasimadalan.pe.hu/yemekler/resimler/${food.yemek_resim_adi}")
            .into(holder.binding.foodImage)
        holder.itemView.setOnClickListener { onItemClick(food) }
    }

    override fun getItemCount() = foods.size
}
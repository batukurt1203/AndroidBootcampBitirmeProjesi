package com.example.bitirmeprojesi.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bitirmeprojesi.data.model.CartItem
import com.example.bitirmeprojesi.databinding.ItemCartBinding

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onDeleteClick: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.binding.cartFoodName.text = item.yemek_adi
        holder.binding.cartFoodPrice.text = "${item.yemek_fiyat} ₺"
        holder.binding.cartFoodCount.text = "Adet: ${item.yemek_siparis_adet}"
        Glide.with(holder.itemView.context)
            .load("http://kasimadalan.pe.hu/yemekler/resimler/${item.yemek_resim_adi}")
            .into(holder.binding.cartFoodImage)
        holder.binding.btnDelete.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount() = cartItems.size
}
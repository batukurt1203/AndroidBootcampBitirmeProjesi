package com.example.bitirmeprojesi.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.bitirmeprojesi.databinding.FragmentFoodDetailBinding
import com.example.bitirmeprojesi.viewmodel.CartViewModel

class FoodDetailFragment : Fragment() {
    private var _binding: FragmentFoodDetailBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel: CartViewModel by viewModels()
    private val args: FoodDetailFragmentArgs by navArgs()
    private var adet = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFoodDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val food = args.food
        binding.foodName.text = food.yemek_adi
        binding.foodPrice.text = "${food.yemek_fiyat} ₺"
        
        Glide.with(this)
            .load("http://kasimadalan.pe.hu/yemekler/resimler/${food.yemek_resim_adi}")
            .into(binding.foodImage)

        setupQuantityControls()
        setupCartButtons(food)
        setupObservers()
    }

    private fun setupQuantityControls() {
        binding.adetText.text = adet.toString()
        binding.btnIncrease.setOnClickListener {
            adet++
            binding.adetText.text = adet.toString()
        }
        binding.btnDecrease.setOnClickListener {
            if (adet > 1) {
                adet--
                binding.adetText.text = adet.toString()
            }
        }
    }

    private fun setupCartButtons(food: com.example.bitirmeprojesi.data.model.Food) {
        binding.btnAddToCart.setOnClickListener {
            cartViewModel.addToCart(
                food.yemek_adi,
                food.yemek_resim_adi,
                food.yemek_fiyat.toInt(),
                adet,
                "batuhan_user"
            )
        }
        binding.btnGoToCart.setOnClickListener {
            findNavController().navigate(FoodDetailFragmentDirections.actionFoodDetailFragmentToCartFragment())
        }
    }

    private fun setupObservers() {
        cartViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnAddToCart.isEnabled = !isLoading
        }

        cartViewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        cartViewModel.cartItems.observe(viewLifecycleOwner) { items ->
            if (items.isNotEmpty()) {
                Toast.makeText(requireContext(), "Ürün sepete eklendi!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
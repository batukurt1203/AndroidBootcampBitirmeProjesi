package com.example.bitirmeprojesi.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitirmeprojesi.databinding.FragmentCartBinding
import com.example.bitirmeprojesi.viewmodel.CartViewModel

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CartViewModel by viewModels()
    private val kullaniciAdi = "batuhan_user" // Sabit kullanıcı adı

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupConfirmButton()
        setupClearCartButton()

        // Sepeti yükle
        viewModel.fetchCart(kullaniciAdi)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setupObservers() {
        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            binding.recyclerView.adapter = CartAdapter(items) { item ->
                viewModel.removeFromCart(item.sepet_yemek_id.toInt(), kullaniciAdi)
            }
            
            // Toplam tutarı güncelle
            val total = items.sumOf { 
                val fiyat = it.yemek_fiyat.toIntOrNull() ?: 0
                val adet = it.yemek_siparis_adet.toIntOrNull() ?: 0
                fiyat * adet
            }
            binding.totalPrice.text = "Toplam: $total ₺"
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupConfirmButton() {
        binding.btnConfirmOrder.setOnClickListener {
            if (viewModel.cartItems.value?.isNotEmpty() == true) {
                viewModel.confirmOrder(kullaniciAdi)
                Toast.makeText(requireContext(), "Siparişiniz onaylandı!", Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Sepetiniz boş!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupClearCartButton() {
        binding.btnClearCart.setOnClickListener {
            viewModel.clearCart(kullaniciAdi)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
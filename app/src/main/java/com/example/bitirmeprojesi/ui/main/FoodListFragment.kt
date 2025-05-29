package com.example.bitirmeprojesi.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bitirmeprojesi.R
import com.example.bitirmeprojesi.databinding.FragmentFoodListBinding
import com.example.bitirmeprojesi.viewmodel.FoodViewModel

class FoodListFragment : Fragment() {
    private var _binding: FragmentFoodListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FoodViewModel by viewModels()
    private var adapter: FoodListAdapter? = null
    private var allFoods: List<com.example.bitirmeprojesi.data.model.Food> = emptyList()
    private var filteredFoods: List<com.example.bitirmeprojesi.data.model.Food> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_food_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                val action = FoodListFragmentDirections.actionFoodListFragmentToCartFragment()
                findNavController().navigate(action)
                true
            }
            R.id.action_sort -> {
                showSortDialog()
                true
            }
            R.id.action_filter -> {
                showFilterDialog()
                true
            }
            R.id.action_refresh -> {
                viewModel.fetchFoods()
                Toast.makeText(requireContext(), "Yemekler yenilendi!", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        viewModel.fetchFoods()
    }

    private fun setupRecyclerView() {
        adapter = FoodListAdapter(emptyList()) { food ->
            val action = FoodListFragmentDirections.actionFoodListFragmentToFoodDetailFragment(food)
            findNavController().navigate(action)
        }
        
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@FoodListFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.foods.observe(viewLifecycleOwner) { foods ->
            allFoods = foods ?: emptyList()
            filteredFoods = allFoods
            adapter?.updateFoods(filteredFoods)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showSortDialog() {
        val options = arrayOf("Fiyata Göre Artan", "Fiyata Göre Azalan", "İsme Göre A-Z", "İsme Göre Z-A")
        AlertDialog.Builder(requireContext())
            .setTitle("Sırala")
            .setItems(options) { _, which ->
                filteredFoods = when (which) {
                    0 -> filteredFoods.sortedBy { it.yemek_fiyat.toIntOrNull() ?: 0 }
                    1 -> filteredFoods.sortedByDescending { it.yemek_fiyat.toIntOrNull() ?: 0 }
                    2 -> filteredFoods.sortedBy { it.yemek_adi }
                    3 -> filteredFoods.sortedByDescending { it.yemek_adi }
                    else -> filteredFoods
                }
                adapter?.updateFoods(filteredFoods)
            }
            .show()
    }

    private fun showFilterDialog() {
        val categories = arrayOf("Tümü", "İçecekler", "Tatlılar", "Ana Yemekler")
        AlertDialog.Builder(requireContext())
            .setTitle("Filtrele")
            .setItems(categories) { _, which ->
                filteredFoods = when (which) {
                    1 -> allFoods.filter { it.yemek_adi.contains("ayran", true) || it.yemek_adi.contains("fanta", true)
                            || it.yemek_adi.contains("kahve", true) || it.yemek_adi.equals("su", true)  }
                    2 -> allFoods.filter { it.yemek_adi.contains("baklava", true) || it.yemek_adi.contains("kadayıf", true)
                            || it.yemek_adi.contains("tiramisu", true)|| it.yemek_adi.contains("sütlaç", true)}
                    3 -> allFoods.filter { it.yemek_adi.contains("ızgara", true) || it.yemek_adi.contains("köfte", true)
                            || it.yemek_adi.contains("lazanya", true)|| it.yemek_adi.contains("makarna", true)
                            || it.yemek_adi.contains("pizza", true)}
                    else -> allFoods
                }
                adapter?.updateFoods(filteredFoods)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
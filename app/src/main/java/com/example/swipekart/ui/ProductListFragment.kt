package com.example.swipekart.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swipekart.R
import com.example.swipekart.Utils
import com.example.swipekart.adapter.ProductAdapter
import com.example.swipekart.databinding.FragmentProductListBinding
import com.example.swipekart.model.Product
import com.example.swipekart.network.ProductServiceImpl
import com.example.swipekart.viewmodel.ProductViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductListFragment : Fragment() {

    private val productViewModel: ProductViewModel by viewModel()
    private lateinit var productAdapter: ProductAdapter
    private lateinit var binding: FragmentProductListBinding
    private var originalProductList: List<Product> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.swipeToRefresh.isRefreshing=true
        getData() //load data offline or from API

        binding.swipeToRefresh.setOnRefreshListener {
            binding.searchView.setQuery("",false)
            getData()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                productViewModel.searchProducts(query) //search results from room DB
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                productViewModel.searchProducts(newText) //search results from room DB
                return true
            }
        })

        binding.searchView.setOnCloseListener {
            if (originalProductList.isNotEmpty()) {
                productAdapter.submitList(originalProductList) //for loading full list when search is cleared
            } else {
                getData()
            }
            true
        }


        // Initialize RecyclerView and Adapter
        productAdapter = ProductAdapter(requireContext())
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = productAdapter
        }



        // Observe products LiveData
        productViewModel.products.observe(viewLifecycleOwner) { products ->
            binding.swipeToRefresh.isRefreshing=false
            originalProductList = products
            productAdapter.submitList(products)
        }

        // Observe products search LiveData
        productViewModel.searchResults.observe(viewLifecycleOwner){products->
            productAdapter.submitList(products)
        }


        //Add new product option
        binding.fab.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }

    }

    private fun getData(){
        if (Utils().isNetworkAvailable(requireContext())) {
            productViewModel.loadProducts()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.no_internet),
                Toast.LENGTH_SHORT
            ).show()
            productViewModel.loadOfflineData()
        }
    }


}
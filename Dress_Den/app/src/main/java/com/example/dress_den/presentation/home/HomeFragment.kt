package com.example.dress_den.presentation.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.dress_den.R
import com.example.dress_den.databinding.FragmentHomeBinding
import com.example.dress_den.domain.model.Category
import com.example.dress_den.domain.model.Product
import com.example.dress_den.presentation.base.BaseFragment
import com.example.dress_den.data.remote.Resource
import com.example.dress_den.util.MarginItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.refreshData()
            }

            retryButton.setOnClickListener {
                viewModel.refreshData()
            }

            // Setup RecyclerViews with margin decorations
            setupRecyclerView(featuredProductsRecyclerView)
            setupRecyclerView(categoriesRecyclerView)
            setupRecyclerView(newArrivalsRecyclerView)
            setupRecyclerView(trendingProductsRecyclerView)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            setHasFixedSize(true)
            addItemDecoration(
                MarginItemDecoration(
                    resources.getDimensionPixelSize(R.dimen.item_margin)
                )
            )
        }
    }

    private fun observeViewModel() {
        viewModel.featuredProducts.observe(viewLifecycleOwner) { resource ->
            handleProductsResource(resource, binding.featuredProductsRecyclerView)
        }

        viewModel.categories.observe(viewLifecycleOwner) { resource ->
            handleCategoriesResource(resource)
        }

        viewModel.newArrivals.observe(viewLifecycleOwner) { resource ->
            handleProductsResource(resource, binding.newArrivalsRecyclerView)
        }

        viewModel.trendingProducts.observe(viewLifecycleOwner) { resource ->
            handleProductsResource(resource, binding.trendingProductsRecyclerView)
        }
    }

    private fun handleProductsResource(resource: Resource<List<Product>>, recyclerView: RecyclerView) {
        when (resource) {
            is Resource.Loading -> showLoading()
            is Resource.Success -> {
                hideLoading()
                recyclerView.adapter = ProductAdapter(resource.data) { product ->
                    navigateToProductDetail(product)
                }
            }
            is Resource.Error -> showError(resource.message)
        }
    }

    private fun handleCategoriesResource(resource: Resource<List<Category>>) {
        when (resource) {
            is Resource.Loading -> showLoading()
            is Resource.Success -> {
                hideLoading()
                binding.categoriesRecyclerView.adapter = CategoryAdapter(resource.data) { category ->
                    navigateToCategory(category)
                }
            }
            is Resource.Error -> showError(resource.message)
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            errorView.visibility = View.GONE
        }
    }

    private fun hideLoading() {
        binding.apply {
            progressBar.visibility = View.GONE
            errorView.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showError(message: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            errorView.visibility = View.VISIBLE
            errorMessageTextView.text = message
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun navigateToProductDetail(product: Product) {
        val action = HomeFragmentDirections.actionHomeToProductDetail(product.id)
        findNavController().navigate(action)
    }

    private fun navigateToCategory(category: Category) {
        val action = HomeFragmentDirections.actionHomeToProductList(category.id)
        findNavController().navigate(action)
    }
}

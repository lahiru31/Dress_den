package com.example.dress_den.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dress_den.domain.model.Category
import com.example.dress_den.domain.model.Product
import com.example.dress_den.domain.repository.CategoryRepository
import com.example.dress_den.domain.repository.ProductRepository
import com.example.dress_den.presentation.base.BaseViewModel
import com.example.dress_den.data.remote.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : BaseViewModel() {

    private val _featuredProducts = MutableLiveData<Resource<List<Product>>>()
    val featuredProducts: LiveData<Resource<List<Product>>> = _featuredProducts

    private val _categories = MutableLiveData<Resource<List<Category>>>()
    val categories: LiveData<Resource<List<Category>>> = _categories

    private val _newArrivals = MutableLiveData<Resource<List<Product>>>()
    val newArrivals: LiveData<Resource<List<Product>>> = _newArrivals

    private val _trendingProducts = MutableLiveData<Resource<List<Product>>>()
    val trendingProducts: LiveData<Resource<List<Product>>> = _trendingProducts

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        loadFeaturedProducts()
        loadCategories()
        loadNewArrivals()
        loadTrendingProducts()
    }

    private fun loadFeaturedProducts() {
        viewModelScope.launch {
            _featuredProducts.value = Resource.Loading()
            try {
                val products = productRepository.getFeaturedProducts()
                _featuredProducts.value = Resource.Success(products)
            } catch (e: Exception) {
                _featuredProducts.value = Resource.Error(e.message ?: "Failed to load featured products")
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _categories.value = Resource.Loading()
            try {
                val categories = categoryRepository.getCategories()
                _categories.value = Resource.Success(categories)
            } catch (e: Exception) {
                _categories.value = Resource.Error(e.message ?: "Failed to load categories")
            }
        }
    }

    private fun loadNewArrivals() {
        viewModelScope.launch {
            _newArrivals.value = Resource.Loading()
            try {
                val products = productRepository.getNewArrivals()
                _newArrivals.value = Resource.Success(products)
            } catch (e: Exception) {
                _newArrivals.value = Resource.Error(e.message ?: "Failed to load new arrivals")
            }
        }
    }

    private fun loadTrendingProducts() {
        viewModelScope.launch {
            _trendingProducts.value = Resource.Loading()
            try {
                val products = productRepository.getTrendingProducts()
                _trendingProducts.value = Resource.Success(products)
            } catch (e: Exception) {
                _trendingProducts.value = Resource.Error(e.message ?: "Failed to load trending products")
            }
        }
    }

    fun refreshData() {
        loadHomeData()
    }
}

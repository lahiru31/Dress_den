package com.example.dress_den.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dress_den.databinding.ItemProductBinding
import com.example.dress_den.domain.model.Product
import com.example.dress_den.util.CurrencyUtils

class ProductAdapter(
    private val products: List<Product>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onProductClick(products[position])
                }
            }
        }

        fun bind(product: Product) {
            binding.apply {
                // Load product image
                Glide.with(productImageView)
                    .load(product.imageUrls.firstOrNull())
                    .centerCrop()
                    .into(productImageView)

                // Set product name
                productNameTextView.text = product.name

                // Set prices
                productPriceTextView.text = CurrencyUtils.formatPrice(product.getFinalPrice())
                
                if (product.hasDiscount()) {
                    productDiscountPriceTextView.isVisible = true
                    productDiscountPriceTextView.text = CurrencyUtils.formatPrice(product.price)
                    productDiscountPriceTextView.paintFlags = android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    productDiscountPriceTextView.isVisible = false
                }

                // Set rating
                productRatingBar.rating = product.rating
            }
        }
    }
}

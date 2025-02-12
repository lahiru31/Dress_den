package com.example.dress_den.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dress_den.R
import com.example.dress_den.databinding.ItemCategoryBinding
import com.example.dress_den.domain.model.Category

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCategoryClick(categories[position])
                }
            }
        }

        fun bind(category: Category) {
            binding.apply {
                // Load category image
                Glide.with(categoryImageView)
                    .load(category.imageUrl)
                    .centerCrop()
                    .into(categoryImageView)

                // Set category name
                categoryNameTextView.text = category.name

                // Set product count
                productCountTextView.text = itemView.context.resources.getQuantityString(
                    R.plurals.product_count,
                    category.productCount,
                    category.productCount
                )
            }
        }
    }
}

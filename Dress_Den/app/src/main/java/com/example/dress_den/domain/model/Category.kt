package com.example.dress_den.domain.model

data class Category(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val parentCategoryId: String? = null,
    val subCategories: List<Category> = emptyList(),
    val productCount: Int = 0,
    val isActive: Boolean = true,
    val displayOrder: Int = 0
) {
    fun hasSubCategories(): Boolean = subCategories.isNotEmpty()
    
    fun isSubCategory(): Boolean = parentCategoryId != null
    
    fun getFullPath(): String {
        return if (isSubCategory()) {
            "$parentCategoryId > $name"
        } else {
            name
        }
    }
}

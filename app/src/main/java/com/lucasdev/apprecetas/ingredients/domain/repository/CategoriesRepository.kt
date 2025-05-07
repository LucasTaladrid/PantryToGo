package com.lucasdev.apprecetas.ingredients.domain.repository

import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel


interface CategoriesRepository {
    suspend fun getCategories(): List<CategoryModel>
    suspend fun addCategory(ingredient: CategoryModel): Boolean
    suspend fun updateCategory(ingredient: CategoryModel): Boolean
    suspend fun deleteCategory(name: String): Boolean
}
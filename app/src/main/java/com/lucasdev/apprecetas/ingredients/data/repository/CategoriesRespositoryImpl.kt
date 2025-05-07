package com.lucasdev.apprecetas.ingredients.data.repository

import com.lucasdev.apprecetas.ingredients.data.datasource.CategoryFirebaseDataSource
import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import com.lucasdev.apprecetas.ingredients.domain.repository.CategoriesRepository
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val categoryFirebaseDataSource: CategoryFirebaseDataSource
) : CategoriesRepository {

    override suspend fun getCategories(): List<CategoryModel> {
        return categoryFirebaseDataSource.getCategories()
    }

    override suspend fun addCategory(category: CategoryModel): Boolean {
        return categoryFirebaseDataSource.addCategory(category)
    }

    override suspend fun updateCategory(category: CategoryModel): Boolean {
        return categoryFirebaseDataSource.updateCategory(category)
    }

    override suspend fun deleteCategory(name: String): Boolean {
        return categoryFirebaseDataSource.deleteCategory(name)
    }
}
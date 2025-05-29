package com.lucasdev.apprecetas.ingredients.data.repository

import com.lucasdev.apprecetas.ingredients.data.datasource.CategoryFirebaseDataSource
import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import com.lucasdev.apprecetas.ingredients.domain.repository.CategoriesRepository
import javax.inject.Inject


/**
 * Implementation of [CategoriesRepository] that interacts with Firebase Firestore
 * through [CategoryFirebaseDataSource] to manage ingredient categories.
 *
 * @property categoryFirebaseDataSource The data source used for performing category-related operations.
 */
class CategoriesRepositoryImpl @Inject constructor(
    private val categoryFirebaseDataSource: CategoryFirebaseDataSource
) : CategoriesRepository {

    /**
     * Retrieves a list of all ingredient categories from Firestore.
     *
     * @return A list of [CategoryModel] objects.
     */
    override suspend fun getCategories(): List<CategoryModel> {
        return categoryFirebaseDataSource.getCategories()
    }

    /**
     * Adds a new ingredient category to Firestore.
     *
     * @param category The [CategoryModel] to be added.
     * @return `true` if the operation was successful, `false` otherwise.
     */
    override suspend fun addCategory(category: CategoryModel): Boolean {
        return categoryFirebaseDataSource.addCategory(category)
    }

    /**
     * Updates an existing ingredient category in Firestore.
     *
     * @param category The updated [CategoryModel].
     * @return `true` if the update was successful, `false` otherwise.
     */
    override suspend fun updateCategory(category: CategoryModel): Boolean {
        return categoryFirebaseDataSource.updateCategory(category)
    }

    /**
     * Deletes an ingredient category from Firestore by its name.
     *
     * @param name The name of the category to delete.
     * @return `true` if the deletion was successful, `false` otherwise.
     */
    override suspend fun deleteCategory(name: String): Boolean {
        return categoryFirebaseDataSource.deleteCategory(name)
    }
}
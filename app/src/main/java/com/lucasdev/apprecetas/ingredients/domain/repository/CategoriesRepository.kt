package com.lucasdev.apprecetas.ingredients.domain.repository

import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel


/**
 * Repository interface for managing ingredient categories.
 */
interface CategoriesRepository {

    /**
     * Retrieves all categories.
     *
     * @return A list of [CategoryModel] representing all categories.
     */
    suspend fun getCategories(): List<CategoryModel>

    /**
     * Adds a new category.
     *
     * @param ingredient The [CategoryModel] to add.
     * @return `true` if the category was added successfully, `false` otherwise.
     */
    suspend fun addCategory(ingredient: CategoryModel): Boolean

    /**
     * Updates an existing category.
     *
     * @param ingredient The [CategoryModel] with updated data.
     * @return `true` if the update was successful, `false` otherwise.
     */
    suspend fun updateCategory(ingredient: CategoryModel): Boolean

    /**
     * Deletes a category by name.
     *
     * @param name The name of the category to delete.
     * @return `true` if the deletion was successful, `false` otherwise.
     */
    suspend fun deleteCategory(name: String): Boolean
}

package com.lucasdev.apprecetas.ingredients.domain.usecase

import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import com.lucasdev.apprecetas.ingredients.domain.repository.CategoriesRepository
import javax.inject.Inject

/**
 * Use case for retrieving categories.
 *
 * In principle, these use cases should not be executed except for getting categories.
 * Adding, deleting, or updating a category is considered an exceptional case
 * and should be handled directly at the database level.
 */
class GetCategoriesUseCase @Inject constructor(private val repository: CategoriesRepository) {
    /**
     * Retrieves the list of categories from the repository.
     */
    suspend operator fun invoke() = repository.getCategories()
}

/**
 * Use case for adding a new category.
 *
 * Use only in exceptional cases as modifications should primarily be done at the database level.
 */
class AddCategoryUseCase @Inject constructor(private val repository: CategoriesRepository) {
    /**
     * Adds a new category via the repository.
     *
     * @param category The category to add.
     */
    suspend operator fun invoke(category: CategoryModel) = repository.addCategory(category)
}

/**
 * Use case for updating an existing category.
 *
 * Use only in exceptional cases as modifications should primarily be done at the database level.
 */
class UpdateCategoryUseCase @Inject constructor(private val repository: CategoriesRepository) {
    /**
     * Updates a category via the repository.
     *
     * @param category The category to update.
     */
    suspend operator fun invoke(category: CategoryModel) = repository.updateCategory(category)
}

/**
 * Use case for deleting a category.
 *
 * Use only in exceptional cases as modifications should primarily be done at the database level.
 */
class DeleteCategoryUseCase @Inject constructor(private val repository: CategoriesRepository) {
    /**
     * Deletes a category by name via the repository.
     *
     * @param name The name of the category to delete.
     */
    suspend operator fun invoke(name: String) = repository.deleteCategory(name)
}

package com.lucasdev.apprecetas.ingredients.data.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
/**
 * Data source responsible for handling category-related operations
 * in Firebase Firestore for ingredient management.
 */
class CategoryFirebaseDataSource @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Returns a reference to the "ingredient_categories" collection in Firestore.
     */
    private fun categoryCollection() = db.collection("ingredient_categories")

    /**
     * Retrieves all ingredient categories from Firestore.
     *
     * @return A list of [CategoryModel] objects if successful, or an empty list in case of error.
     */
    suspend fun getCategories(): List<CategoryModel> {
        return try {
            val snapshot = categoryCollection().get().await()
            snapshot.mapNotNull { it.toObject(CategoryModel::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Adds a new category to Firestore.
     *
     * The category is stored with its name as the document ID.
     *
     * @param category The category to be added.
     * @return `true` if the operation was successful, `false` otherwise.
     */
    suspend fun addCategory(category: CategoryModel): Boolean {
        return try {
            val docRef = categoryCollection().document(category.name)
            docRef.set(category).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Updates an existing category in Firestore.
     *
     * This overwrites the document with the same name.
     *
     * @param category The updated category data.
     * @return `true` if the update was successful, `false` otherwise.
     */
    suspend fun updateCategory(category: CategoryModel): Boolean {
        return try {
            val docRef = categoryCollection().document(category.name)
            docRef.set(category).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Deletes a category from Firestore based on its name.
     *
     * @param name The name of the category to delete.
     * @return `true` if deletion was successful, `false` otherwise.
     */
    suspend fun deleteCategory(name: String): Boolean {
        return try {
            val docRef = categoryCollection().document(name)
            docRef.delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
package com.lucasdev.apprecetas.ingredients.data.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CategoryFirebaseDataSource @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()

    private fun categoryCollection() = db.collection("ingredient_categories")

    suspend fun getCategories(): List<CategoryModel> {
        return try {
            val snapshot = categoryCollection().get().await()
            snapshot.mapNotNull { it.toObject(CategoryModel::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }


    suspend fun addCategory(category: CategoryModel): Boolean {
        return try {
            val docRef = categoryCollection().document(category.name)
            docRef.set(category).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateCategory(category: CategoryModel): Boolean {
        return try {
            val docRef = categoryCollection().document(category.name)
            docRef.set(category).await()
            true
        } catch (e: Exception) {
            false
        }
    }

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
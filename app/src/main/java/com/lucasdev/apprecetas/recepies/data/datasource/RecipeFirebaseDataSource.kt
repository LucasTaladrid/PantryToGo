package com.lucasdev.apprecetas.recepies.data.datasource

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel
import com.lucasdev.apprecetas.recepies.domain.model.RecipeModel
import com.lucasdev.apprecetas.shopping.data.datasource.ShoppingListFirebaseDataSource
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingIngredientModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RecipeFirebaseDataSource @Inject constructor() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val uid = auth.currentUser?.uid ?: "anon"
    private val shoppingDataSource = ShoppingListFirebaseDataSource()


    private fun userRecipesRef() = db.collection("users").document(uid).collection("recipes")

    private fun favoritesRef() = db.collection("users").document(uid).collection("favorites")

    private fun pendingRef() = db.collection("users").document(uid).collection("pending")

    private fun pantryRef() = db.collection("users").document(uid).collection("inventory")

    private fun commonRecipesRef() = db.collection("recipes")

    suspend fun isAdmin(): Boolean {
        val snapshot = db.collection("users").document(uid).get().await()
        return snapshot.getBoolean("admin") ?: false

    }

    suspend fun addRecipe(recipe: RecipeModel): RecipeModel? {
        val admin = isAdmin()
        val ref = if (admin) commonRecipesRef() else userRecipesRef()

        val docRef = ref.document()
        val withId = recipe.copy(id = docRef.id, dateCreated = Timestamp.now())

        return try {
            docRef.set(withId).await()
            withId
        } catch (e: Exception) {
            Log.e("RecipeFirebaseDataSource", "Error adding recipe", e)
            null
        }

    }

    suspend fun getCommonRecipes(): List<RecipeModel> = suspendCoroutine { cont ->
        commonRecipesRef().get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.mapNotNull { it.toObject(RecipeModel::class.java) }
                cont.resume(list)
            }
            .addOnFailureListener {
                cont.resume(emptyList())
            }
    }

    suspend fun getUserRecipes(): List<RecipeModel> {
        val admin = isAdmin() // ✔️ ahora sí, porque ya estamos en un contexto suspendido
        val ref = if (admin) commonRecipesRef() else userRecipesRef()
        return suspendCoroutine { cont ->
            ref.get()
                .addOnSuccessListener { snapshot ->
                    val list = snapshot.mapNotNull { it.toObject(RecipeModel::class.java) }
                    cont.resume(list)
                }
                .addOnFailureListener {
                    cont.resume(emptyList())
                }
        }
    }

    suspend fun addToFavorites(recipe: RecipeModel) {
        try {
            favoritesRef()
                .document(recipe.id)
                .set(
                    mapOf(
                        "id" to recipe.id,
                        "name" to recipe.name,
                        "ingredients" to recipe.ingredients,
                        "steps" to recipe.steps
                    )
                )
                .await()
        } catch (e: Exception) {
            Log.e("RecipeDataSource", "Error adding to favorites", e)
        }
    }

    suspend fun addToPending(recipe: RecipeModel,shoppingListId: String) {
        try {
            verifyPantryAndUpdateShoppingList(recipe, shoppingListId)
            pendingRef()
                .document(recipe.id)
                .set(
                    mapOf(
                        "id" to recipe.id,
                        "name" to recipe.name,
                        "ingredients" to recipe.ingredients,
                        "steps" to recipe.steps
                    )
                )
                .await()
        } catch (e: Exception) {
            Log.e("RecipeDataSource", "Error adding to pending", e)
        }
    }

    suspend fun removeFromFavorites(recipe: RecipeModel) {
        try {
            favoritesRef()
                .document(recipe.id)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("RecipeDataSource", "Error removing from favorites", e)
        }
    }

    suspend fun removeFromPending(recipe: RecipeModel, shoppingListId: String) {
        try {

            pendingRef().document(recipe.id).delete().await()


            val shoppingIngredients = recipe.ingredients.map {
                ShoppingIngredientModel(
                    id = it.ingredientId,
                    ingredientId = it.ingredientId,
                    name = it.name,
                    unit = it.unit,
                    quantity = it.quantity,
                    category = it.category,
                    checked = false
                )
            }

            shoppingDataSource.subtractIngredientsFromShoppingList(shoppingListId, shoppingIngredients)

        } catch (e: Exception) {
            Log.e("RecipeDataSource", "Error removing from pending", e)
        }
    }

    suspend fun getFavoriteRecipes(): List<RecipeModel> = try {
        db.collection("users")
            .document(uid)
            .collection("favorites")
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(RecipeModel::class.java) }
    } catch (e: Exception) {
        Log.e("RecipeDataSource", "Error getting favorite recipes", e)
        emptyList()
    }

    suspend fun getPendingRecipes(): List<RecipeModel> = try {
        db.collection("users")
            .document(uid)
            .collection("pending")
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(RecipeModel::class.java) }
    } catch (e: Exception) {
        Log.e("RecipeDataSource", "Error getting pending recipes", e)
        emptyList()
    }

    /*
      This method should be modified in the future to accommodate a larger number of users (Cloud Function)
     */
    suspend fun deleteRecipe(recipeId: String): Boolean {
        return try {
            val admin = isAdmin()
            val mainRef = if (admin) commonRecipesRef() else userRecipesRef()

            mainRef.document(recipeId).delete().await()

            if (admin) {
                val usersSnapshot = db.collection("users").get().await()
                for (userDoc in usersSnapshot.documents) {
                    val userId = userDoc.id
                    val userFavorites =
                        db.collection("users").document(userId).collection("favorites")
                    val userPending = db.collection("users").document(userId).collection("pending")

                    userFavorites.document(recipeId).delete().await()
                    userPending.document(recipeId).delete().await()
                }
            } else {
                favoritesRef().document(recipeId).delete().await()
                pendingRef().document(recipeId).delete().await()
            }

            true
        } catch (e: Exception) {
            Log.e("RecipeFirebaseDataSource", "Error deleting recipe completely", e)
            false
        }

    }

    suspend fun updateRecipe(recipe: RecipeModel): Boolean {
        return try {
            val admin = isAdmin()
            val mainRef = if (admin) commonRecipesRef() else userRecipesRef()

            // 1. Actualizar receta principal
            mainRef.document(recipe.id).set(recipe).await()

            if (admin) {
                val usersSnapshot = db.collection("users").get().await()
                for (userDoc in usersSnapshot.documents) {
                    val userId = userDoc.id
                    val favDoc = db.collection("users").document(userId).collection("favorites")
                        .document(recipe.id)
                    val pendDoc = db.collection("users").document(userId).collection("pending")
                        .document(recipe.id)

                    if (favDoc.get().await().exists()) {
                        favDoc.set(recipe).await()
                    }
                    if (pendDoc.get().await().exists()) {
                        pendDoc.set(recipe).await()
                    }
                }
            } else {
                val favDoc = favoritesRef().document(recipe.id)
                val pendDoc = pendingRef().document(recipe.id)

                if (favDoc.get().await().exists()) {
                    favDoc.set(recipe).await()
                }
                if (pendDoc.get().await().exists()) {
                    pendDoc.set(recipe).await()
                }
            }

            true
        } catch (e: Exception) {
            Log.e("RecipeFirebaseDataSource", "Error updating recipe", e)
            false
        }
    }

    private suspend fun verifyPantryAndUpdateShoppingList(recipe: RecipeModel, listId: String) {
        val pantryRef = pantryRef()

        val pantrySnapshot = pantryRef.get().await()
        val pantryItems = pantrySnapshot.documents.mapNotNull { it.toObject(PantryIngredientModel::class.java) }

        for (ingredient in recipe.ingredients) {
            val pantryItem = pantryItems.find {
                it.ingredientId == ingredient.ingredientId && it.unit.name == ingredient.unit.name
            }

            val availableQty = pantryItem?.quantity ?: 0.0
            if (availableQty < ingredient.quantity) {
                val neededQty = ingredient.quantity - availableQty

                val shoppingItem = ShoppingIngredientModel(
                    id = ingredient.ingredientId,
                    ingredientId = ingredient.ingredientId,
                    name = ingredient.name,
                    quantity = neededQty,
                    unit = ingredient.unit,
                    category = ingredient.category,
                    checked = false
                )

                shoppingDataSource.addIngredientToShoppingListItemCollection(listId, shoppingItem)
            }
        }
    }

    suspend fun markRecipeAsCooked(recipe: RecipeModel) {
        try {
            // 1. Eliminar de pendientes
            pendingRef().document(recipe.id).delete().await()

            // 2. Obtener snapshot actual de la despensa
            val pantrySnapshot = pantryRef().get().await()
            val pantryItems = pantrySnapshot.documents.mapNotNull { it.toObject(PantryIngredientModel::class.java) }

            // 3. Recorremos los ingredientes de la receta
            for (ingredient in recipe.ingredients) {
                val pantryItem = pantryItems.find {
                    it.ingredientId == ingredient.ingredientId && it.unit.name == ingredient.unit.name
                }

                if (pantryItem != null) {
                    val updatedQty = pantryItem.quantity - ingredient.quantity
                    val newQty = if (updatedQty < 0) 0.0 else updatedQty

                    pantryRef().document(pantryItem.ingredientId).update("quantity", newQty).await()
                } else {
                    Log.w("markRecipeAsCooked", "Ingrediente no encontrado en despensa: ${ingredient.name}")
                }
            }

            Log.d("markRecipeAsCooked", "Receta procesada correctamente")

        } catch (e: Exception) {
            Log.e("markRecipeAsCooked", "Error al marcar receta como cocinada", e)
        }
    }


}
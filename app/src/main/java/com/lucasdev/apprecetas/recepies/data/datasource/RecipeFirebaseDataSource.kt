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
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Data source for interacting with Firebase Firestore to manage recipes, favorites, pending recipes,
 * and pantry synchronization.
 */
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

    /**
     * Checks whether the current user is an admin.
     * @return true if the user is an admin, false otherwise.
     */
    suspend fun isAdmin(): Boolean {
        val snapshot = db.collection("users").document(uid).get().await()
        return snapshot.getBoolean("admin") ?: false

    }

    /**
     * Adds a new recipe to the appropriate Firestore collection depending on admin status.
     * @param recipe The recipe to add.
     * @return The added recipe with an ID and creation timestamp, or null if an error occurred.
     */
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

    /**
     * Retrieves a list of public (common) recipes.
     * @return List of [RecipeModel] from the public collection.
     */
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

    /**
     * Retrieves the user's recipes or all public ones if the user is an admin.
     * @return List of [RecipeModel].
     */
    suspend fun getUserRecipes(): List<RecipeModel> {
        val admin = isAdmin()
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

    /**
     * Adds a recipe to the user's favorites collection.
     * @param recipe The recipe to favorite.
     */
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

    /**
     * Adds a recipe to the pending list and updates the shopping list if needed.
     * @param recipe The recipe to mark as pending.
     * @param shoppingListId The shopping list ID to update with missing ingredients.
     */
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

    /**
     * Removes a recipe from the favorites list.
     * @param recipe The recipe to remove.
     */
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

    /**
     * Removes a recipe from the pending list and subtracts its ingredients from the shopping list.
     * @param recipe The recipe to remove.
     * @param shoppingListId The shopping list ID to update.
     */
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

    /**
     * Retrieves the user's favorite recipes.
     * @return List of [RecipeModel].
     */
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

    /**
     * Retrieves the user's pending recipes.
     * @return List of [RecipeModel].
     */
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
      todo This method should be modified in the future to accommodate a larger number of users (Cloud Function)
     */
    /**
     * Deletes a recipe from all relevant collections.
     * If admin, deletes from all users' favorites and pending lists.
     * @param recipeId ID of the recipe to delete.
     * @return true if deleted successfully, false otherwise.
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

    /**
     * Updates a recipe and propagates changes to all relevant collections.
     * @param recipe The updated recipe.
     * @return true if updated successfully, false otherwise.
     */
    suspend fun updateRecipe(recipe: RecipeModel): Boolean {

        return try {
            val admin = isAdmin()
            val mainRef = if (admin) commonRecipesRef() else userRecipesRef()

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

    /**
     * Verifies which ingredients are missing from the pantry and adds them to the shopping list.
     * @param recipe The recipe to check.
     * @param listId The ID of the shopping list to update.
     */
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

    /**
     * Marks a recipe as cooked: removes it from pending list and subtracts used ingredients from pantry.
     * @param recipe The recipe that was cooked.
     */
    suspend fun markRecipeAsCooked(recipe: RecipeModel) {
        try {

            pendingRef().document(recipe.id).delete().await()
            Log.d("markRecipeAsCooked", "Receta eliminada de pendientes: ${recipe.id}")

            fun formatQuantity(quantity: Double): String {
                val df = DecimalFormat("#.###") // Hasta 3 decimales sin ceros innecesarios
                return df.format(quantity)
            }

            fun roundToDecimals(value: Double, decimals: Int): Double {
                return BigDecimal(value).setScale(decimals, RoundingMode.HALF_UP).toDouble()
            }

            val pantrySnapshot = pantryRef().get().await()
            val pantryItems = pantrySnapshot.documents.mapNotNull { it.toObject(PantryIngredientModel::class.java) }

            for (ingredient in recipe.ingredients) {
                val pantryItem = pantryItems.find {
                    it.ingredientId == ingredient.ingredientId && it.unit.name == ingredient.unit.name
                }

                if (pantryItem != null) {
                    val rawUpdatedQty = pantryItem.quantity - ingredient.quantity
                    val updatedQty = if (rawUpdatedQty < 0) 0.0 else roundToDecimals(rawUpdatedQty, 3) // Redondeo aquí

                    val epsilon = 0.00001

                    if (updatedQty < epsilon) {
                        pantryRef().document(pantryItem.ingredientId).delete().await()
                        Log.d("markRecipeAsCooked", "Ingrediente eliminado de la despensa: ${ingredient.name}")
                    } else {
                        pantryRef().document(pantryItem.ingredientId).update("quantity", updatedQty).await()
                        Log.d("markRecipeAsCooked", "Cantidad actualizada para ${ingredient.name}: ${formatQuantity(updatedQty)} ${ingredient.unit.name}")
                    }
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
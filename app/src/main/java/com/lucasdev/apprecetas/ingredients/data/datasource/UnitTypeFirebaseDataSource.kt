package com.lucasdev.apprecetas.ingredients.data.datasource

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.lucasdev.apprecetas.ingredients.domain.model.UnitTypeModel
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Data source class for interacting with unit type data stored in Firestore.
 * Handles fetching and adding unit types from/to the "ingredient_units" collection.
 */
class UnitTypeFirebaseDataSource @Inject constructor() {
    private val db = Firebase.firestore
    /**
     * Reference to the "ingredient_units" Firestore collection.
     */
    private fun unitTypesRef() = db.collection("ingredient_units")

    /**
     * Fetches all unit types from Firestore.
     *
     * @return A list of [UnitTypeModel] representing all available ingredient unit types.
     */
    suspend fun getUnitType(): List<UnitTypeModel> = suspendCoroutine { cont ->
        unitTypesRef().get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.mapNotNull { it.toObject(UnitTypeModel::class.java) }
                cont.resume(list)
            }
            .addOnFailureListener { cont.resume(emptyList()) }
    }

    /**
     * Adds a new unit type to Firestore.
     *
     * @param unitTypeModel The unit type to be added.
     * @return `true` if the operation succeeded, `false` otherwise.
     */
    suspend fun addUnitType(unitTypeModel: UnitTypeModel): Boolean = suspendCoroutine { cont ->
        unitTypesRef().add(unitTypeModel)
            .addOnSuccessListener { documentReference ->
                cont.resume(true)
            }
            .addOnFailureListener { e ->

                cont.resume(false)
            }
    }
}
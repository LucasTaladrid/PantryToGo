package com.lucasdev.apprecetas.ingredients.data.datasource

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.lucasdev.apprecetas.ingredients.domain.model.UnitTypeModel
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UnitTypeFirebaseDataSource @Inject constructor() {
    private val db = Firebase.firestore
    private fun unitTypesRef() = db.collection("ingredient_units")

    suspend fun getUnitType(): List<UnitTypeModel> = suspendCoroutine { cont ->
        unitTypesRef().get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.mapNotNull { it.toObject(UnitTypeModel::class.java) }
                cont.resume(list)
            }
            .addOnFailureListener { cont.resume(emptyList()) }
    }

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
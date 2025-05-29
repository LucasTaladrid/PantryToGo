package com.lucasdev.apprecetas.ingredients.domain.repository.impl


import com.lucasdev.apprecetas.ingredients.data.datasource.UnitTypeFirebaseDataSource
import com.lucasdev.apprecetas.ingredients.domain.model.UnitTypeModel
import com.lucasdev.apprecetas.ingredients.domain.repository.UnitTypeRepository
import javax.inject.Inject

/**
 * Implementation of [UnitTypeRepository] that delegates unit type operations
 * to [UnitTypeFirebaseDataSource].
 *
 * This repository handles retrieval and addition of unit types related to ingredients.
 *
 * @property dataSource the data source responsible for interacting with Firebase Firestore.
 */
class UnitTypeRepositoryImpl @Inject constructor(
    private val dataSource: UnitTypeFirebaseDataSource
): UnitTypeRepository {

    /**
     * Retrieves the list of all unit types available.
     *
     * @return a list of [UnitTypeModel] representing unit types.
     */
    override suspend fun getUnitTypes(): List<UnitTypeModel> =dataSource.getUnitType()

    /**
     * Adds a new unit type.
     *
     * @param unitTypeModel the unit type to add.
     * @return true if the addition was successful, false otherwise.
     */
    override suspend fun addUnitType(unitTypeModel: UnitTypeModel): Boolean {
        return dataSource.addUnitType(unitTypeModel)
    }

}



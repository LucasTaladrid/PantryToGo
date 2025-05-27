package com.lucasdev.apprecetas.ingredients.domain.repository


import com.lucasdev.apprecetas.ingredients.domain.model.UnitTypeModel

/**
 * Repository interface for managing unit types used in ingredients.
 */
interface UnitTypeRepository {

    /**
     * Retrieves all available unit types.
     *
     * @return A list of [UnitTypeModel] representing all unit types.
     */
    suspend fun getUnitTypes(): List<UnitTypeModel>

    /**
     * Adds a new unit type.
     *
     * @param unitTypeModel The [UnitTypeModel] to add.
     * @return `true` if the unit type was added successfully, `false` otherwise.
     */
    suspend fun addUnitType(unitTypeModel: UnitTypeModel): Boolean
}

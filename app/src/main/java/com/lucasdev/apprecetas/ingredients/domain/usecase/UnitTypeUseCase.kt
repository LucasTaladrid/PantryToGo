package com.lucasdev.apprecetas.ingredients.domain.usecase


import com.lucasdev.apprecetas.ingredients.domain.model.UnitTypeModel
import com.lucasdev.apprecetas.ingredients.domain.repository.UnitTypeRepository
import javax.inject.Inject

/**
 * Similar to categories, unit types should not be modified by anyone.
 * If changes are needed, they should be done at the database level.
 */
class GetUnitTypeUseCase @Inject constructor(private val repository: UnitTypeRepository) {
    /**
     * Executes retrieval of the list of unit types.
     */
    suspend operator fun invoke() = repository.getUnitTypes()
}

/**
 * Similar to categories, unit types should not be modified by anyone.
 * If changes are needed, they should be done at the database level.
 */
class AddUnityTypeUseCase @Inject constructor(private val repository: UnitTypeRepository) {
    /**
     * Executes adding a new unit type.
     *
     * @param unitTypeModel The unit type model to add.
     */
    suspend operator fun invoke(unitTypeModel: UnitTypeModel) = repository.addUnitType(unitTypeModel)
}


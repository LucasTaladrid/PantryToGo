package com.lucasdev.apprecetas.ingredients.domain.repository


import com.lucasdev.apprecetas.ingredients.domain.model.UnitTypeModel

interface UnitTypeRepository {
    suspend fun getUnitTypes(): List<UnitTypeModel>
    suspend fun addUnitType(unitTypeModel: UnitTypeModel): Boolean
}
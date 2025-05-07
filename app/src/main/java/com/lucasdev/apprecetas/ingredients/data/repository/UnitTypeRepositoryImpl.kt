package com.lucasdev.apprecetas.ingredients.data.repository


import com.lucasdev.apprecetas.ingredients.data.datasource.UnitTypeFirebaseDataSource
import com.lucasdev.apprecetas.ingredients.domain.model.UnitTypeModel
import com.lucasdev.apprecetas.ingredients.domain.repository.UnitTypeRepository
import javax.inject.Inject

class UnitTypeRepositoryImpl @Inject constructor(
    private val dataSource: UnitTypeFirebaseDataSource
): UnitTypeRepository {
    override suspend fun getUnitTypes(): List<UnitTypeModel> =dataSource.getUnitType()



    override suspend fun addUnitType(unitTypeModel: UnitTypeModel): Boolean {
        return dataSource.addUnitType(unitTypeModel)
    }

}



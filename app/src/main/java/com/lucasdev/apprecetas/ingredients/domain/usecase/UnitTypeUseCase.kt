package com.lucasdev.apprecetas.ingredients.domain.usecase


import com.lucasdev.apprecetas.ingredients.domain.model.UnitTypeModel
import com.lucasdev.apprecetas.ingredients.domain.repository.UnitTypeRepository
import javax.inject.Inject

/*
Al igual que ocurre con las categorías esto no debería de estar siendo modificado por nadie, si hay que hacer cambios serán a nivel de base de datos
 */
class GetUnitTypeUseCase @Inject constructor(private val repository: UnitTypeRepository) {
    suspend operator fun invoke() = repository.getUnitTypes()
}

class AddUnityTypeUseCase @Inject constructor(private val repository: UnitTypeRepository) {
    suspend operator fun invoke(unitTypeModel: UnitTypeModel) = repository.addUnitType(unitTypeModel)
}

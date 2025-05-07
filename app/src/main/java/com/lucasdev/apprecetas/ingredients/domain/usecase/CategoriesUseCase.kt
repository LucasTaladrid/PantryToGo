package com.lucasdev.apprecetas.ingredients.domain.usecase

import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.repository.CategoriesRepository

import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(private val repository: CategoriesRepository) {
    suspend operator fun invoke() = repository.getCategories()
}
//En principio estos casos de uso no deberían de ejecutarse, solo interesea el get, en caso de necesitar alguna modificación se hará a nivel de base de datos
//ya que el añadir una nueva categoría, eliminar o actualizar es un caso excepcional.
class AddCategoryUseCase @Inject constructor(private val repository: CategoriesRepository) {
    suspend operator fun invoke(category: CategoryModel) = repository.addCategory(category)
}

class UpdateCategoryUseCase @Inject constructor(private val repository: CategoriesRepository) {
    suspend operator fun invoke(category: CategoryModel) = repository.updateCategory(category)
}

class DeleteCategoryUseCase @Inject constructor(private val repository: CategoriesRepository) {
    suspend operator fun invoke(name:String) = repository.deleteCategory(name)
}
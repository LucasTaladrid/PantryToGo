package com.lucasdev.apprecetas.shopping.domain.di

import com.lucasdev.apprecetas.shopping.data.datasource.ShoppingListFirebaseDataSource
import com.lucasdev.apprecetas.shopping.data.repository.ShoppingListRepositoryImpl
import com.lucasdev.apprecetas.shopping.domain.repository.ShoppingListRepository
import com.lucasdev.apprecetas.shopping.domain.usecase.AddShoppingListUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.GetShoppingListsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ShoppingListModule {

    @Provides
    @ViewModelScoped
    fun provideShoppingListRepository(
        dataSource: ShoppingListFirebaseDataSource
    ): ShoppingListRepository {
        return ShoppingListRepositoryImpl(dataSource)
    }

    @Provides
    @ViewModelScoped
    fun provideAddShoppingListUseCase(
        repository: ShoppingListRepository
    ): AddShoppingListUseCase = AddShoppingListUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideGetShoppingListsUseCase(
        repository: ShoppingListRepository
    ): GetShoppingListsUseCase = GetShoppingListsUseCase(repository)


}

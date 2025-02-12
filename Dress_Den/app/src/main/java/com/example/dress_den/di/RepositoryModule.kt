package com.example.dress_den.di

import com.example.dress_den.data.repository.OrderRepositoryImpl
import com.example.dress_den.data.repository.ProductRepositoryImpl
import com.example.dress_den.data.repository.UserRepositoryImpl
import com.example.dress_den.domain.repository.OrderRepository
import com.example.dress_den.domain.repository.ProductRepository
import com.example.dress_den.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryHelperModule {
    // Add any additional repository dependencies here if needed
    // For example, database instances, API services, etc.
    // that aren't covered by other modules
}

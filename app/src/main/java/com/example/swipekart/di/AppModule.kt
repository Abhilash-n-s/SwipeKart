package com.example.swipekart.di

import com.example.swipekart.db.AppDatabase
import com.example.swipekart.network.ProductService
import com.example.swipekart.network.ProductServiceImpl
import com.example.swipekart.repository.ProductRepository
import com.example.swipekart.repository.ProductRepositoryImpl
import com.example.swipekart.viewmodel.ProductViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getInstance(androidContext()) }
    single { get<AppDatabase>().productDao() }
    single<ProductRepository> { ProductRepositoryImpl(get()) }
    single<ProductService> { ProductServiceImpl() }
    viewModel { ProductViewModel(get()) }
}

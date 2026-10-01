package com.example.tiketbantu.di

import com.example.tiketbantu.data.local.AppDatabase
import com.example.tiketbantu.data.repository.TicketRepositoryImpl
import com.example.tiketbantu.domain.repository.TicketRepository
import com.example.tiketbantu.domain.usecase.GetFeedUseCase
import com.example.tiketbantu.ui.screens.feed.FeedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Main Koin Dependency Injection module for TiketBantu Mobile.
 * Configures Room Database, DAOs, Repositories, UseCases, and ViewModels.
 */
val appModule = module {

    // Application-wide CoroutineScope for DB callbacks & background tasks
    single { CoroutineScope(SupervisorJob() + Dispatchers.IO) }

    // Room Database instance
    single { AppDatabase.buildDatabase(androidContext(), get()) }

    // Room DAOs
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().categoryDao() }
    single { get<AppDatabase>().ticketDao() }
    single { get<AppDatabase>().supportDao() }
    single { get<AppDatabase>().commentDao() }

    // Repository Layer (Backed by Room Database)
    single<TicketRepository> { TicketRepositoryImpl(get(), get()) }

    // UseCases Layer
    factory { GetFeedUseCase(get()) }

    // ViewModel Layer
    viewModel { FeedViewModel(get()) }
}

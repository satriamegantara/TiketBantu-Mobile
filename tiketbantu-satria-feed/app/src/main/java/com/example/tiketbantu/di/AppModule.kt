package com.example.tiketbantu.di

import com.example.tiketbantu.data.repository.FakeAuthRepository
import com.example.tiketbantu.data.repository.FakeTicketRepository
import com.example.tiketbantu.domain.repository.AuthRepository
import com.example.tiketbantu.domain.repository.TicketRepository
import com.example.tiketbantu.domain.usecase.GetFeedUseCase
import com.example.tiketbantu.domain.usecase.ToggleSupportUseCase
import com.example.tiketbantu.ui.screens.feed.FeedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Main Koin Dependency Injection module for TiketBantu Mobile.
 */
val appModule = module {
    // Repository Layer (Singleton)
    // TODO(Pancar): once AppDatabase exists, switch to:
    //   single<TicketRepository> { TicketRepositoryImpl(get<AppDatabase>().ticketDao()) }
    single<TicketRepository> { FakeTicketRepository() }
    // TODO(Anggota 4): replace with the DataStore-backed AuthRepository implementation.
    single<AuthRepository> { FakeAuthRepository() }

    // UseCases Layer (Factory)
    factory { GetFeedUseCase(get()) }
    factory { ToggleSupportUseCase(get()) }

    // ViewModel Layer (Koin ViewModel)
    viewModel { FeedViewModel(get(), get(), get()) }
}

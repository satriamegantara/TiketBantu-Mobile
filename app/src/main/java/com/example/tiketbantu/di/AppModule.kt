package com.example.tiketbantu.di

import com.example.tiketbantu.data.repository.FakeTicketRepository
import com.example.tiketbantu.domain.repository.TicketRepository
import com.example.tiketbantu.domain.usecase.GetFeedUseCase
import com.example.tiketbantu.ui.screens.feed.FeedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Main Koin Dependency Injection module for TiketBantu Mobile.
 */
val appModule = module {
    // Repository Layer (Singleton)
    single<TicketRepository> { FakeTicketRepository() }

    // UseCases Layer (Factory)
    factory { GetFeedUseCase(get()) }

    // ViewModel Layer (Koin ViewModel)
    viewModel { FeedViewModel(get()) }
}

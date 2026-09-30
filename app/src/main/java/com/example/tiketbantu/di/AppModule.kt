package com.example.tiketbantu.di

import com.example.tiketbantu.data.FakeTicketRepository
import com.example.tiketbantu.data.TicketRepository
import com.example.tiketbantu.domain.usecase.GetFeedUseCase
import com.example.tiketbantu.ui.feed.FeedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Modul Koin yang menyediakan semua dependensi utama aplikasi.
 *
 * - `TicketRepository` : implementasi fake untuk development/testing.
 * - `GetFeedUseCase` : use‑case yang mengembalikan aliran tiket.
 * - `FeedViewModel` : ViewModel yang mengonsumsi use‑case.
 */
val appModule = module {
    // Repository (singleton)
    single<TicketRepository> { FakeTicketRepository() }
    // Use‑case (factory, tiap pemanggilan baru)
    factory { GetFeedUseCase(get()) }
    // ViewModel (Koin‑aware)
    viewModel { FeedViewModel(get()) }
}

package com.example.tiketbantu.di

import com.example.tiketbantu.data.local.AppDatabase
import com.example.tiketbantu.data.preferences.SessionManager
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
 *
 * Configures:
 * - Application-scoped [CoroutineScope]
 * - Room Database + all DAOs
 * - [SessionManager] (shared session state holder)
 * - Repository + UseCase layers
 * - ViewModel layer
 */
val appModule = module {

    // ── Application-wide CoroutineScope ──────────────────────────────────────
    // Used for DB callbacks, background tasks, and SessionManager lifecycle.
    single { CoroutineScope(SupervisorJob() + Dispatchers.IO) }

    // ── Room Database ────────────────────────────────────────────────────────
    single { AppDatabase.buildDatabase(androidContext(), get()) }

    // ── Room DAOs ────────────────────────────────────────────────────────────
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().categoryDao() }
    single { get<AppDatabase>().ticketDao() }
    single { get<AppDatabase>().supportDao() }
    single { get<AppDatabase>().commentDao() }

    // ── Session Manager (Shared State Holder — point 1.3) ────────────────────
    // Singleton: one DataStore instance per app process.
    single { SessionManager(androidContext(), get()) }

    // ── Repository Layer ─────────────────────────────────────────────────────
    single<TicketRepository> { TicketRepositoryImpl(get(), get()) }

    // ── UseCase Layer ────────────────────────────────────────────────────────
    factory { GetFeedUseCase(get()) }

    // ── ViewModel Layer ──────────────────────────────────────────────────────
    viewModel { FeedViewModel(get()) }
}

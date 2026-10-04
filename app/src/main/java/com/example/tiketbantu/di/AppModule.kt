package com.example.tiketbantu.di

import com.example.tiketbantu.data.local.AppDatabase
import com.example.tiketbantu.data.preferences.SessionManager
import com.example.tiketbantu.data.repository.AuthRepositoryImpl
import com.example.tiketbantu.data.repository.CommentRepositoryImpl
import com.example.tiketbantu.data.repository.TicketRepositoryImpl
import com.example.tiketbantu.domain.repository.AuthRepository
import com.example.tiketbantu.domain.repository.CommentRepository
import com.example.tiketbantu.domain.repository.TicketRepository
import com.example.tiketbantu.domain.usecase.GetFeedUseCase
import com.example.tiketbantu.domain.usecase.ToggleSupportUseCase
import com.example.tiketbantu.domain.usecase.UpdateTicketStatusUseCase
import com.example.tiketbantu.ui.screens.auth.AuthViewModel
import com.example.tiketbantu.ui.screens.create.CreateTicketViewModel
import com.example.tiketbantu.ui.screens.detail.TicketDetailViewModel
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
 * - Repository layer (backed by Room DB)
 * - UseCase layer
 * - ViewModel layer (Feed & Detail)
 */
val appModule = module {

    // ── Application-wide CoroutineScope ──────────────────────────────────────
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
    single { SessionManager(androidContext(), get()) }

    // ── Repository Layer (Backed by Room Database) ───────────────────────────
    single<TicketRepository> { TicketRepositoryImpl(ticketDao = get()) }
    single<CommentRepository> { CommentRepositoryImpl(commentDao = get()) }
    single<AuthRepository> { AuthRepositoryImpl(userDao = get(), sessionManager = get()) }

    // ── UseCase Layer ────────────────────────────────────────────────────────
    factory { GetFeedUseCase(get()) }
    factory { ToggleSupportUseCase(get()) }
    factory { UpdateTicketStatusUseCase(get()) }

    // ── ViewModel Layer ──────────────────────────────────────────────────────
    viewModel { AuthViewModel(authRepository = get()) }
    viewModel {
        CreateTicketViewModel(
            ticketRepository = get(),
            categoryDao = get(),
            sessionManager = get(),
            context = androidContext()
        )
    }
    viewModel { FeedViewModel(get(), get(), get()) }
    viewModel { (ticketId: Long) ->
        TicketDetailViewModel(
            ticketId = ticketId,
            ticketRepository = get(),
            commentRepository = get(),
            authRepository = get(),
            toggleSupportUseCase = get(),
            updateTicketStatusUseCase = get()
        )
    }
}

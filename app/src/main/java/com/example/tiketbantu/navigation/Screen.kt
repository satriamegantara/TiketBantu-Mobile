package com.example.tiketbantu.navigation

import kotlinx.serialization.Serializable

/**
 * Type-Safe Navigation destinations for TiketBantu Mobile.
 * Aligned with APP_FLOW_TiketBantu.md.
 */
sealed interface Screen {
    // Auth Graph
    @Serializable
    data object Login : Screen

    @Serializable
    data object Register : Screen

    // Main Graph
    @Serializable
    data object Dashboard : Screen

    @Serializable
    data object CreateTicket : Screen

    @Serializable
    data class TicketDetail(val ticketId: Long) : Screen

    @Serializable
    data object MyTickets : Screen

    @Serializable
    data object SupportedTickets : Screen

    @Serializable
    data object Profile : Screen

    // Admin Graph
    @Serializable
    data object Monitoring : Screen

    @Serializable
    data object UserManagement : Screen
}

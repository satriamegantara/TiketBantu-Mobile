package com.example.tiketbantu.navigation

import kotlinx.serialization.Serializable

/**
 * Type-Safe Navigation destinations for TiketBantu Mobile.
 *
 * Implements Jetpack Compose Navigation type-safe destinations using Kotlinx Serialization
 * in accordance with APP_FLOW_TiketBantu.md and Outline Jobdesc 1.4 (Pancar).
 *
 * Route destinations:
 * - [Login]: Authentication login screen (Multi-Role: Pelapor, Agen, Admin).
 * - [Register]: Account registration screen.
 * - [Dashboard]: Public complaints feed (2-column on tablet, single-column on phone).
 * - [CreateTicket]: Form for creating a new complaint with location and photo attachment.
 * - [Detail]: Detail view of a complaint (route: Detail/{id}).
 * - [MyTickets]: Filtered list of complaints submitted by the logged-in user.
 * - [Profile]: User profile view with role badges and logout trigger.
 * - [Monitoring]: Pure monitoring dashboard for Admin role.
 * - [UserManagement]: User and category management (Admin only).
 */
sealed interface Screen {

    // ── Auth Graph ────────────────────────────────────────────────────────────

    @Serializable
    data object Login : Screen

    @Serializable
    data object Register : Screen

    // ── Main Graph ────────────────────────────────────────────────────────────

    @Serializable
    data object Dashboard : Screen

    @Serializable
    data object CreateTicket : Screen

    /**
     * Detail route destination: maps to route pattern Detail/{id}
     *
     * @param id The unique identifier of the ticket.
     */
    @Serializable
    data class Detail(val id: Long) : Screen {
        /** Alias for compatibility with code referencing ticketId */
        val ticketId: Long get() = id
    }

    @Serializable
    data object MyTickets : Screen

    @Serializable
    data object Profile : Screen

    // ── Admin Graph ───────────────────────────────────────────────────────────

    @Serializable
    data object Monitoring : Screen

    @Serializable
    data object UserManagement : Screen
}

/**
 * Backward compatibility typealias for code referencing TicketDetail.
 */
typealias TicketDetail = Screen.Detail

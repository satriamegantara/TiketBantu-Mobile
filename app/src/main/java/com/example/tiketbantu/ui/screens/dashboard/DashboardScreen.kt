package com.example.tiketbantu.ui.screens.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tiketbantu.ui.screens.feed.FeedScreen

/**
 * Dashboard = public feed (Beranda). The greeting header lives inside FeedScreen so it scrolls
 * together with the feed.
 */
@Composable
fun DashboardScreen(
    onTicketClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {}
) {
    FeedScreen(
        onTicketClick = onTicketClick,
        onProfileClick = onProfileClick,
        modifier = modifier
    )
}

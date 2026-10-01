package com.example.tiketbantu.ui.screens.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tiketbantu.ui.screens.feed.FeedScreen

/**
 * Simple Dashboard: app bar + public complaint feed.
 * (Bottom navigation and the 2-column tablet layout are added later by Pancar / Anggota 2.)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onTicketClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("TiketBantu") }) }
    ) { padding ->
        FeedScreen(
            onTicketClick = onTicketClick,
            modifier = Modifier.padding(padding)
        )
    }
}

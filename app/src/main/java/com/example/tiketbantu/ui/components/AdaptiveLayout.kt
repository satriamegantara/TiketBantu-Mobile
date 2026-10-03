package com.example.tiketbantu.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Single-Column Feed Container helper (Task 2.6 - Anggota 2).
 * Ensures feed cards are rendered in a clean 1-Column layout.
 */
@Composable
fun AdaptiveFeedLayout(
    feedContent: @Composable () -> Unit,
    sidebarContent: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Single Column Layout for all screen sizes
    Box(modifier = modifier.fillMaxSize()) {
        feedContent()
    }
}

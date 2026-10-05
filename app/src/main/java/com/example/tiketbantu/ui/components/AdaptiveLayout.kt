package com.example.tiketbantu.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Adaptive Feed Layout container (Task 2.6 - Anggota 2).
 * Responsive layout:
 * - Compact screens (< 600dp, e.g. Phone Portrait): Single Column feed layout.
 * - Expanded screens (>= 600dp, e.g. Tablet / Landscape): 2-Column layout
 *   (Left: Feed List, Right: Sidebar Filter/Stats).
 */
@Composable
fun AdaptiveFeedLayout(
    feedContent: @Composable () -> Unit,
    sidebarContent: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpanded = maxWidth >= 600.dp && sidebarContent != null
        if (isExpanded) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Kolom Kiri: Feed Utama
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    feedContent()
                }

                // Kolom Kanan: Sidebar Ringkasan & Filter (Tablet >= 600dp)
                Box(
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight()
                        .padding(end = 20.dp, top = 16.dp, bottom = 120.dp)
                ) {
                    sidebarContent?.invoke()
                }
            }
        } else {
            // Single Column untuk ponsel (<600dp)
            Box(modifier = Modifier.fillMaxSize()) {
                feedContent()
            }
        }
    }
}


package com.example.tiketbantu.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiketbantu.ui.session.AppRole
import com.example.tiketbantu.ui.theme.BrandCyan
import com.example.tiketbantu.ui.theme.BrandIndigo
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.TiketBantuTheme

/** Kept for API compatibility with older call sites. */
enum class Role { USER, AGEN, ADMIN }

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

object NavRoutes {
    const val HOME = "dashboard"
    const val MY_TICKETS = "my_tickets"
    const val CREATE = "create_ticket"
    const val SUPPORTED = "supported_tickets"
    const val MONITORING = "monitoring"
    const val MANAGE = "user_management"
    const val PROFILE = "profile"
}

private fun itemsFor(role: AppRole): List<BottomNavItem> = buildList {
    add(BottomNavItem(NavRoutes.HOME, "Beranda", Icons.Filled.Home, Icons.Outlined.Home))
    when (role) {
        AppRole.PELAPOR -> {
            add(BottomNavItem(NavRoutes.MY_TICKETS, "Aduan Saya", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong))
            add(BottomNavItem(NavRoutes.SUPPORTED, "Dukungan", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder))
        }
        AppRole.AGEN -> add(BottomNavItem(NavRoutes.MY_TICKETS, "Tugas Saya", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong))
        AppRole.ADMIN -> add(BottomNavItem(NavRoutes.MANAGE, "Kelola", Icons.Filled.Tune, Icons.Outlined.Tune))
    }
    if (role == AppRole.ADMIN) {
        add(BottomNavItem(NavRoutes.MONITORING, "Statistik", Icons.Filled.Insights, Icons.Outlined.Insights))
    }
    add(BottomNavItem(NavRoutes.PROFILE, "Profil", Icons.Filled.Person, Icons.Outlined.Person))
}

/**
 * Floating white-glass bottom navigation. Pelapor & Admin get a raised gradient "+" button in the
 * centre (feature 3.1.1 Buat Aduan — U/A only); Agen does not create tickets so it is hidden.
 */
@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    role: AppRole,
    modifier: Modifier = Modifier
) {
    val items = itemsFor(role)
    val showCreate = role == AppRole.PELAPOR
    val left = if (showCreate) items.take((items.size + 1) / 2) else items
    val right = if (showCreate) items.drop((items.size + 1) / 2) else emptyList()
    val shape = RoundedCornerShape(32.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = shape,
            color = Color.White.copy(alpha = 0.86f),
            border = BorderStroke(1.dp, Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(24.dp, shape, ambientColor = Color(0xFF6366F1).copy(alpha = 0.18f), spotColor = Color(0xFF0F172A).copy(alpha = 0.18f))
        ) {
            Box(
                modifier = Modifier.background(
                    Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.65f), Color(0xFFF5F6FF).copy(alpha = 0.55f)))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    left.forEach { NavItem(it, currentRoute == it.route) { onNavigate(it.route) } }
                    if (showCreate) CreateButton { onNavigate(NavRoutes.CREATE) }
                    right.forEach { NavItem(it, currentRoute == it.route) { onNavigate(it.route) } }
                }
            }
        }
    }
}

@Composable
private fun NavItem(item: BottomNavItem, selected: Boolean, onClick: () -> Unit) {
    val tint by animateColorAsState(if (selected) BrandIndigo else InkMuted, label = "navTint")
    val scale by animateFloatAsState(if (selected) 1.08f else 1f, label = "navScale")
    Column(
        modifier = Modifier
            .width(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.title,
            tint = tint,
            modifier = Modifier.size(24.dp).scale(scale)
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = item.title,
            color = tint,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ),
            maxLines = 1
        )
        Spacer(Modifier.height(3.dp))
        Box(
            Modifier
                .size(width = if (selected) 14.dp else 0.dp, height = 3.dp)
                .clip(CircleShape)
                .background(BrandIndigo)
        )
    }
}

@Composable
private fun CreateButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.Transparent,
        modifier = Modifier
            .size(56.dp)
            .shadow(14.dp, CircleShape, ambientColor = BrandIndigo, spotColor = BrandIndigo)
    ) {
        Box(
            modifier = Modifier.background(Brush.linearGradient(listOf(BrandIndigo, BrandCyan))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Buat Aduan", tint = Color.White, modifier = Modifier.size(28.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F6FB)
@Composable
private fun BottomNavPreview() {
    TiketBantuTheme {
        Column {
            BottomNavigationBar(NavRoutes.HOME, {}, AppRole.PELAPOR)
            BottomNavigationBar(NavRoutes.MONITORING, {}, AppRole.ADMIN)
            BottomNavigationBar(NavRoutes.MY_TICKETS, {}, AppRole.AGEN)
        }
    }
}

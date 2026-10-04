package com.example.tiketbantu.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════
// TiketBantu "Campus Glass" Design Tokens
// Light airy background, white glass cards, indigo brand,
// cyan accent, orange "Saya Juga Mengalami" support.
// ═══════════════════════════════════════════════════════

// Brand
val BrandIndigo = Color(0xFF3B3FE8)          // Primary action (Tandai Selesai, Kirim Laporan)
val BrandIndigoDark = Color(0xFF2E2BC7)
val BrandIndigoSoft = Color(0xFFEEF0FF)      // Selected surfaces / soft chips
val BrandCyan = Color(0xFF22C3EE)            // Gradient end (Publikasikan Aduan)
val BrandViolet = Color(0xFF7C5CFA)

// Ink (text)
val Ink = Color(0xFF0F172A)                  // Headlines
val InkSoft = Color(0xFF475569)              // Body
val InkMuted = Color(0xFF94A3B8)             // Hints / meta
val Hairline = Color(0xFFE6E8F0)             // Card borders / dividers

// Surfaces
val AppBg = Color(0xFFF5F6FB)                // Screen background
val CardWhite = Color(0xFFFFFFFF)
val FieldBg = Color(0xFFF8FAFC)              // Input background

// Support / Urgency (orange)
val SupportOrange = Color(0xFFF97316)
val SupportOrangeLight = Color(0xFFFB923C)
val SupportOrangeSoft = Color(0xFFFFF1E6)
val SupportOrangeText = Color(0xFFEA580C)

// Status chips (bg / fg)
val ChipBaruBg = Color(0xFFFEF3C7)
val ChipBaruFg = Color(0xFFB45309)
val ChipProsesBg = Color(0xFFEDE9FE)
val ChipProsesFg = Color(0xFF6D28D9)
val ChipSelesaiBg = Color(0xFFDCFCE7)
val ChipSelesaiFg = Color(0xFF15803D)
val ChipTutupBg = Color(0xFFF1F5F9)
val ChipTutupFg = Color(0xFF64748B)

// Category tag
val TagBg = Color(0xFFEEF2FF)
val TagFg = Color(0xFF4F46E5)

// Info / notices
val InfoBlueBg = Color(0xFFEFF6FF)
val InfoBlue = Color(0xFF2563EB)
val SuccessSoftBg = Color(0xFFECFDF5)
val SuccessText = Color(0xFF047857)
val DangerRed = Color(0xFFEF4444)

// Category accent colors (distribution bar)
val CatIT = Color(0xFF14B8A6)
val CatRuangan = Color(0xFF6366F1)
val CatUmum = Color(0xFF22D3EE)

// Avatar palette (initials)
val AvatarPalette = listOf(
    Color(0xFF3B82F6), Color(0xFF8B5CF6), Color(0xFFEC4899),
    Color(0xFF14B8A6), Color(0xFFF59E0B), Color(0xFF6366F1)
)

// ═══════════════════════════════════════════════════════
// Material 3 roles (mapped onto the tokens above)
// ═══════════════════════════════════════════════════════
val Primary = BrandIndigo
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = BrandIndigoSoft
val OnPrimaryContainer = Color(0xFF1E1B6B)

val Secondary = BrandCyan
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFE0F7FD)
val OnSecondaryContainer = Color(0xFF0E4F61)

val Tertiary = SupportOrange
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = SupportOrangeSoft
val OnTertiaryContainer = Color(0xFF7C2D12)

val Background = AppBg
val OnBackground = Ink
val Surface = CardWhite
val OnSurface = Ink
val SurfaceVariant = Color(0xFFF1F3F9)
val OnSurfaceVariant = InkSoft

val SurfaceContainerLowest = Color(0xFFFFFFFF)
val SurfaceContainerLow = Color(0xFFFAFBFE)
val SurfaceContainer = Color(0xFFF3F5FA)
val SurfaceContainerHigh = Color(0xFFEDEFF6)
val SurfaceContainerHighest = Color(0xFFE6E9F2)

val Outline = Color(0xFFD5D9E4)
val OutlineVariant = Hairline

val Error = Color(0xFFDC2626)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFFEE2E2)
val OnErrorContainer = Color(0xFF7F1D1D)

// ═══════════════════════════════════════════════════════
// Legacy tokens (kept for backward compatibility)
// ═══════════════════════════════════════════════════════
val StatusBadgeBaru = Color(0xFFF59E0B)
val StatusBadgeDiproses = Color(0xFFD97706)
val StatusBadgeSelesai = Color(0xFF10B981)
val StatusBadgeDitutup = Color(0xFF94A3B8)

val GradientVioletStart = BrandIndigo
val GradientVioletMid = BrandViolet
val GradientVioletEnd = Color(0xFFC4B5FD)
val GradientIndigoStart = Color(0xFF6366F1)
val GradientPinkEnd = Color(0xFFF0ABFC)

val BentoCharcoal = Ink
val PastelBackground = AppBg

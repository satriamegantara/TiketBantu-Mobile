package com.example.tiketbantu.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiketbantu.domain.model.TicketStatus
import com.example.tiketbantu.ui.theme.AppBg
import com.example.tiketbantu.ui.theme.AvatarPalette
import com.example.tiketbantu.ui.theme.BrandCyan
import com.example.tiketbantu.ui.theme.BrandIndigo
import com.example.tiketbantu.ui.theme.BrandViolet
import com.example.tiketbantu.ui.theme.ChipBaruBg
import com.example.tiketbantu.ui.theme.ChipBaruFg
import com.example.tiketbantu.ui.theme.ChipProsesBg
import com.example.tiketbantu.ui.theme.ChipProsesFg
import com.example.tiketbantu.ui.theme.ChipSelesaiBg
import com.example.tiketbantu.ui.theme.ChipSelesaiFg
import com.example.tiketbantu.ui.theme.ChipTutupBg
import com.example.tiketbantu.ui.theme.ChipTutupFg
import com.example.tiketbantu.ui.theme.DangerRed
import com.example.tiketbantu.ui.theme.FieldBg
import com.example.tiketbantu.ui.theme.Hairline
import com.example.tiketbantu.ui.theme.Ink
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.InkSoft
import com.example.tiketbantu.ui.theme.SupportOrange
import com.example.tiketbantu.ui.theme.SupportOrangeLight
import com.example.tiketbantu.ui.theme.SupportOrangeSoft
import com.example.tiketbantu.ui.theme.SupportOrangeText
import com.example.tiketbantu.ui.theme.TagBg
import com.example.tiketbantu.ui.theme.TagFg
import kotlin.math.abs

// ═══════════════════════════════════════════════════════
// Layout primitives
// ═══════════════════════════════════════════════════════

/** Screen background: clean app background with subtle top wash. */
@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBg)
            .drawBehind {
                // Subtle top-to-bottom wash (very light, clean)
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF3F4F6),
                            AppBg
                        ),
                        startY = 0f,
                        endY = size.height * 0.4f
                    )
                )
            },
        content = content
    )
}

/** Translucent glass card with floating ambient shadow and crisp hairline border. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    contentPadding: PaddingValues = PaddingValues(18.dp),
    containerColor: Color = Color.White,
    borderColor: Color = Color(0xFFF1F5F9),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val base = modifier.shadow(
        elevation = 3.dp,
        shape = shape,
        ambientColor = Color.Black.copy(alpha = 0.04f),
        spotColor = Color.Black.copy(alpha = 0.06f)
    )
    if (onClick != null) {
        Surface(
            onClick = onClick,
            shape = shape,
            color = containerColor,
            border = BorderStroke(1.dp, borderColor),
            modifier = base
        ) { Column(Modifier.padding(contentPadding), content = content) }
    } else {
        Surface(
            shape = shape,
            color = containerColor,
            border = BorderStroke(1.dp, borderColor),
            modifier = base
        ) { Column(Modifier.padding(contentPadding), content = content) }
    }
}

// ═══════════════════════════════════════════════════════
// Chips & badges
// ═══════════════════════════════════════════════════════

@Composable
fun TagChip(
    text: String,
    modifier: Modifier = Modifier,
    container: Color = TagBg,
    content: Color = TagFg,
    icon: ImageVector? = null,
    fontSize: TextUnit = 11.sp
) {
    Surface(shape = RoundedCornerShape(50), color = container, modifier = modifier) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = content, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(4.dp))
            }
            Text(
                text = text,
                color = content,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = fontSize),
                maxLines = 1
            )
        }
    }
}

fun statusColors(status: String): Pair<Color, Color> = when (status) {
    TicketStatus.BARU -> ChipBaruBg to ChipBaruFg
    TicketStatus.DIPROSES -> ChipProsesBg to ChipProsesFg
    TicketStatus.SELESAI -> ChipSelesaiBg to ChipSelesaiFg
    else -> ChipTutupBg to ChipTutupFg
}

fun statusLabel(status: String): String = when (status) {
    TicketStatus.BARU -> "Baru"
    TicketStatus.DIPROSES -> "Diproses"
    TicketStatus.SELESAI -> "Selesai"
    TicketStatus.DITUTUP -> "Ditutup"
    else -> status.lowercase().replaceFirstChar { it.uppercase() }
}

/** Clean Status Pill badge without leading dot or checkmark. */
@Composable
fun StatusPill(status: String, modifier: Modifier = Modifier) {
    val (bg, fg) = statusColors(status)
    Surface(shape = RoundedCornerShape(50), color = bg, modifier = modifier) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = statusLabel(status),
                color = fg,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════
// Buttons
// ═══════════════════════════════════════════════════════

@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    container: Color = Color.White,
    tint: Color = Ink,
    showBadge: Boolean = false,
    bordered: Boolean = true
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = container,
        border = if (bordered) BorderStroke(1.dp, Hairline) else null,
        modifier = modifier.size(size)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(size * 0.48f))
            if (showBadge) {
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-9).dp, y = 9.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(DangerRed)
                        .border(1.5.dp, Color.White, CircleShape)
                )
            }
        }
    }
}

/** Full-width gradient call-to-action (e.g. "Publikasikan Aduan"). */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    colors: List<Color> = listOf(BrandIndigo, BrandCyan),
    height: Dp = 56.dp
) {
    val shape = RoundedCornerShape(50)
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        color = Color.Transparent,
        modifier = modifier
            .height(height)
            .shadow(if (enabled) 14.dp else 0.dp, shape, ambientColor = colors.first(), spotColor = colors.first())
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (enabled) Brush.horizontalGradient(colors)
                    else Brush.horizontalGradient(listOf(Color(0xFFCBD5E1), Color(0xFFCBD5E1)))
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                }
                Text(
                    text = text,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

/**
 * Red/Orange Love Heart button for ticket support — Requirements 32-37.
 * - Unsupported: ♡ Outline heart + count text (e.g. ♡ 12).
 * - Supported: ♥ Filled heart + soft rose container + count (e.g. ♥ 13).
 */
@Composable
fun SupportPill(
    count: Int,
    supported: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val scale by animateFloatAsState(
        targetValue = if (supported) 1.08f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "supportScale"
    )
    val heartColor by animateColorAsState(
        if (supported) Color(0xFFE11D48) else InkSoft,
        label = "heartColor"
    )
    val containerBg by animateColorAsState(
        if (supported) Color(0xFFFFF1F2) else FieldBg,
        label = "containerBg"
    )
    val borderColor by animateColorAsState(
        if (supported) Color(0xFFFECDD3) else Hairline,
        label = "borderColor"
    )
    val textColor by animateColorAsState(
        if (supported) Color(0xFFBE123C) else Ink,
        label = "textColor"
    )

    val shape = RoundedCornerShape(50)
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        color = containerBg,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .scale(scale)
            .height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (supported) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (supported) "Dukungan diberikan" else "Dukung aduan",
                tint = heartColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "$count",
                color = textColor,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp)
            )
        }
    }
}

/**
 * Matching circular/pill button for comment interaction — Requirements 32 & 36.
 * Styled symmetrically with [SupportPill].
 */
@Composable
fun CommentPill(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(50)
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        color = FieldBg,
        border = BorderStroke(1.dp, Hairline),
        modifier = modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Chat,
                contentDescription = "Buka diskusi komentar",
                tint = InkSoft,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "$count",
                color = Ink,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp)
            )
        }
    }
}

/**
 * Neutral, non-interactive metric pill for informational counters (e.g. support count & comment count on Agent/Staff role).
 * Matches the visual language of CommentPill without click/interaction states.
 */
@Composable
fun InfoCountPill(
    icon: ImageVector,
    count: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = InkSoft
) {
    val shape = RoundedCornerShape(50)
    Surface(
        shape = shape,
        color = FieldBg,
        border = BorderStroke(1.dp, Hairline),
        modifier = modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "$count",
                color = Ink,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════
// Avatars
// ═══════════════════════════════════════════════════════

fun initialsOf(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() && !it.endsWith(".") }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "${parts[0].first()}${parts[1].first()}".uppercase()
    }
}

fun avatarColor(name: String): Color = AvatarPalette[abs(name.hashCode()) % AvatarPalette.size]

@Composable
fun InitialsAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    soft: Boolean = true
) {
    val color = avatarColor(name)
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(if (soft) color.copy(alpha = 0.14f) else color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initialsOf(name),
            color = if (soft) color else Color.White,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.36f).sp
            )
        )
    }
}

/** Overlapping avatar stack with "+n" counter (supporters). */
@Composable
fun AvatarStack(names: List<String>, extra: Int, modifier: Modifier = Modifier, size: Dp = 28.dp) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        names.take(3).forEachIndexed { index, n ->
            InitialsAvatar(
                name = n,
                size = size,
                soft = false,
                modifier = Modifier
                    .offset(x = (-8 * index).dp)
                    .border(2.dp, Color.White, CircleShape)
            )
        }
        if (extra > 0) {
            Box(
                modifier = Modifier
                    .offset(x = (-8 * names.take(3).size).dp)
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0))
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("+$extra", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = InkSoft)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════
// Text helpers
// ═══════════════════════════════════════════════════════

@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color = BrandIndigo,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Ink,
            modifier = Modifier.weight(1f)
        )
        trailing?.invoke()
    }
}

@Composable
fun FieldLabel(text: String, modifier: Modifier = Modifier, required: Boolean = false) {
    Row(modifier = modifier) {
        Text(text, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Ink)
        if (required) Text(" *", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = DangerRed)
    }
}

@Composable
fun MetaRow(icon: ImageVector, text: String, modifier: Modifier = Modifier, tint: Color = InkMuted) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = InkSoft,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** Rounded light input used by every form in the app. */
@Composable
fun AppInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation =
        androidx.compose.ui.text.input.VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = InkMuted, style = MaterialTheme.typography.bodyMedium) },
        singleLine = singleLine,
        minLines = minLines,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        isError = isError,
        visualTransformation = visualTransformation,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Ink),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandIndigo,
            unfocusedBorderColor = Hairline,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = FieldBg,
            cursorColor = BrandIndigo
        )
    )
}

// ═══════════════════════════════════════════════════════
// Formatting helpers
// ═══════════════════════════════════════════════════════

fun ticketCode(id: Long): String = "#TKT-2024-${id.toString().padStart(3, '0')}"

fun relativeTime(millis: Long, now: Long = System.currentTimeMillis()): String {
    val diffMin = ((now - millis) / 60_000L).coerceAtLeast(0)
    return when {
        diffMin < 1 -> "Baru saja"
        diffMin < 60 -> "$diffMin menit lalu"
        diffMin < 60 * 24 -> "${diffMin / 60} jam lalu"
        diffMin < 60 * 24 * 7 -> "${diffMin / (60 * 24)} hari lalu"
        else -> "${diffMin / (60 * 24 * 7)} minggu lalu"
    }
}


@Composable
fun VerticalSpace(height: Dp) = Spacer(Modifier.height(height))

@Composable
fun DotDivider(modifier: Modifier = Modifier) {
    Box(modifier.size(3.dp).clip(CircleShape).background(InkMuted))
}

/** Pill for horizontally scrolling filter rows with vibrant active brand gradient. */
@Composable
fun FilterPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color = SupportOrange
) {
    val shape = RoundedCornerShape(50)
    Surface(
        onClick = onClick,
        shape = shape,
        color = Color.Transparent,
        border = if (selected) null else BorderStroke(1.dp, Hairline),
        modifier = modifier
            .height(38.dp)
            .shadow(if (selected) 6.dp else 0.dp, shape, ambientColor = BrandIndigo, spotColor = BrandIndigo)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (selected) Brush.horizontalGradient(listOf(BrandIndigo, BrandViolet))
                    else Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.94f), Color.White.copy(alpha = 0.94f)))
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = if (selected) Color.White else iconTint, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                }
                Text(
                    text = text,
                    color = if (selected) Color.White else InkSoft,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

package com.example.tiketbantu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiketbantu.ui.theme.GradientVioletEnd
import com.example.tiketbantu.ui.theme.GradientVioletMid
import com.example.tiketbantu.ui.theme.GradientVioletStart
import com.example.tiketbantu.ui.theme.TiketBantuTheme

/**
 * Standard Modern Pill Button matching "Generate New" from reference design Screen 3.
 * Features violet gradient, fully rounded corners, and subtle glow shadow.
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    content: (@Composable RowScope.() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GradientVioletStart,
            disabledContainerColor = Color(0xFFDDD6FE)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .height(54.dp)
            .shadow(
                elevation = if (enabled) 12.dp else 0.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = GradientVioletStart.copy(alpha = 0.25f),
                spotColor = GradientVioletStart.copy(alpha = 0.35f)
            )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.height(22.dp),
                color = Color.White,
                strokeWidth = 2.5.dp
            )
        } else if (content != null) {
            content()
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = Color.White
            )
        }
    }
}

@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: (@Composable RowScope.() -> Unit)? = null
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = GradientVioletStart
        ),
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .height(54.dp)
    ) {
        if (content != null) {
            content()
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = GradientVioletStart
            )
        }
    }
}

@Composable
fun AppTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = GradientVioletStart
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.defaultMinSize(minHeight = 48.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = if (enabled) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppButtonPreview() {
    TiketBantuTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            AppButton(text = "Generate New", onClick = {}, modifier = Modifier.fillMaxWidth())
            AppOutlinedButton(text = "Batal", onClick = {}, modifier = Modifier.fillMaxWidth())
            AppTextButton(text = "Lupa Password?", onClick = {})
        }
    }
}

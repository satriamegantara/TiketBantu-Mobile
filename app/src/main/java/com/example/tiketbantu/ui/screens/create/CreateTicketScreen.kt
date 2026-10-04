package com.example.tiketbantu.ui.screens.create

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.tiketbantu.base.UiState
import com.example.tiketbantu.data.local.entity.CategoryEntity
import com.example.tiketbantu.ui.components.AppBackground
import com.example.tiketbantu.ui.components.AppInput
import com.example.tiketbantu.ui.components.FieldLabel
import com.example.tiketbantu.ui.components.GlassCard
import com.example.tiketbantu.ui.components.GradientButton
import com.example.tiketbantu.ui.components.TagChip
import com.example.tiketbantu.ui.theme.BrandIndigo
import com.example.tiketbantu.ui.theme.BrandIndigoSoft
import com.example.tiketbantu.ui.theme.DangerRed
import com.example.tiketbantu.ui.theme.FieldBg
import com.example.tiketbantu.ui.theme.Hairline
import com.example.tiketbantu.ui.theme.InfoBlue
import com.example.tiketbantu.ui.theme.InfoBlueBg
import com.example.tiketbantu.ui.theme.Ink
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.InkSoft
import com.example.tiketbantu.ui.theme.SuccessText
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

private const val TITLE_MAX = 80
private const val MAX_PHOTO_BYTES = 5L * 1024 * 1024

private val FALLBACK_CATEGORIES = listOf(
    CategoryEntity(id = 1, name = "Teknologi & IT"),
    CategoryEntity(id = 2, name = "Fasilitas Ruangan"),
    CategoryEntity(id = 3, name = "Infrastruktur Umum")
)

private fun getCategoryIcon(name: String): ImageVector = when {
    name.contains("IT", ignoreCase = true) || name.contains("Teknologi", ignoreCase = true) -> Icons.Outlined.Computer
    name.contains("Ruang", ignoreCase = true) -> Icons.Outlined.MeetingRoom
    else -> Icons.Outlined.Apartment
}

/**
 * Buat Aduan Publik (features 3.1.1, 3.2.1–3.2.4, 3.5.1–3.5.2 & Task 4.3).
 * Stateless screen driven by [CreateTicketViewModel].
 */
@Composable
fun CreateTicketScreen(
    onCancel: () -> Unit,
    onCreated: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateTicketViewModel = koinViewModel()
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val submitState by viewModel.submitState.collectAsStateWithLifecycle()
    val dbCategories by viewModel.categories.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    val categoriesToDisplay = if (dbCategories.isNotEmpty()) dbCategories else FALLBACK_CATEGORIES
    val isSubmitting = submitState is UiState.Loading

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            viewModel.onPhotoSelected(uri)
        }
    }

    LaunchedEffect(formState.photoError) {
        formState.photoError?.let {
            snackbar.showSnackbar(it)
        }
    }

    LaunchedEffect(submitState) {
        when (val state = submitState) {
            is UiState.Success -> {
                snackbar.showSnackbar("Aduan berhasil dibuat")
                viewModel.resetSubmitState()
                onCreated(state.data)
            }
            is UiState.Error -> {
                snackbar.showSnackbar(state.message)
            }
            else -> Unit
        }
    }

    AppBackground(modifier) {
        Column(Modifier.fillMaxSize()) {
            // Top bar: Batal | title | Kirim Laporan
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 8.dp, end = 8.dp, top = 14.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onCancel) {
                    Icon(Icons.Default.Close, null, tint = InkSoft, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Batal", color = InkSoft)
                }
                Text(
                    "Buat Aduan Publik",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = Ink,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    onClick = { viewModel.submitTicket(onSuccess = onCreated) },
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(50),
                    color = if (isSubmitting) BrandIndigo.copy(alpha = 0.5f) else BrandIndigo,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        if (isSubmitting) "Mengirim..." else "Kirim Laporan",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .imePadding(),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Judul
                item {
                    GlassCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FieldLabel("Judul Aduan", required = true, modifier = Modifier.weight(1f))
                            TagChip("${formState.title.length}/$TITLE_MAX", container = FieldBg, content = InkMuted)
                        }
                        Spacer(Modifier.height(10.dp))
                        AppInput(
                            value = formState.title,
                            onValueChange = viewModel::onTitleChanged,
                            placeholder = "Contoh: Proyektor R.301 Rusak / Lampu Mati",
                            isError = formState.titleError != null,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                        )
                        if (formState.titleError != null) {
                            ErrorText(formState.titleError!!)
                        } else {
                            Spacer(Modifier.height(8.dp))
                            Hint("Tuliskan nama fasilitas dan kendala pokok secara singkat.")
                        }
                    }
                }

                // Kategori
                item {
                    GlassCard {
                        FieldLabel("Kategori Permasalahan", required = true)
                        Hint("Pilih kategori penanganan teknisi yang relevan")
                        Spacer(Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            categoriesToDisplay.forEach { category ->
                                val isSelected = formState.categoryId == category.id
                                CategoryRow(
                                    label = category.name,
                                    icon = getCategoryIcon(category.name),
                                    selected = isSelected,
                                    onClick = { viewModel.onCategorySelected(category) }
                                )
                            }
                        }
                        if (formState.categoryError != null) {
                            ErrorText(formState.categoryError!!)
                        }
                    }
                }

                // Lokasi
                item {
                    GlassCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(38.dp).clip(CircleShape).background(InfoBlueBg),
                                contentAlignment = Alignment.Center
                            ) { Icon(Icons.Outlined.Place, null, tint = InfoBlue, modifier = Modifier.size(20.dp)) }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Lokasi Spesifik Fasilitas", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                Hint("Bantu regu sarpras menemukan titik lokasi")
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        FieldLabel("Gedung / Fakultas", required = true)
                        Spacer(Modifier.height(6.dp))
                        AppInput(
                            value = formState.building,
                            onValueChange = viewModel::onBuildingChanged,
                            placeholder = "Misal: Gedung FTI (Teknologi Informasi)",
                            isError = formState.buildingError != null
                        )
                        if (formState.buildingError != null) {
                            ErrorText(formState.buildingError!!)
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(Modifier.weight(1f)) {
                                FieldLabel("Lantai", required = true)
                                Spacer(Modifier.height(6.dp))
                                AppInput(
                                    value = formState.floor,
                                    onValueChange = viewModel::onFloorChanged,
                                    placeholder = "Misal: Lantai 2",
                                    isError = formState.floorError != null,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                                )
                                if (formState.floorError != null) {
                                    ErrorText(formState.floorError!!)
                                }
                            }
                            Column(Modifier.weight(1f)) {
                                FieldLabel("Ruangan / Titik", required = true)
                                Spacer(Modifier.height(6.dp))
                                AppInput(
                                    value = formState.room,
                                    onValueChange = viewModel::onRoomChanged,
                                    placeholder = "Misal: R. Teater 301",
                                    isError = formState.roomError != null
                                )
                                if (formState.roomError != null) {
                                    ErrorText(formState.roomError!!)
                                }
                            }
                        }
                    }
                }

                // Deskripsi
                item {
                    GlassCard {
                        FieldLabel("Deskripsi Lengkap Masalah", required = true)
                        Spacer(Modifier.height(10.dp))
                        AppInput(
                            value = formState.description,
                            onValueChange = viewModel::onDescriptionChanged,
                            placeholder = "Jelaskan detail kerusakan fasilitas yang dialami. Contoh: Port HDMI longgar dan kabel power proyektor mengeluarkan bunyi.",
                            singleLine = false,
                            minLines = 4,
                            isError = formState.descriptionError != null,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                        )
                        if (formState.descriptionError != null) {
                            ErrorText(formState.descriptionError!!)
                        }
                    }
                }

                // Foto
                item {
                    PhotoSection(
                        photo = formState.photo,
                        onPick = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        onRemove = viewModel::onRemovePhoto
                    )
                }

                // Pernyataan
                item {
                    GlassCard(contentPadding = PaddingValues(12.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Checkbox(
                                checked = formState.agreed,
                                onCheckedChange = viewModel::onAgreedChanged,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = BrandIndigo,
                                    uncheckedColor = if (formState.agreedError != null) DangerRed else InkMuted
                                )
                            )
                            Column(Modifier.padding(top = 12.dp, end = 6.dp)) {
                                Text(
                                    "Saya menyatakan bahwa data yang dilaporkan adalah fasilitas kampus yang sebenarnya dan dapat dipertanggungjawabkan kepada biro sarana prasarana.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = InkSoft
                                )
                                if (formState.agreedError != null) {
                                    ErrorText(formState.agreedError!!)
                                }
                            }
                        }
                    }
                }

                item {
                    GradientButton(
                        text = if (isSubmitting) "Mempublikasikan..." else "Publikasikan Aduan",
                        icon = Icons.AutoMirrored.Filled.Send,
                        onClick = { viewModel.submitTicket(onSuccess = onCreated) },
                        enabled = !isSubmitting,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        Modifier.fillMaxWidth().navigationBarsPadding(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.VerifiedUser, null, tint = InkMuted, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Tiket akan otomatis diteruskan ke petugas teknisi gedung", style = MaterialTheme.typography.labelSmall, color = InkMuted)
                    }
                }
            }
        }
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter).navigationBarsPadding())
    }
}

@Composable
private fun CategoryRow(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) BrandIndigoSoft else Color.White,
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, if (selected) BrandIndigo else Hairline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(if (selected) Color.White else FieldBg),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = if (selected) BrandIndigo else InkSoft, modifier = Modifier.size(18.dp)) }
            Spacer(Modifier.width(12.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium),
                color = if (selected) BrandIndigo else Ink,
                modifier = Modifier.weight(1f)
            )
            if (selected) Icon(Icons.Filled.CheckCircle, null, tint = BrandIndigo, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun PhotoSection(
    photo: PickedPhotoState?,
    onPick: () -> Unit,
    onRemove: () -> Unit
) {
    GlassCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                FieldLabel("Lampiran Bukti Foto")
                Hint("Maksimal 1 Foto, JPG/PNG, Max 5MB")
            }
            TagChip(if (photo != null) "1 / 1 Slot" else "0 / 1 Slot")
        }
        Spacer(Modifier.height(12.dp))
        if (photo != null) {
            Surface(shape = RoundedCornerShape(16.dp), color = FieldBg, border = BorderStroke(1.dp, Hairline)) {
                Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = photo.uri,
                        contentDescription = "Pratinjau foto",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(58.dp).clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                photo.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                Icons.Outlined.DeleteOutline, "Hapus foto", tint = DangerRed,
                                modifier = Modifier.size(20.dp).clip(CircleShape).let { it }
                                    .then(Modifier)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CheckCircle, null, tint = SuccessText, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "${formatMb(photo.sizeBytes)} / 5.0 MB • ${photo.ext}",
                                style = MaterialTheme.typography.labelSmall,
                                color = InkSoft
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (photo.sizeBytes.toFloat() / MAX_PHOTO_BYTES).coerceIn(0.02f, 1f) },
                            color = BrandIndigo,
                            trackColor = Hairline,
                            strokeCap = StrokeCap.Round,
                            modifier = Modifier.fillMaxWidth().height(5.dp)
                        )
                    }
                }
            }
            TextButton(onClick = onRemove, modifier = Modifier.align(Alignment.End)) {
                Text("Hapus foto", color = DangerRed, style = MaterialTheme.typography.labelMedium)
            }
        }
        DashedButton(
            text = if (photo != null) "Ganti Foto dari Galeri" else "Pilih Foto dari Galeri",
            onClick = onPick
        )
    }
}

@Composable
private fun DashedButton(text: String, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = RoundedCornerShape(50), color = Color.Transparent, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .drawBehind {
                    drawRoundRect(
                        color = BrandIndigo.copy(alpha = 0.5f),
                        style = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f))),
                        cornerRadius = CornerRadius(size.height / 2, size.height / 2)
                    )
                }
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.AddAPhoto, null, tint = BrandIndigo, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(text, color = BrandIndigo, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
private fun Hint(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = InkMuted)
}

@Composable
private fun ErrorText(text: String) {
    Text(text, style = MaterialTheme.typography.labelSmall, color = DangerRed, modifier = Modifier.padding(top = 8.dp))
}

private fun formatMb(bytes: Long): String = String.format(Locale.US, "%.1f MB", bytes / (1024f * 1024f))

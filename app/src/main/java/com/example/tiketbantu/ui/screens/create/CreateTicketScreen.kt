package com.example.tiketbantu.ui.screens.create

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.platform.LocalContext
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
import coil.compose.AsyncImage
import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.repository.TicketRepository
import com.example.tiketbantu.ui.components.AppBackground
import com.example.tiketbantu.ui.components.AppInput
import com.example.tiketbantu.ui.components.FieldLabel
import com.example.tiketbantu.ui.components.GlassCard
import com.example.tiketbantu.ui.components.GradientButton
import com.example.tiketbantu.ui.components.TagChip
import com.example.tiketbantu.ui.session.DemoSession
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.io.File
import java.util.Locale

private const val TITLE_MAX = 80
private const val MAX_PHOTO_BYTES = 5L * 1024 * 1024

private data class CategoryOption(val id: Long, val label: String, val icon: ImageVector)

private val CATEGORIES = listOf(
    CategoryOption(1, "Teknologi & IT", Icons.Outlined.Computer),
    CategoryOption(2, "Fasilitas Ruangan", Icons.Outlined.MeetingRoom),
    CategoryOption(3, "Infrastruktur Umum", Icons.Outlined.Apartment)
)

private data class PickedPhoto(val uri: Uri, val name: String, val sizeBytes: Long, val ext: String)

/**
 * Buat Aduan Publik (features 3.1.1, 3.2.1–3.2.4, 3.5.1–3.5.2).
 * Saves through [TicketRepository] (currently the in-memory fake) and returns the new id.
 */
@Composable
fun CreateTicketScreen(
    onCancel: () -> Unit,
    onCreated: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = koinInject<TicketRepository>()
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    var title by rememberSaveable { mutableStateOf("") }
    var categoryId by rememberSaveable { mutableStateOf<Long?>(null) }
    var building by rememberSaveable { mutableStateOf("") }
    var floor by rememberSaveable { mutableStateOf("") }
    var room by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var agreed by rememberSaveable { mutableStateOf(false) }
    var photo by remember { mutableStateOf<PickedPhoto?>(null) }
    var submitting by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val info = readPhoto(context, uri)
        when {
            info == null -> scope.launch { snackbar.showSnackbar("Foto tidak dapat dibaca") }
            info.ext !in listOf("JPG", "JPEG", "PNG") -> scope.launch { snackbar.showSnackbar("Format harus JPG atau PNG") }
            info.sizeBytes > MAX_PHOTO_BYTES -> scope.launch { snackbar.showSnackbar("Ukuran foto melebihi 5 MB") }
            else -> photo = info
        }
    }

    val isValid = title.isNotBlank() && categoryId != null && building.isNotBlank() &&
        floor.isNotBlank() && room.isNotBlank() && description.isNotBlank() && agreed

    fun submit() {
        if (!isValid) {
            showErrors = true
            scope.launch {
                snackbar.showSnackbar(if (!agreed && title.isNotBlank()) "Centang pernyataan terlebih dahulu" else "Lengkapi semua kolom wajib (*)")
            }
            return
        }
        submitting = true
        scope.launch {
            try {
                val category = CATEGORIES.first { it.id == categoryId }
                val savedImagePath = photo?.let { savePhotoToInternal(context, it.uri) }
                val id = repository.createTicket(
                    Ticket(
                        title = title.trim(),
                        description = description.trim(),
                        categoryId = category.id,
                        categoryName = category.label,
                        locationBuilding = building.trim(),
                        locationFloor = floor.trim(),
                        locationRoom = room.trim(),
                        imageUrl = savedImagePath,
                        reporterId = DemoSession.userId,
                        reporterName = DemoSession.name
                    )
                )
                submitting = false
                onCreated(id)
            } catch (e: Exception) {
                submitting = false
                snackbar.showSnackbar("Gagal menyimpan aduan: ${e.localizedMessage ?: "Terjadi kesalahan"}")
            }
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
                    onClick = ::submit,
                    enabled = !submitting,
                    shape = RoundedCornerShape(50),
                    color = BrandIndigo,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        "Kirim Laporan",
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
                item { TransparencyNotice() }

                // Judul
                item {
                    GlassCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FieldLabel("Judul Aduan", required = true, modifier = Modifier.weight(1f))
                            TagChip("${title.length}/$TITLE_MAX", container = FieldBg, content = InkMuted)
                        }
                        Spacer(Modifier.height(10.dp))
                        AppInput(
                            value = title,
                            onValueChange = { if (it.length <= TITLE_MAX) title = it },
                            placeholder = "Contoh: Proyektor R.301 Rusak / Lampu Mati",
                            isError = showErrors && title.isBlank(),
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                        )
                        Spacer(Modifier.height(8.dp))
                        Hint("Tuliskan nama fasilitas dan kendala pokok secara singkat.")
                    }
                }

                // Kategori
                item {
                    GlassCard {
                        FieldLabel("Kategori Permasalahan", required = true)
                        Hint("Pilih kategori penanganan teknisi yang relevan")
                        Spacer(Modifier.height(10.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(CATEGORIES) { option ->
                                val selected = categoryId == option.id
                                Surface(
                                    onClick = { categoryId = option.id },
                                    shape = RoundedCornerShape(50),
                                    color = if (selected) BrandIndigo else Color.White,
                                    border = BorderStroke(1.dp, if (selected) BrandIndigo else Hairline),
                                    shadowElevation = if (selected) 4.dp else 0.dp
                                ) {
                                    Row(
                                        Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(option.icon, null, tint = if (selected) Color.White else BrandIndigo, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            option.label,
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium),
                                            color = if (selected) Color.White else Ink
                                        )
                                        if (selected) {
                                            Spacer(Modifier.width(6.dp))
                                            Icon(Icons.Filled.CheckCircle, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                        if (showErrors && categoryId == null) ErrorText("Pilih salah satu kategori")
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
                        AppInput(building, { building = it }, "Misal: Gedung FTI (Teknologi Informasi)", isError = showErrors && building.isBlank())
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(Modifier.weight(1f)) {
                                FieldLabel("Lantai", required = true)
                                Spacer(Modifier.height(6.dp))
                                AppInput(
                                    floor, { floor = it }, "Misal: Lantai 2",
                                    isError = showErrors && floor.isBlank(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                                )
                            }
                            Column(Modifier.weight(1f)) {
                                FieldLabel("Ruangan / Titik", required = true)
                                Spacer(Modifier.height(6.dp))
                                AppInput(room, { room = it }, "Misal: R. Teater 301", isError = showErrors && room.isBlank())
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
                            value = description,
                            onValueChange = { description = it },
                            placeholder = "Jelaskan detail kerusakan fasilitas yang dialami. Contoh: Port HDMI longgar dan kabel power proyektor mengeluarkan bunyi.",
                            singleLine = false,
                            minLines = 4,
                            isError = showErrors && description.isBlank(),
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                        )
                    }
                }

                // Foto
                item {
                    PhotoSection(
                        photo = photo,
                        onPick = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        onRemove = { photo = null }
                    )
                }

                // Pernyataan
                item {
                    GlassCard(contentPadding = PaddingValues(12.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Checkbox(
                                checked = agreed,
                                onCheckedChange = { agreed = it },
                                colors = CheckboxDefaults.colors(checkedColor = BrandIndigo, uncheckedColor = if (showErrors && !agreed) DangerRed else InkMuted)
                            )
                            Text(
                                "Saya menyatakan bahwa data yang dilaporkan adalah fasilitas kampus yang sebenarnya dan dapat dipertanggungjawabkan kepada biro sarana prasarana.",
                                style = MaterialTheme.typography.bodySmall,
                                color = InkSoft,
                                modifier = Modifier.padding(top = 12.dp, end = 6.dp)
                            )
                        }
                    }
                }

                item {
                    GradientButton(
                        text = if (submitting) "Mempublikasikan..." else "Publikasikan Aduan",
                        icon = Icons.AutoMirrored.Filled.Send,
                        onClick = ::submit,
                        enabled = !submitting,
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
private fun TransparencyNotice() {
    GlassCard(containerColor = Color.White) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                Modifier.size(42.dp).clip(CircleShape).background(InfoBlue),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Outlined.Lightbulb, null, tint = Color.White, modifier = Modifier.size(22.dp)) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Transparansi Fasilitas Kampus",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(Modifier.width(6.dp))
                    TagChip("PUBLIK", fontSize = 10.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    buildAnnotatedString {
                        append("Semua laporan di TiketBantu bersifat ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Ink)) { append("100% Terbuka") }
                        append(". Pastikan cek isu serupa di feed dengan ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = DangerRed)) { append("\"Saya Juga Mengalami\"") }
                        append(" agar percepat verifikasi!")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSoft
                )
            }
        }
    }
}

@Composable
private fun CategoryRow(option: CategoryOption, selected: Boolean, onClick: () -> Unit) {
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
            ) { Icon(option.icon, null, tint = if (selected) BrandIndigo else InkSoft, modifier = Modifier.size(18.dp)) }
            Spacer(Modifier.width(12.dp))
            Text(
                option.label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium),
                color = if (selected) BrandIndigo else Ink,
                modifier = Modifier.weight(1f)
            )
            if (selected) Icon(Icons.Filled.CheckCircle, null, tint = BrandIndigo, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun PhotoSection(photo: PickedPhoto?, onPick: () -> Unit, onRemove: () -> Unit) {
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

private fun readPhoto(context: Context, uri: Uri): PickedPhoto? = runCatching {
    var name = "foto_bukti.jpg"
    var size = 0L
    context.contentResolver.query(uri, null, null, null, null)?.use { c ->
        val nameIdx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIdx = c.getColumnIndex(OpenableColumns.SIZE)
        if (c.moveToFirst()) {
            if (nameIdx >= 0) name = c.getString(nameIdx) ?: name
            if (sizeIdx >= 0) size = c.getLong(sizeIdx)
        }
    }
    val mime = context.contentResolver.getType(uri).orEmpty()
    val ext = when {
        mime.endsWith("png") -> "PNG"
        mime.endsWith("jpeg") || mime.endsWith("jpg") -> "JPG"
        else -> name.substringAfterLast('.', "").uppercase()
    }
    PickedPhoto(uri, name, size, ext)
}.getOrNull()

private suspend fun savePhotoToInternal(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
    val dir = File(context.filesDir, "ticket_photos").apply { if (!exists()) mkdirs() }
    val filename = "ticket_${System.currentTimeMillis()}.jpg"
    val destFile = File(dir, filename)
    context.contentResolver.openInputStream(uri)?.use { input ->
        destFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    destFile.absolutePath
}

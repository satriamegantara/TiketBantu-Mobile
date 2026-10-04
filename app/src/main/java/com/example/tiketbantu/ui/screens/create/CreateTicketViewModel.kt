package com.example.tiketbantu.ui.screens.create

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.viewModelScope
import com.example.tiketbantu.base.BaseViewModel
import com.example.tiketbantu.base.UiState
import com.example.tiketbantu.data.local.dao.CategoryDao
import com.example.tiketbantu.data.local.entity.CategoryEntity
import com.example.tiketbantu.data.preferences.SessionManager
import com.example.tiketbantu.domain.model.Ticket
import com.example.tiketbantu.domain.repository.TicketRepository
import com.example.tiketbantu.ui.session.DemoSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.util.Locale

private const val TITLE_MAX = 80
private const val MAX_PHOTO_BYTES = 5L * 1024 * 1024 // 5 MB

data class PickedPhotoState(
    val uri: Uri,
    val name: String,
    val sizeBytes: Long,
    val ext: String
)

data class CreateTicketFormState(
    val title: String = "",
    val categoryId: Long? = null,
    val categoryName: String = "",
    val building: String = "",
    val floor: String = "",
    val room: String = "",
    val description: String = "",
    val photo: PickedPhotoState? = null,
    val titleError: String? = null,
    val categoryError: String? = null,
    val buildingError: String? = null,
    val floorError: String? = null,
    val roomError: String? = null,
    val descriptionError: String? = null,
    val photoError: String? = null
) {
    val isTitleValid: Boolean get() = title.isNotBlank() && title.length <= TITLE_MAX
    val isCategoryValid: Boolean get() = categoryId != null
    val isBuildingValid: Boolean get() = building.isNotBlank()
    val isFloorValid: Boolean get() = floor.isNotBlank()
    val isRoomValid: Boolean get() = room.isNotBlank()
    val isDescriptionValid: Boolean get() = description.isNotBlank()

    val isValid: Boolean
        get() = isTitleValid && isCategoryValid && isBuildingValid &&
                isFloorValid && isRoomValid && isDescriptionValid
}

/**
 * ViewModel for [CreateTicketScreen] adhering to MVVM + UDF (Task 4.3 & 4.4).
 *
 * Responsibilities:
 * - Manage form state and validations (mandatory fields, 80-char title limit, photo size <= 5MB).
 * - Load dynamic categories from Room DB via [CategoryDao].
 * - Resolve current reporter identity via [SessionManager] (fallback to [DemoSession]).
 * - Compress and persist uploaded photos into internal storage (`filesDir/ticket_photos/`).
 * - Execute ticket creation through [TicketRepository].
 */
class CreateTicketViewModel(
    private val ticketRepository: TicketRepository,
    private val categoryDao: CategoryDao,
    private val sessionManager: SessionManager,
    private val context: Context
) : BaseViewModel() {

    private val _formState = MutableStateFlow(CreateTicketFormState())
    val formState: StateFlow<CreateTicketFormState> = _formState.asStateFlow()

    private val _submitState = MutableStateFlow<UiState<Long>>(UiState.Idle)
    val submitState: StateFlow<UiState<Long>> = _submitState.asStateFlow()

    val categories: StateFlow<List<CategoryEntity>> = categoryDao.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    fun onTitleChanged(newTitle: String) {
        if (newTitle.length <= TITLE_MAX) {
            _formState.update { it.copy(title = newTitle, titleError = null) }
        }
    }

    fun onCategorySelected(category: CategoryEntity) {
        _formState.update {
            it.copy(
                categoryId = category.id,
                categoryName = category.name,
                categoryError = null
            )
        }
    }

    fun onBuildingChanged(newBuilding: String) {
        _formState.update { it.copy(building = newBuilding, buildingError = null) }
    }

    fun onFloorChanged(newFloor: String) {
        _formState.update { it.copy(floor = newFloor, floorError = null) }
    }

    fun onRoomChanged(newRoom: String) {
        _formState.update { it.copy(room = newRoom, roomError = null) }
    }

    fun onDescriptionChanged(newDescription: String) {
        _formState.update { it.copy(description = newDescription, descriptionError = null) }
    }


    fun onPhotoSelected(uri: Uri?) {
        if (uri == null) return
        val pickedPhoto = readPhotoMetadata(uri)
        when {
            pickedPhoto == null -> {
                _formState.update { it.copy(photoError = "Foto tidak dapat dibaca") }
            }
            pickedPhoto.ext !in listOf("JPG", "JPEG", "PNG") -> {
                _formState.update { it.copy(photoError = "Format harus berupa JPG atau PNG") }
            }
            pickedPhoto.sizeBytes > MAX_PHOTO_BYTES -> {
                _formState.update { it.copy(photoError = "Ukuran foto melebihi batas 5 MB") }
            }
            else -> {
                _formState.update { it.copy(photo = pickedPhoto, photoError = null) }
            }
        }
    }

    fun onRemovePhoto() {
        _formState.update { it.copy(photo = null, photoError = null) }
    }

    fun resetSubmitState() {
        _submitState.value = UiState.Idle
    }

    fun submitTicket(onSuccess: (Long) -> Unit) {
        val current = _formState.value
        var hasError = false
        var titleErr: String? = null
        var categoryErr: String? = null
        var buildingErr: String? = null
        var floorErr: String? = null
        var roomErr: String? = null
        var descErr: String? = null

        if (current.title.isBlank()) {
            titleErr = "Judul aduan wajib diisi"
            hasError = true
        }
        if (current.categoryId == null) {
            categoryErr = "Kategori wajib dipilih"
            hasError = true
        }
        if (current.building.isBlank()) {
            buildingErr = "Gedung wajib diisi"
            hasError = true
        }
        if (current.floor.isBlank()) {
            floorErr = "Lantai wajib diisi"
            hasError = true
        }
        if (current.room.isBlank()) {
            roomErr = "Ruangan wajib diisi"
            hasError = true
        }
        if (current.description.isBlank()) {
            descErr = "Deskripsi detail wajib diisi"
            hasError = true
        }

        if (hasError) {
            _formState.update {
                it.copy(
                    titleError = titleErr,
                    categoryError = categoryErr,
                    buildingError = buildingErr,
                    floorError = floorErr,
                    roomError = roomErr,
                    descriptionError = descErr
                )
            }
            return
        }

        _submitState.value = UiState.Loading

        launchSafe {
            try {
                // 1. Resolve photo internal storage path (Task 4.4: filesDir/ticket_photos/)
                val savedImagePath = current.photo?.let { saveAndCompressPhoto(it.uri) }

                // 2. Resolve current user identity
                val sessionUser = sessionManager.sessionState.firstOrNull()?.currentUser
                val reporterId = sessionUser?.id ?: if (DemoSession.userId > 0) DemoSession.userId else 1L
                val reporterName = sessionUser?.name?.ifBlank { null } ?: DemoSession.name.ifBlank { "Pengguna" }

                // 3. Resolve category name
                val categoryName = current.categoryName.ifBlank {
                    current.categoryId?.let { categoryDao.getCategoryById(it)?.name } ?: "Umum"
                }

                val newTicket = Ticket(
                    title = current.title.trim(),
                    description = current.description.trim(),
                    categoryId = current.categoryId ?: 1L,
                    categoryName = categoryName,
                    locationBuilding = current.building.trim(),
                    locationFloor = current.floor.trim(),
                    locationRoom = current.room.trim(),
                    imageUrl = savedImagePath,
                    reporterId = reporterId,
                    reporterName = reporterName
                )

                val createdId = ticketRepository.createTicket(newTicket)
                _submitState.value = UiState.Success(createdId)
                withContext(Dispatchers.Main) {
                    onSuccess(createdId)
                }
            } catch (t: Throwable) {
                _submitState.value = UiState.Error(
                    message = t.localizedMessage ?: "Gagal membuat aduan",
                    throwable = t
                )
            }
        }
    }

    private fun readPhotoMetadata(uri: Uri): PickedPhotoState? = runCatching {
        var name = "foto_bukti.jpg"
        var size = 0L
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst()) {
                if (nameIdx >= 0) name = cursor.getString(nameIdx) ?: name
                if (sizeIdx >= 0) size = cursor.getLong(sizeIdx)
            }
        }
        val mime = context.contentResolver.getType(uri).orEmpty()
        val ext = when {
            mime.endsWith("png", ignoreCase = true) -> "PNG"
            mime.endsWith("jpeg", ignoreCase = true) || mime.endsWith("jpg", ignoreCase = true) -> "JPG"
            else -> name.substringAfterLast('.', "").uppercase(Locale.US)
        }
        PickedPhotoState(uri, name, size, ext)
    }.getOrNull()

    /**
     * Saves and compresses the photo into internal storage (`filesDir/ticket_photos/`),
     * adhering to feature 3.5.4 & Task 4.4.
     */
    private suspend fun saveAndCompressPhoto(uri: Uri): String = withContext(Dispatchers.IO) {
        val dir = File(context.filesDir, "ticket_photos").apply { if (!exists()) mkdirs() }
        val filename = "ticket_${System.currentTimeMillis()}.jpg"
        val destFile = File(dir, filename)

        val stream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = stream?.use { BitmapFactory.decodeStream(it) }

        if (bitmap != null) {
            destFile.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
            bitmap.recycle()
        } else {
            // Fallback: direct stream copy if decoding fails
            context.contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        destFile.absolutePath
    }
}

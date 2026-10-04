package com.example.tiketbantu.ui.screens.admin

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiketbantu.data.local.dao.CategoryDao
import com.example.tiketbantu.data.local.dao.UserDao
import com.example.tiketbantu.data.local.entity.CategoryEntity
import com.example.tiketbantu.data.local.entity.UserEntity
import com.example.tiketbantu.domain.model.Category
import com.example.tiketbantu.domain.model.User
import com.example.tiketbantu.ui.components.AppBackground
import com.example.tiketbantu.ui.components.AppInput
import com.example.tiketbantu.ui.components.CircleIconButton
import com.example.tiketbantu.ui.components.GlassCard
import com.example.tiketbantu.ui.components.GradientButton
import com.example.tiketbantu.ui.components.InitialsAvatar
import com.example.tiketbantu.ui.components.TagChip
import com.example.tiketbantu.ui.theme.BrandCyan
import com.example.tiketbantu.ui.theme.BrandIndigo
import com.example.tiketbantu.ui.theme.BrandIndigoSoft
import com.example.tiketbantu.ui.theme.DangerRed
import com.example.tiketbantu.ui.theme.FieldBg
import com.example.tiketbantu.ui.theme.Hairline
import com.example.tiketbantu.ui.theme.Ink
import com.example.tiketbantu.ui.theme.InkMuted
import com.example.tiketbantu.ui.theme.InkSoft
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Admin Master Data Management screen (Users & Categories)
 */
@Composable
fun UserManagementScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    initialTab: Int = 0
) {
    val userDao = koinInject<UserDao>()
    val categoryDao = koinInject<CategoryDao>()
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    val usersFromDb by userDao.getAllUsers().collectAsState(initial = emptyList())
    val categoriesFromDb by categoryDao.getAllCategories().collectAsState(initial = emptyList())

    val users = remember(usersFromDb) {
        usersFromDb.map { User(it.id, it.name, it.email, it.nimNip, it.role, it.isActive) }
    }

    val categories = remember(categoriesFromDb) {
        categoriesFromDb.map { Category(it.id, it.name) }
    }

    var selectedTab by remember(initialTab) { mutableStateOf(initialTab) }
    val tabs = listOf("Akun Pengguna", "Kategori Masalah")

    var showAddUserDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }

    AppBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kelola Master Data",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = Ink,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    onClick = {
                        if (selectedTab == 0) showAddUserDialog = true
                        else showAddCategoryDialog = true
                    },
                    shape = CircleShape,
                    color = BrandIndigo,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                divider = { Box(Modifier.fillMaxWidth().height(1.dp).background(Hairline)) },
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = BrandIndigo,
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (selectedTab == index) BrandIndigo else InkSoft
                            )
                        }
                    )
                }
            }

            // Tab Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (selectedTab == 0) {
                    items(users, key = { it.id }) { user ->
                        UserCardItem(
                            user = user,
                            onToggleActive = { active ->
                                scope.launch {
                                    userDao.updateActiveStatus(user.id, active)
                                    android.widget.Toast.makeText(
                                        context,
                                        if (active) "Akun ${user.name} diaktifkan" else "Akun ${user.name} dinonaktifkan",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    }
                } else {
                    items(categories, key = { it.id }) { category ->
                        CategoryCardItem(
                            category = category,
                            onEdit = { categoryToEdit = category }
                        )
                    }
                }
            }
        }
    }

    if (showAddUserDialog) {
        AddUserDialog(
            onDismiss = { showAddUserDialog = false },
            onAdd = { name, email, nim, role ->
                scope.launch {
                    userDao.insertUser(
                        UserEntity(
                            name = name,
                            email = email,
                            nimNip = nim,
                            passwordHash = "pass123",
                            role = role,
                            isActive = true
                        )
                    )
                    android.widget.Toast.makeText(context, "Pengguna $name berhasil ditambahkan", android.widget.Toast.LENGTH_SHORT).show()
                }
                showAddUserDialog = false
            }
        )
    }

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onAdd = { name ->
                scope.launch {
                    categoryDao.insertCategory(CategoryEntity(name = name))
                    android.widget.Toast.makeText(context, "Kategori $name berhasil ditambahkan", android.widget.Toast.LENGTH_SHORT).show()
                }
                showAddCategoryDialog = false
            }
        )
    }

    if (categoryToEdit != null) {
        val target = categoryToEdit!!
        EditCategoryDialog(
            category = target,
            onDismiss = { categoryToEdit = null },
            onSave = { newName ->
                scope.launch {
                    categoryDao.updateCategory(CategoryEntity(id = target.id, name = newName))
                    android.widget.Toast.makeText(context, "Kategori berhasil diubah menjadi $newName", android.widget.Toast.LENGTH_SHORT).show()
                }
                categoryToEdit = null
            }
        )
    }
}

@Composable
private fun UserCardItem(
    user: User,
    onToggleActive: (Boolean) -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InitialsAvatar(name = user.name, size = 44.dp, soft = false)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Ink
                    )
                    Spacer(Modifier.width(8.dp))
                    val (badgeBg, badgeFg) = when (user.role) {
                        "ADMIN" -> Color(0xFFF3E8FF) to Color(0xFF7E22CE)
                        "AGEN" -> BrandIndigoSoft to BrandIndigo
                        else -> Color(0xFFE0F2FE) to Color(0xFF0369A1)
                    }
                    TagChip(text = user.role, container = badgeBg, content = badgeFg, fontSize = 9.sp)
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = InkMuted
                )
                if (!user.nimNip.isNullOrBlank()) {
                    Text(
                        text = "ID/NIM: ${user.nimNip}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = InkSoft
                    )
                }
            }
            Switch(
                checked = user.isActive,
                onCheckedChange = onToggleActive,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = BrandIndigo,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Hairline
                )
            )
        }
    }
}

@Composable
private fun CategoryCardItem(
    category: Category,
    onEdit: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onEdit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE0F7FA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Category,
                    contentDescription = null,
                    tint = Color(0xFF00838F),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Ink
                )
                Text(
                    text = "ID Kategori: #${category.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = InkMuted
                )
            }
            CircleIconButton(
                icon = Icons.Outlined.Edit,
                contentDescription = "Edit Kategori",
                onClick = onEdit,
                bordered = false,
                container = FieldBg,
                size = 36.dp
            )
        }
    }
}

@Composable
private fun AddUserDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, email: String, nim: String, role: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("AGEN") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text("Tambah Pengguna / Teknisi", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AppInput(value = name, onValueChange = { name = it }, placeholder = "Nama Lengkap")
                AppInput(value = email, onValueChange = { email = it }, placeholder = "Alamat Email")
                AppInput(value = nim, onValueChange = { nim = it }, placeholder = "NIM / NIP")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("AGEN", "PELAPOR", "ADMIN").forEach { r ->
                        val selected = role == r
                        Surface(
                            onClick = { role = r },
                            shape = RoundedCornerShape(50),
                            color = if (selected) BrandIndigo else FieldBg,
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = r,
                                    color = if (selected) Color.White else InkSoft,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onAdd(name, email, nim, role) },
                enabled = name.isNotBlank() && email.isNotBlank()
            ) {
                Text("Simpan", color = BrandIndigo, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = InkSoft) }
        }
    )
}

@Composable
private fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text("Tambah Kategori Sarpras", fontWeight = FontWeight.Bold) },
        text = {
            AppInput(value = name, onValueChange = { name = it }, placeholder = "Nama Kategori (contoh: Sanitasi Gedung)")
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onAdd(name) },
                enabled = name.isNotBlank()
            ) {
                Text("Simpan", color = BrandIndigo, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = InkSoft) }
        }
    )
}

@Composable
private fun EditCategoryDialog(
    category: Category,
    onDismiss: () -> Unit,
    onSave: (newName: String) -> Unit
) {
    var name by remember { mutableStateOf(category.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text("Edit Kategori Sarpras", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Perbarui nama untuk kategori #${category.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkMuted
                )
                AppInput(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Nama Kategori (contoh: Fasilitas Ruangan)"
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onSave(name.trim()) },
                enabled = name.isNotBlank()
            ) {
                Text("Simpan", color = BrandIndigo, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = InkSoft) }
        }
    )
}

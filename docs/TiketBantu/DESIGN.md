# DESIGN.md — Design System & UI Guidelines (Android Jetpack Compose)

## 🎨 Design Philosophy
Sistem desain e-Notulen versi mobile mengadaptasi gaya **Fresh & Clean UI** [cite: 5] dengan mengimplementasikan panduan **Material Design 3 (M3)** dari Android. Antarmuka dirancang secara khusus untuk kenyamanan layar sentuh dengan menjaga kesan elegan, fungsional, dan hierarki visual yang jelas.

---

## 🎨 Color System (Color.kt)
Implementasi warna di Jetpack Compose menggunakan palet utama yang disesuaikan dari versi web [cite: 5], dipetakan ke skema Light & Dark Theme M3.

```kotlin
// Primary Palette — Deep Teal [cite: 5]
val md_theme_light_primary = Color(0xFF10B981) 
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFD1FAE5) 

// Secondary Palette — Slate Blue [cite: 5]
val md_theme_light_secondary = Color(0xFF64748B) 
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFF1F5F9) 

// Background & Surface
val md_theme_light_background = Color(0xFFF8FAFC) 
val md_theme_light_surface = Color(0xFFFFFFFF) 

// Error & Warning
val md_theme_light_error = Color(0xFFEF4444)
```

---

## 🔤 Typography (Type.kt)
Keluarga huruf utama menggunakan **Plus Jakarta Sans** [cite: 5] yang dikonfigurasi ke dalam skema `Typography` bawaan Material Design 3.

| Skala M3 | Ukuran (sp) | Ketebalan (Weight) | Penggunaan UI Compose |
| :--- | :--- | :--- | :--- |
| `displayLarge` | 36sp | Bold (700) | Angka statistik utama pada Dashboard [cite: 5]. |
| `titleLarge` | 22sp | Bold (700) | Judul pada `TopAppBar`. |
| `titleMedium` | 16sp | SemiBold (600) | Judul rapat di dalam komponen `Card` [cite: 5]. |
| `bodyLarge` | 16sp | Regular (400) | Teks isi atau paragraf utama [cite: 5]. |
| `bodyMedium` | 14sp | Regular (400) | Nilai input pada `OutlinedTextField` [cite: 5]. |
| `labelSmall` | 11sp | Medium (500) | Label pada `Badge` atau menu `NavigationBarItem` [cite: 5]. |

---

## 🧩 Component Design (Jetpack Compose)

### 1. Navigasi (Scaffold Components)
- **Top Bar:** Menggunakan `CenterAlignedTopAppBar` dengan latar belakang transparan tersinkronisasi guliran layar.
- **Bottom Navigation:** Menggantikan struktur *Sidebar* web [cite: 5] menggunakan `NavigationBar` (M3). Item yang aktif disorot dengan latar belakang `primaryContainer` yang oval, dengan transisi *fade* khas M3.

### 2. Cards (Item Notulen & Statistik)
- Menggunakan komponen `ElevatedCard` atau `OutlinedCard`.
- **Bentuk:** `RoundedCornerShape(12.dp)` [cite: 5].
- **Padding Dalam:** 16.dp hingga 24.dp sesuai tingkat hierarki [cite: 5].
- **Interaksi:** Menerapkan efek *Ripple* (`clickable`) saat diketuk.

### 3. Buttons
- Menggunakan radius lengkungan `RoundedCornerShape(8.dp)` pada seluruh tombol aplikasi [cite: 5].
- **Primary:** Komponen `Button` reguler dengan pengisi warna `primary`.
- **Secondary / Batalkan:** Komponen `OutlinedButton` agar tidak berebut fokus dengan tombol simpan [cite: 5].
- **Ghost Action:** Komponen `TextButton` dengan efek ketukan sederhana [cite: 5].

### 4. Form Inputs & Pencarian
- Seluruh isian data (Judul, Tempat, dll.) mengimplementasikan `OutlinedTextField` M3 [cite: 5].
- Ketinggian form standar disesuaikan dengan `Modifier.heightIn(min = 48.dp)`.
- Menggunakan `isError = true` dipadukan `supportingText` untuk menyampaikan galat validasi pengisian.

### 5. Dialog & Umpan Balik (Feedback)
- **Modal:** Menggunakan `AlertDialog` (M3) dengan radius sudut yang dipertajam, menampilkan aksi konfirmasi/batal di bagian bawah sudut kanan.
- **Notifikasi/Toast:** Diimplementasikan dengan memanggil `Snackbar` lewat status *Host* Scaffold, tampil melayang dari posisi bawah.

---

## 🎭 Animations & Transitions
Pengalaman pengguna (UX) seluler memerlukan *micro-interactions* reaktif [cite: 5]:
- Menggunakan `animateColorAsState` saat indikator `UiState` berubah dari *Draft* ke *Final*.
- `AnimatedVisibility` dengan pendaran `slideInVertically()` dan `fadeIn()` untuk memunculkan dan menghilangkan baris di dalam `LazyColumn`.
- Menyertakan efek *shimmer* untuk memvisualisasikan `UiState.Loading`.

---

## 🖼️ Iconography
Berbeda dengan web yang menggunakan *Lucide React* [cite: 5], ekosistem Compose memanfaatkan himpunan bawaan **Material Icons Extended** untuk konsistensi resolusi sistem Android.
- Tipe `Icons.Filled` untuk indikator aktif.
- Tipe `Icons.Outlined` untuk tombol, aksi edit, dan indikator tidak aktif.

# CLAUDE.md — Aturan Utama untuk AI (Aplikasi Mobile e-Notulen)

## 🎯 Tentang Proyek
**e-Notulen Mobile** adalah aplikasi manajemen notulen rapat digital berbasis Android Native untuk Pemerintah Kabupaten Purbalingga. Dokumen ini merupakan adaptasi dari aturan pengembangan versi web [cite: 4], dioptimalkan secara khusus untuk memenuhi pedoman pengerjaan proyek pemrograman mobile (Tugas Akhir) dengan menggunakan Jetpack Compose.

---

## 🏗️ Tech Stack (Mobile App)
| Layer | Teknologi |
| :--- | :--- |
| Bahasa Pemrograman | Kotlin |
| UI Toolkit | Jetpack Compose |
| Desain Sistem | Material Design 3 (M3) |
| Arsitektur | MVVM (Model-View-ViewModel) |
| Asynchronous / Concurrency | Kotlin Coroutines & Flow |
| Networking | Retrofit / Ktor + OkHttp |
| Navigasi | Compose Navigation (Type-Safe) |
| Dependensi Backend | Go (Golang), PostgreSQL, MinIO, Keycloak [cite: 4] |

---

## 📁 Struktur Repositori (Android App)
```text
e-notulen-mobile/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/pemkab/enotulen/
│   │   │   │   ├── ui/             # UI Layer: Jetpack Compose (Theme, Screens, Components)
│   │   │   │   ├── viewmodel/      # Presentation Layer: ViewModel & UiState
│   │   │   │   ├── data/           # Data Layer: Repository, Remote (Retrofit), Local (DataStore)
│   │   │   │   ├── di/             # Dependency Injection
│   │   │   │   └── utils/          # Helpers & Constants
│   │   │   └── AndroidManifest.xml
│   │   └── res/                    # Assets, Strings, XML resources (minimal)
│   ├── build.gradle.kts            # App-level build script
│   └── proguard-rules.pro
├── build.gradle.kts                # Project-level build script
└── README.md
```

---

## ⚙️ Aturan Pengembangan (Wajib Ditaati oleh AI)

### Aturan Umum
1. **Bahasa Kode:** Seluruh kode, komentar, dan *commit message* ditulis dalam **Bahasa Inggris** [cite: 4].
2. **Bahasa UI:** Seluruh teks yang ditampilkan ke pengguna di layar (UI) ditulis dalam **Bahasa Indonesia** [cite: 4].
3. **Pembagian Tim:** Proyek dikerjakan dengan pembagian peran yang jelas antara UI/UX/Frontend dan Backend/State Management.

### Aturan Frontend (Android Jetpack Compose)
Setiap penulisan kode antarmuka seluler **HARUS** memenuhi kriteria materi berikut:
1. **UI & Layout Dasar:** Selalu gunakan `Column`, `Row`, `Box`, serta `Modifier` yang terstruktur rapi untuk menyusun layout aplikasi. Dilarang keras menggunakan XML Layout lama.
2. **Material Design 3 (M3):** Terapkan tema dengan palet M3 (Color, Typography), serta gunakan komponen standar seperti `Button`, `OutlinedTextField`, dan `ElevatedCard` / `OutlinedCard`.
3. **State Management & UDF:** 
   - Konsep *Unidirectional Data Flow* (UDF) wajib diterapkan. 
   - State selalu di-hoist (*State Hoisting*) ke ViewModel atau pengontrol layar teratas. 
   - Wajib menggunakan `remember` dan `rememberSaveable` untuk mempertahankan input dari perubahan konfigurasi (misal: rotasi layar).
4. **Lazy Layouts:** Untuk daftar yang panjang (Daftar Notulen, Daftar Hadir), WAJIB menggunakan `LazyColumn` atau `LazyGrid`. Pastikan selalu menyertakan atribut `key` untuk efisiensi rendering.
5. **Arsitektur MVVM & UiState:** 
   - Dilarang menempatkan logika bisnis di dalam fungsi `@Composable`.
   - Gunakan ViewModel untuk mengelola logika.
   - Status antarmuka harus direpresentasikan melalui *sealed class* `UiState` (`Loading`, `Success`, `Error`).
6. **Networking:** Gunakan Retrofit atau Ktor untuk panggilan REST API secara asinkron. Jalankan proses ini di luar *Main Thread* menggunakan `viewModelScope` dan Coroutines.
7. **Navigation Compose:** Implementasikan navigasi berbasis *Type-Safe Navigation* (memanfaatkan *sealed class/objects*). Wajib mendukung integrasi `Scaffold` dan `BottomNavigation` (NavigationBar) untuk struktur menu utama.

### Aturan Backend & Database (Eksternal)
*(Catatan: Aplikasi seluler akan mengonsumsi API yang telah didefinisikan pada dokumen web [cite: 4])*
1. **Otorisasi API:** Permintaan (request) API ke backend WAJIB menyertakan JSON Web Token (JWT) dari Keycloak pada `Authorization: Bearer` header [cite: 4].
2. **Proxy Eksternal:** Permintaan data OPD tidak boleh langsung ke API eksternal pemkab, melainkan melalui proxy backend e-Notulen yang telah disiapkan [cite: 4].
3. **Penyimpanan Berkas:** Berkas foto/dokumen diunggah ke backend, yang kemudian meneruskannya ke MinIO [cite: 4].

---

## 🚫 Jangan Lakukan (Strict Do Nots)

1. **JANGAN** membuat antarmuka berbasis XML (Wajib Jetpack Compose murni).
2. **JANGAN** melakukan operasi Jaringan (Networking) atau Database pada *Main Thread* (UI Thread). Selalu gunakan `Dispatchers.IO`.
3. **JANGAN** menyimpan token JWT Keycloak dalam `SharedPreferences` biasa dalam bentuk *plaintext*. Gunakan `EncryptedSharedPreferences` atau implementasi `DataStore` yang aman.
4. **JANGAN** menempatkan logika perhitungan berat atau manipulasi API langsung di dalam fungsi `@Composable`.
5. **JANGAN** menggunakan variabel global untuk menyimpan *state* UI. Selalu patuhi kaidah *State Hoisting* dan isolasi ViewModel.
6. **JANGAN** menggunakan pustaka navigasi eksternal usang jika Compose Navigation sudah cukup mengakomodasi kebutuhan *Type-Safe routing*.
7. **JANGAN** mengubah titik akhir (endpoint) REST API yang sudah ditentukan dalam dokumentasi arsitektur web [cite: 3, 4].

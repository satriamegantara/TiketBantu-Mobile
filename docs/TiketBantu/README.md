# 🎫 TiketBantu Mobile App

**TiketBantu** adalah Sistem Helpdesk & Pengaduan Fasilitas Kampus berbasis **Android Native (Jetpack Compose)** yang menyimpan data secara lokal menggunakan **Room**. Aplikasi ini mendemonstrasikan arsitektur MVVM, unidirectional data flow, dan penyimpanan permanen di perangkat, cocok untuk tugas akhir atau showcase Jetpack Compose.

Proyek ini dibangun sebagai Tugas Akhir/Proyek Pemrograman Mobile dengan pendekatan *Mobile-First Adaptive Design* dan berpedoman pada arsitektur MVVM (Model-View-ViewModel).

---

## 🌟 Fitur Unggulan

- **Aduan 100% Publik:** Semua laporan bersifat transparan dan dapat dilihat oleh seluruh civitas akademika kampus.
- **Fitur "Saya Juga Mengalami" (Most Liked):** Menghilangkan penentuan prioritas manual. Urgensi tiket ditentukan secara otomatis dari banyaknya dukungan (*Most Liked*) yang diberikan oleh pengguna lain.
- **Linear Queue & Auto-Claim:** Agen/Petugas dapat langsung mengambil (*claim*) aduan secara mandiri (first-come-first-served) tanpa perlu menunggu penugasan manual dari Admin.
- **Pure Monitoring Admin:** Admin difokuskan 100% pada pemantauan data, statistik, manajemen akun, dan *soft-delete*, tanpa intervensi operasional penugasan tiket.
- **Realtime Polling:** Pembaruan komentar dan dukungan secara *near real-time* menggunakan teknik polling *Kotlin Coroutines* tanpa membebani server dengan WebSocket.
- **Upload Foto Bukti:** Pelapor dapat melampirkan 1 buah foto bukti (JPG/PNG, maks 5MB) pada setiap pelaporan aduan.

---

## 👥 Peran Pengguna (Roles)

Sistem ini mendukung 3 peran pengguna utama yang diatur melalui *JSON Web Token* (JWT):

1. **USER (Pelapor):** Dapat mendaftar secara mandiri, membuat aduan baru, melihat feed publik, memberikan dukungan aduan, serta berkomentar.
2. **AGEN (Petugas):** Memiliki akses melihat feed yang diurutkan berdasarkan *Most Liked*, mengklaim tugas secara linier, memperbarui status (`Baru` → `Diproses` → `Selesai`/`Ditutup`), dan memberikan catatan penanganan.
3. **ADMIN (Monitoring):** Memantau total aduan dan statistik, mengelola akun (membuat akun agen/admin, menonaktifkan akun), mengelola kategori, serta menghapus/memulihkan aduan (Trash).

---

## 🛠️ Tech Stack & Arsitektur

### Android App (Frontend)
- **Bahasa:** Kotlin
- **UI Toolkit:** Jetpack Compose (Material Design 3)
- **Arsitektur:** MVVM + Unidirectional Data Flow (UDF)
- **Networking:** *Tidak ada* (semua data disimpan secara lokal)
- **Persistensi Lokal:** Room + DataStore (Preferences) untuk sesi & token lokal
- **Asynchronous:** Kotlin Coroutines & StateFlow
- **Navigation:** Compose Type‑Safe Navigation, Scaffold, BottomNavigation
- **Image Loader:** Coil

---

## 🎨 UI & UX Design

Aplikasi ini menerapkan **Material Design 3 (M3)** dengan skema *Fresh & Clean UI*:
- **Adaptive Layout:** Layout dua kolom (`Row` + `weight`) untuk layar lebar (tablet), dan navigasi tersusun laci/drawer untuk perangkat *compact* (HP).
- **Infinite Scroll:** Menerapkan `LazyColumn` yang secara otomatis memuat (*load more*) halaman tiket selanjutnya.
- **Visual Status:** Tiket yang berstatus `Selesai` dan `Ditutup` akan selalu ditempatkan pada blok paling bawah feed aplikasi, ditandai dengan **Badge Centang Hijau** agar fokus pengguna tetap pada masalah yang masih aktif.
- **Feedback Instan:** Menggunakan *Snackbar* dan *Toast* untuk memberikan umpan balik aksi pengguna (berhasil *claim*, gagal *upload*, dll).

---

## 🚀 Cara Menjalankan Proyek (Android‑only)

### Prasyarat
- Android Studio (versi terbaru dengan dukungan Compose)
- JDK 11 atau lebih tinggi

### Langkah Menjalankan Aplikasi

1. **Clone Repository**
   ```bash
   git clone <repo-url>
   cd TiketBantu-Mobile
   ```
2. **Buka Proyek di Android Studio**
   - Pilih *Open an existing project* dan arahkan ke folder root.
3. **Sinkronisasi Gradle**
   - Klik *Sync Project with Gradle Files*.
4. **Jalankan Aplikasi**
   - Pilih emulator atau perangkat Android fisik, lalu klik *Run*.

---

---

## 📄 Lisensi & Hak Cipta
Dikembangkan untuk keperluan Tugas Akhir / Proyek Pemrograman Mobile.
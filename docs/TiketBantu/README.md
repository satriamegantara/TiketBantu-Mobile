# 🎫 TiketBantu Mobile App

**TiketBantu** adalah Sistem Helpdesk & Pengaduan Fasilitas Kampus berbasis **Android Native (Jetpack Compose)** dengan backend **Kotlin/Ktor**. Aplikasi ini dirancang untuk mendigitalisasi dan menyederhanakan proses pelaporan kerusakan fasilitas, penugasan teknisi, hingga pemantauan penyelesaian masalah secara *real-time* dan transparan.

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

Proyek ini dipisah menjadi dua repositori/modul utama: **Frontend (Android)** dan **Backend (REST API)**.

### Android App (Frontend)
- **Bahasa:** Kotlin
- **UI Toolkit:** Jetpack Compose (Material Design 3)
- **Arsitektur:** MVVM (Model-View-ViewModel) + Unidirectional Data Flow (UDF)
- **Networking:** Retrofit2, OkHttp3 (Interceptor untuk Bearer Token)
- **Asynchronous:** Kotlin Coroutines & StateFlow
- **Navigation:** Compose Type-Safe Navigation, Scaffold, BottomNavigation
- **Local Storage:** DataStore (Preferences) untuk sesi/JWT
- **Image Loader:** Coil

### Backend Server
- **Framework:** Ktor (Kotlin)
- **Database:** PostgreSQL
- **ORM:** JetBrains Exposed
- **Autentikasi:** Custom JWT (JSON Web Token)
- **Storage:** Local File System (untuk file *upload* gambar)

---

## 🎨 UI & UX Design

Aplikasi ini menerapkan **Material Design 3 (M3)** dengan skema *Fresh & Clean UI*:
- **Adaptive Layout:** Layout dua kolom (`Row` + `weight`) untuk layar lebar (tablet), dan navigasi tersusun laci/drawer untuk perangkat *compact* (HP).
- **Infinite Scroll:** Menerapkan `LazyColumn` yang secara otomatis memuat (*load more*) halaman tiket selanjutnya.
- **Visual Status:** Tiket yang berstatus `Selesai` dan `Ditutup` akan selalu ditempatkan pada blok paling bawah feed aplikasi, ditandai dengan **Badge Centang Hijau** agar fokus pengguna tetap pada masalah yang masih aktif.
- **Feedback Instan:** Menggunakan *Snackbar* dan *Toast* untuk memberikan umpan balik aksi pengguna (berhasil *claim*, gagal *upload*, dll).

---

## 🚀 Cara Menjalankan Proyek (Development)

### Prasyarat
- Android Studio (versi terbaru yang mendukung Compose)
- IntelliJ IDEA (untuk menjalankan Backend Ktor)
- PostgreSQL Server terinstal dan berjalan lokal

### 1. Setup Backend (Ktor)
1. Buka folder/repositori Backend.
2. Salin `.env.example` menjadi `.env` dan konfigurasikan kredensial PostgreSQL Anda.
3. Jalankan aplikasi (secara default akan berjalan di `http://localhost:8080`).
4. Eksekusi script *migration/seeder* yang tersedia untuk mengisi data kategori dan admin awal.

### 2. Setup Android (Frontend)
1. Buka folder proyek Android menggunakan Android Studio.
2. Buka file `gradle.properties` atau modul konfigurasi `Retrofit` dan ubah `BASE_URL` mengarah ke IP lokal komputer Anda (contoh: `http://192.168.1.x:8080`).
3. Lakukan *Sync Project with Gradle Files*.
4. *Build & Run* aplikasi pada Emulator atau perangkat Android fisik Anda.

---

## 📄 Lisensi & Hak Cipta
Dikembangkan untuk keperluan Tugas Akhir / Proyek Pemrograman Mobile.
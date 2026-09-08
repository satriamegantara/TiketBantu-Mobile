# FEATURES.md — Aplikasi Mobile e-Notulen (Android Jetpack Compose)

## 📋 Overview
Dokumen ini berisi daftar fitur aplikasi e-Notulen versi Mobile (Android Native) yang diadaptasi dari spesifikasi e-Notulen versi Web [cite: 1, 3]. Proyek ini dikembangkan secara khusus untuk memenuhi Tugas Akhir Pemrograman Mobile dengan menggunakan framework Jetpack Compose.

### 👥 Ketentuan Tim & Peran (Maksimal 4 Orang)
Pembagian peran dalam tim disesuaikan dengan arsitektur MVVM dan kebutuhan UI/UX:
- **UI/UX & Frontend (2 Orang):** Bertanggung jawab merancang antarmuka aplikasi dengan Material Design 3 (menggunakan palet warna Deep Teal dan Slate Blue) [cite: 5], menyusun navigasi multi-screen (minimal 3 layar) menggunakan Type-Safe Navigation [cite: 2], dan merender data ke dalam Lazy Layouts.
- **Backend Integration & State Management (2 Orang):** Bertanggung jawab menangani networking via Retrofit/Ktor untuk koneksi ke REST API (login JWT dan CRUD Notulen) [cite: 3], mengatur Unidirectional Data Flow (UDF), dan mengelola UiState di dalam ViewModel [cite: 3].

*Catatan: Koordinator kelas akan mengatur anggota kelompok, dan peserta dengan rekam jejak kurang baik akan ditempatkan di kelompok tersendiri sesuai instruksi.*

---

## 🛠️ Pemenuhan Requirement Teknis (Jetpack Compose)
Aplikasi mobile e-Notulen ini dirancang untuk mengimplementasikan materi Native Android berikut:

1. **UI & Layout Dasar:** Menggunakan `Column`, `Row`, `Box`, serta manipulasi `Modifiers` yang rapi pada seluruh halaman seperti Dashboard, Daftar Notulen, dan Detail Notulen [cite: 2].
2. **Material Design 3 (M3):** Penerapan tema aplikasi sesuai panduan desain (Color: Deep Teal, Typography: Plus Jakarta Sans) [cite: 5], serta penggunaan komponen interaktif seperti `Button`, `OutlinedTextField` untuk form pencarian/input, dan `Card` untuk item notulen [cite: 5].
3. **State Management & UDF:** Penerapan `remember`, `rememberSaveable` (untuk form input agar data tidak hilang saat rotasi), State Hoisting, dan konsep Unidirectional Data Flow (UDF) pada form pembuatan notulen [cite: 1, 2].
4. **Lazy Layouts:** Menampilkan koleksi data dinamis (Daftar Notulen dan Daftar Hadir) menggunakan `LazyColumn` beserta penanganan key parameter untuk performa yang optimal [cite: 1, 2].
5. **Networking & API:** Koneksi ke REST API Backend e-Notulen (menggunakan Retrofit atau Ktor) untuk proses autentikasi (Keycloak) dan manajemen data asinkron [cite: 3, 4].
6. **Arsitektur Aplikasi:** Penerapan pola MVVM dengan ViewModel untuk mengelola status tampilan secara reaktif (`UiState`: Loading, Success, Error) pada setiap halaman [cite: 3, 4].
7. **Navigation Compose:** Navigasi multi-screen berbasis Type-Safe Navigation yang mencakup layar Login, Dashboard, dan Form Notulen [cite: 2]. Dilengkapi integrasi `Scaffold` dan `BottomNavigation` untuk menu utama [cite: 2].

---

## 📱 Daftar Modul & Fitur (Mobile MVP)

### 1. 🔐 Modul Autentikasi
| # | Fitur | Prioritas | Deskripsi & Implementasi Compose |
|---|---|---|---|
| 1.1 | Login API | 🔴 P0 | Form login user untuk mendapatkan JWT Token dari Keycloak [cite: 1, 3]. Menggunakan `OutlinedTextField`, `Button`, dan penanganan `UiState` (Loading/Error) di ViewModel. |
| 1.2 | Logout | 🔴 P0 | Menghapus sesi JWT dan kembali ke halaman Login [cite: 1]. Menggunakan Navigation `popUpTo` agar backstack bersih. |

### 2. 📊 Modul Dashboard (Beranda)
| # | Fitur | Prioritas | Deskripsi & Implementasi Compose |
|---|---|---|---|
| 2.1 | Ringkasan Statistik | 🔴 P0 | Menampilkan total notulen, draft, dan final [cite: 1, 2]. Diimplementasikan menggunakan `Row`, `Column`, dan M3 `Card` [cite: 5]. |
| 2.2 | Notulen Terbaru | 🔴 P0 | Menampilkan daftar notulen terakhir yang dibuat [cite: 1, 2]. |
| 2.3 | Navigasi Utama | 🔴 P0 | Menu navigasi (Home, Daftar Notulen, Profil). Diimplementasikan dengan `Scaffold` dan `BottomNavigationBar` [cite: 2]. |

### 3. 📝 Modul Daftar & Pencarian Notulen
| # | Fitur | Prioritas | Deskripsi & Implementasi Compose |
|---|---|---|---|
| 3.1 | List View Notulen | 🔴 P0 | Menampilkan seluruh notulen dari API dengan paginasi [cite: 1]. Diimplementasikan menggunakan `LazyColumn` dengan *key parameter* [cite: 2]. |
| 3.2 | Pencarian Global | 🔴 P0 | Pencarian notulen berdasarkan judul rapat [cite: 1]. Menggunakan `OutlinedTextField` dengan State Hoisting untuk filter data. |
| 3.3 | Filter Status | 🟡 P1 | Filter notulen berdasarkan status Draft atau Final [cite: 1]. |

### 4. ✍️ Modul Detail & Manajemen Notulen
| # | Fitur | Prioritas | Deskripsi & Implementasi Compose |
|---|---|---|---|
| 4.1 | Detail Notulen | 🔴 P0 | Menampilkan informasi lengkap rapat secara *read-only* (metadata, daftar hadir, isi) [cite: 1, 2]. Transfer data antar layar menggunakan Type-Safe Navigation. |
| 4.2 | Buat Notulen Baru | 🔴 P0 | Form input metadata (judul, tanggal, waktu, tempat, dan isi ringkas) [cite: 1, 2]. State form dijaga dengan `rememberSaveable`. |
| 4.3 | Update Status | 🔴 P0 | Mengubah status notulen dari Draft menjadi Final [cite: 1]. Memanfaatkan MVVM `UiState` untuk menampilkan loading indicator. |

### 5. ⚙️ Modul Profil
| # | Fitur | Prioritas | Deskripsi & Implementasi Compose |
|---|---|---|---|
| 5.1 | Profil Pengguna | 🔴 P0 | Menampilkan nama, NIP, jabatan, dan OPD pengguna yang di-extract dari payload JWT [cite: 1, 3]. |

---
*Dokumen ini merupakan adaptasi sistem e-Notulen untuk memenuhi kriteria penilaian (rubrik) pemrograman mobile native Android dengan Jetpack Compose.*

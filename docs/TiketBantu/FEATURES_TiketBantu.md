# FEATURES.md — Daftar Fitur TiketBantu

## 📋 Overview

Dokumen ini berisi daftar lengkap fitur aplikasi **TiketBantu** (Sistem Helpdesk & Pengaduan Fasilitas Kampus), dikelompokkan berdasarkan modul dan prioritas pengembangan.

> **📝 Catatan Revisi (Sinkronisasi dengan ARCHITRCTURE.md)**
> Dokumen ini telah direvisi untuk menyelaraskan 2 hal berikut dengan `ARCHITRCTURE.md`:
> 1. Referensi backend dihapus; aplikasi kini **Android native tanpa backend** menggunakan **Room** untuk penyimpanan lokal dan **DataStore** untuk sesi.
> 2. Fitur lampiran tetap 1 foto per tiket, format JPG/PNG.

### Legenda Prioritas
| Label | Arti                              |
| ----- | ---------------------------------- |
| 🔴 P0 | Must Have — Fitur wajib, MVP      |
| 🟡 P1 | Should Have — Penting, post-MVP   |
| 🟢 P2 | Nice to Have — Enhancement, v2    |

### Legenda Role
| Kode | Role                                                  |
| ---- | ----------------------------------------------------- |
| A    | Admin (Monitoring & Pengelolaan Sistem)               |
| G    | Agen / Petugas (Teknisi Penanganan Aduan)             |
| U    | User / Pelapor Umum (Mahasiswa, Dosen, Civitas)       |

### Data Source
| Data          | Sumber                                                            |
| ------------- | -------------------------------------------------------------------- |
| User & Auth   | Local storage via Room `users` table; session stored in DataStore (no external auth) |
| Tiket & Stat  | Local Room tables `tickets`, `categories` |
| Dukungan      | Local Room table `ticket_supports` |
| Komentar      | Local Room table `comments` |
| Lampiran Foto | Local file storage in app internal directory (`filesDir/ticket_photos/{ticketId}.jpg`) |


---

## 1. 🔐 Modul Autentikasi & Manajemen Sesi

| #   | Fitur                                   | Prioritas | Role  | Deskripsi |
| --- | ---------------------------------------- | --------- | ----- | --------- |
| 1.1 | Login Multi-Role                        | 🔴 P0     | A/G/U | Form login terpusat untuk Admin, Agen, dan User Umum |
| 1.2 | Logout & Clear Session                  | 🔴 P0     | A/G/U | Logout aman dari aplikasi, membersihkan session & cookies |
| 1.3 | Session Management & Middleware Guard   | 🔴 P0     | A/G/U | Redirect otomatis jika session expired, membatasi akses rute sesuai role |
| 1.4 | User Context & Profile Bar              | 🔴 P0     | A/G/U | Menampilkan identitas user (nama, role, email, avatar) di header aplikasi |
| 1.5 | Register User Baru                      | 🔴 P0     | U     | Pendaftaran akun pelapor umum (Mahasiswa/Dosen/Civitas) |

---

## 2. 📊 Modul Dashboard (Tampilan 2 Kolom)

| #   | Fitur                                   | Prioritas | Role  | Deskripsi |
| --- | ---------------------------------------- | --------- | ----- | --------- |
| 2.1 | Layout 2 Kolom (Feed Utama & Sidebar)   | 🔴 P0     | A/G/U | Antarmuka 2 kolom: Kolom Kiri untuk Feed Aduan Publik, Kolom Kanan untuk Search Bar, Filter Status/Kategori/Most Liked & Statistik |
| 2.2 | Dashboard User (Public Feed)            | 🔴 P0     | U     | Pelapor dapat melihat seluruh aduan publik, melakukan pencarian, filter status, filter *Most Liked*, dan memberi dukungan |
| 2.3 | Dashboard Agen (Urutan Default Most Liked) | 🔴 P0  | G     | Tampilan default Agen menampilkan daftar aduan publik yang diurutkan berdasarkan jumlah dukungan terbanyak (*Most Liked*) |
| 2.4 | Dashboard Admin (Pure Monitoring)       | 🔴 P0     | A     | Halaman khusus monitoring total aduan, status penanganan, dan statistik per kategori tanpa fungsi assignment manual |
| 2.5 | Infinite Scroll Feed                    | 🔴 P0     | A/G/U | Muat konten aduan secara otomatis (*Infinite Scroll*) saat scroll ke bawah hingga aduan habis |
| 2.6 | Widget Aduan Selesai (Badge Centang Hijau) | 🔴 P0 | A/G/U | Aduan yang sudah `Selesai` otomatis ditampilkan di bagian paling bawah feed dengan indikator **Badge Centang Hijau (`✓ Selesai`)** |
| 2.7 | Widget Ringkasan Statistik              | 🟡 P1     | A/G/U | Menampilkan jumlah aduan baru, diproses, selesai, dan total pengguna terdampak |
| 2.8 | Quick Action Buttons                    | 🟡 P1     | A/G/U | Tombol pintas: Buat Aduan Baru, Lihat Aduan Saya, dan Filter Cepat |

---

## 3. 🎫 Modul Pengaduan & Tiket (Inti)

### 3.1 Manajemen Aduan & Penanganan Linear

| #     | Fitur                                 | Prioritas | Role  | Deskripsi |
| ----- | -------------------------------------- | --------- | ----- | --------- |
| 3.1.1 | Buat Aduan Baru                       | 🔴 P0     | U/A   | Form pelaporan permasalahan fasilitas kampus (100% Aduan Publik) |
| 3.1.2 | Claim Tugas Langsung (Linear Queue)   | 🔴 P0     | G     | Agen/Petugas dapat langsung mengambil (*claim*) aduan dari dashboard untuk langsung diproses tanpa menunggu tugas dari Admin |
| 3.1.3 | Edit Aduan (Kondisi Status Baru)      | 🔴 P0     | U/A   | Pelapor dapat mengedit judul/deskripsi selama status aduan masih `Baru` |
| 3.1.4 | Pembaruan Status Penanganan           | 🔴 P0     | G/A   | Agen mengubah status aduan secara linier (`Baru` → `Diproses` → `Selesai` / `Ditutup`) |
| 3.1.5 | Detail View Aduan Mode Read-Only      | 🔴 P0     | A/G/U | Halaman detail informasi aduan lengkap beserta riwayat penanganan |
| 3.1.6 | Hapus Aduan (Soft Delete)             | 🔴 P0     | A     | Admin dapat menghapus aduan yang tidak sesuai/duplikat ke trash |
| 3.1.7 | Penguncian Aduan Selesai/Ditutup      | 🔴 P0     | A/G/U | Tiket berstatus `Selesai` / `Ditutup` otomatis terkunci dari pengeditan lanjutan |

### 3.2 Metadata Aduan (100% Publik, Tanpa Prioritas)

| #     | Fitur                                 | Prioritas | Role  | Deskripsi |
| ----- | -------------------------------------- | --------- | ----- | --------- |
| 3.2.1 | Judul & Deskripsi Masalah             | 🔴 P0     | U/A   | Input judul dan rincian deskripsi lokasi/kerusakan fasilitas |
| 3.2.2 | Kategori Permasalahan                 | 🔴 P0     | U/A   | Pilihan kategori: Teknologi & IT, Fasilitas Ruangan, Infrastruktur Umum |
| 3.2.3 | Lokasi Spesifik                       | 🔴 P0     | U/A   | Input lokasi kampus (Gedung, Lantai, Ruangan) |
| 3.2.4 | Aduan 100% Publik                     | 🔴 P0     | U/A   | Seluruh aduan bersifat **Publik** sehingga dapat dilihat oleh semua civitas akademika |
| 3.2.5 | Urgensi Berdasarkan *Most Liked*      | 🔴 P0     | A/G/U | Skala prioritas manual (Low/Med/High) dihapus; tingkat urgensi otomatis ditentukan dari akumulasi dukungan (*Most Liked*) |

### 3.3 Dukungan Aduan ("Saya Juga Mengalami" / Most Liked)

| #     | Fitur                                 | Prioritas | Role  | Deskripsi |
| ----- | -------------------------------------- | --------- | ----- | --------- |
| 3.3.1 | Tombol "Saya Juga Mengalami"          | 🔴 P0     | U/G/A | Fitur satu-klik bagi pengguna lain untuk memberikan dukungan pada aduan publik yang sama |
| 3.3.2 | Counter Jumlah Pengguna Terdampak     | 🔴 P0     | A/G/U | Menampilkan angka akumulasi pengguna yang mengalami masalah tersebut secara real-time |
| 3.3.3 | Agregasi Laporan Duplikat             | 🔴 P0     | A/G/U | Mengurangi pembuatan tiket duplikat dengan mengalihkan user untuk melakukan dukungan |
| 3.3.4 | Pengurutan *Most Liked*               | 🔴 P0     | A/G/U | Fitur sorting feed aduan berdasarkan jumlah dukungan terbanyak |

### 3.4 Komentar Thread

| #     | Fitur                                       | Prioritas | Role  | Deskripsi |
| ----- | --------------------------------------------- | --------- | ----- | --------- |
| 3.4.1 | Thread Komentar                             | 🔴 P0     | A/G/U | Diskusi/tanya jawab antara Pelapor, Agen penanggung jawab, dan Admin |
| 3.4.2 | Polling Realtime Update (Kotlin Coroutines) | 🔴 P0     | A/G/U | Pesan komentar & counter dukungan diperbarui otomatis via polling berkala (± setiap 5 detik) tanpa perlu refresh manual |
| 3.4.3 | Catatan Penanganan Agen                     | 🟡 P1     | G/A   | Petugas dapat memberikan pembaruan progres teknis melalui komentar |

### 3.5 Bukti Foto Lampiran

| #     | Fitur                                 | Prioritas | Role  | Deskripsi |
| ----- | -------------------------------------- | --------- | ----- | --------- |
| 3.5.1 | Upload Foto Bukti (1 Foto)            | 🔴 P0     | U/A   | Unggah 1 foto kerusakan/bukti pendukung, format JPG atau PNG saja |
| 3.5.2 | Batasan Ukuran Maksimal 5MB           | 🔴 P0     | -     | Validasi lokal maksimal 5MB untuk foto yang dipilih/diambil |
| 3.5.3 | Preview Foto Lampiran                 | 🔴 P0     | A/G/U | Menampilkan foto (jika ada) pada halaman detail aduan |
| 3.5.4 | Image Compression & Storage           | 🟡 P1     | -     | Kompresi gambar dan penyimpanan pada direktori internal aplikasi (`filesDir/ticket_photos/{id}.jpg`) |

---

## 4. 🔍 Modul Pencarian, Filter & Infinite Scroll

| #   | Fitur                                   | Prioritas | Role  | Deskripsi |
| --- | ---------------------------------------- | --------- | ----- | --------- |
| 4.1 | Search Bar Terpusat                     | 🔴 P0     | A/G/U | Kotak pencarian aduan berdasarkan Kata Kunci Judul dan Deskripsi |
| 4.2 | Filter Berdasarkan Status               | 🔴 P0     | A/G/U | Menyaring aduan berdasarkan status penanganan (`Baru`, `Diproses`, `Selesai`, `Ditutup`) |
| 4.3 | Filter *Most Liked* (Dukungan Terbanyak)| 🔴 P0     | A/G/U | Menyaring dan menampilkan aduan dengan jumlah dukungan terbanyak di posisi teratas |
| 4.4 | Filter Berdasarkan Kategori             | 🔴 P0     | A/G/U | Menyaring aduan berdasarkan Kategori (IT, Ruangan, Umum) |
| 4.5 | Infinite Scroll Engine                  | 🔴 P0     | A/G/U | Memuat sisa daftar aduan secara perlahan saat pengguna melakukan scroll hingga aduan paling bawah |
| 4.6 | Urutan Aduan Selesai di Bagian Bawah    | 🔴 P0     | A/G/U | Logika khusus yang menempatkan aduan `Selesai` di posisi paling bawah feed dengan **Badge Centang Hijau (`✓ Selesai`)** |

---

## 5. 📈 Modul Monitoring & Statistik (Khusus Monitoring Admin)

> **Note**: Admin fokus 100% pada fungsi **Monitoring & Pengelolaan Sistem** tanpa terlibat dalam penugasan agen secara manual. SLA Tracker dan Modul Notifikasi tidak digunakan.

| #   | Fitur                                   | Prioritas | Role  | Deskripsi |
| --- | ---------------------------------------- | --------- | ----- | --------- |
| 5.1 | Monitoring Total & Status Aduan         | 🔴 P0     | A     | Monitoring visual akumulasi aduan baru, diproses, selesai, dan ditolak |
| 5.2 | Laporan Distribusi Per Kategori         | 🟡 P1     | A     | Grafik/tabel sebaran aduan fasilitas berdasarkan lokasi & kategori |
| 5.3 | Manajemen Akun Pengguna & Kategori      | 🔴 P0     | A     | Pengelolaan data master user (Admin, Agen, Pelapor) dan Kategori Fasilitas |

---

## 6. 🛠️ Modul Pengaturan & Sistem (Non-UI)

| #    | Fitur                                  | Prioritas | Deskripsi |
| ---- | --------------------------------------- | --------- | --------- |
| 6.1  | Database Migrations & Seeders          | 🔴 P0     | Room schema migrations and initial seed data (prepopulate tables) |
| 6.2  | Local Auth & Role Guard                | 🔴 P0     | Session & role management via DataStore token and ViewModel checks (no backend) |
| 6.3  | File Upload Security & Mimes Check     | 🔴 P0     | Validasi mime types foto lampiran (JPG, JPEG, PNG saja), maksimal 1 foto per tiket |
| 6.4  | Dynamic Badge UI Helpers               | 🔴 P0     | Helper rendering badge status dinamis (Termasuk Badge Centang Hijau untuk `Selesai`) |
| 6.5  | Realtime Polling Engine (Kotlin Coroutines) | 🟡 P1 | Local polling for comment & support counts within app (no external API) |
| 6.6  | Database Export / Backup                | 🟡 P1     | Export Room database to external storage for safety (optional) |

---

## 📊 Ringkasan Prioritas

| Prioritas | Jumlah Fitur | Fokus                          |
| --------- | ------------- | -------------------------------- |
| 🔴 P0     | ~26 fitur     | MVP — Core Helpdesk Functionality (Local Room & Compose) |
| 🟡 P1     | ~7 fitur      | Post-MVP — Enhancements & Local Analytics |

---

## 🗓️ Roadmap Pengembangan (Android Native — Jetpack Compose)

### Phase 1 — Core MVP (Android Local Standalone)
- Local Authentication & Multi-Role Guard (User, Agen, Admin via DataStore)
- Room Database Setup (Entities, DAOs, Database Pre-population / Seeder)
- 2-Column Responsive Dashboard Layout (Feed Utama + Sidebar / Drawer Search & Filter)
- Form Aduan Publik + Lampiran 1 Foto (Camera/Gallery picker, internal storage)
- Fitur Dukungan "Saya Juga Mengalami" (*Most Liked Sorting*)
- Linear Claim Ticket secara mandiri oleh Agen dari Feed
- Infinite Scroll Feed (`LazyColumn`) + Badge Centang Hijau (`✓ Selesai`) di posisi terbawah
- Monitoring Dashboard untuk Admin (Statistik agregat dari Room DB lokal)
- In-App Feedback (Snackbar & Toast)

### Phase 2 — UX Refinements & Advanced Local Features
- Image Compression sebelum disimpan ke direktori internal aplikasi
- Filter Kategori & Status interaktif dengan dynamic chips
- Export & Backup data Room ke JSON / SQLite file
- Animasi transisi antar-halaman menggunakan Jetpack Compose Animation

### Phase 3 — Future Enhancements (Jika Menghubungkan Backend di Masa Depan)
- Sinkronisasi Cloud / REST API Adapter
- Multi-device sync & Push Notifications


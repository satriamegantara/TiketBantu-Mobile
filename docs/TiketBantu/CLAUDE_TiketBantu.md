# CLAUDE.md — Aturan Utama & Pedoman Pengembangan TiketBantu Mobile

## 👥 Tim Pengembangan & Pembagian Peran (4 Orang)

| Peran | Nama | Fokus Utama | Dokumen Referensi |
|---|---|---|---|
| **Lead Architect & Core Infra** | Pancar | Arsitektur MVVM, Room DB, Navigation Graph & DI | [ARCHITECTURE_TiketBantu.md](file:///d:/Organize/Projects/Mobile%20Project/TiketBantu-Mobile/docs/TiketBantu/ARCHITECTURE_TiketBantu.md) |
| **UI/UX Designer & Compose Specialist** | Anggota 2 | Design System M3, Components & Layout Adaptif | [DESIGN_TiketBantu.md](file:///d:/Organize/Projects/Mobile%20Project/TiketBantu-Mobile/docs/TiketBantu/DESIGN_TiketBantu.md) |
| **Feature Engineer 1** | Satria | Feed, Most Liked, Detail Tiket & Coroutines Polling | [FEATURES_TiketBantu.md](file:///d:/Organize/Projects/Mobile%20Project/TiketBantu-Mobile/docs/TiketBantu/FEATURES_TiketBantu.md) |
| **Feature Engineer 2** | Anggota 4 | Auth Multi-Role, Form Aduan, Profil & Admin Monitoring | [APP_FLOW_TiketBantu.md](file:///d:/Organize/Projects/Mobile%20Project/TiketBantu-Mobile/docs/TiketBantu/APP_FLOW_TiketBantu.md) |

<!-- INTERNAL ONLY – dokumen ini menjadi acuan pedoman AI dan seluruh pengembang proyek -->

---

## 🎯 Tentang Proyek

**TiketBantu Mobile** adalah Sistem Helpdesk & Pengaduan Fasilitas Kampus publik berbasis **Android Native (Jetpack Compose)** dengan penyimpanan data permanen lokal menggunakan **Room Database** dan manajemen sesi lokal menggunakan **DataStore Preferences** (*Standalone Android / No Backend*).

Aplikasi menggantikan model birokrasi penanganan manual dengan sistem **Urgensi Publik (Most Liked)**, di mana aduan yang paling banyak didukung civitas akademika ("Saya Juga Mengalami") akan diprioritaskan di feed secara otomatis.

### Dokumen Sumber Kebenaran (Sources of Truth):
* `README.md` — Ringkasan Visi Proyek & Panduan Menjalankan Aplikasi
* `FEATURES_TiketBantu.md` — Spesifikasi Lengkap Modul & Prioritas Fitur
* `APP_FLOW_TiketBantu.md` — Alur Navigasi Antar-Layar & Route Graph
* `ARCHITECTURE_TiketBantu.md` — Arsitektur MVVM, Room SQLite Schema & State Flow
* `DESIGN_TiketBantu.md` — Spesifikasi Material 3 (M3), Tokens, & Layout Adaptif
* `Outline TiketBantu - 4 Anggota.md` — Master Timeline, Flowchart & Rincian Tugas

---

## 🏗️ Tech Stack

| Layer | Teknologi | Keterangan |
|---|---|---|
| **Platform** | Android Native (Kotlin) | Min SDK 24, Target SDK 34/35 |
| **UI Framework** | Jetpack Compose | Material Design 3 (M3), Compose Foundation, AnimatedVisibility |
| **Arsitektur** | MVVM + UDF | Unidirectional Data Flow, `StateFlow`, `UiState<T>` sealed interface |
| **Persistensi Data** | Room Database | SQLite lokal (`users`, `tickets`, `categories`, `ticket_supports`, `comments`) |
| **Manajemen Sesi** | Jetpack DataStore Preferences | Penyimpanan token sesi lokal & data role pengguna yang aktif |
| **Asynchronous & Polling** | Kotlin Coroutines & Flow | Coroutine Scope di ViewModel, Polling lokal (interval ±5 detik) |
| **Penyimpanan Foto** | App Internal Storage | Direktori privat aplikasi (`filesDir/ticket_photos/{id}.jpg`), 1 foto per tiket |
| **Image Loading** | Coil Compose | Memuat dan me-render foto lampiran dari file lokal |
| **Navigasi** | Jetpack Navigation Compose | Type-Safe Navigation dengan object route `@Serializable` |
| **Dependency Injection** | Hilt / Koin *(Lightweight)* | Inject Database, DAOs, Repositories, dan ViewModels |

---

## 📁 Struktur Repositori Android

```
TiketBantu-Mobile/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/tiketbantu/
│   │   │   ├── data/
│   │   │   │   ├── local/              # Room Database, TypeConverters, Pre-populate Callback
│   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   ├── dao/            # UserDao, TicketDao, CategoryDao, CommentDao, SupportDao
│   │   │   │   │   └── entity/         # UserEntity, TicketEntity, CategoryEntity, dll.
│   │   │   │   ├── preferences/        # DataStore Preferences (SessionManager)
│   │   │   │   └── repository/         # Implementasi Repository (UserRepository, TicketRepository)
│   │   │   ├── domain/
│   │   │   │   ├── model/              # Domain Models (User, Ticket, Category, Comment)
│   │   │   │   └── repository/         # Repository Interfaces
│   │   │   ├── ui/
│   │   │   │   ├── theme/              # Color.kt, Type.kt, Shape.kt, Theme.kt (Tokens M3)
│   │   │   │   ├── components/         # AppButton, AppTextField, TicketCard, StatusBadge, MostLikedBtn
│   │   │   │   ├── screens/            # Layar fitur utama:
│   │   │   │   │   ├── auth/           # LoginScreen, RegisterScreen, AuthViewModel
│   │   │   │   │   ├── feed/           # FeedScreen, FeedViewModel
│   │   │   │   │   ├── detail/         # TicketDetailScreen, DetailViewModel
│   │   │   │   │   ├── create/         # CreateTicketScreen, CreateTicketViewModel
│   │   │   │   │   ├── profile/        # ProfileScreen, MyTicketsScreen, ProfileViewModel
│   │   │   │   │   └── admin/          # MonitoringScreen, AdminViewModel
│   │   │   │   └── navigation/         # NavGraph.kt, AppDestination routes
│   │   │   ├── di/                     # AppModule.kt (DI Setup)
│   │   │   ├── base/                   # BaseViewModel.kt, UiState.kt
│   │   │   └── TiketBantuApp.kt        # Application class & MainActivity
│   │   └── res/                        # Vector assets, strings.xml, icons
│   └── build.gradle.kts
└── docs/TiketBantu/                    # Dokumentasi lengkap proyek
```

---

## 🔐 Matriks Akses Pengguna (3 Role)

| Fitur / Akses | Pelapor (User) | Agen (Petugas) | Admin |
|---|:---:|:---:|:---:|
| **Pendaftaran Mandiri (Register)** | ✅ (Default) | ❌ (Dibuat Admin) | ❌ (Dibuat Admin) |
| **Login Multi-Role** | ✅ | ✅ | ✅ |
| **Lihat Feed Publik & Search** | ✅ (Sort Default: Terbaru) | ✅ (Sort Default: Most Liked) | ✅ |
| **Dukungan "Saya Juga Mengalami"** | ✅ | ✅ | ✅ |
| **Buat Aduan Publik (+1 Foto)** | ✅ | ❌ | ✅ |
| **Linear Auto-Claim Tiket** | ❌ | ✅ (First-Come-First-Served) | ❌ |
| **Pembaruan Status Penanganan** | ❌ | ✅ (`Baru` → `Diproses` → `Selesai`/`Ditutup`) | ❌ |
| **Komentar pada Thread Aduan** | ✅ | ✅ | ✅ |
| **Pure Monitoring & Statistik** | ❌ | ❌ | ✅ |
| **Soft Delete & Kelola Kategori/Akun** | ❌ | ❌ | ✅ |

---

## 🗄️ Skema Room Database Lokal

```
UserEntity (tabel: "users")
├── id: Long (PK, autoGenerate = true)
├── name: String
├── email: String (UNIQUE)
├── nimNip: String?
├── passwordHash: String
├── role: String             -- PELAPOR | AGEN | ADMIN
└── isActive: Boolean

CategoryEntity (tabel: "categories")
├── id: Long (PK, autoGenerate = true)
└── name: String             -- Teknologi & IT | Fasilitas Ruangan | Infrastruktur Umum

TicketEntity (tabel: "tickets")
├── id: Long (PK, autoGenerate = true)
├── title: String
├── description: String
├── categoryId: Long (FK → categories.id)
├── locationBuilding: String
├── locationFloor: String
├── locationRoom: String
├── status: String           -- BARU | DIPROSES | SELESAI | DITUTUP
├── imageUrl: String?        -- Path file lokal di filesDir (bukan URL remote)
├── reporterId: Long (FK → users.id)
├── agentId: Long? (FK → users.id)
├── createdAt: Long
├── updatedAt: Long
└── deletedAt: Long?         -- Soft delete (Trash)

TicketSupportEntity (tabel: "ticket_supports")
├── id: Long (PK, autoGenerate = true)
├── ticketId: Long (FK → tickets.id)
├── userId: Long (FK → users.id)
└── UNIQUE(ticketId, userId) -- 1 user = 1 dukungan (Toggle)

CommentEntity (tabel: "comments")
├── id: Long (PK, autoGenerate = true)
├── ticketId: Long (FK → tickets.id)
├── userId: Long (FK → users.id)
├── content: String
└── createdAt: Long
```

---

## ⚙️ Aturan Pengembangan Kode (Development Rules)

### 1. General Rules
* **Bahasa Kode**: Seluruh nama variabel, fungsi, class, entity, DAO, dan commit message wajib menggunakan **Bahasa Inggris**.
* **Bahasa Antarmuka (UI)**: Seluruh label tombol, placeholder, judul layar, teks pesan, dan dialog wajib menggunakan **Bahasa Indonesia**.
* **Offline-First & Standalone**: Semua data disimpan di Room Database lokal dan sesi disimpan di DataStore Preferences. Tidak ada jaringan backend eksternal (no REST API / Ktor).

### 2. UI / Jetpack Compose Rules
* **State Hoisting & UDF**: UI Composable harus *stateless*. Seluruh state dikelola ViewModel dan di-expose menggunakan `StateFlow<UiState<T>>`.
* **Design Tokens M3**: Wajib memanfaatkan token M3 yang telah didefinisikan di `Theme.kt` dan `Color.kt`. Hindari *hardcoded colors*.
* **Bottom Navigation**:
  * Pelapor: Feed, Buat Aduan, Profil.
  * Agen: Feed, Profil.
  * Admin: Feed, Buat Aduan, Monitoring, Profil.
  * *Catatan*: Layar **"Aduan Saya"** dibuka dari dalam layar Profil, bukan sebagai tab navigasi utama tersendiri.
* **Urutan Feed**: Aduan aktif (`BARU`, `DIPROSES`) tampil di posisi atas. Aduan yang sudah `SELESAI` / `DITUTUP` **wajib selalu berada di blok paling bawah feed** dan diberi indikator **Badge Centang Hijau (`✓ Selesai`)**.
* **Feedback Pengguna**: Gunakan **Snackbar M3** atau **Toast**. Dilarang mengimplementasikan atau mensyaratkan Push Notification / Email.

### 3. Data & Storage Rules
* **Query Reaktif**: Gunakan Kotlin `Flow<List<Ticket>>` pada DAO agar UI otomatis ter-update saat data Room berubah.
* **Penyimpanan Foto**: Simpan file foto langsung ke direktori privat aplikasi menggunakan `context.filesDir.resolve("ticket_photos/{id}.jpg")`. Simpan path string-nya ke kolom `imageUrl` di `TicketEntity`.

---

## 🚫 Batasan Mutlak & Larangan Keras (Anti-Patterns)

1. **JANGAN membuat backend service terpisah** (Ktor, PostgreSQL, Laravel, Express, dsb.) atau menambahkan dependensi Retrofit/OkHttp untuk jaringan — proyek ini adalah aplikasi **Android Standalone lokal**.
2. **JANGAN menambahkan kolom manual priority** (`LOW`/`MEDIUM`/`HIGH`) pada tiket — tingkat urgensi murni ditentukan otomatis oleh jumlah dukungan civitas akademika (*Most Liked*).
3. **JANGAN membuat fitur penugasan tiket manual oleh Admin** — Agen mengambil tiket secara mandiri (*Linear Auto-Claim / First-Come-First-Served*).
4. **JANGAN membuat fitur upload multi-file atau format dokumen** (PDF, DOCX, ZIP) — lampiran dibatasi maksimal **1 foto** (JPG/PNG, ukuran maks. 5MB).
5. **JANGAN menambahkan modul Notifikasi Email, Push Notifications (FCM), atau SLA Tracker** — di luar cakupan sistem MVP.
6. **JANGAN menempatkan menu "Aduan Saya" sebagai item Bottom Navigation utama** — harus diakses melalui layar Profil pengguna.
7. **JANGAN menampilkan tiket yang berstatus `SELESAI` / `DITUTUP` bercampur acak di atas tiket aktif** — tiket selesai selalu dikelompokkan di bagian terbawah feed.

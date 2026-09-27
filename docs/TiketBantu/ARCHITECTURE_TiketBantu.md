# ARCHITECTURE.md — Arsitektur Sistem TiketBantu Mobile

**Proyek Tugas Pemrograman Mobile — Pertemuan ke-8 (UTS)**  
Adaptasi dari sistem web **TiketBantu** menjadi aplikasi **Android native** dengan backend berbasis **Kotlin/Ktor**.

**Sumber Kebenaran (Source of Truth)**: `FEATURES_TiketBantu.md` & `APP_FLOW_TiketBantu.md`.  
Dokumen arsitektur ini telah diselaraskan 100% dengan kedua dokumen tersebut — mencakup **3 role** (Admin, Agen/Petugas, Pelapor/User), **fitur Most Liked** (`ticket_supports`), **penghapusan field prioritas manual**, **pendaftaran akun pelapor (dengan NIM/NIP)**, **soft delete (trash)**, serta **lampiran foto tunggal (`image_url`)**.

---

## 📐 High-Level Architecture

```
┌────────────────────────────┐            ┌───────────────────────────┐
│    Android App (Kotlin)    │            │      Ktor Backend          │
│                            │            │                            │
│   Jetpack Compose UI       │   HTTPS    │   :8080                   │
│   ViewModel + UiState      │───────────▶│   REST API (JSON)         │
│   Retrofit + OkHttp        │◀───────────│                            │
│                            │            └──────────┬────────────────┘
└────────────────────────────┘                       │
                                                     ▼
                                        ┌────────────────────────────┐
                                        │   PostgreSQL / SQLite      │
                                        │   (Exposed ORM)            │
                                        └────────────────────────────┘
                                                     │
                                                     ▼
                                        ┌────────────────────────────┐
                                        │   Local File Storage       │
                                        │   /uploads/tickets/{id}/   │
                                        └────────────────────────────┘
```

Sistem terdiri dari dua bagian utama yang dikembangkan terpisah oleh tim:
1. **Android App** — Antarmuka Jetpack Compose untuk Pelapor, Petugas (Agen), dan Admin.
2. **Ktor Backend** — REST API yang menangani autentikasi & registrasi, data tiket aduan, dukungan (Most Liked), komentar, statistik monitoring, dan upload lampiran foto.

Tidak ada dependensi ke layanan eksternal (identity provider, object storage terpisah, dsb). Semua komponen dijalankan dalam satu backend service terpadu agar sesuai dengan skala dan batas waktu pengerjaan proyek.

---

## 🏛️ Backend Architecture (Layered)

```
┌──────────────────────────────────────────────────┐
│                   Route Layer                    │
│  Ktor Routing: /api/auth, /api/tickets,           │
│  /api/tickets/{id}/claim, /api/tickets/{id}/support,
│  /api/tickets/{id}/comments, /api/tickets/{id}/status,
│  /api/categories, /api/stats, /api/users, /api/trash │
└────────────────────┬─────────────────────────────┘
                     ▼
┌──────────────────────────────────────────────────┐
│                 Service Layer                    │
│  Business logic, validasi status transition linier │
│  (Baru → Diproses → Selesai / Ditutup),          │
│  cek role (Pelapor / Agen / Admin), klaim tiket   │
│  (first-come-first-served, tanpa assign Admin)     │
└────────────────────┬─────────────────────────────┘
                     ▼
┌──────────────────────────────────────────────────┐
│               Repository Layer                   │
│  TicketRepository, UserRepository, CommentRepo,   │
│  SupportRepository (Exposed ORM → PostgreSQL/SQLite)│
└──────────────────────────────────────────────────┘
```

### Layer Responsibilities

| Layer | Tanggung Jawab |
| ------ | ------ |
| **Route** | Terima HTTP request, parsing & validasi format input, serialisasi JSON response. |
| **Service** | Logika bisnis, verifikasi role & kepemilikan aduan, validasi alur status tiket linier, agregasi akumulasi dukungan (Most Liked). |
| **Repository** | Query database (Exposed ORM), manajemen file fisik foto lampiran, operasi *soft delete*. |
| **Model** | Entity domain, DTO request/response. |
| **Middleware** | Validasi JWT Bearer token, CORS, request logging. |

---

## 🔐 Autentikasi & Otorisasi

Autentikasi sederhana berbasis JWT yang dibuat sendiri oleh Ktor backend (tanpa *identity provider* eksternal). **Registrasi mandiri tersedia khusus untuk role PELAPOR (User)**, sedangkan akun Agen dan Admin dibuat secara khusus oleh Admin melalui menu Manajemen Pengguna.

```
1. User login (email + password) atau register (nama, email, nim_nip, password → role default PELAPOR)
2. Backend verifikasi / hash password (BCrypt)
3. Backend generate JWT berisi: sub (userId), role, name, exp
4. Android menyimpan token di DataStore
5. Setiap request lanjutan menyertakan header "Authorization: Bearer <token>"
6. Backend middleware melakukan validasi signature + expiry token
```

### Struktur Token Claims

```kotlin
data class TokenClaims(
    val sub: String,       // user id
    val role: String,      // "PELAPOR" | "AGEN" | "ADMIN"
    val name: String,
    val exp: Long
)
```

### Otorisasi Berdasarkan Role (3 Role — Sesuai FEATURES & APP_FLOW)

| Role | Hak Akses & Kewenangannya |
| ------ | ------ |
| **PELAPOR (User)** | Buat aduan publik (dengan opsi 1 foto lampiran JPG/PNG max 5MB), lihat seluruh *feed* aduan publik + pencarian/filter/pilihan urutan *Most Liked*, berikan dukungan *"Saya Juga Mengalami"*, edit judul & deskripsi aduan (khusus status Baru), serta menambah komentar. |
| **AGEN (Petugas)** | Semua akses Pelapor + **klaim tiket mandiri (*linear queue*, *first-come-first-served*)**, mengubah status linier (Baru → Diproses → Selesai / Ditutup), dan menambah catatan penanganan (komentar berlabel Petugas). |
| **ADMIN** | **Pure Monitoring & Pengelolaan Sistem**: Monitoring total & distribusi status aduan, statistik per kategori, **manajemen akun (buat/edit/nonaktifkan User/Agen)** & kategori, serta **soft delete / restore aduan duplikat/spam (Trash)**. ❌ **Tidak ada fitur assignment/penugasan agen manual**. |

Pengecekan otorisasi dilakukan secara terpusat di **Service Layer**. Admin secara eksplisit **tidak dapat** menugaskan agen secara manual, mengubah prioritas secara manual, atau mengedit isi deskripsi aduan pelapor.

---

## 🗄️ Skema Database (6 Tabel Utama)

Sesuai dengan spesifikasi `FEATURES.md` (3.5.1) dan `APP_FLOW.md` (2.1), lampiran foto disatukan langsung ke dalam kolom `image_url` pada tabel `Ticket` (tanpa tabel `Attachment` terpisah).

```
User
├── id (PK)
├── name
├── email (unique)
├── nim_nip          -- String (nullable / identitas sivitas akademika)
├── password_hash
├── role             -- PELAPOR | AGEN | ADMIN
└── is_active        -- status aktif akun (diatur oleh Admin)

Category
├── id (PK)
└── name             -- Teknologi & IT | Fasilitas Ruangan | Infrastruktur Umum

Ticket
├── id (PK)
├── title
├── description
├── category_id (FK → Category)
├── location_building   -- Lokasi Gedung
├── location_floor      -- Lantai
├── location_room       -- Ruangan
├── status           -- BARU | DIPROSES | SELESAI | DITUTUP
├── image_url        -- nullable (opsional, maks 1 foto JPG/PNG, max 5MB)
├── reporter_id (FK → User)
├── agent_id (FK → User, nullable)   -- diisi saat agen klaim tiket
├── created_at
├── updated_at
└── deleted_at       -- NULL = aktif; terisi = soft delete (Trash)
│
│   CATATAN: Kolom `priority` (RENDAH | SEDANG | TINGGI) DIHAPUS.
│   Urgensi aduan ditentukan OTOMATIS dari akumulasi dukungan (Most Liked).
│
└── (Urutan Feed: BARU/DIPROSES di atas; SELESAI/DITUTUP selalu di posisi
     paling bawah dengan badge centang hijau ✓ Selesai)

TicketSupport                -- Tabel dukungan "Saya Juga Mengalami" (Most Liked)
├── id (PK)
├── ticket_id (FK → Ticket)
├── user_id (FK → User)
├── created_at
└── UNIQUE (ticket_id, user_id)   -- 1 user = 1 dukungan (toggle on/off)

Comment                      -- Catatan penanganan & diskusi publik
├── id (PK)
├── ticket_id (FK → Ticket)
├── user_id (FK → User)
├── content
└── created_at
```

> **Catatan**: Modul SLA tracker, push notification, dan email notification **tidak digunakan / di luar scope**. Pembaruan data *realtime* di-handle dari sisi Android menggunakan **polling Coroutines (± 5 detik)**.

---

## 🌐 REST API Endpoints

| Method | Endpoint | Deskripsi | Akses Role |
| ------ | ------ | ------ | ------ |
| `POST` | `/api/auth/login` | Login user, mengembalikan token JWT. | Publik |
| `POST` | `/api/auth/register` | **Registrasi akun pelapor (nama, email, nim_nip, password → role default PELAPOR).** | Publik |
| `GET` | `/api/tickets` | List tiket publik + pagination + query filter (search, status, category, sort: `most_liked` / `terbaru`). Blok Selesai/Ditutup selalu di posisi bawah. | Pelapor / Agen / Admin |
| `GET` | `/api/tickets/{id}` | Detail tiket + daftar komentar + counter total dukungan. | Pelapor / Agen / Admin |
| `POST` | `/api/tickets` | Buat aduan publik baru (multipart: data teks + 1 foto lampiran opsional JPG/PNG, max 5MB). | Pelapor / Admin |
| `PATCH` | `/api/tickets/{id}` | Edit judul & deskripsi — hanya saat status Baru, hanya oleh pemilik aduan. | Pelapor |
| `POST` | `/api/tickets/{id}/claim` | **Agen klaim tiket (linear queue, first-come-first-served). Mengembalikan HTTP 409 jika sudah diklaim agen lain.** | Agen |
| `POST` | `/api/tickets/{id}/support` | **Dukungan "Saya Juga Mengalami" (toggle on/off; counter Most Liked tetap aktif meski tiket Selesai/Ditutup).** | Semua Role |
| `PATCH` | `/api/tickets/{id}/status` | Ubah status linier (Diproses → Selesai / Ditutup) + catatan penanganan opsional. | Agen / Admin |
| `POST` | `/api/tickets/{id}/comments` | Tambah komentar (dinonaktifkan / read-only saat tiket berstatus Selesai/Ditutup). | Pelapor / Agen / Admin |
| `GET` | `/api/categories` | List kategori (dropdown pilihan saat buat aduan). | Pelapor / Agen / Admin |
| `GET` | `/api/stats/monitoring` | **Monitoring Admin: Total & status aduan, distribusi per kategori, akumulasi pengguna terdampak.** | Admin |
| `GET/POST/PATCH` | `/api/users` | **Manajemen akun Admin: List akun, buat akun baru (Agen/Admin), edit, dan nonaktifkan akun.** | Admin |
| `DELETE` | `/api/tickets/{id}` | **Soft delete aduan duplikat/spam ke Trash.** | Admin |
| `GET` | `/api/trash` | **Daftar aduan yang ada di Trash.** | Admin |
| `POST` | `/api/trash/{id}/restore` | **Restore aduan dari Trash kembali ke status Baru.** | Admin |

### Mekanisme Utama Backend
* **Prinsip Linear Queue**: Agen **tidak menunggu instruksi atau assignment dari Admin**. Klaim dieksekusi langsung oleh agen. Backend mengunci baris data secara atomik (`WHERE status = 'BARU' AND agent_id IS NULL`) untuk mencegah *race condition*. Kegagalan klaim akibat konflik ganda akan mengembalikan **HTTP 409 Conflict**.
* **Agregasi Duplikat (Most Liked)**: Endpoint `POST /api/tickets/{id}/support` dengan constraint `UNIQUE(ticket_id, user_id)` mengimplementasikan sistem dukungan komunitas, mengarahkan pengguna untuk mendukung aduan yang sudah ada dibanding membuat tiket duplikat.

---

## 📷 Upload Lampiran Foto (Direct Upload)

Sesuai spesifikasi `FEATURES.md` (3.5.1), penanganan foto lampiran menggunakan sistem **Direct Upload** tunggal:

```
Jetpack Compose (PhotoPicker, maks 1 foto JPG/PNG, maks 5MB)
   │
   ▼
Retrofit Multipart POST /api/tickets
   │
   ▼
Ktor Backend terima file, validasi format (JPG/PNG) & ukuran (≤ 5MB)
   │
   ▼
File disimpan ke direktori lokal: /uploads/tickets/{ticket_id}/photo.jpg
   │
   ▼
Path/URL disimpan langsung ke kolom `Ticket.image_url`
   │
   ▼
Response JSON mengembalikan data tiket lengkap beserta URL foto
   │
   ▼
Android App memuat gambar menggunakan library Coil
```

Upload foto bersifat **opsional** — aduan tetap valid dan dapat dibuat tanpa melampirkan foto.

---

## 📱 Android App Architecture (MVVM + UDF)

```
┌─────────────────────────────────────┐
│           UI Layer (Compose)        │
│  Screen: Login, Register,           │
│  TicketList (Feed), TicketDetail,   │
│  CreateTicket, MyTickets, Profile,  │
│  Monitoring, UserManagement         │
│  ← collectAsState()                 │
│  → onEvent() / lambda               │
└───────────────────┬─────────────────┘
                    ▼
┌─────────────────────────────────────┐
│              ViewModel              │
│  StateFlow<UiState>                 │
│  sealed interface UiState {         │
│    Loading, Success(data), Error(msg)│
│  }                                  │
└───────────────────┬─────────────────┘
                    ▼
┌─────────────────────────────────────┐
│      Repository (interface)         │
│  TicketRepository, AuthRepository,  │
│  AdminRepository                     │
│  ├── Real*Repository (Retrofit)     │
│  └── Fake*Repository (dummy data)   │
└───────────────────┬─────────────────┘
                    ▼
┌─────────────────────────────────────┐
│        Retrofit + OkHttp            │
│  AuthInterceptor (attach Bearer token)│
│  HTTP 401 → clear token → LoginScreen│
└─────────────────────────────────────┘
```

### Structure & Package Layout

```
com.tiketbantu.mobile
├── data/
│   ├── model/          # TicketDto, UserDto, CommentDto, SupportDto, CategoryDto
│   ├── remote/         # ApiService, AuthInterceptor
│   └── repository/     # TicketRepository, AuthRepository, AdminRepository, Fake*Repository
├── domain/
│   └── model/          # Domain model (mapping dari DTO)
├── ui/
│   ├── theme/          # Color.kt, Type.kt, Theme.kt
│   ├── component/      # TicketCard, StatusBadge (✓ hijau utk Selesai), LoadingView, ErrorView
│   ├── screen/
│   │   ├── login/
│   │   ├── register/
│   │   ├── ticketlist/
│   │   ├── ticketdetail/
│   │   ├── createticket/
│   │   ├── mytickets/
│   │   ├── profile/
│   │   ├── monitoring/      # Admin only
│   │   └── usermanagement/  # Admin only
│   └── navigation/     # NavGraph, Screen routes (type-safe, nested graph Auth/Main/Admin)
└── MainActivity.kt
```

---

## 🧭 Navigation & Structure (Bottom Navigation per Role)

Sesuai dengan petunjuk di `APP_FLOW.md` (2.2), struktur navigasi menggunakan `Scaffold` + `BottomNavigation` yang disesuaikan berdasarkan *role* pengguna:

```
LoginScreen → RegisterScreen (Graph Auth, di luar BottomNavigation)

Scaffold + BottomNavigation (Graph Main)
├── Tab: 🏠 Feed Aduan  → TicketListScreen → TicketDetailScreen (arg: ticketId)
├── Tab: ➕ Buat Aduan  → CreateTicketScreen (Pelapor & Admin)
├── Tab: 📊 Monitoring  → MonitoringScreen (ADMIN saja)
└── Tab: 👤 Profil      → ProfileScreen (Daftar "Aduan Saya" diakses dari layar Profil)

Graph Admin (Diakses via Monitoring / Profil Admin)
├── UserManagementScreen
└── TrashScreen
```

### Akses Menu Bottom Navigation per Role

| Menu Bottom Bar | Pelapor (User) | Agen (Petugas) | Admin |
| ------ | :---: | :---: | :---: |
| 🏠 **Feed Aduan** | ✅ | ✅ | ✅ |
| ➕ **Buat Aduan** | ✅ | ❌ | ✅ |
| 📊 **Monitoring** | ❌ | ❌ | ✅ |
| 👤 **Profil** | ✅ | ✅ | ✅ |

* **Aduan Saya (My Tickets)**: Tidak menjadi tab mandiri di Bottom Bar, melainkan diakses melalui tombol/menu pada **ProfileScreen** (sesuai `APP_FLOW.md`).
* Navigasi menggunakan **Type-Safe Navigation Compose** (`@Serializable` route objects), termasuk pengiriman argumen `ticketId` ke `TicketDetailScreen`.

---

## 🚀 Deployment

```
┌───────────────┐     ┌──────────────────────────────┐
│   Internet    │────▶│  Railway / Render            │
│   (HTTPS)     │     │  ┌───────────┐  ┌────────────┐│
│               │     │  │  Ktor     │  │ PostgreSQL ││
│               │     │  │  Backend  │  │  Addon     ││
│               │     │  └───────────┘  └────────────┘│
└───────────────┘     └──────────────────────────────┘
```

* **Platform Hosting**: Railway atau Render (Free Tier, HTTPS otomatis).
* **Database**: PostgreSQL Addon bawaan platform hosting.
* **Storage Foto**: Penyimpanan disk lokal dalam container backend (memadai untuk skala proyek UTS & ukuran foto ≤ 5MB).

---

## 📈 Batasan & Simplifikasi Skala Proyek

Untuk menjaga efisiensi dan fokus pada pengerjaan proyek UTS (21 hari), beberapa fungsi disederhanakan:

| Aspek | Keputusan Arsitektur & Alasan |
| ------ | ------ |
| **Penyimpanan Lampiran** | Memakai 1 kolom `image_url` (JPG/PNG, max 5MB) langsung di tabel `Ticket`. Tanpa tabel `Attachment` terpisah atau S3/MinIO. |
| **SLA Tracker & Notifikasi** | Modul SLA dan notifikasi (Email/Push) ditiadakan. Feedback dikirim via UI **Snackbar/Toast**. |
| **Realtime Updates** | Menggunakan **Polling Coroutines (± 5 detik)** dari Android client, tanpa WebSocket. |
| **Assignment Agen** | Tanpa penugasan manual oleh Admin. Menggunakan sistem **Self-Claim (Linear Queue)**. |
| **Identity Provider** | Menggunakan sistem Auth JWT custom bawaan backend. |

---

## ✅ Pemenuhan Evaluasi Perkuliahan (UTS)

| No | Topik Perkuliahan | Implementasi dalam Arsitektur |
| :-: | ------ | ------ |
| 1 | **UI & Layout Dasar** | Penggunaan `Column`, `Row`, `Box`, dan `Modifier` pada Jetpack Compose; penataan layout responsif dua kolom. |
| 2 | **Material Design 3** | Penerapan M3 Theme (`Color.kt`, `Type.kt`), `OutlinedTextField`, `Card`, serta **Badge Centang Hijau (✓ Selesai)**. |
| 3 | **State Management & UDF** | Menggunakan `remember`, `StateFlow` di ViewModel, dan *Unidirectional Data Flow*. |
| 4 | **Lazy Layouts** | `LazyColumn` untuk feed aduan & komentar, dioptimalkan dengan `derivedStateOf` dan *infinite scroll*. |
| 5 | **Networking & REST API** | Integration Retrofit ke Ktor Backend, Coroutines, token interceptor, multipart photo upload, dan polling 5 detik. |
| 6 | **Arsitektur MVVM** | Pemisahan layer yang bersih dengan ViewModel, `sealed interface UiState` (Loading/Success/Error), dan Hilt Dependency Injection. |
| 7 | **Type-Safe Navigation** | Multi-screen navigation dengan `@Serializable` route, `Scaffold` + `BottomNavigation` dinamis per role. |

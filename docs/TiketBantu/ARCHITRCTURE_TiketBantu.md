# ARCHITECTURE.md — Arsitektur Sistem TiketBantu Mobile

> Proyek Tugas Pemrograman Mobile — Pertemuan ke-8 (UTS)
> Adaptasi dari sistem web **TiketBantu** menjadi aplikasi **Android native** dengan backend baru berbasis **Kotlin/Ktor**.
>
> **Sumber kebenaran (source of truth)**: `FEATURES_TiketBantu.md` & `APP_FLOW_TiketBantu.md`.
> Dokumen ini diselaraskan dengan kedua dokumen tersebut — mencakup **3 role** (Admin, Agen/Petugas, User/Pelapor),
> **fitur Most Liked** (tabel `ticket_supports`), **penghapusan field prioritas manual**, **endpoint register**,
> **soft delete (trash)**, serta tabel `attachments`.

---

## 📐 High-Level Architecture

```
┌────────────────────────────┐            ┌───────────────────────────┐
│    Android App (Kotlin)    │            │      Ktor Backend          │
│                            │            │                            │
│   Jetpack Compose UI       │   HTTPS    │   :8080                   │
│   ViewModel + UiState      │───────────▶│   REST API (JSON)         │
│   Retrofit + OkHttp        │◀───────────│                            │
│                            │            └──────────┬─────────────────┘
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

1. **Android App** — antarmuka Jetpack Compose untuk Pelapor, Petugas (Agen), dan Admin.
2. **Ktor Backend** — REST API yang menangani autentikasi & registrasi, data tiket, dukungan (Most Liked), komentar, statistik monitoring, dan upload lampiran.

Tidak ada dependensi ke layanan eksternal (identity provider, object storage terpisah, dsb). Semua komponen dijalankan dalam satu backend service agar sesuai skala dan waktu pengerjaan (21 hari).

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
│  cek role (User / Agen / Admin), klaim tiket      │
│  (first-come-first-served, tanpa assign Admin)     │
└────────────────────┬─────────────────────────────┘
                     ▼
┌──────────────────────────────────────────────────┐
│               Repository Layer                   │
│  TicketRepository, UserRepository, CommentRepo,   │
│  SupportRepository, AttachmentRepository          │
│  (Exposed ORM → PostgreSQL/SQLite)                │
└──────────────────────────────────────────────────┘
```

### Layer Responsibilities

| Layer          | Tanggung Jawab                                                        |
| -------------- | ---------------------------------------------------------------------- |
| **Route**      | Terima HTTP request, parsing & validasi format input, serialisasi JSON |
| **Service**    | Logika bisnis, cek role & kepemilikan, validasi alur status tiket linier, agregasi dukungan |
| **Repository** | Query database, operasi file (simpan gambar/dokumen), soft delete      |
| **Model**      | Entity domain, DTO request/response                                    |
| **Middleware** | Validasi JWT, CORS, logging request                                    |

---

## 🔐 Autentikasi & Otorisasi

Autentikasi sederhana berbasis JWT yang dibuat sendiri oleh backend (tanpa identity provider eksternal). **Registrasi tersedia untuk role User/Pelapor** (default), sementara akun Agen dan Admin dibuat oleh Admin melalui manajemen akun.

```
1. User login (email + password) atau register (nama, email, password → role default PELAPOR)
2. Backend cek / hash password (BCrypt)
3. Backend generate JWT berisi: sub (userId), role, exp
4. Android simpan token di DataStore
5. Setiap request lain: header "Authorization: Bearer <token>"
6. Backend middleware validasi signature + expiry token
```

### Struktur Token

```kotlin
data class TokenClaims(
    val sub: String,       // user id
    val role: String,      // "PELAPOR" | "AGEN" | "ADMIN"
    val name: String,
    val exp: Long
)
```

### Otorisasi berdasarkan Role (3 Role — disesuaikan dengan FEATURES & APP_FLOW)

| Role        | Akses                                                                                              |
| ----------- | -------------------------------------------------------------------------------------------------- |
| **PELAPOR (User)** | Buat tiket (publik), lihat seluruh feed aduan publik + search/filter/Most Liked, dukung "Saya Juga Mengalami", edit judul/deskripsi saat status `Baru`, komentar, unggah lampiran |
| **AGEN (Petugas)** | Semua akses Pelapor + **klaim tiket mandiri (linear queue, first-come-first-served)**, ubah status `Baru → Diproses → Selesai / Ditutup`, catatan penanganan (komentar berlabel petugas) |
| **ADMIN**   | **Pure Monitoring & pengelolaan sistem**: monitoring total/status aduan, statistik per kategori, **manajemen akun (buat/edit/nonaktifkan User/Agen)** & kategori, **soft delete / restore aduan (trash)**. ❌ **Tidak ada fitur assignment/penugasan agen** |

Pengecekan role dilakukan di **Service Layer**, bukan di Route — supaya logika otorisasi terpusat dan mudah diuji. Admin secara eksplisit **tidak dapat** menugaskan agen, mengatur prioritas manual, atau mengedit isi aduan pelapor.

---

## 🗄️ Skema Database (7 Tabel — disesuaikan dengan FEATURES & APP_FLOW)

```
User
├── id (PK)
├── name
├── email (unique)
├── password_hash
├── role             -- PELAPOR | AGEN | ADMIN
└── is_active        -- untuk nonaktifkan akun oleh Admin

Category
├── id (PK)
└── name             -- Teknologi & IT | Fasilitas Ruangan | Infrastruktur Umum

Ticket
├── id (PK)
├── title
├── description
├── category_id (FK → Category)
├── location_building   -- Gedung  (APP_FLOW: lokasi spesifik)
├── location_floor      -- Lantai
├── location_room       -- Ruangan
├── status           -- BARU | DIPROSES | SELESAI | DITUTUP
├── image_url        -- nullable (opsional, tetap boleh tanpa foto)
├── reporter_id (FK → User)
├── agent_id (FK → User, nullable)   -- diisi saat agen claim
├── created_at
├── updated_at
└── deleted_at       -- NULL = aktif; terisi = soft delete (trash)
│
│   CATATAN: kolom `priority` (RENDAH | SEDANG | TINGGI) DIHAPUS —
│   urgensi ditentukan OTOMATIS dari akumulasi dukungan (Most Liked).
│
└── (urutan feed: BARU/DIPROSES di atas; SELESAI/DITUTUP selalu di blok
     bawah dengan badge centang hijau ✓ Selesai)

TicketSupport                -- tabel dukungan "Saya Juga Mengalami" (Most Liked)
├── id (PK)
├── ticket_id (FK → Ticket)
├── user_id (FK → User)
├── created_at
└── UNIQUE (ticket_id, user_id)   -- 1 user = 1 dukungan (toggle on/off)

Comment
├── id (PK)
├── ticket_id (FK → Ticket)
├── user_id (FK → User)
├── content
└── created_at

Attachment                 -- lampiran dokumen & foto (bukan hanya 1 image_url)
├── id (PK)
├── ticket_id (FK → Ticket)
├── file_name
├── file_path            -- /uploads/tickets/{ticket_id}/{file_name}
├── file_mime            -- jpg, jpeg, png, pdf, doc, docx, zip, txt
├── file_size            -- max 5MB per file (divalidasi server)
└── created_at
```

Tidak ada tabel untuk SLA tracking atau push notification — di luar scope aplikasi ini. Realtime komentar & counter dukungan di-handle client-side dengan **polling Coroutines (± 5 detik)** dari Android, tanpa WebSocket.

---

## 🌐 REST API Endpoints (disesuaikan dengan FEATURES & APP_FLOW)

| Method | Endpoint                             | Deskripsi                                                    | Role           |
| ------ | ------------------------------------ | ------------------------------------------------------------ | -------------- |
| POST   | `/api/auth/login`                    | Login, dapatkan JWT                                          | Publik         |
| **POST** | **`/api/auth/register`**           | **Registrasi akun pelapor (nama, email, password → role default PELAPOR)** | **Publik**     |
| GET    | `/api/tickets`                       | List tiket publik + pagination (`page`, `per_page`) + query: `search`, `status`, `category`, `sort` (`most_liked` / `terbaru`); blok SELESAI/DITUTUP selalu di posisi bawah | Pelapor/Agen/Admin |
| GET    | `/api/tickets/{id}`                  | Detail tiket + komentar + lampiran + counter dukungan        | Pelapor/Agen/Admin |
| POST   | `/api/tickets`                       | Buat tiket baru **100% publik** (multipart: data + lampiran opsional, max 5MB/file) | Pelapor/Admin  |
| PATCH  | `/api/tickets/{id}`                  | Edit judul & deskripsi — hanya saat status `Baru`, hanya pelapor pemilik | Pelapor        |
| **POST** | **`/api/tickets/{id}/claim`**      | **Agen klaim tiket (linear queue, first-come-first-served). Gagal → 409 jika sudah di-claim agen lain** | **Agen**       |
| **POST** | **`/api/tickets/{id}/support`**    | **Dukungan "Saya Juga Mengalami" (toggle; counter Most Liked tetap aktif walau tiket Selesai/Ditutup)** | **Semua role** |
| PATCH  | `/api/tickets/{id}/status`           | Ubah status linier (`Diproses`, `Selesai`, `Ditutup`) + catatan penanganan opsional | Agen/Admin     |
| POST   | `/api/tickets/{id}/comments`         | Tambah komentar (dinonaktifkan/read-only saat `Selesai`/`Ditutup`) | Pelapor/Agen/Admin |
| GET    | `/api/categories`                    | List kategori (untuk dropdown buat tiket)                    | Pelapor/Agen/Admin |
| **GET** | **`/api/stats/monitoring`**         | **Monitoring Admin: total & status aduan, distribusi per kategori, jumlah pengguna terdampak** | **Admin**      |
| **GET/POST/PATCH** | **`/api/users`**        | **Manajemen akun Admin: list / buat (Agen, Admin) / edit / nonaktifkan** | **Admin**      |
| **DELETE** | **`/api/tickets/{id}`**          | **Soft delete aduan duplikat/spam → trash**                  | **Admin**      |
| **GET**  | **`/api/trash`**                   | **Daftar aduan di trash**                                    | **Admin**      |
| **POST** | **`/api/trash/{id}/restore`**      | **Restore aduan dari trash → status `Baru`**                 | **Admin**      |

Kontrak detail (contoh JSON request/response tiap endpoint) disepakati terpisah oleh tim di awal pengerjaan agar Android dan Backend dapat berjalan paralel.

**Prinsip Linear Queue (APP_FLOW Alur 4)**: Agen **tidak menunggu tugas dari Admin**. Klaim dieksekusi langsung oleh agen; server mengunci secara atomik (cek `status = BARU` AND `agent_id IS NULL`) agar dua agen tidak dapat meng-claim tiket yang sama — kegagalan kondisi balapan dikembalikan sebagai **HTTP 409 Conflict**.

**Agregasi duplikat**: endpoint `POST /api/tickets/{id}/support` dengan constraint `UNIQUE(ticket_id, user_id)` mengimplementasikan agregasi Most Liked — user diarahkan memberi dukungan pada aduan serupa alih-alih membuat tiket baru (FEATURES 3.3.3).

---

## 📷 Upload Lampiran — Direct Upload

Tidak menggunakan presigned URL / object storage terpisah — cukup satu kali request langsung ke backend.

```
Compose (photo/file picker, maks. 5MB per file)
   → Retrofit multipart POST /api/tickets
   → Ktor terima file, validasi mime (jpg, jpeg, png, pdf, doc, docx, zip, txt) & max 5MB
   → Simpan ke /uploads/tickets/{ticket_id}/{nama_file}
   → Path + metadata disimpan ke tabel `attachments`
   → Response mengembalikan full URL tiap lampiran
   → Android load gambar dengan Coil / preview dokumen
```

Upload lampiran bersifat **opsional** — pembuatan tiket tetap valid tanpa lampiran (FEATURES 3.5.1).

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
│  SupportRepository, AdminRepository │
│  ├── Real*Repository (Retrofit)     │
│  └── Fake*Repository (dummy data)   │
└───────────────────┬─────────────────┘
                    ▼
┌─────────────────────────────────────┐
│        Retrofit + OkHttp            │
│  AuthInterceptor (attach Bearer token)│
│  401 → clear token → navigate(Login)│
└─────────────────────────────────────┘
```

### Package Structure

```
com.tiketbantu.mobile
├── data/
│   ├── model/          # TicketDto, UserDto, CommentDto, SupportDto, AttachmentDto, CategoryDto
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

### State Hoisting & UDF

Komponen UI (`TicketCard`, `AppTextField`, dsb) dibuat **stateless** — menerima data via parameter dan mengirim aksi via lambda. State dikelola di level ViewModel atau di layer pemanggil, sesuai prinsip *Unidirectional Data Flow*.

---

## 🧭 Navigation (disesuaikan dengan APP_FLOW — BottomNavigation per Role)

```
LoginScreen → RegisterScreen (graph Auth, di luar BottomNavigation)

Scaffold + BottomNavigation (graph Main)
├── Tab: 🏠 Feed Aduan  → TicketListScreen → TicketDetailScreen (arg: ticketId)
├── Tab: ➕ Buat Aduan  → CreateTicketScreen
├── Tab: 📊 Monitoring  → MonitoringScreen          (ADMIN saja)
├── Tab: 👤 Profil      → ProfileScreen (MyTickets dapat diakses dari sini)

Graph Admin
└── UserManagementScreen → MonitoringScreen
```

| Menu Item     | User (Pelapor) | Agen (Petugas) | Admin |
| ------------- | :------------: | :------------: | :---: |
| 🏠 Feed Aduan | ✅             | ✅             | ✅    |
| ➕ Buat Aduan | ✅             | ❌             | ✅    |
| 📊 Monitoring | ❌             | ❌             | ✅    |
| 👤 Profil      | ✅             | ✅             | ✅    |

Navigasi antar layar menggunakan **type-safe navigation** (`@Serializable` route object), termasuk transfer parameter `ticketId` ke layar detail. Menu disusun dinamis berdasarkan role hasil login (JWT claims).

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

- **Platform**: Railway atau Render (free tier, HTTPS otomatis)
- **Database**: PostgreSQL addon dari platform yang sama
- **File storage**: disk lokal di container (cukup untuk skala tugas; lampiran kecil < 5MB)
- Tidak menggunakan reverse proxy custom, load balancer, atau monitoring tambahan — di luar kebutuhan skala proyek.

---

## 📈 Batasan & Simplifikasi Skala Tugas

Beberapa pola arsitektur enterprise **sengaja tidak diadopsi** karena tidak relevan dengan skala dan waktu pengerjaan tugas ini:

| Pola                                         | Alasan tidak dipakai                                       |
| -------------------------------------------- | ---------------------------------------------------------- |
| Identity provider eksternal (OIDC/Keycloak)  | Auth sendiri (JWT) sudah cukup untuk 3 role                |
| Object storage terpisah (S3/MinIO)           | Direct upload ke disk lokal lebih sederhana & cukup cepat  |
| Presigned URL upload                         | Satu tahap upload langsung lebih sesuai skala data kecil   |
| Full-text search (tsvector, GIN index)       | `LIKE` query cukup untuk volume data demo                  |
| WebSocket / push notification                | Polling Coroutines 5 detik + Snackbar cukup (realtime P2)  |
| Docker Compose multi-service                 | Satu service backend + DB addon sudah memadai              |
| Integrasi API eksternal (kepegawaian dsb.)   | Tidak ada kebutuhan data eksternal di TiketBantu Mobile    |

---

## ✅ Cakupan Materi Perkuliahan

| No | Materi                    | Implementasi di Arsitektur                                              |
| -- | ------------------------- | ----------------------------------------------------------------------- |
| 1  | UI & Layout Dasar         | Column/Row/Box + Modifier di seluruh layar Compose; layout 2 kolom `Row` + `weight` |
| 2  | Material Design 3         | `ui/theme/`, Buttons, OutlinedTextField, Cards, **Badge centang hijau ✓ Selesai** |
| 3  | State Management & UDF    | `remember`/`rememberSaveable`, state hoisting, StateFlow di ViewModel   |
| 4  | Lazy Layouts              | LazyColumn feed aduan & komentar dengan `key`, infinite scroll + `derivedStateOf` |
| 5  | Networking & API          | Retrofit ke Ktor backend, Coroutines, token interceptor, multipart upload, polling 5 detik |
| 6  | Arsitektur MVVM           | ViewModel + `sealed interface UiState` (Loading/Success/Error) + Hilt   |
| 7  | Navigation Compose        | Multi-screen type-safe navigation + BottomNavigation/Scaffold, nested graph Auth/Main/Admin |

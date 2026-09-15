# ARCHITECTURE.md — Arsitektur Sistem TiketBantu Mobile

> Proyek Tugas Pemrograman Mobile — Pertemuan ke-8 (UTS)
> Adaptasi dari sistem web **TiketBantu** (Laravel + Livewire) menjadi aplikasi **Android native** dengan backend baru berbasis **Kotlin/Ktor**.

---

## 📐 High-Level Architecture

```
┌────────────────────────────┐            ┌───────────────────────────┐
│    Android App (Kotlin)     │            │      Ktor Backend          │
│                              │            │                             │
│   Jetpack Compose UI        │   HTTPS    │   :8080                    │
│   ViewModel + UiState       │───────────▶│   REST API (JSON)          │
│   Retrofit + OkHttp         │◀───────────│                             │
│                              │            └──────────┬──────────────────┘
└──────────────────────────────┘                       │
                                                         ▼
                                          ┌────────────────────────────┐
                                          │   PostgreSQL / SQLite       │
                                          │   (Exposed ORM)             │
                                          └────────────────────────────┘
                                                         │
                                                         ▼
                                          ┌────────────────────────────┐
                                          │   Local File Storage        │
                                          │   /uploads/tickets/{id}/    │
                                          └────────────────────────────┘
```

Sistem terdiri dari dua bagian utama yang dikembangkan terpisah oleh tim:

1. **Android App** — antarmuka Jetpack Compose untuk Pelapor dan Petugas.
2. **Ktor Backend** — REST API yang menangani autentikasi, data tiket, komentar, dan upload gambar.

Tidak ada dependensi ke layanan eksternal (identity provider, object storage terpisah, dsb). Semua komponen dijalankan dalam satu backend service agar sesuai skala dan waktu pengerjaan (21 hari).

---

## 🏛️ Backend Architecture (Layered)

```
┌──────────────────────────────────────────────────┐
│                   Route Layer                      │
│  Ktor Routing: /api/auth, /api/tickets,            │
│  /api/tickets/{id}/comments, /api/categories        │
└────────────────────┬─────────────────────────────┘
                      ▼
┌──────────────────────────────────────────────────┐
│                 Service Layer                      │
│  Business logic, validasi status transition,        │
│  cek role (Pelapor vs Petugas), klaim tiket          │
└────────────────────┬─────────────────────────────┘
                      ▼
┌──────────────────────────────────────────────────┐
│               Repository Layer                     │
│  TicketRepository, UserRepository, CommentRepo      │
│  (Exposed ORM → PostgreSQL/SQLite)                   │
└──────────────────────────────────────────────────┘
```

### Layer Responsibilities

| Layer          | Tanggung Jawab                                                        |
| -------------- | ---------------------------------------------------------------------- |
| **Route**      | Terima HTTP request, parsing & validasi format input, serialisasi JSON |
| **Service**    | Logika bisnis, cek role & kepemilikan, validasi alur status tiket      |
| **Repository** | Query database, operasi file (simpan gambar)                          |
| **Model**      | Entity domain, DTO request/response                                   |
| **Middleware** | Validasi JWT, CORS, logging request                                   |

---

## 🔐 Autentikasi & Otorisasi

Autentikasi sederhana berbasis JWT yang dibuat sendiri oleh backend (tanpa identity provider eksternal).

```
1. User login (email + password)
2. Backend cek hash password (BCrypt)
3. Backend generate JWT berisi: sub (userId), role, exp
4. Android simpan token di DataStore
5. Setiap request lain: header "Authorization: Bearer <token>"
6. Backend middleware validasi signature + expiry token
```

### Struktur Token

```kotlin
data class TokenClaims(
    val sub: String,       // user id
    val role: String,      // "PELAPOR" atau "PETUGAS"
    val name: String,
    val exp: Long
)
```

### Otorisasi berdasarkan Role

| Role        | Akses                                                          |
| ----------- | ---------------------------------------------------------------- |
| **PELAPOR** | Buat tiket, lihat tiket miliknya, komentar di tiketnya sendiri  |
| **PETUGAS** | Lihat tiket miliknya + tiket belum di-assign, klaim tiket, ubah status, komentar |

Pengecekan role dilakukan di **Service Layer**, bukan di Route — supaya logika otorisasi terpusat dan mudah diuji.

---

## 🗄️ Skema Database (4 Tabel)

```
User
├── id (PK)
├── name
├── email (unique)
├── password_hash
└── role            -- PELAPOR | PETUGAS

Category
├── id (PK)
└── name

Ticket
├── id (PK)
├── title
├── description
├── category_id (FK → Category)
├── status          -- BARU | DIPROSES | SELESAI
├── priority         -- RENDAH | SEDANG | TINGGI
├── image_url        -- nullable
├── reporter_id (FK → User)
├── agent_id (FK → User, nullable)
├── created_at
└── updated_at

Comment
├── id (PK)
├── ticket_id (FK → Ticket)
├── user_id (FK → User)
├── content
└── created_at
```

Tidak ada tabel tambahan untuk SLA tracking, sorting order, atau notifikasi — di luar scope aplikasi ini.

---

## 🌐 REST API Endpoints

| Method | Endpoint                        | Deskripsi                                  | Role         |
| ------ | -------------------------------- | -------------------------------------------- | ------------ |
| POST   | `/api/auth/login`                | Login, dapatkan JWT                          | Publik       |
| GET    | `/api/tickets`                   | List tiket (query: `status`, `search`)       | Pelapor/Petugas |
| GET    | `/api/tickets/{id}`              | Detail tiket + komentar                      | Pelapor/Petugas |
| POST   | `/api/tickets`                   | Buat tiket baru (multipart: data + gambar)   | Pelapor      |
| PATCH  | `/api/tickets/{id}/status`       | Ubah status / klaim tiket                    | Petugas      |
| POST   | `/api/tickets/{id}/comments`     | Tambah komentar                              | Pelapor/Petugas |
| GET    | `/api/categories`                | List kategori (untuk dropdown buat tiket)    | Pelapor/Petugas |

Kontrak detail (contoh JSON request/response tiap endpoint) disepakati terpisah oleh tim di awal pengerjaan agar Android dan Backend dapat berjalan paralel.

---

## 📷 Upload Gambar — Direct Upload

Tidak menggunakan presigned URL / object storage terpisah — cukup satu kali request langsung ke backend.

```
Compose (image picker, max 1 foto)
   → Retrofit multipart POST /api/tickets
   → Ktor terima file, validasi (jpg/png, max 5MB)
   → Simpan ke /uploads/tickets/{ticket_id}/foto.jpg
   → Path disimpan ke kolom image_url
   → Response mengembalikan full URL gambar
   → Android load gambar dengan Coil
```

Upload gambar bersifat **opsional** — pembuatan tiket tetap valid tanpa lampiran foto.

---

## 📱 Android App Architecture (MVVM + UDF)

```
┌─────────────────────────────────────┐
│           UI Layer (Compose)          │
│  Screen: Login, TicketList,           │
│  TicketDetail, CreateTicket, Profile  │
│  ← collectAsState()                   │
│  → onEvent() / lambda                 │
└───────────────────┬───────────────────┘
                     ▼
┌─────────────────────────────────────┐
│              ViewModel                 │
│  StateFlow<UiState>                    │
│  sealed interface UiState {            │
│    Loading, Success(data), Error(msg)  │
│  }                                     │
└───────────────────┬───────────────────┘
                     ▼
┌─────────────────────────────────────┐
│      Repository (interface)            │
│  TicketRepository                      │
│  ├── RealTicketRepository (Retrofit)   │
│  └── FakeTicketRepository (dummy data) │
└───────────────────┬───────────────────┘
                     ▼
┌─────────────────────────────────────┐
│        Retrofit + OkHttp               │
│  AuthInterceptor (attach Bearer token) │
└─────────────────────────────────────┘
```

### Package Structure

```
com.tiketbantu.mobile
├── data/
│   ├── model/          # TicketDto, UserDto, CommentDto
│   ├── remote/          # ApiService, AuthInterceptor
│   └── repository/      # TicketRepository, FakeTicketRepository
├── domain/
│   └── model/           # Domain model (mapping dari DTO)
├── ui/
│   ├── theme/            # Color.kt, Type.kt, Theme.kt
│   ├── component/        # TicketCard, StatusBadge, LoadingView, ErrorView
│   ├── screen/
│   │   ├── login/
│   │   ├── ticketlist/
│   │   ├── ticketdetail/
│   │   ├── createticket/
│   │   └── profile/
│   └── navigation/        # NavGraph, Screen routes (type-safe)
└── MainActivity.kt
```

### State Hoisting & UDF

Komponen UI (`TicketCard`, `AppTextField`, dsb) dibuat **stateless** — menerima data via parameter dan mengirim aksi via lambda. State dikelola di level ViewModel atau di layer pemanggil, sesuai prinsip *Unidirectional Data Flow*.

---

## 🧭 Navigation

```
Scaffold + BottomNavigation
├── Tab: Daftar Tiket  → TicketListScreen → TicketDetailScreen (argumen: ticketId)
├── Tab: Buat Tiket    → CreateTicketScreen
└── Tab: Profil        → ProfileScreen

LoginScreen (di luar BottomNavigation, layar awal sebelum autentikasi)
```

Navigasi antar layar menggunakan **type-safe navigation** (`@Serializable` route object), termasuk transfer parameter `ticketId` ke layar detail.

---

## 🚀 Deployment

```
┌───────────────┐     ┌──────────────────────────────┐
│   Internet    │────▶│  Railway / Render              │
│   (HTTPS)     │     │  ┌───────────┐  ┌────────────┐ │
│               │     │  │  Ktor      │  │ PostgreSQL │ │
│               │     │  │  Backend   │  │  Addon     │ │
│               │     │  └───────────┘  └────────────┘ │
└───────────────┘     └──────────────────────────────┘
```

- **Platform**: Railway atau Render (free tier, HTTPS otomatis)
- **Database**: PostgreSQL addon dari platform yang sama
- **File storage**: disk lokal di container (cukup untuk skala tugas; foto tidak perlu persist jangka panjang)
- Tidak menggunakan reverse proxy custom, load balancer, atau monitoring tambahan — di luar kebutuhan skala proyek.

---

## 📈 Batasan & Simplifikasi Skala Tugas

Beberapa pola arsitektur enterprise **sengaja tidak diadopsi** karena tidak relevan dengan skala dan waktu pengerjaan tugas ini:

| Pola                                       | Alasan tidak dipakai                                   |
| -------------------------------------------- | ---------------------------------------------------------- |
| Identity provider eksternal (OIDC/Keycloak)  | Auth sendiri (JWT) sudah cukup untuk 2 role               |
| Object storage terpisah (S3/MinIO)           | Direct upload ke disk lokal lebih sederhana & cukup cepat |
| Presigned URL upload                         | Satu tahap upload langsung lebih sesuai skala data kecil   |
| Full-text search (tsvector, GIN index)       | `LIKE` query cukup untuk volume data demo                 |
| Auto-save & debounce                         | Tidak relevan — tiket bukan dokumen yang diketik berkelanjutan |
| Docker Compose multi-service                 | Satu service backend + DB addon sudah memadai              |
| Integrasi API eksternal (kepegawaian dsb.)   | Tidak ada kebutuhan data eksternal di TiketBantu Mobile     |

---

## ✅ Cakupan Materi Perkuliahan

| No | Materi                          | Implementasi di Arsitektur                                          |
| -- | ---------------------------------- | ----------------------------------------------------------------------- |
| 1  | UI & Layout Dasar                | Column/Row/Box + Modifier di seluruh layar Compose                      |
| 2  | Material Design 3                | `ui/theme/`, komponen Buttons, OutlinedTextField, Cards                 |
| 3  | State Management & UDF           | `remember`, state hoisting komponen, StateFlow di ViewModel             |
| 4  | Lazy Layouts                     | LazyColumn pada daftar tiket & komentar, dengan `key` parameter          |
| 5  | Networking & API                 | Retrofit ke Ktor backend, penanganan async via Coroutine                |
| 6  | Arsitektur MVVM                  | ViewModel + `sealed interface UiState` (Loading/Success/Error)          |
| 7  | Navigation Compose                | Multi-screen type-safe navigation + BottomNavigation/Scaffold           |
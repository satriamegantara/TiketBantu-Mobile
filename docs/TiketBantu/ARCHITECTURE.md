# ARCHITECTURE.md — Arsitektur Aplikasi Mobile e-Notulen (Jetpack Compose)

## 📐 High-Level Architecture
Arsitektur sistem ini mengadaptasi versi web [cite: 3], di mana *frontend* berbasis Next.js digantikan oleh Aplikasi Mobile Android Native. Aplikasi mobile berkomunikasi langsung dengan REST API Backend (Golang) yang sudah ada.

```text
┌─────────────────────────┐
│     Aplikasi Mobile     │
│   (Android Native)      │
│                         │
│  ┌───────────────────┐  │        HTTP/REST (JSON)          ┌──────────────────────┐
│  │ UI: Jetpack       │  │        Retrofit / Ktor           │                      │
│  │ Compose           │  │─────────────────────────────────▶│  Go API Backend      │
│  └───────────────────┘  │        + JWT Bearer Token        │  (Existing)          │
│  ┌───────────────────┐  │                                  │  :8080               │
│  │ State: MVVM       │  │◀─────────────────────────────────│                      │
│  │ (ViewModel/UDF)   │  │                                  └──────┬───────┬───────┘
│  └───────────────────┘  │                                         │       │
│  ┌───────────────────┐  │                                         │       │
│  │ Data: Retrofit/   │  │                                         ▼       ▼
│  │ DataStore         │  │                                 ┌─────────┐   ┌─────────┐
│  └───────────────────┘  │                                 │Postgre  │   │ MinIO   │
└─────────────────────────┘                                 │SQL (DB) │   │ (S3)    │
                                                            └─────────┘   └─────────┘
```

---

## 📱 Arsitektur Klien Mobile (MVVM & Clean Architecture)
Aplikasi Android ini dibangun menggunakan pola arsitektur **Model-View-ViewModel (MVVM)** dan prinsip *Unidirectional Data Flow* (UDF) untuk memisahkan logika bisnis dari antarmuka pengguna.

### 1. UI Layer (View)
- Dibangun sepenuhnya menggunakan **Jetpack Compose**.
- Hanya bertugas mengobservasi `UiState` dari ViewModel dan mengirimkan *UI Events* (seperti klik tombol atau input teks) ke ViewModel.
- Menerapkan *State Hoisting* agar komponen *composable* bersifat *stateless* dan dapat digunakan kembali (reusable).

### 2. Presentation Layer (ViewModel)
- Mengelola state aplikasi menggunakan `StateFlow` atau `MutableState`.
- Memetakan respons dari Data Layer menjadi `UiState` (contoh: `Loading`, `Success`, `Error`).
- Menyimpan *UI state* sementara, seperti *form input*, menggunakan `rememberSaveable` di level UI atau menyimpannya di ViewModel agar bertahan dari *configuration changes* (misal rotasi layar).

### 3. Data Layer (Repository & Data Source)
- **Remote Data Source:** Menggunakan **Retrofit** (atau Ktor) untuk melakukan panggilan HTTP ke endpoints REST API e-Notulen [cite: 3].
- **Local Data Source:** Menggunakan `DataStore` (Preferences) atau `EncryptedSharedPreferences` untuk menyimpan JWT Token (Access & Refresh Token) secara aman di perangkat.
- **Repository:** Menjembatani ViewModel dengan *data sources*, menangani logika *caching* sederhana jika diperlukan, dan mengembalikan aliran data (biasanya dibungkus dalam kelas *Resource* atau *Result*).

---

## 🔐 Manajemen Autentikasi (JWT & Interceptor)
Mekanisme autentikasi mengadaptasi alur Keycloak OIDC [cite: 3] ke ranah aplikasi seluler.

1. **Login & Penyimpanan Token:**
   - Aplikasi mengirimkan kredensial (username/password) ke endpoint `/auth/login` (atau berinteraksi dengan halaman Keycloak via Custom Tabs) [cite: 3].
   - Backend memvalidasi dan mengembalikan JWT Token (berisi claims seperti `sub`, `name`, `kode_unit`, `officer`, dll) [cite: 3].
   - Token disimpan secara lokal (EncryptedSharedPreferences).

2. **Retrofit Interceptor:**
   - Aplikasi mengimplementasikan `Interceptor` pada OkHttp (klien Retrofit).
   - Setiap permintaan (request) keluar ke endpoint API yang membutuhkan otorisasi secara otomatis disisipi `Authorization: Bearer <token>` pada *header* HTTP.
   - Jika menerima respons HTTP 401 (Unauthorized), sebuah *Authenticator* akan mencoba me-refresh token via endpoint `/auth/refresh` [cite: 3] atau melempar event *logout* jika sesi sudah benar-benar kedaluwarsa.

3. **Identity Resolution:**
   - Sama seperti backend [cite: 3], aplikasi mobile dapat melakukan dekode (tanpa memvalidasi *signature*) pada *payload* JWT Token untuk mengekstrak informasi dasar pengguna (Nama, NIP, Jabatan, Role) dan menampilkannya di halaman profil tanpa harus selalu melakukan request API ke `/auth/me`.

---

## 🔌 API Endpoints Integration
Aplikasi mobile akan mengonsumsi *endpoints* existing dari Backend Go [cite: 3]. Fokus implementasi mobile mencakup:

*   **Auth:** `/auth/login`, `/auth/refresh`, `/auth/logout` [cite: 3]
*   **Notulen:** `GET /notulen` (Daftar dengan parameter paginasi & pencarian), `POST /notulen` (Buat baru), `GET /notulen/:id` (Detail), `PATCH /notulen/:id/status` (Ubah status Draft/Final) [cite: 3]
*   **Profil/User:** Ekstraksi mandiri dari JWT, atau mengambil dari endpoint `/auth/me` [cite: 3]

---

## 📁 Struktur Paket Direktori (Android Project)

```text
com.pemkab.enotulen.mobile/
├── ui/                 # UI Layer (Jetpack Compose)
│   ├── theme/          # Color, Type, Shape, Theme definitions
│   ├── screens/        # Komponen layar utama (Login, Dashboard, NotulenList)
│   └── components/     # Reusable composables (Custom Button, Form, Cards)
├── viewmodel/          # Presentation Layer
│   ├── AuthViewModel.kt
│   └── NotulenViewModel.kt
├── data/               # Data Layer
│   ├── remote/         # Retrofit interfaces & API models (DTO)
│   ├── local/          # DataStore / Token preferences
│   └── repository/     # Repository implementations
├── di/                 # Dependency Injection (Hilt/Dagger modules)
└── utils/              # Helper, Constants, Extension functions
```

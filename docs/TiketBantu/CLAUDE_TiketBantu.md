### CLAUDE.md — Aturan Utama untuk AI & Pedoman Pengembangan TiketBantu Mobile
## 👥 Tim Pengembangan (4 orang)

| Peran | Nama | Fokus Utama | Dokumen Referensi |
|------|------|-------------|-------------------|
| **Backend Lead** | Anda | Arsitektur & Keamanan | [ANALYSIS_Compatibility.md](file:///C:/Users/hp_5c/.gemini/antigravity-ide/brain/306f8a85-861e-414d-a4dd-1c7c2452c708/ANALYSIS_Compatibility.md) |
| **Backend Engineer** | Satria | Service & Repository | Same as above |
| **Frontend Lead** | Anda | UI Architecture & State | Same as above |
| **UI/UX Designer** | (Orang ke‑2) | Design System & Interaksi | Same as above |

<!-- INTERNAL ONLY – this file is for team reference and AI tooling, not included in production bundles -->
#### 🎯 Tentang Proyek
**TiketBantu Mobile** adalah sistem helpdesk kampus publik dan transparan berbasis Android Native (**Jetpack Compose**) dengan backend **Kotlin/Ktor** dan **PostgreSQL**. Aplikasi ini menggantikan proses penanganan aduan sarana & prasarana kampus yang manual dengan model **urgensi publik (Most Liked)**, di mana aduan dengan dukungan terbanyak dari civitas akademika akan naik ke urutan teratas secara otomatis.

**Dokumen Sumber Kebenaran (Sources of Truth)**:
* `README.md` — Ringkasan & Visi Sistem
* `FEATURES_TiketBantu.md` — Spesifikasi Fitur & Kebutuhan Bisnis
* `APP_FLOW_TiketBantu.md` — Alur Pengguna & Spesifikasi Layar
* `ARCHITECTURE_TiketBantu.md` — Arsitektur Sistem, Database, & REST API
* `DESIGN_TiketBantu.md` — Design System Material 3 (M3) & Panduan UI/UX

--------------------------------------------------------------------------------

#### 🏗️ Tech Stack
| Layer | Teknologi | Keterangan |
| ------ | ------ | ------ |
| **Android Client** | Kotlin Native | Jetpack Compose, Material Design 3 (M3) |
| **Architecture (Client)** | MVVM + UDF | StateFlow, UiState (`Loading`, `Success`, `Error`), ViewModel |
| **Networking & Async** | Retrofit 2 + OkHttp 4 | AuthInterceptor (Bearer JWT), Coroutines & Flow |
| **Realtime Updates** | Coroutines Polling | Polling interval ±5 detik untuk feed & komentar |
| **Image Loading** | Coil | Memuat foto aduan dari backend |
| **Local Storage (Client)** | Android DataStore | Penyimpanan token JWT & sesi pengguna |
| **Backend API** | Kotlin / Ktor | REST API (JSON), Routing, Services, Repositories |
| **Database & ORM** | PostgreSQL + Exposed ORM | Custom SQL & DAO API |
| **Autentikasi** | Custom JWT Auth | Dikelola backend (tanpa Keycloak/Identity Provider eksternal) |
| **File Storage** | Local Disk Storage | `/uploads/tickets/{id}/` (1 foto JPG/PNG max 5MB per tiket) |

--------------------------------------------------------------------------------

#### 📁 Struktur Repositori (Monorepo)
```
tiketbantu/
├── android/                        # Aplikasi Android Native
│   ├── app/src/main/java/com/tiketbantu/mobile/
│   │   ├── data/                   # DTO, ApiService, AuthInterceptor, Repository Impl
│   │   ├── domain/                 # Domain Models & Business Rules
│   │   ├── ui/
│   │   │   ├── theme/              # Color.kt, Type.kt, Theme.kt (M3 Tokens)
│   │   │   ├── component/          # TicketCard, StatusBadge (✓ Selesai), AppTextField
│   │   │   ├── screen/             # Login, Register, Feed, Detail, Create, Profile, Monitoring
│   │   │   └── navigation/         # NavGraph, Route objects (@Serializable)
│   │   └── MainActivity.kt
│   └── build.gradle.kts
├── backend/                        # Ktor Backend Service
│   ├── src/main/kotlin/com/tiketbantu/backend/
│   │   ├── routes/                 # Ktor Routing (/api/auth, /api/tickets, /api/users, dll)
│   │   ├── services/               # Business logic & Linear Queue Status Validation
│   │   ├── repositories/           # Exposed ORM (PostgreSQL Query)
│   │   ├── models/                 # Entities & DTO Request/Response
│   │   └── Application.kt          # Main Entry & Ktor Modules
│   └── build.gradle.kts
├── docs/                           # Dokumentasi Spesifikasi & Arsitektur
│   ├── README.md
│   ├── FEATURES_TiketBantu.md
│   ├── APP_FLOW_TiketBantu.md
│   ├── ARCHITECTURE_TiketBantu.md
│   └── DESIGN_TiketBantu.md
└── CLAUDE.md                       # Pedoman Utama AI & Dev Rules
```

--------------------------------------------------------------------------------

#### 🔐 Role & Akses Pengguna (3 Role)
| Fitur / Akses | PELAPOR (User) | AGEN (Petugas) | ADMIN |
| ------ | :---: | :---: | :---: |
| **Registrasi Mandiri** | ✅ (Default) | ❌ (Dibuat Admin) | ❌ (Dibuat Admin) |
| **Buat Aduan Publik** | ✅ | ❌ | ✅ |
| **Lihat Feed & Search** | ✅ | ✅ | ✅ |
| **Dukungan "Saya Juga Mengalami"** | ✅ | ✅ | ✅ |
| **Klaim Tiket Mandiri (Auto-Claim)** | ❌ | ✅ (Linear Queue) | ❌ |
| **Ubah Status Aduan** | ❌ | ✅ (Baru → Diproses → Selesai/Ditutup) | ❌ |
| **Monitoring Dashboard & Stats** | ❌ | ❌ | ✅ (Pure Monitoring) |
| **Manajemen Akun (User/Agen)** | ❌ | ❌ | ✅ |
| **Soft Delete & Trash (Restore)** | ❌ | ❌ | ✅ |
| **Penugasan Agen Manual / Prioritas Manual** | ❌ (Dihapus) | ❌ (Dihapus) | ❌ (Dihapus) |

--------------------------------------------------------------------------------

#### 🗄️ Skema Database Utama (Exposed ORM / PostgreSQL)
```
User
├── id (PK, UUID/Int)
├── name (String)
├── email (String, UNIQUE)
├── nim_nip (String, Nullable)   -- Identitas Sivitas Akademika
├── password_hash (String)
├── role (String)               -- PELAPOR | AGEN | ADMIN
└── is_active (Boolean)         -- Status akun dari Admin

Category
├── id (PK)
└── name (String)               -- Teknologi & IT | Fasilitas Ruangan | Infrastruktur Umum

Ticket
├── id (PK)
├── title (String)
├── description (Text)
├── category_id (FK → Category)
├── location_building (String)  -- Gedung
├── location_floor (String)     -- Lantai
├── location_room (String)      -- Ruangan
├── status (String)             -- BARU | DIPROSES | SELESAI | DITUTUP
├── image_url (String, Nullable)-- 1 Foto Lampiran (JPG/PNG <= 5MB)
├── reporter_id (FK → User)
├── agent_id (FK → User, Nullable) -- Diisi saat Agen meng-claim
├── created_at (Timestamp)
├── updated_at (Timestamp)
└── deleted_at (Timestamp, Nullable) -- Soft delete (Trash)

TicketSupport (ticket_supports)  -- Dukungan "Saya Juga Mengalami" (Most Liked)
├── id (PK)
├── ticket_id (FK → Ticket)
├── user_id (FK → User)
├── created_at (Timestamp)
└── UNIQUE (ticket_id, user_id)  -- 1 User = 1 Support (Toggle)

Comment
├── id (PK)
├── ticket_id (FK → Ticket)
├── user_id (FK → User)
├── content (Text)
└── created_at (Timestamp)
```

--------------------------------------------------------------------------------

#### ⚙️ Aturan Pengembangan Kode (Development Rules)

##### General Rules
1. **Bahasa Kode**: Seluruh kode, DTO, nama variabel, komentar, dan commit message ditulis dalam **Bahasa Inggris**.
2. **Bahasa UI**: Seluruh teks antarmuka pengguna pada Android App ditulis dalam **Bahasa Indonesia**.
3. **No External Identity Provider**: Autentikasi dikelola oleh Ktor backend sendiri menggunakan JWT.
4. **Source of Truth Alignment**: Semua penambahan fitur harus mengacu pada kelima dokumen utama (`README`, `FEATURES`, `APP_FLOW`, `ARCHITECTURE`, `DESIGN`).

##### Android (Jetpack Compose) Rules
1. **MVVM + UDF**: Komponen UI harus *stateless*. State dikelola di ViewModel menggunakan `StateFlow<UiState>`.
2. **Type-Safe Navigation**: Gunakan route object bertipe (@Serializable) dengan Compose Navigation.
3. **BottomNavigation per Role**:
   * **Pelapor**: Feed, Buat Aduan, Profil.
   * **Agen**: Feed, Profil.
   * **Admin**: Feed, Buat Aduan, Monitoring, Profil.
   * *Catatan*: Layar "Aduan Saya" diakses dari dalam layar Profil, bukan sebagai tab navigasi utama.
4. **Urutan Feed Aduan**: Aduan aktif (`BARU`, `DIPROSES`) berada di atas (diurutkan berdasarkan *Most Liked* / *Terbaru*). Aduan selesai (`SELESAI`, `DITUTUP`) **selalu berada di blok paling bawah** dengan **Badge Centang Hijau (`✓ Selesai`)**.
5. **Feedback UI**: Gunakan **Material 3 Snackbar / Toast** untuk memberikan feedback sukses/gagal. Jangan gunakan/meminta Push Notification atau Email.

##### Backend (Ktor) Rules
1. **Clean Layered Architecture**: `Route` → `Service` → `Repository`.
2. **Linear Queue Atomicity**: Saat agen meng-claim tiket (`POST /api/tickets/{id}/claim`), Service harus mengunci tiket secara atomik (`status = 'BARU' AND agent_id IS NULL`). Jika sudah di-claim agen lain, kembalikan **HTTP 409 Conflict**.
3. **No Admin Assignment**: Admin TIDAK memunculkan tombol/fitur untuk menugaskan agen ke tiket. Penanganan tiket MURNI diklaim oleh agen (*first-come-first-served*).
4. **Validation**: Lampiran foto divalidasi server (MIME: `image/jpeg`, `image/png`, ukuran maks 5MB). Simpan ke `/uploads/tickets/{id}/` dan set kolom `image_url`.
5. **Polling Support**: Sediakan query parameter pencarian, filter status/kategori, serta pagination untuk mendukung polling coroutines dari Android client.

--------------------------------------------------------------------------------

#### 🚫 Dilarang keras (Anti-Patterns)
1. **JANGAN** menambah kolom `priority` manual (`LOW`/`MEDIUM`/`HIGH`) pada tabel `Ticket` — tingkat urgensi murni dihitung dari jumlah akumulasi dukungan `TicketSupport` (*Most Liked*).
2. **JANGAN** membuat fitur penugasan agen manual oleh Admin — Agen mengambil tiket secara mandiri (*Auto-Claim / Linear Queue*).
3. **JANGAN** menggunakan WebSocket, Livewire, atau Laravel — pembaruan *realtime* menggunakan polling Coroutines 5 detik.
4. **JANGAN** membuat tabel `Attachment` terpisah dengan multi-file/PDF/DOC/ZIP — lampiran dibatasi 1 foto (`image_url`) per tiket.
5. **JANGAN** menambahkan fitur Email Notification, Push Notification, atau SLA Tracker — di luar scope sistem MVP.
6. **JANGAN** meletakkan menu "Aduan Saya" sebagai tab Bottom Navigation utama.
7. **JANGAN** menampilkan aduan yang sudah `SELESAI` / `DITUTUP` di atas aduan yang sedang `BARU` / `DIPROSES` pada feed utama.

# CLAUDE.md — Aturan Utama untuk AI

## 🎯 Tentang Proyek

**e-Notulen** adalah aplikasi manajemen notulen rapat digital untuk Pemerintah Kabupaten Purbalingga. Aplikasi ini mendigitalisasi proses pembuatan, penyimpanan, pencarian, dan pengunduhan notulen rapat yang sebelumnya dilakukan secara manual (kertas). Digunakan oleh berbagai OPD (Organisasi Perangkat Daerah) dalam satu kabupaten.

---

## 🏗️ Tech Stack

| Layer             | Teknologi                |
| ----------------- | ------------------------ |
| Frontend          | Next.js (App Router)     |
| Styling           | Tailwind CSS             |
| Rich Text Editor  | Tiptap                   |
| Backend           | Go (Golang)              |
| Database          | PostgreSQL               |
| Authentication    | Keycloak (existing)      |
| Object Storage    | MinIO (S3-compatible)    |
| Containerization  | Docker                   |
| Orchestration     | Docker Compose           |

---

## 🔗 External Services (Existing Infrastructure)

| Service    | URL                                                              | Keterangan |
| ---------- | ---------------------------------------------------------------- | ---------- |
| Keycloak   | `https://accounts.purbalinggakab.go.id/auth/realms/apps`         | Realm: `apps`, Client: `e-notulen` |
| OPD API    | `https://api.pegawai.e-kinerja.purbalinggakab.go.id/v2/skpd/list?filter_pesona=true` | Data SKPD/OPD |

> **PENTING**: Keycloak dan API OPD sudah berjalan di infrastruktur Pemkab Purbalingga. Aplikasi e-Notulen TIDAK perlu deploy Keycloak sendiri. Cukup konfigurasi sebagai client di realm `apps`.

---

## 📁 Struktur Monorepo

```
e-notulen/
├── frontend/          # Next.js application
│   ├── src/
│   │   ├── app/       # App Router pages & layouts
│   │   ├── components/# Reusable UI components
│   │   ├── lib/       # Utilities, API client, helpers
│   │   ├── hooks/     # Custom React hooks
│   │   ├── types/     # TypeScript type definitions
│   │   └── styles/    # Global styles & Tailwind config
│   ├── public/        # Static assets
│   ├── tailwind.config.ts
│   ├── next.config.ts
│   └── package.json
├── backend/           # Go application
│   ├── cmd/
│   │   └── server/    # Main entry point
│   ├── internal/
│   │   ├── handler/   # HTTP handlers (controllers)
│   │   ├── service/   # Business logic layer
│   │   ├── repository/# Data access layer
│   │   ├── model/     # Domain models & DTOs
│   │   ├── middleware/ # Auth, CORS, logging middleware
│   │   └── config/    # Configuration loading
│   ├── pkg/           # Shared packages
│   │   ├── database/  # DB connection & migrations
│   │   ├── storage/   # MinIO/S3 client
│   │   ├── pdf/       # PDF generation
│   │   ├── keycloak/  # Keycloak JWT validation
│   │   └── validator/ # Input validation
│   ├── migrations/    # SQL migration files
│   ├── go.mod
│   └── go.sum
├── docker/            # Dockerfiles
│   ├── frontend.Dockerfile
│   └── backend.Dockerfile
├── docker-compose.yml
├── docker-compose.dev.yml
├── .env.example
├── CLAUDE.md
├── DESIGN.md
├── ARCHITECTURE.md
├── FEATURES.md
├── APP_FLOW.md
└── README.md
```

---

## 🔐 Authentication & User Data

### Keycloak Configuration
- **Realm**: `apps` (existing, shared across all Pemkab apps)
- **Client ID**: `e-notulen`
- **Issuer URL**: `https://accounts.purbalinggakab.go.id/auth/realms/apps`
- **Grant Type**: Authorization Code Flow (OIDC)

### Role Extraction from JWT
Role diambil dari `resource_access["e-notulen"].roles[0]`:
```json
{
  "resource_access": {
    "e-notulen": {
      "roles": ["admin"]   // atau "operator" atau "officer"
    }
  }
}
```

### User Identity per Role

| Field          | Admin (Super Admin)          | Operator (OPD)                          | Officer (Pegawai)                        |
| -------------- | ---------------------------- | --------------------------------------- | ---------------------------------------- |
| `sub`          | UUID Keycloak                | UUID Keycloak                           | UUID Keycloak                            |
| `name`         | "Super Admin E-Notulen"      | Nama OPD (e.g., "Dinas Komunikasi...")  | Nama pegawai (e.g., "RAFLI FIRDAUSY...") |
| `email`        | -                            | Email OPD                               | Email pegawai                            |
| `kode_unit`    | ❌ Tidak ada                  | ✅ "T8"                                  | ❌ Tidak ada (ada di `officer.unit.id`)   |
| `officer`      | `{}`                         | `{}`                                    | Objek lengkap (NIP, nama, unit, jabatan) |
| OPD identifier | Akses semua OPD              | `kode_unit`                             | `officer.unit.id`                        |

### TIDAK ADA Tabel `users` dan `opd` di Database
- **User data** → Langsung dari JWT token Keycloak
- **OPD data** → Dari API external SKPD (`/v2/skpd/list`)
- **Notulen ownership** → Disimpan via `created_by_sub` (UUID Keycloak) dan `opd_code` (kode_unit)

---

## ⚙️ Aturan Pengembangan

### General Rules
1. **Bahasa kode**: Seluruh kode, komentar, dan commit message ditulis dalam **Bahasa Inggris**.
2. **Bahasa UI**: Seluruh teks yang ditampilkan ke user di frontend ditulis dalam **Bahasa Indonesia**.
3. **Tidak ada hardcode**: Semua konfigurasi (URL, port, secrets) harus via environment variables.
4. **Error handling wajib**: Setiap error harus di-handle dengan proper, tidak boleh ada silent error.
5. **Logging**: Gunakan structured logging (JSON format) di backend.

### Frontend Rules
1. **App Router**: Gunakan Next.js App Router (bukan Pages Router).
2. **TypeScript**: Seluruh kode frontend HARUS TypeScript (`.ts` / `.tsx`), tidak boleh `.js` / `.jsx`.
3. **Server Components by default**: Gunakan React Server Components sebagai default. Client Components (`"use client"`) hanya jika diperlukan (interaksi, state, effects).
4. **Tailwind CSS**: Semua styling menggunakan Tailwind CSS. Tidak boleh ada inline style atau CSS modules kecuali sangat diperlukan.
5. **Tiptap Editor**: Gunakan `@tiptap/react` untuk rich text editor notulen. Implementasi auto-save dengan debounce (2 detik setelah user berhenti mengetik).
6. **API Client**: Buat centralized API client di `src/lib/api.ts` menggunakan `fetch` dengan proper error handling dan token management.
7. **Form Validation**: Gunakan `zod` untuk schema validation dan `react-hook-form` untuk form handling.
8. **State Management**: Gunakan React Context + `useReducer` untuk global state sederhana. Tidak perlu Redux/Zustand.
9. **Komponen reusable**: Semua komponen UI yang dipakai lebih dari 1 tempat harus dijadikan komponen reusable di `components/`.
10. **Loading & Error States**: Setiap halaman dan komponen yang fetch data HARUS punya loading state dan error state yang proper.

### Backend Rules
1. **Clean Architecture**: Ikuti layered architecture: Handler → Service → Repository.
2. **Handler** hanya bertanggung jawab untuk parsing request dan formatting response.
3. **Service** berisi semua business logic.
4. **Repository** berisi semua interaksi dengan database.
5. **Router**: Gunakan `chi` atau `gorilla/mux` sebagai HTTP router.
6. **Database**: Gunakan `pgx` sebagai PostgreSQL driver (bukan `database/sql` + `lib/pq`).
7. **Migrations**: Gunakan `golang-migrate` untuk database migrations. File migration di `backend/migrations/`.
8. **Naming Convention**:
   - File: `snake_case.go`
   - Package: `lowercase` (single word)
   - Struct/Interface: `PascalCase`
   - Function/Method: `PascalCase` (exported), `camelCase` (unexported)
   - Variable: `camelCase`
   - Constant: `PascalCase` atau `UPPER_SNAKE_CASE`
9. **API Response Format** (JSON):
   ```json
   {
     "success": true,
     "message": "Notulen berhasil dibuat",
     "data": { ... },
     "meta": {
       "page": 1,
       "per_page": 20,
       "total": 100,
       "total_pages": 5
     }
   }
   ```
10. **Error Response Format** (JSON):
    ```json
    {
      "success": false,
      "message": "Notulen tidak ditemukan",
      "error_code": "NOTULEN_NOT_FOUND",
      "errors": []
    }
    ```
11. **PDF Generation**: Gunakan library Go untuk generate PDF notulen sesuai template standar (termasuk kop surat, daftar hadir, isi, tanda tangan).
12. **File Upload**: Foto dokumentasi di-upload ke MinIO. Gunakan presigned URL untuk akses.
13. **External API**: Data OPD/SKPD diambil dari API Pemkab. Backend harus proxy request ini (jangan langsung dari frontend) dan cache hasilnya (TTL 1 jam).

### Database Rules
1. **UUID**: Gunakan UUID v7 sebagai primary key (sortable by time).
2. **Timestamps**: Semua tabel HARUS punya kolom `created_at` dan `updated_at` (timezone-aware: `TIMESTAMPTZ`).
3. **Soft Delete**: Gunakan kolom `deleted_at` untuk soft delete.
4. **Naming**: Tabel dan kolom menggunakan `snake_case`.
5. **Indexes**: Buat index pada kolom yang sering di-query (foreign keys, search fields).
6. **Full-Text Search**: Gunakan PostgreSQL `tsvector` dan `GIN index` untuk full-text search pada isi notulen.
7. **No FK to external data**: Kolom `opd_code` dan `created_by_sub` TIDAK pakai FK constraint karena data-nya di external system (Keycloak & API OPD).
8. **Migrations**: Setiap ada perubahan skema atau struktur database, SELALU gunakan file migration (`golang-migrate`), DILARANG keras mengubah langsung di database.

### Authentication Rules
1. **Keycloak**: Autentikasi via Keycloak realm `apps` yang sudah existing. Backend memvalidasi JWT token.
2. **Roles**: Role dibaca dari `resource_access["e-notulen"].roles` di JWT. Tiga role: `admin`, `operator`, `officer`.
3. **RBAC**: Setiap endpoint HARUS dicek role-nya di middleware.
4. **Token Refresh**: Frontend harus handle token refresh secara otomatis.
5. **OPD Scope**:
   - Admin: `kode_unit` kosong → akses semua OPD
   - Operator: OPD code dari `kode_unit` di JWT
   - Officer: OPD code dari `officer.unit.id` di JWT

### Docker Rules
1. **Multi-stage build**: Semua Dockerfile harus menggunakan multi-stage build untuk image size optimal.
2. **Non-root user**: Container harus berjalan sebagai non-root user.
3. **Health checks**: Setiap service harus punya health check endpoint.
4. **Environment**: Gunakan `.env` file untuk konfigurasi. Jangan commit `.env` (hanya `.env.example`).

---

## 🔐 Role & Permission Matrix

| Resource                  | Admin | Operator              | Officer               |
| ------------------------- | ----- | --------------------- | --------------------- |
| Melihat semua notulen     | ✅     | Hanya OPD sendiri     | Hanya milik sendiri   |
| Membuat notulen           | ✅     | ✅                     | ✅                     |
| Mengedit notulen          | ✅     | Hanya OPD sendiri     | Hanya milik sendiri   |
| Menghapus notulen         | ✅     | Hanya OPD sendiri     | Hanya milik sendiri   |
| Mengunduh notulen (PDF)   | ✅     | Hanya OPD sendiri     | Hanya milik sendiri   |
| Melihat rekap laporan     | ✅     | Hanya OPD sendiri     | ❌                     |
| Setting kop surat OPD     | ✅     | Hanya OPD sendiri     | ❌                     |
| Dashboard statistik       | ✅     | Hanya OPD sendiri     | Statistik sendiri     |

> **Note**: Tidak ada "Kelola OPD" dan "Kelola User" — OPD diambil dari API external, User dikelola via Keycloak Admin Console.

---

## 🧪 Testing Guidelines

1. **Backend**: Wajib ada unit test untuk layer Service. Gunakan `testing` package dan `testify`.
2. **Frontend**: Komponen kritis (editor, form) harus punya test. Gunakan `jest` + `@testing-library/react`.
3. **API Testing**: Sediakan file Postman/Insomnia collection untuk testing API.
4. **Coverage target**: Minimal 70% untuk backend service layer.

---

## 📝 Git Convention

### Commit Message (Conventional Commits)
```
feat: add notulen editor with auto-save
fix: fix PDF generation missing header logo
refactor: restructure repository layer
docs: update API documentation
chore: update dependencies
```

---

## 🚫 Jangan Lakukan

1. **JANGAN** commit secrets, API keys, atau credentials ke repository.
2. **JANGAN** gunakan `any` type di TypeScript. Selalu definisikan proper types.
3. **JANGAN** bypass RBAC/permission check.
4. **JANGAN** query database langsung dari handler. Selalu lewat service → repository.
5. **JANGAN** store file/foto di filesystem lokal. Selalu gunakan MinIO.
6. **JANGAN** hardcode teks Indonesia di komponen. Siapkan untuk i18n di masa depan (tapi implementasi i18n belum perlu sekarang).
7. **JANGAN** buat endpoint tanpa validasi input.
8. **JANGAN** return error detail/stack trace ke client di production.
9. **JANGAN** buat tabel `users` atau `opd` di database — data tersebut dari Keycloak dan API external.
10. **JANGAN** akses Keycloak Admin API dari aplikasi — user management dilakukan lewat Keycloak Admin Console.
11. **JANGAN** mengubah skema database secara manual. Selalu buat dan jalankan file migration.

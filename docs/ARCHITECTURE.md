# ARCHITECTURE.md — Arsitektur Aplikasi e-Notulen

## 📐 High-Level Architecture

```
┌──────────────────────────────────────────────────────────────────────────┐
│                          Docker Compose                                  │
│                                                                          │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────────────┐       │
│  │              │    │              │    │                      │       │
│  │  Next.js     │───▶│  Go API      │───▶│  PostgreSQL          │       │
│  │  Frontend    │    │  Backend     │    │  (App Database)      │       │
│  │  :3000       │    │  :8080       │    │  :5432               │       │
│  │              │    │              │    │                      │       │
│  └──────┬───────┘    └──────┬───────┘    └──────────────────────┘       │
│         │                   │                                           │
│         │                   │            ┌──────────────────────┐       │
│         │                   ├───────────▶│                      │       │
│         │                   │            │  MinIO               │       │
│         │                   │            │  (Object Storage)    │       │
│         │                   │            │  :9000 / :9001       │       │
│         │                   │            └──────────────────────┘       │
│         │                   │                                           │
└─────────┼───────────────────┼───────────────────────────────────────────┘
          │                   │
          │                   │        ┌──────────────────────────────┐
          │                   │        │  EXTERNAL SERVICES           │
          │                   │        │  (Infrastruktur Pemkab)      │
          │                   │        │                              │
          ▼                   ▼        │  ┌────────────────────────┐  │
   ┌──────────────┐    ┌───────────┐   │  │  Keycloak              │  │
   │  Keycloak    │    │  Proxy to │──▶│  │  accounts.purbalingga  │  │
   │  JS Adapter  │    │  OPD API  │   │  │  kab.go.id             │  │
   │  (OIDC)      │    │           │   │  │  Realm: apps           │  │
   └──────────────┘    └───────────┘   │  └────────────────────────┘  │
                                       │                              │
                                       │  ┌────────────────────────┐  │
                                       │  │  API SKPD              │  │
                                       │  │  api.pegawai.e-kinerja │  │
                                       │  │  .purbalinggakab.go.id │  │
                                       │  └────────────────────────┘  │
                                       │                              │
                                       └──────────────────────────────┘
```

---

## 🏛️ Backend Architecture (Clean Architecture)

```
┌──────────────────────────────────────────────────┐
│                   HTTP Layer                      │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐  │
│  │ Middleware  │  │  Router    │  │  Handler   │  │
│  │ (Auth,CORS │  │  (Chi)     │  │ (Request/  │  │
│  │  Logging)  │  │            │  │  Response) │  │
│  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘  │
│        │               │               │          │
└────────┼───────────────┼───────────────┼──────────┘
         │               │               │
         ▼               ▼               ▼
┌──────────────────────────────────────────────────┐
│                 Service Layer                     │
│  ┌──────────────────────────────────────────┐    │
│  │  Business Logic, Validation, RBAC check  │    │
│  │  PDF Generation, Search Processing       │    │
│  └──────────────────┬───────────────────────┘    │
└─────────────────────┼────────────────────────────┘
                      │
                      ▼
┌──────────────────────────────────────────────────┐
│               Repository Layer                    │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐  │
│  │ PostgreSQL │  │   MinIO    │  │  External  │  │
│  │   (pgx)   │  │   (S3)    │  │  API (OPD) │  │
│  └────────────┘  └────────────┘  └────────────┘  │
└──────────────────────────────────────────────────┘
```

### Layer Responsibilities

| Layer          | Responsibility                                                        |
| -------------- | --------------------------------------------------------------------- |
| **Handler**    | Parse HTTP request, validate input format, serialize response         |
| **Service**    | Business logic, authorization check, orchestration, PDF generation    |
| **Repository** | Database queries, S3 operations, external API calls                   |
| **Model**      | Domain entities, DTOs, request/response structs                       |
| **Middleware**  | JWT validation, CORS, request logging, rate limiting, error recovery |

---

## 🔐 Keycloak JWT Token Structure

### Cara Extract Informasi User dari JWT

```go
// Role: resource_access["e-notulen"].roles[0]
// Possible values: "admin", "operator", "officer"

type TokenClaims struct {
    Sub               string          `json:"sub"`
    Name              string          `json:"name"`
    Email             string          `json:"email"`
    PreferredUsername  string          `json:"preferred_username"`
    GivenName         string          `json:"given_name"`
    FamilyName        string          `json:"family_name"`
    KodeUnit          string          `json:"kode_unit,omitempty"`   // Operator only
    Officer           *OfficerClaims  `json:"officer,omitempty"`     // Officer only
    ResourceAccess    map[string]struct {
        Roles []string `json:"roles"`
    } `json:"resource_access"`
}

type OfficerClaims struct {
    NIP    string `json:"nip"`
    Nama   string `json:"nama"`
    GDP    string `json:"gdp"`      // Gelar depan
    GDB    string `json:"gdb"`      // Gelar belakang
    Jenkel struct {
        ID   string `json:"id"`
        Nama string `json:"nama"`
    } `json:"jenkel"`
    Unit struct {
        ID   string `json:"id"`     // Kode unit OPD (e.g., "T8")
        Nama string `json:"nama"`   // Nama OPD
    } `json:"unit"`
    Subunit struct {
        ID   string `json:"id"`
        Nama string `json:"nama"`
    } `json:"subunit"`
    Jenjab struct {
        ID   string `json:"id"`
        Nama string `json:"nama"`
    } `json:"jenjab"`
    Jab struct {
        ID   string `json:"id"`
        Nama string `json:"nama"`   // Jabatan (e.g., "PRANATA KOMPUTER AHLI PERTAMA")
    } `json:"jab"`
    Golru struct {
        ID   string `json:"id"`
        Nama string `json:"nama"`   // Golongan (e.g., "III/B - PENATA MUDA TK. I")
    } `json:"golru"`
    Photo string `json:"photo"`
}
```

### Identity Resolution Logic

```go
func ResolveUserContext(claims *TokenClaims) UserContext {
    role := claims.ResourceAccess["e-notulen"].Roles[0]

    switch role {
    case "admin":
        return UserContext{
            Sub:      claims.Sub,
            Name:     claims.Name,
            Role:     "admin",
            OPDCode:  "",              // Admin has no OPD scope → access all
            OPDName:  "",
        }
    case "operator":
        return UserContext{
            Sub:      claims.Sub,
            Name:     claims.Name,      // Nama OPD (e.g., "Dinas Komunikasi...")
            Role:     "operator",
            OPDCode:  claims.KodeUnit,  // e.g., "T8"
            OPDName:  claims.Name,
            Email:    claims.Email,
        }
    case "officer":
        return UserContext{
            Sub:      claims.Sub,
            Name:     claims.Officer.Nama,
            Role:     "officer",
            OPDCode:  claims.Officer.Unit.ID,   // e.g., "T8"
            OPDName:  claims.Officer.Unit.Nama,
            NIP:      claims.Officer.NIP,
            Position: claims.Officer.Jab.Nama,
            Email:    claims.Email,
        }
    }
}
```

---

## 🏢 External OPD/SKPD API

### Endpoint
```
GET https://api.pegawai.e-kinerja.purbalinggakab.go.id/v2/skpd/list?filter_pesona=true
```

### Response Format
```json
{
  "code": 200,
  "status": "OK",
  "data": [
    { "id": "02", "label": "SEKRETARIAT DAERAH", "value": "02" },
    { "id": "T8", "label": "DINAS KOMUNIKASI DAN INFORMATIKA", "value": "T8" },
    ...
  ]
}
```

### Caching Strategy
- Backend proxy endpoint: `GET /api/v1/opd`
- Cache di memory (in-process) dengan TTL **1 jam**
- Cache invalidation: manual via admin endpoint atau restart
- Fallback: jika API external down, gunakan cache terakhir

---

## 🗄️ Database Schema

### Entity Relationship Diagram

```
┌──────────────────┐       ┌──────────────────┐
│  opd_settings    │       │    notulen       │
├──────────────────┤       ├──────────────────┤
│ kode_unit (PK)   │◀──────│ opd_code         │
│ opd_name         │       │ id (PK, UUID)    │
│ logo_url         │       │ title            │
│ letterhead_config│       │ meeting_date     │
│ address          │       │ start_time       │
│ phone            │       │ end_time         │
│ email            │       │ location         │
│ created_at       │       │ led_by           │
│ updated_at       │       │ led_by_position  │
│                  │       │ led_by_nip       │
│                  │       │ notulist_name    │
│                  │       │ notulist_position│
│                  │       │ notulist_nip     │
│                  │       │ input_type       │
│                  │       │ content (JSONB)  │
│                  │       │ file_url         │
│                  │       │ file_name        │
│                  │       │ summary (TEXT)   │
│                  │       │ content_search   │
│                  │       │   (TSVECTOR)     │
│                  │       │ opd_code         │
│                  │       │ opd_name         │
│                  │       │ created_by_sub   │
│                  │       │ created_by_name  │
│                  │       │ status           │
│                  │       │ created_at       │
│                  │       │ updated_at       │
└──────────────────┘       │ deleted_at       │
                           └─────┬────────────┘
                                 │
                    ┌────────────┼────────────────┐
                    │            │                │
                    ▼            ▼                ▼
┌──────────────────┐ ┌────────────────┐ ┌────────────────┐
│  notulen_        │ │  notulen_      │ │  notulen_      │
│  attendees       │ │  attachments   │ │  auto_saves    │
├──────────────────┤ ├────────────────┤ ├────────────────┤
│ id (PK, UUID)    │ │ id (PK, UUID)  │ │ id (PK, UUID)  │
│ notulen_id (FK)  │ │ notulen_id (FK)│ │ notulen_id (FK)│
│ name             │ │ file_name      │ │ content (JSONB)│
│ institution      │ │ file_url       │ │ saved_by_sub   │
│ position         │ │ file_size      │ │ saved_by_name  │
│ sort_order       │ │ mime_type      │ │ created_at     │
│ created_at       │ │ sort_order     │ └────────────────┘
└──────────────────┘ │ created_at     │
                     └────────────────┘
```

### Table Details

#### `opd_settings` — Pengaturan Kop Surat per OPD
```sql
CREATE TABLE opd_settings (
    kode_unit           VARCHAR(20) PRIMARY KEY,        -- "T8", "02", etc. (dari JWT/API SKPD)
    opd_name            VARCHAR(255) NOT NULL,          -- Cached: "DINAS KOMUNIKASI DAN INFORMATIKA"
    logo_url            VARCHAR(512),                   -- URL logo di MinIO
    letterhead_config   JSONB NOT NULL DEFAULT '{}',    -- Konfigurasi kop surat
    address             TEXT,                           -- Alamat lengkap
    phone               VARCHAR(50),                   -- Nomor telepon
    email               VARCHAR(255),                  -- Email instansi
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- letterhead_config example:
-- {
--   "district_name": "PEMERINTAH KABUPATEN PURBALINGGA",
--   "font_size_title": 14,
--   "font_size_address": 10,
--   "show_phone": true,
--   "show_fax": true,
--   "show_email": true,
--   "fax": "(0281) 8902091"
-- }
```

#### `notulen` — Data Notulen Utama
```sql
CREATE TABLE notulen (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title             VARCHAR(500) NOT NULL,              -- Judul rapat
    meeting_date      DATE NOT NULL,                      -- Tanggal rapat
    start_time        TIME,                               -- Waktu mulai
    end_time          TIME,                               -- Waktu selesai (NULL = "selesai")
    location          VARCHAR(500),                       -- Tempat rapat
    led_by            VARCHAR(255),                       -- Nama pemimpin rapat
    led_by_position   VARCHAR(255),                       -- Jabatan pemimpin rapat
    led_by_nip        VARCHAR(30),                        -- NIP pemimpin rapat
    notulist_name     VARCHAR(255),                       -- Nama notulis
    notulist_position VARCHAR(255),                       -- Jabatan notulis
    notulist_nip      VARCHAR(30),                        -- NIP notulis
    input_type        VARCHAR(20) NOT NULL DEFAULT 'editor'
                      CHECK (input_type IN ('editor', 'upload')), -- 'editor' (Tiptap) atau 'upload' (File PDF/Docx)
    content           JSONB,                              -- Isi notulen jika input_type='editor'
    file_url          VARCHAR(512),                       -- URL file di MinIO jika input_type='upload'
    file_name         VARCHAR(255),                       -- Nama file asli jika input_type='upload'
    summary           TEXT,                               -- Plain text dari content (untuk search)
    content_search    TSVECTOR GENERATED ALWAYS AS (
                        to_tsvector('simple', COALESCE(title, '') || ' ' || COALESCE(summary, ''))
                      ) STORED,                           -- Full-text search vector
    opd_code          VARCHAR(20) NOT NULL,               -- Kode OPD (dari JWT: kode_unit / officer.unit.id)
    opd_name          VARCHAR(255) NOT NULL,              -- Nama OPD (cached saat create)
    created_by_sub    UUID NOT NULL,                      -- Keycloak `sub` UUID
    created_by_name   VARCHAR(255) NOT NULL,              -- Nama pembuat (cached dari JWT)
    status            VARCHAR(20) NOT NULL DEFAULT 'draft'
                      CHECK (status IN ('draft', 'final')),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at        TIMESTAMPTZ
);

-- Indexes
CREATE INDEX idx_notulen_opd_code ON notulen(opd_code);
CREATE INDEX idx_notulen_created_by_sub ON notulen(created_by_sub);
CREATE INDEX idx_notulen_meeting_date ON notulen(meeting_date);
CREATE INDEX idx_notulen_status ON notulen(status);
CREATE INDEX idx_notulen_content_search ON notulen USING GIN(content_search);
CREATE INDEX idx_notulen_deleted_at ON notulen(deleted_at) WHERE deleted_at IS NULL;
```

#### `notulen_attendees` — Daftar Hadir
```sql
CREATE TABLE notulen_attendees (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    notulen_id      UUID NOT NULL REFERENCES notulen(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,           -- Nama peserta
    institution     VARCHAR(255),                    -- Instansi/OPD asal
    position        VARCHAR(255),                    -- Jabatan (opsional)
    sort_order      INT NOT NULL DEFAULT 0,          -- Urutan tampil
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notulen_attendees_notulen_id ON notulen_attendees(notulen_id);
```

#### `notulen_attachments` — Foto Dokumentasi
```sql
CREATE TABLE notulen_attachments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    notulen_id      UUID NOT NULL REFERENCES notulen(id) ON DELETE CASCADE,
    file_name       VARCHAR(255) NOT NULL,           -- Nama file asli
    file_url        VARCHAR(512) NOT NULL,           -- Path di MinIO
    file_size       BIGINT NOT NULL DEFAULT 0,       -- Ukuran file (bytes)
    mime_type       VARCHAR(100) NOT NULL,           -- image/jpeg, image/png
    sort_order      INT NOT NULL DEFAULT 0,          -- Urutan tampil
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notulen_attachments_notulen_id ON notulen_attachments(notulen_id);
```

#### `notulen_auto_saves` — Auto-Save History
```sql
CREATE TABLE notulen_auto_saves (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    notulen_id      UUID NOT NULL REFERENCES notulen(id) ON DELETE CASCADE,
    content         JSONB NOT NULL,                  -- Snapshot konten Tiptap
    saved_by_sub    UUID NOT NULL,                   -- Keycloak `sub` UUID
    saved_by_name   VARCHAR(255) NOT NULL,           -- Nama user (cached)
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notulen_auto_saves_notulen_id ON notulen_auto_saves(notulen_id);

-- Auto cleanup: hanya simpan 20 auto-save terakhir per notulen
```

---

## 🔌 API Design

### Base URL
```
Production : https://e-notulen.purbalinggakab.go.id/api/v1
Development: http://localhost:8080/api/v1
```

### Authentication Endpoints
| Method | Endpoint           | Description              |
| ------ | ------------------ | ------------------------ |
| GET    | `/auth/login`      | Redirect to Keycloak login |
| GET    | `/auth/callback`   | Keycloak OIDC callback   |
| POST   | `/auth/refresh`    | Refresh access token     |
| POST   | `/auth/logout`     | Logout user              |
| GET    | `/auth/me`         | Get current user from JWT |

### Notulen Endpoints
| Method | Endpoint                              | Description                    | Role Access       |
| ------ | ------------------------------------- | ------------------------------ | ----------------- |
| GET    | `/notulen`                            | List notulen (paginated)       | All (filtered)    |
| POST   | `/notulen`                            | Create new notulen             | All               |
| GET    | `/notulen/:id`                        | Get notulen detail             | All (filtered)    |
| PUT    | `/notulen/:id`                        | Update notulen                 | All (filtered)    |
| DELETE | `/notulen/:id`                        | Soft delete notulen            | All (filtered)    |
| PATCH  | `/notulen/:id/status`                 | Update status (draft/final)    | All (filtered)    |
| POST   | `/notulen/:id/auto-save`              | Auto-save content              | All (filtered)    |
| GET    | `/notulen/:id/auto-saves`             | List auto-save history         | All (filtered)    |
| GET    | `/notulen/:id/download`               | Download notulen as PDF        | All (filtered)    |

### Attendee Endpoints
| Method | Endpoint                              | Description                    |
| ------ | ------------------------------------- | ------------------------------ |
| GET    | `/notulen/:id/attendees`              | List attendees                 |
| POST   | `/notulen/:id/attendees`              | Add attendee(s)                |
| PUT    | `/notulen/:id/attendees/:attendeeId`  | Update attendee                |
| DELETE | `/notulen/:id/attendees/:attendeeId`  | Remove attendee                |
| PUT    | `/notulen/:id/attendees/reorder`      | Reorder attendees              |

### Attachment Endpoints
| Method | Endpoint                                    | Description              |
| ------ | ------------------------------------------- | ------------------------ |
| GET    | `/notulen/:id/attachments`                  | List attachments         |
| POST   | `/notulen/:id/attachments`                  | Upload attachment(s)     |
| DELETE | `/notulen/:id/attachments/:attachmentId`    | Remove attachment        |
| PUT    | `/notulen/:id/attachments/reorder`          | Reorder attachments      |

### OPD Endpoints (Proxy + Settings)
| Method | Endpoint              | Description                              | Role Access    |
| ------ | --------------------- | ---------------------------------------- | -------------- |
| GET    | `/opd`                | List all OPD (proxy dari API SKPD, cached)| All           |
| GET    | `/opd/:kodeUnit/settings` | Get OPD settings (kop surat)          | Admin/Operator |
| PUT    | `/opd/:kodeUnit/settings` | Update OPD settings (kop surat)       | Admin/Operator |
| POST   | `/opd/:kodeUnit/logo`     | Upload OPD logo                       | Admin/Operator |

### Report Endpoints
| Method | Endpoint                   | Description                           | Role Access       |
| ------ | -------------------------- | ------------------------------------- | ----------------- |
| GET    | `/reports/summary`         | Dashboard summary statistics          | Admin/Operator    |
| GET    | `/reports/monthly`         | Monthly notulen count                 | Admin/Operator    |
| GET    | `/reports/opd-ranking`     | OPD ranking by notulen count          | Admin             |
| GET    | `/reports/activity`        | Recent activity log                   | Admin/Operator    |
| GET    | `/reports/export`          | Export report data to Excel           | Admin/Operator    |

### Search Endpoint
| Method | Endpoint     | Description                                  |
| ------ | ------------ | -------------------------------------------- |
| GET    | `/search`    | Full-text search across notulen (paginated)  |

**Query Parameters**: `q`, `opd_code`, `date_from`, `date_to`, `status`, `page`, `per_page`, `sort_by`, `sort_order`

### Health Check
| Method | Endpoint     | Description              |
| ------ | ------------ | ------------------------ |
| GET    | `/health`    | Service health status    |
| GET    | `/health/db` | Database connectivity    |

---

## 🔐 Authentication Flow

```
┌────────┐     ┌──────────┐     ┌───────────────────┐     ┌────────────┐
│  User  │     │ Next.js  │     │ Keycloak          │     │  Go API    │
│Browser │     │ Frontend │     │ accounts.purba... │     │  Backend   │
└───┬────┘     └────┬─────┘     └────────┬──────────┘     └─────┬──────┘
    │               │                     │                      │
    │  1. Access App│                     │                      │
    │──────────────▶│                     │                      │
    │               │                     │                      │
    │               │ 2. Redirect to      │                      │
    │               │    Keycloak OIDC     │                      │
    │◀──────────────│────────────────────▶│                      │
    │               │                     │                      │
    │  3. Login Form│                     │                      │
    │  (username/   │                     │                      │
    │   password)   │                     │                      │
    │──────────────────────────────────▶│                      │
    │               │                     │                      │
    │  4. Auth Code │                     │                      │
    │◀─────────────────────────────────│                      │
    │               │                     │                      │
    │  5. Code      │                     │                      │
    │──────────────▶│                     │                      │
    │               │ 6. Exchange          │                      │
    │               │    Code → Tokens     │                      │
    │               │────────────────────▶│                      │
    │               │                     │                      │
    │               │ 7. access_token +    │                      │
    │               │    refresh_token     │                      │
    │               │◀────────────────────│                      │
    │               │                     │                      │
    │  8. Set       │                     │                      │
    │  HttpOnly     │                     │                      │
    │  Cookie       │                     │                      │
    │◀──────────────│                     │                      │
    │               │                     │                      │
    │  9. API Call  │                     │                      │
    │──────────────▶│ 10. Forward         │                      │
    │               │    with Bearer JWT  │                      │
    │               │─────────────────────────────────────────▶│
    │               │                     │                      │
    │               │                     │  11. Validate JWT    │
    │               │                     │  (verify signature   │
    │               │                     │   via JWKS endpoint) │
    │               │                     │◀─────────────────────│
    │               │                     │                      │
    │               │ 12. Response        │                      │
    │◀──────────────│◀────────────────────────────────────────│
```

### Backend JWT Validation
Backend validates JWT token by:
1. Fetch JWKS from `https://accounts.purbalinggakab.go.id/auth/realms/apps/protocol/openid-connect/certs`
2. Verify token signature with public key
3. Check `exp` (expiry), `iss` (issuer), `azp` (authorized party = `e-notulen`)
4. Extract role from `resource_access["e-notulen"].roles`
5. Build `UserContext` based on role (see Identity Resolution Logic above)

---

## 📦 MinIO (Object Storage) Structure

```
e-notulen-bucket/
├── opd/
│   └── {kode_unit}/
│       └── logo/
│           └── logo.png              # Logo OPD untuk kop surat
├── notulen/
│   └── {notulen_id}/
│       └── attachments/
│           ├── foto-1.jpg            # Foto dokumentasi
│           ├── foto-2.jpg
│           └── foto-3.jpg
└── exports/
    └── {timestamp}/
        └── report.xlsx               # Temporary export files
```

### Upload Flow
1. Frontend request presigned upload URL dari Backend
2. Frontend upload langsung ke MinIO via presigned URL
3. Frontend notify Backend dengan file metadata
4. Backend simpan metadata ke database

### Download Flow
1. Frontend request presigned download URL dari Backend
2. Backend generate presigned URL (expire 1 jam)
3. Frontend redirect/fetch dari presigned URL

---

## 🔄 Auto-Save Mechanism

```
┌───────────────┐     ┌───────────────┐     ┌───────────────┐
│  Tiptap       │     │  Debounce     │     │  API Call     │
│  Editor       │────▶│  (2 seconds)  │────▶│  POST         │
│  onChange     │     │               │     │  /auto-save   │
└───────────────┘     └───────────────┘     └───────┬───────┘
                                                     │
                                                     ▼
                                            ┌───────────────┐
                                            │  PostgreSQL   │
                                            │  notulen_     │
                                            │  auto_saves   │
                                            └───────────────┘
```

### Auto-Save Rules
1. **Debounce**: 2 detik setelah user berhenti mengetik
2. **Throttle**: Maksimal 1 save per 5 detik
3. **History**: Simpan 20 snapshot terakhir, lebih lama auto-cleanup
4. **Conflict**: Jika ada error saat save, retry 3x dengan exponential backoff
5. **Indicator**: UI menampilkan status save (Tersimpan / Menyimpan... / Gagal)
6. **Content**: Simpan seluruh Tiptap JSON document, bukan diff

---

## 📊 Full-Text Search Architecture

```
┌─────────────┐     ┌──────────────┐     ┌──────────────────┐
│  Search     │     │  Go API      │     │  PostgreSQL      │
│  Query      │────▶│  Parse &     │────▶│  to_tsquery()    │
│  (Frontend) │     │  Sanitize    │     │  + GIN Index     │
└─────────────┘     └──────────────┘     └────────┬─────────┘
                                                   │
                                                   ▼
                                         ┌──────────────────┐
                                         │  ts_rank() +     │
                                         │  ts_headline()   │
                                         │  Ranked Results  │
                                         └──────────────────┘
```

### Search Strategy
1. **`content_search` column**: TSVECTOR dari judul + summary (plain text content)
2. **GIN Index**: Untuk fast lookup pada `content_search`
3. **Bahasa**: Gunakan `simple` text search configuration (karena `indonesian` config tidak available by default di PostgreSQL)
4. **Ranking**: `ts_rank_cd()` untuk relevancy ranking
5. **Highlight**: `ts_headline()` untuk menampilkan snippet dengan highlight
6. **Filters**: Kombinasi full-text search dengan filter OPD, tanggal, status

---

## 🐳 Docker Compose Services

```yaml
services:
  frontend:       # Next.js app (port 3000)
  backend:        # Go API (port 8080)
  postgres:       # PostgreSQL for app data (port 5432)
  minio:          # MinIO Object Storage (port 9000, console 9001)
```

> **Note**: Keycloak TIDAK di-deploy via Docker Compose karena sudah running di infrastruktur Pemkab (`accounts.purbalinggakab.go.id`).

### Service Dependencies
```
frontend ──▶ backend ──▶ postgres
                    ──▶ minio
                    ──▶ keycloak (external)
                    ──▶ API SKPD (external)
```

### Network
- Semua service dalam satu Docker network: `e-notulen-network`
- Hanya `frontend` (3000) dan `minio console` (9001) yang di-expose ke host di development
- Di production, semua di-belakang reverse proxy (Nginx)

### Volumes
```
postgres-data:     # PostgreSQL data persistence
minio-data:        # MinIO object storage persistence
```

---

## 📈 Performance Considerations

1. **Database Connection Pool**: Backend menggunakan connection pool (pgx pool) — min 5, max 25 connections
2. **Pagination**: Semua list endpoint menggunakan offset pagination (default 20 items)
3. **Image Optimization**: Foto dokumentasi di-resize saat upload (max 1920px width, JPEG quality 85%)
4. **OPD API Cache**: Cache response API SKPD di memory, TTL 1 jam
5. **Lazy Loading**: Foto dokumentasi di frontend di-lazy load
6. **Gzip**: Backend response di-gzip compress via middleware
7. **PDF Generation**: Dilakukan secara synchronous (notulen biasanya 1-5 halaman, cukup cepat)

---

## 🛡️ Security Considerations

1. **Input Sanitization**: Semua input di-sanitize sebelum disimpan (XSS prevention)
2. **SQL Injection**: Gunakan parameterized queries (pgx default behavior)
3. **CORS**: Whitelist domain frontend saja
4. **Rate Limiting**: Implementasi rate limit pada endpoint upload dan search
5. **File Validation**: Validasi mime type dan ukuran file saat upload (max 10MB per foto)
6. **JWT Validation**: Validate signature via JWKS, check expiry, issuer, dan authorized party
7. **HTTPS**: Wajib HTTPS di production (terminate di reverse proxy)
8. **Helmet Headers**: Security headers (X-Frame-Options, CSP, dll) via Next.js config

---

## 🚀 Deployment Architecture (Production)

```
┌───────────────┐     ┌───────────────┐     ┌────────────────────────┐
│   Internet    │────▶│  Nginx/       │────▶│  Docker Compose        │
│   (HTTPS)     │     │  Reverse      │     │  (frontend, backend,   │
│               │     │  Proxy        │     │   postgres, minio)     │
└───────────────┘     └───────────────┘     └────────────────────────┘
                                                      │
                                                      ▼
                                            ┌────────────────────────┐
                                            │  External Services     │
                                            │  - Keycloak (Pemkab)   │
                                            │  - API SKPD (Pemkab)   │
                                            └────────────────────────┘
```

- **Reverse Proxy**: Nginx di depan untuk SSL termination, static caching, dan routing
- **SSL**: Let's Encrypt / certificate dari pemerintah
- **Backup**: PostgreSQL auto backup daily (pg_dump)
- **Monitoring**: Health check endpoints + basic logging

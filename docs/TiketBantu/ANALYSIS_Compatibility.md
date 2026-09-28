# Analisis Kesesuaian Dokumentasi dengan **Ketentuan Project (aspek teknis)**

| No | Aspek Teknis | Ditemukan di Dokumen | ✔ Kesesuaian | Catatan Singkat |
|----|--------------|----------------------|--------------|-----------------|
| 1 | **UI & Layout Dasar** – `Column`, `Row`, `Box`, Modifiers | **APP_FLOW_TiketBantu.md** (contoh `Row(Modifier.fillMaxSize())`), **DESIGN_TiketBantu.md** (penjelasan layout adaptif) | ✔ | Penggunaan layout dua‑kolom, `Row`, `Column`, `Box` serta modifikator (`weight`, `fillMaxSize`) telah didefinisikan. |
| 2 | **Material Design 3 (M3)** – tema, warna, tipografi, komponen (Buttons, OutlinedTextField, Cards) | **DESIGN_TiketBantu.md** (design tokens, warna M3, tipografi), **README.md** (penyebutan M3), **CLAUDE_TiketBantu.md** (aturan penggunaan M3) | ✔ | Sistem design token lengkap (warna, shape, elevation) serta contoh komponen M3 (Card, Badge, Button, OutlinedTextField). |
| 3 | **State Management & UDF** – `remember`, `rememberSaveable`, State Hoisting, Unidirectional Data Flow | **ARCHITECTURE_TiketBantu.md** (MVVM + UDF, `StateFlow<UiState>`), **CLAUDE_TiketBantu.md** (aturan MVVM + UDF) | ✔ | ViewModel meng‑expose `UiState` (Loading/Success/Error) dan UI bersifat stateless; penggunaan `remember*` didokumentasikan di kode contoh. |
| 4 | **Lazy Layouts** – `LazyColumn`/`LazyGrid` dengan `key` | **APP_FLOW_TiketBantu.md** (implementasi `LazyColumn { items(activeTickets, key = { it.id }) … }`), **FEATURES_TiketBantu.md** (Infinite Scroll, LazyColumn) | ✔ | Lazy layout terpakai pada feed aduan, komentar, serta mekanisme infinite scroll. |
| 5 | **Networking & API** – Retrofit / Ktor, async handling | **DESIGN_TiketBantu.md** (stack: Retrofit + OkHttp, Ktor backend), **CLAUDE_TiketBantu.md** (Retrofit + Coroutines), **FEATURES_TiketBantu.md** (REST API endpoints) | ✔ | Semua endpoint terdefinisi; Retrofit dipakai di client dengan interceptor JWT; backend Ktor menyediakan API. |
| 6 | **Arsitektur Aplikasi** – MVVM, ViewModel, `UiState` (Loading/Success/Error) | **ARCHITECTURE_TiketBantu.md** (diagram MVVM, `sealed interface UiState`), **CLAUDE_TiketBantu.md** (rule MVVM + UDF) | ✔ | Layered (Route → Service → Repository) dan MVVM di Android sudah terstruktur. |
| 7 | **Navigation Compose** – multi‑screen (≥ 3), Type‑Safe Navigation, data transfer, BottomNavigation/Scaffold | **APP_FLOW_TiketBantu.md** (route graph, nested graphs), **ARCHITECTURE_TiketBantu.md** (type‑safe routes `@Serializable`), **DESIGN_TiketBantu.md** (BottomNavigation per‑role) | ✔ | Navigasi tipe‑aman, tiga screen utama (Feed, Detail, Create), serta BottomNavigation yang berubah per role. |

> **Kesimpulan:** Semua dokumen yang ada di folder `docs/TiketBantu` **memenuhi** 7 materi Native Android dengan Jetpack Compose yang diwajibkan pada ketentuan proyek.

---

## 👥 Job Description – Tim Pengembangan

Berikut pembagian tugas **rata dan seimbang** untuk masing‑masing anggota. *Setiap peran memiliki tanggung jawab yang jelas dan tidak bercampur‑campur.*

### 🎯 Core Backend (2 orang)

| Peran | Nama (contoh) | Fokus Utama | Tugas Spesifik | Deliverables | Kolaborasi |
|------|----------------|-------------|----------------|--------------|------------|
| **Backend Lead** | **Anda** | Arsitektur & Keamanan | • Desain & review **Route → Service → Repository** (lihat `ARCHITECTURE_TiketBantu.md`).<br>• Implementasi **JWT auth**, middleware role‑guard.<br>• Menentukan strategi **Atomic Claim** (linear queue) dan menulis unit‑test untuk konflik `409`.<br>• Menyusun **CI/CD pipeline** (Gradle + Docker) serta backup DB otomatis.<br>• Dokumentasi API (OpenAPI) & versi. | – Diagram arsitektur akhir.<br>– Modul `Auth`, `Ticket`, `Support` dengan tes coverage ≥ 80 %.<br>– Skrip migrasi + seed terverifikasi. | Bekerja erat dengan *Backend Engineer* (Satria) pada repository layer & data model. |
| **Backend Engineer** | **Satria** | Implementasi Service & Data Layer | • Kembangkan **Repository** menggunakan **Exposed ORM** (model `Ticket`, `User`, `Comment`, `TicketSupport`).<br>• Implementasi **file upload** (single JPG/PNG ≤ 5 MB) dan penyimpanan ke `/uploads/tickets/{id}/`.<br>• Buat **Polling API** (parameter `page`, `per_page`) untuk feed & komentar.<br>• Optimasi kueri (index pada `status`, `category_id`).<br>• Menulis integration test (Ktor test client). | – Kode `TicketRepository.kt`, `AuthService.kt` dll.<br>– Test suite (`BackendTestSuite`) lulus.<br>– Dokumentasi endpoint pada Swagger. | Koordinasi dengan *Backend Lead* untuk standar error handling, serta dengan *Frontend Lead* untuk kontrak API. |

### 🎯 Core Frontend (2 orang) – (termasuk UI/UX)

| Peran | Nama (contoh) | Fokus Utama | Tugas Spesifik | Deliverables | Kolaborasi |
|------|----------------|-------------|----------------|--------------|------------|
| **Frontend Lead (Compose Engineer)** | **Anda** | Arkitektur UI & State | • Struktur **package** (`ui/theme`, `ui/component`, `ui/screen`, `ui/navigation`) sesuai `DESIGN_TiketBantu.md`.<br>• Implementasi **ViewModel** dengan `StateFlow<UiState>`; pastikan **UDF** (event → ViewModel).<br>• Integrasi **Retrofit** (service layer) dan **DataStore** untuk token.<br>• Optimasi **LazyColumn** (key, `derivedStateOf`, infinite scroll).<br>• Tambahkan **unit‑test** UI (compose‑testing) untuk navigasi dan state transitions. | – Kode `MainActivity.kt`, `TicketListViewModel.kt`, `TicketRepositoryImpl.kt`.<br>– Coverage UI ≥ 70 %.<br>– Dokumentasi flow state diagram. | Berkoordinasi dengan *UI/UX Designer* untuk memastikan design token dipakai, serta dengan *Backend Lead* untuk API contract. |
| **UI/UX Designer (Design System Engineer)** | **(Orang ke‑2)** | Design System & Interaksi | • Implementasi **Design Tokens** (warna, spacing, shape, typography) dari `DESIGN_TiketBantu.md` ke file `Color.kt`, `Type.kt`, `Theme.kt`.<br>• Buat **reusable composables**: `TicketCard`, `StatusBadge`, `SupportButton`, `SnackbarHelper`.<br>• Pastikan **Accessibility**: contrast ≥ 4.5:1, contentDescription, TalkBack labels.<br>• Definisikan **micro‑animations** (ripple, fade‑in pada badge, loading shimmer).<br>• Produksi **style guide** (Figma atau PNG) yang di‑embed dalam artefak. | – Library `ui/component/*` lengkap.<br>– Design‑system dokumentasi (`DESIGN_TiketBantu.md` versi final).<br>– Prototipe interaktif (gambar). | Mengirim design token ke *Frontend Lead*, dan memvalidasi implementasi visual pada device (emulator). |

---

### 📎 Link ke Dokumen Referensi

- [APP_FLOW_TiketBantu.md](file:///d:/Organize/Projects/Mobile%20Project/TiketBantu-Mobile/docs/TiketBantu/APP_FLOW_TiketBantu.md)
- [ARCHITECTURE_TiketBantu.md](file:///d:/Organize/Projects/Mobile%20Project/TiketBantu-Mobile/docs/TiketBantu/ARCHITECTURE_TiketBantu.md)
- [CLAUDE_TiketBantu.md](file:///d:/Organize/Projects/Mobile%20Project/TiketBantu-Mobile/docs/TiketBantu/CLAUDE_TiketBantu.md)
- [DESIGN_TiketBantu.md](file:///d:/Organize/Projects/Mobile%20Project/TiketBantu-Mobile/docs/TiketBantu/DESIGN_TiketBantu.md)
- [FEATURES_TiketBantu.md](file:///d:/Organize/Projects/Mobile%20Project/TiketBantu-Mobile/docs/TiketBantu/FEATURES_TiketBantu.md)
- [README.md](file:///d:/Organize/Projects/Mobile%20Project/TiketBantu-Mobile/docs/TiketBantu/README.md)

---

**Selanjutnya:**
- Pastikan setiap anggota menandai milestone di issue tracker sesuai job‑description di atas.
- Lakukan **code review** lintas tim (backend ↔ frontend) untuk menjaga konsistensi API contract dan design system.

👍 Semoga analisis dan pembagian tugas ini membantu tim mencapai deliverable tepat waktu!

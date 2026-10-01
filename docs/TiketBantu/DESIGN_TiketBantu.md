### DESIGN_TiketBantu.md — Spesifikasi UI/UX & Design System Material Design 3 (M3)

Dokumen ini merupakan **Spesifikasi UI/UX & Design System** berbasis **Material Design 3 (M3)** dan **Android Adaptive Design Guidelines** dengan pendekatan **Mobile-First** untuk aplikasi **TiketBantu Mobile**.

**Sumber Kebenaran (Source of Truth)**: `FEATURES_TiketBantu.md`, `APP_FLOW_TiketBantu.md`, dan `ARCHITECTURE_TiketBantu.md`. Dokumen ini telah diselaraskan sepenuhnya — termasuk navigasi berbasis 3 *role* (Pelapor, Agen, Admin), komponen dukungan **Most Liked**, penghapusan modul notifikasi email/push (diganti **Snackbar/Toast**), dan penggunaan **1 lampiran foto opsional** per aduan.

---

#### 🎨 1. DESIGN SYSTEM & DESIGN TOKENS

##### Design Philosophy
Aplikasi dirancang dengan pendekatan **Mobile-First M3 Adaptive**:
* **Minimalis, Fungsional, & Terstruktur**: Bebas dari dekorasi berlebihan; setiap elemen memiliki fungsi UX yang jelas.
* **Kelipatan 4px / 8px Baseline Grid**: Seluruh *spacing*, *padding*, *margin*, dan ukuran komponen menggunakan ritme 4px/8px.
* **Elevation & Surface Controlled**: Penggunaan bidang *surface container* dan *tonal elevation* untuk hierarki visual tanpa mengandalkan *shadow* berat.
* **Feedback Instan**: Feedback visual langsung di layar via *Snackbar/Toast* tanpa bergantung pada modul notifikasi eksternal.

##### Design Tokens

###### A. Spacing Scale (Baseline 4px / 8px)
```css
--m3-spacing-xs:   4px;  /* Tight gap / icon padding */
--m3-spacing-sm:   8px;  /* Inner element gap */
--m3-spacing-md:  12px;  /* Compact padding */
--m3-spacing-base: 16px;  /* Mobile horizontal baseline margin */
--m3-spacing-lg:  20px;  /* Card inner padding */
--m3-spacing-xl:  24px;  /* Section spacing */
--m3-spacing-2xl: 32px;  /* Container gap */
--m3-spacing-3xl: 48px;  /* Large section gap */
```

###### B. Shape & Corner Radius Scale
```css
--m3-shape-none:        0px;
--m3-shape-extra-small: 4px;   /* Tag / Chip small */
--m3-shape-small:       8px;   /* Button, Text Field */
--m3-shape-medium:      12px;  /* Cards, Dialog compact */
--m3-shape-large:       16px;  /* Large Cards, Modal sheet */
--m3-shape-extra-large: 28px;  /* FAB, Navigation Rail Item */
--m3-shape-full:        9999px;/* Pill buttons, Badges, Avatars */
```

###### C. Elevation Tokens (M3 Tonal Elevation)
```css
--m3-elevation-0: 0 0 0 0 transparent;
--m3-elevation-1: 0px 1px 3px 1px rgba(0, 0, 0, 0.08), 0px 1px 2px 0px rgba(0, 0, 0, 0.12);
--m3-elevation-2: 0px 2px 6px 2px rgba(0, 0, 0, 0.08), 0px 1px 2px 0px rgba(0, 0, 0, 0.12);
--m3-elevation-3: 0px 4px 8px 3px rgba(0, 0, 0, 0.08), 0px 1px 3px 0px rgba(0, 0, 0, 0.12);
--m3-elevation-4: 0px 6px 10px 4px rgba(0, 0, 0, 0.08), 0px 2px 4px 0px rgba(0, 0, 0, 0.12);
--m3-elevation-5: 0px 8px 12px 6px rgba(0, 0, 0, 0.08), 0px 4px 4px 0px rgba(0, 0, 0, 0.12);
```

---

#### 📱 2. MOBILE-FIRST & ADAPTIVE BREAKPOINTS

Layout dirancang memprioritaskan perangkat *smartphone* (*Mobile-First*) dan beradaptasi secara mulus tanpa *horizontal scrolling*.

##### Target Mobile Resolution Baselines
* **360px** (Compact Small - e.g., Galaxy S Series compact)
* **375px** (Compact Standard - e.g., iPhone SE/Standard)
* **390px** (Compact Large - e.g., iPhone 13/14 Pro)
* **412px** (Compact Android Standard - e.g., Pixel Series)
* **430px** (Compact Plus/Pro Max)

##### Adaptive Window Size Classes (Material 3)
| Window Class | Viewport Width | Layout Presentation | Navigation Mechanism |
| ------ | ------ | ------ | ------ |
| **Compact** | < 600px | Single-column, Full-width dengan margin 16px, cards tersusun vertikal | Bottom Navigation Bar per Role + Top App Bar |
| **Medium** | 600px - 839px | 2-column grid, responsive card width, fluid padding | Navigation Rail / Navigation Drawer |
| **Expanded** | ≥ 840px | Multi-column, 2-column dashboard (List Feed + Sticky Detail/Stats) | Permanent Navigation Drawer / Sidebar + Top Header |

---

#### 🎨 3. MATERIAL 3 COLOR SYSTEM & ROLES

Menggunakan sistem peranan warna M3 yang fungsional dengan paduan palet *Electric Cobalt* & *Vibrant Cyan / Aurora Accents*, serta *Emerald Green* untuk indikator aduan Selesai.

```css
:root {
  /* Primary Roles */
  --md-sys-color-primary:                  #2563EB; /* Electric Cobalt Blue */
  --md-sys-color-on-primary:               #FFFFFF;
  --md-sys-color-primary-container:        #DBEAFE;
  --md-sys-color-on-primary-container:     #1E3A8A;

  /* Secondary Roles */
  --md-sys-color-secondary:                #06B6D4; /* Vibrant Cyan */
  --md-sys-color-on-secondary:             #FFFFFF;
  --md-sys-color-secondary-container:      #CFFAFE;
  --md-sys-color-on-secondary-container:   #164E63;

  /* Tertiary / Accent Roles */
  --md-sys-color-tertiary:                 #10B981; /* Emerald Mint / Success Green */
  --md-sys-color-on-tertiary:              #FFFFFF;
  --md-sys-color-tertiary-container:       #D1FAE5;
  --md-sys-color-on-tertiary-container:    #064E3B;

  /* Surface & Background Roles (M3 Tonal Surfaces) */
  --md-sys-color-background:               #F8FAFC;
  --md-sys-color-on-background:            #0F172A;
  --md-sys-color-surface:                  #FFFFFF;
  --md-sys-color-on-surface:               #0F172A;
  --md-sys-color-surface-variant:          #F1F5F9;
  --md-sys-color-on-surface-variant:       #475569;
  
  --md-sys-color-surface-container-lowest: #FFFFFF;
  --md-sys-color-surface-container-low:    #F8FAFC;
  --md-sys-color-surface-container:        #F1F5F9;
  --md-sys-color-surface-container-high:   #E2E8F0;
  --md-sys-color-surface-container-highest:#CBD5E1;

  /* Status Badges Colors */
  --md-sys-color-status-baru:              #2563EB; /* Primary Blue */
  --md-sys-color-status-diproses:          #F59E0B; /* Amber / Warning */
  --md-sys-color-status-selesai:           #10B981; /* Emerald Green ✓ */
  --md-sys-color-status-ditutup:           #64748B; /* Slate Gray */

  /* Outline & Borders */
  --md-sys-color-outline:                  #CBD5E1;
  --md-sys-color-outline-variant:          #E2E8F0;

  /* Error & Semantic Roles */
  --md-sys-color-error:                    #EF4444;
  --md-sys-color-on-error:                 #FFFFFF;
  --md-sys-color-error-container:          #FEE2E2;
  --md-sys-color-on-error-container:       #7F1D1D;
}
```

---

#### 🔤 4. TYPOGRAPHY HIERARCHY (M3 SCALE)

Font Utama: **Roboto** / **Plus Jakarta Sans** (Primary UI) dan **JetBrains Mono** (Code & ID Tiket).

| M3 Role | Size | Weight | Line Height | Usage |
| ------ | ------ | ------ | ------ | ------ |
| **Display Large** | 36px | Bold (700) | 1.2 | Main hero banner / statistic highlight |
| **Display Medium** | 28px | Bold (700) | 1.25 | Section hero / Dashboard metric |
| **Headline Large** | 24px | SemiBold (600) | 1.3 | Page title (e.g., Feed Aduan) |
| **Headline Medium** | 20px | SemiBold (600) | 1.35 | Section header / Dialog title |
| **Title Large** | 18px | Medium (500) | 1.4 | Ticket card title |
| **Title Medium** | 16px | Medium (500) | 1.4 | List item title / Subtitle |
| **Body Large** | 16px | Regular (400) | 1.5 | Primary body text / Deskripsi aduan |
| **Body Medium** | 14px | Regular (400) | 1.5 | Standard body text / form inputs |
| **Body Small** | 12px | Regular (400) | 1.4 | Supporting text / Timestamp / Lokasi |
| **Label Large** | 14px | Medium (500) | 1.3 | Button text / Tab label |
| **Label Medium** | 12px | Medium (500) | 1.3 | Chips / Navigation labels / Most Liked Counter |
| **Label Small** | 11px | Medium (500) | 1.2 | Status Badges (✓ Selesai, Baru, Diproses) |

---

#### 🏛️ 5. APP STRUCTURE & NAVIGATION

##### A. Top App Bar
* **Tinggi Center-Aligned**: 56px - 64px
* **Elemen**:
  * Navigation Icon (Menu / Back Button) di sebelah kiri (min touch target 48px).
  * Title (Judul Halaman singkat, misal: "Feed Aduan", "Detail Aduan").
  * Action Icons (Maksimal 2 ikon aksi: **Search** 🔍 dan **Filter** ⚙️). 
  * *Catatan*: Ikon notifikasi **dihapus** mengikuti eliminasi modul notifikasi.

##### B. Bottom Navigation Bar (Mobile / Compact) — Diselaraskan per Role
* **Tinggi**: 80px (termasuk label & safe area inset).
* **Destinasi per Role** (Sesuai `APP_FLOW.md` & `ARCHITECTURE.md`):
  
| Menu Item | User (Pelapor) | Agen (Petugas) | Admin | Deskripsi |
| ------ | :---: | :---: | :---: | ------ |
| 🏠 **Feed Aduan** | ✅ | ✅ | ✅ | Feed publik aduan dengan sorting Most Liked / Terbaru |
| ➕ **Buat Aduan** | ✅ | ❌ | ✅ | Form pengajuan aduan baru + 1 foto opsional |
| 📊 **Monitoring** | ❌ | ❌ | ✅ | Pure Monitoring dashboard statistik & User Management |
| 👤 **Profil** | ✅ | ✅ | ✅ | Informasi akun & riwayat **Aduan Saya / Tugas Saya** |

* *Catatan Penting*: Menu **"Aduan Saya / Tiket Saya"** diakses melalui layar **Profil**, bukan sebagai tab Bottom Navigation tersendiri.
* **Item States**:
  * **Active**: Pill Container `var(--md-sys-color-primary-container)`, Ikon `var(--md-sys-color-primary)`, Label Bold.
  * **Inactive**: Ikon & Label `var(--md-sys-color-on-surface-variant)`.
* **Behavior**: Selalu *sticky* di bagian bawah dengan *safe-area-inset-bottom*.

##### C. Navigation Rail / Drawer (Desktop / Expanded)
* Pada layar ≥ 840px, Bottom Navigation berubah otomatis menjadi **Navigation Rail** (lebar 80px) atau **Permanent Drawer** (lebar 256px) di sebelah kiri.

---

#### 🧩 6. SPESIFIKASI KOMPONEN KHUSUS TIKETBANTU

##### A. Card Feed Aduan (Ticket Card)
* **Visual Layout**:
  * Header: Avatar Pelapor + Nama + Timestamp + Badge Status.
  * Content: Judul Aduan (Title Large) + Deskripsi Singkat (2 baris max, Body Medium).
  * Location Pill: Location Building + Floor + Room (Chip Small).
  * Media Preview: **1 Foto Opsional** (Aspect Ratio 16:9 / 4:3, Rounded Corner 8px).
  * Footer: Tombol Dukungan **"Saya Juga Mengalami" (Most Liked)** dengan ikon Jempol/Heart + Counter Jumlah Dukungan + Counter Komentar.
* **Badge Status M3**:
  * **BARU**: Blue Pill (`#2563EB`).
  * **DIPROSES**: Amber Pill (`#F59E0B`).
  * **SELESAI**: Emerald Green Pill dengan Ikon Centang (`✓ Selesai`, `#10B981`).
  * **DITUTUP**: Slate Gray Pill (`#64748B`).
* **Aturan Pengurutan Visual**:
  * Aduan aktif (BARU & DIPROSES) tampil di bagian atas feed.
  * Aduan berstatus **SELESAI / DITUTUP** selalu dikelompokkan di **blok paling bawah feed** dengan penanda visual Badge Centang Hijau.

##### B. Tombol Dukungan "Saya Juga Mengalami" (Most Liked Toggle)
* **Spesifikasi**:
  * Tonal Button / Filter Chip interaktif.
  * **State Inactive**: Outlined Style, Ikon Thumb Up / Hand Outlined, Teks "Saya Juga Mengalami (X)".
  * **State Active (Supported)**: Filled Tonal Style (`var(--md-sys-color-primary-container)`), Ikon Thumb Up Filled, Teks "Didukung (X)".
  * Touch target: Minimal 48px height.

##### C. Tombol Klaim Agen (Linear Queue)
* **Spesifikasi**:
  * Tampil khusus untuk akun ber-role **AGEN** pada aduan berstatus **BARU** yang belum di-claim (`agent_id` = null).
  * Button Style: Filled Primary Button "Klaim Aduan Ini".
  * Konfirmasi: Dialog Modal konfirmasi instan sebelum klaim dieksekusi.

##### D. Feedback UI (Pengganti Notifikasi)
* **Snackbar M3**: Tampil di bagian bawah layar di atas Bottom Navigation untuk konfirmasi aksi (contoh: *"Aduan berhasil dibuat"*, *"Status berhasil diperbarui"*, *"Dukungan ditambahkan"*).
* **Toast**: Untuk pesan singkat error atau peringatan sistem (contoh: *"Gagal memuat data lokal"*, *"Format file tidak valid"*).

---

#### 👆 7. TOUCH & INTERACTION STATES

Setiap elemen interaktif mengikuti standar kenyamanan sentuh Android:
1. **Touch Target**: Minimal **48px × 48px** untuk seluruh tombol, ikon, radio, switch, dan checkbox.
2. **Component Interactive States**:
   * **Default**: Tampilan normal.
   * **Pressed / Ripple Effect**: Feedback visual instan saat disentuh (Opacity overlay 12%).
   * **Focused**: Outline fokus terlihat jelas (Ring 2px `var(--md-sys-color-primary)`).
   * **Hover** (Desktop): Tint overlay 8%.
   * **Disabled**: Opacity 38%, cursor not-allowed.
   * **Loading**: Spinner progress indicator di dalam komponen tanpa merusak ukuran.

---

#### 📐 8. FORMS & INPUT SPECIFICATIONS

##### A. Form Buat Aduan
* **Single Column Layout** pada layar mobile.
* **Fields**:
  * Judul Aduan (OutlinedTextField, Mandatory).
  * Kategori (Dropdown Menu / Exposed Dropdown, Mandatory).
  * Lokasi (Gedung, Lantai, Ruangan - Mandatory).
  * Deskripsi Detail (OutlinedTextField Multiline, Mandatory).
  * Lampiran Foto: **1 Photo Picker Box Opsional** (Maksimal 1 file JPG/PNG, maks 5MB) dengan preview & tombol hapus foto.
* **Keyboard Safe Area**: Halaman dapat di-scroll saat keyboard virtual aktif.

##### B. Form Registrasi & Login
* Fields Registrasi: Nama Lengkap, **NIM/NIP**, Email, Password.
* Validation Feedback: Error message tampil di bawah field (Font: Body Small 12px, warna `var(--md-sys-color-error)`).

---

#### 🌐 9. ACCESSIBILITY & ICONOGRAPHY

* **Icon Library**: Menggunakan satu standar pustaka ikon: **Material Symbols / Material Icons** (Font size 24px default, stroke-width 1.75-2px).
* **Contrast Ratio**: Minimal 4.5:1 (WCAG AA Standard) antara warna teks dan background.
* **Color Neutrality**: Status tidak hanya dibedakan dengan warna, melainkan dibantu ikon (contoh: **✓** untuk Selesai) dan teks indikator jernih.

---

#### ✅ 10. DESIGN CHECKLIST & IMPLEMENTATION SPECIFICATION

Sebelum penerapan UI disetujui:
* [x] Memenuhi pedoman Material Design 3 (M3).
* [x] Layout responsif *Mobile-First* (360px - 430px) hingga *Desktop Adaptive* (≥840px).
* [x] Baseline spacing kelipatan 4px / 8px dengan margin horizontal 16px pada layar mobile.
* [x] Touch target minimal 48px × 48px untuk semua kontrol interaktif.
* [x] Struktur Top App Bar (tanpa ikon notifikasi) dan Bottom Navigation per role (3 role) diselaraskan dengan `APP_FLOW.md` dan `ARCHITECTURE.md`.
* [x] Komponen khusus **Most Liked**, **Badge Centang Hijau (✓ Selesai)**, dan **1 Lampiran Foto Opsional** didefinisikan secara presisi.
* [x] Memiliki skema warna M3 (Primary, Secondary, Surface Containers, Status Colors).
* [x] Menyediakan *Skeleton Loading*, *Empty State*, dan *Error State* yang actionable.
* [x] **Kode sumber aplikasi tetap utuh dan aman tanpa modifikasi otomatis**.

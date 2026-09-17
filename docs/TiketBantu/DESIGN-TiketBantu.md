# DESIGN.md — Product Design & Material Design 3 (M3) Adaptive Guidelines

Dokumen ini merupakan **Spesifikasi UI/UX & Design System** berbasis **Material Design 3 (M3)** dan **Android Adaptive Design Guidelines** dengan pendekatan **Mobile-First**. Dokumen ini menjadi pedoman utama perancangan UI/UX aplikasi tanpa mengubah atau mengganggu kode sumber yang sudah ada.

---

## 🎨 1. DESIGN SYSTEM & DESIGN TOKENS

### Design Philosophy
Aplikasi dirancang dengan pendekatan **Mobile-First M3 Adaptive**:
* **Minimalis, Fungsional, & Terstruktur**: Bebas dari dekorasi berlebihan yang tidak memiliki fungsi UX.
* **Kelipatan 4px / 8px Baseline Grid**: Seluruh *spacing*, *padding*, *margin*, dan ukuran komponen menggunakan ritme 4px/8px.
* **Elevation & Surface Controlled**: Penggunaan bidang *surface container* dan *elevation* yang terkontrol untuk hirarki visual tanpa mengandalkan *shadow* berat.

### Design Tokens

#### A. Spacing Scale (Baseline 4px / 8px)
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

#### B. Shape & Corner Radius Scale
```css
--m3-shape-none:        0px;
--m3-shape-extra-small: 4px;   /* Tag / Chip small */
--m3-shape-small:       8px;   /* Button, Text Field */
--m3-shape-medium:      12px;  /* Cards, Dialog compact */
--m3-shape-large:       16px;  /* Large Cards, Modal sheet */
--m3-shape-extra-large: 28px;  /* FAB, Navigation Rail Item */
--m3-shape-full:        9999px;/* Pill buttons, Badges, Avatars */
```

#### C. Elevation Tokens (M3 Tonal Elevation)
```css
--m3-elevation-0: 0 0 0 0 transparent;
--m3-elevation-1: 0px 1px 3px 1px rgba(0, 0, 0, 0.08), 0px 1px 2px 0px rgba(0, 0, 0, 0.12);
--m3-elevation-2: 0px 2px 6px 2px rgba(0, 0, 0, 0.08), 0px 1px 2px 0px rgba(0, 0, 0, 0.12);
--m3-elevation-3: 0px 4px 8px 3px rgba(0, 0, 0, 0.08), 0px 1px 3px 0px rgba(0, 0, 0, 0.12);
--m3-elevation-4: 0px 6px 10px 4px rgba(0, 0, 0, 0.08), 0px 2px 4px 0px rgba(0, 0, 0, 0.12);
--m3-elevation-5: 0px 8px 12px 6px rgba(0, 0, 0, 0.08), 0px 4px 4px 0px rgba(0, 0, 0, 0.12);
```

---

## 📱 2. MOBILE-FIRST & ADAPTIVE BREAKPOINTS

Layout dirancang memprioritaskan perangkat *smartphone* terlebih dahulu (*Mobile-First*) dan beradaptasi secara mulus tanpa *horizontal scrolling*.

### Target Mobile Resolution Baselines
* **360px** (Compact Small - e.g., Galaxy S Series compact)
* **375px** (Compact Standard - e.g., iPhone SE/Standard)
* **390px** (Compact Large - e.g., iPhone 13/14 Pro)
* **412px** (Compact Android Standard - e.g., Pixel Series)
* **430px** (Compact Plus/Pro Max)

### Adaptive Window Size Classes (Material 3)

| Window Class | Viewport Width | Layout Presentation | Navigation Mechanism |
| :--- | :--- | :--- | :--- |
| **Compact** | `< 600px` | Single-column, Full-width dengan margin 16px, cards tersusun vertikal | Bottom Navigation Bar (3-5 Destinasi) + Top App Bar |
| **Medium** | `600px - 839px` | 2-column grid, responsive card width, fluid padding | Navigation Rail / Navigation Drawer |
| **Expanded** | `≥ 840px` | Multi-column, Max-content-width (1200px), Side-by-side List-Detail | Permanent Navigation Drawer / Sidebar + Top Header |

---

## 🎨 3. MATERIAL 3 COLOR SYSTEM & ROLES

Menggunakan sistem peranan warna M3 yang fungsional dengan paduan palet segar *Electric Cobalt* & *Vibrant Cyan / Aurora Accents*.

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
  --md-sys-color-tertiary:                 #10B981; /* Emerald Mint */
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

  /* Outline & Borders */
  --md-sys-color-outline:                  #CBD5E1;
  --md-sys-color-outline-variant:          #E2E8F0;

  /* Error & Semantic Roles */
  --md-sys-color-error:                    #EF4444;
  --md-sys-color-on-error:                 #FFFFFF;
  --md-sys-color-error-container:          #FEE2E2;
  --md-sys-color-on-error-container:       #7F1D1D;

  --md-sys-color-success:                  #10B981;
  --md-sys-color-warning:                  #F59E0B;
}
```

---

## 🔤 4. TYPOGRAPHY HIERARCHY (M3 SCALE)

Font Utama: **Roboto** / **Plus Jakarta Sans** (Primary UI) dan **JetBrains Mono** (Code & ID).

| M3 Role | Size | Weight | Line Height | Usage |
| :--- | :--- | :--- | :--- | :--- |
| **Display Large** | 36px | Bold (700) | 1.2 | Main hero banner / statistic highlight |
| **Display Medium** | 28px | Bold (700) | 1.25 | Section hero |
| **Headline Large** | 24px | SemiBold (600) | 1.3 | Page title |
| **Headline Medium** | 20px | SemiBold (600) | 1.35 | Section header / Dialog title |
| **Title Large** | 18px | Medium (500) | 1.4 | Card title |
| **Title Medium** | 16px | Medium (500) | 1.4 | List item title / Subtitle |
| **Body Large** | 16px | Regular (400) | 1.5 | Primary body text |
| **Body Medium** | 14px | Regular (400) | 1.5 | Standard body text / form inputs |
| **Body Small** | 12px | Regular (400) | 1.4 | Supporting text / helper text |
| **Label Large** | 14px | Medium (500) | 1.3 | Button text / Tab label |
| **Label Medium** | 12px | Medium (500) | 1.3 | Chips / Navigation labels |
| **Label Small** | 11px | Medium (500) | 1.2 | Badges / Captions |

---

## 🏛️ 5. APP STRUCTURE & NAVIGATION

### A. Top App Bar
* **Tinggi Center-Aligned**: 56px - 64px
* **Elemen**:
  * Navigation Icon (Menu / Back Button) di sebelah kiri (min touch target 48px).
  * Title (Judul Halaman singkat).
  * Action Icons (Maksimal 2-3 ikon aksi relevan seperti Search, Filter, Notification).

### B. Bottom Navigation Bar (Mobile / Compact)
* **Tinggi**: 80px (termasuk label & safe area inset).
* **Destinasi**: 3 sampai 5 *Primary Destinations* saja (Contoh: Dashboard, Tiket Saya, Pengaduan Baru, Profil).
* **Item States**:
  * **Active**: Pill Container `var(--md-sys-color-primary-container)`, Ikon `var(--md-sys-color-primary)`, Label Bold.
  * **Inactive**: Ikon & Label `var(--md-sys-color-on-surface-variant)`.
* **Behavior**: Selalu *sticky* di bagian bawah dengan *safe-area-inset-bottom*.

### C. Navigation Rail / Drawer (Desktop / Expanded)
* Pada layar `≥ 840px`, Bottom Navigation berubah otomatis menjadi **Navigation Rail** (lebar 80px) atau **Permanent Drawer** (lebar 256px) di sebelah kiri.

---

## 👆 6. TOUCH & INTERACTION STATES

Setiap elemen interaktif mengikuti standar kenyamanan sentuh Android:

1. **Touch Target**: Minimal **48px × 48px** untuk seluruh tombol, ikon, radio, switch, dan checkbox.
2. **Component Interactive States**:
   * **Default**: Tampilan normal.
   * **Pressed / Ripple Effect**: Feedback visual instan saat disentuh (Opacity overlay 12%).
   * **Focused**: Outline fokus terlihat jelas (Ring 2px `var(--md-sys-color-primary)`).
   * **Hover** (Desktop): Tint overlay 8%.
   * **Disabled**: Opacity 38%, cursor `not-allowed`.
   * **Loading**: Spinner progress indicator di dalam komponen tanpa merusak ukuran.

---

## 🧩 7. COMPONENT SPECIFICATIONS

### A. Buttons (M3 Standard)
* **Filled Primary Button**: Height 40px - 48px, Shape Radius `var(--m3-shape-full)`, Background `var(--md-sys-color-primary)`.
* **Tonal Button**: Background `var(--md-sys-color-primary-container)`, Text `var(--md-sys-color-on-primary-container)`.
* **Outlined Button**: Border 1px `var(--md-sys-color-outline)`, Background Transparent.
* **Text Button**: Borderless, padding horizontal 12px.

### B. Cards (M3 Surface Containers)
* **Elevated Card**: Container `var(--md-sys-color-surface)`, Radius 12px/16px, Elevation 1.
* **Filled Card**: Container `var(--md-sys-color-surface-container)`, Elevation 0.
* **Outlined Card**: Container `var(--md-sys-color-surface)`, Border 1px `var(--md-sys-color-outline-variant)`.
* **Margin & Padding**: Padding 16px/20px. Hindari *card di dalam card di dalam card*.

### C. Forms & Inputs
* **Single Column Baseline** pada layar mobile.
* **Floating Label / Outlined Input**: Height 48px - 56px, Radius 8px/12px.
* **Helper / Error Message**: Terletak tepat di bawah field (Font: Body Small 12px).
* **Keyboard Safe Area**: Halaman dapat di-scroll saat keyboard virtual aktif agar input tidak tertutup.

### D. Floating Action Button (FAB)
* **Standard FAB**: 56px × 56px, Radius 16px (`var(--m3-shape-large)`), Container `var(--md-sys-color-primary-container)`.
* **Extended FAB**: Height 56px, termasuk Ikon + Label (dipakai untuk *Primary Action* tunggal seperti "Buat Pengaduan Baru").

### E. Feedback & State Containers
* **Skeleton Loading**: Shimmer effect pada *Surface Container High* saat data dimuat (tanpa halaman kosong).
* **Empty State**: Ikon Material Symbols (48px/64px) + Judul (Headline Medium) + Deskripsi singkat + Primary CTA Button.
* **Error State**: Pesan error yang informatif dan *actionable* (misal: "Gagal memuat tiket. Periksa koneksi internet Anda [Coba Lagi]").

---

## 🌐 8. ACCESSIBILITY & ICONOGRAPHY

* **Icon Library**: Menggunakan satu standar pustaka ikon: **Material Symbols** / **Material Icons** (Font size 24px default, stroke-width 1.75-2px).
* **Contrast Ratio**: Minimal 4.5:1 (WCAG AA Standard) antara warna teks dan background.
* **Color Neutrality**: Status tidak hanya dibedakan dengan warna, melainkan dibantu ikon dan teks indikator.

---

## ✅ 9. DESIGN CHECKLIST & IMPLEMENTATION SPECIFICATION

Sebelum penerapan UI disetujui:
- [x] Memenuhi pedoman Material Design 3 (M3).
- [x] Layout responsif *Mobile-First* (360px - 430px) hingga *Desktop Adaptive* (≥840px).
- [x] Baseline spacing kelipatan 4px / 8px dengan margin horizontal 16px pada layar mobile.
- [x] Touch target minimal 48px × 48px untuk semua kontrol interaktif.
- [x] Struktur top app bar, bottom navigation, FAB, dan rail yang konsisten.
- [x] Memiliki skema warna M3 (Primary, Secondary, Surface Containers, Error).
- [x] Menyediakan *Skeleton Loading*, *Empty State*, dan *Error State* yang actionable.
- [x] **Kode sumber aplikasi tetap utuh dan aman tanpa modifikasi otomatis**.

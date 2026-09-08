# DESIGN.md — Design System & UI Guidelines

## 🎨 Design Philosophy

e-Notulen menggunakan **Fresh & Clean UI** yang modern, profesional, dan mudah digunakan oleh pegawai pemerintahan dari berbagai usia. Desain harus terasa premium tetapi tidak overwhelming — **functional elegance**.

---

## 🎨 Color System

### Primary Palette — Deep Teal
Warna utama yang merepresentasikan profesionalisme dan kepercayaan pemerintahan.

```css
--color-primary-50:  #ECFDF5;
--color-primary-100: #D1FAE5;
--color-primary-200: #A7F3D0;
--color-primary-300: #6EE7B7;
--color-primary-400: #34D399;
--color-primary-500: #10B981;  /* Main Primary */
--color-primary-600: #059669;
--color-primary-700: #047857;
--color-primary-800: #065F46;
--color-primary-900: #064E3B;
--color-primary-950: #022C22;
```

### Secondary Palette — Slate Blue
Warna pendukung untuk elemen navigasi dan aksen.

```css
--color-secondary-50:  #F8FAFC;
--color-secondary-100: #F1F5F9;
--color-secondary-200: #E2E8F0;
--color-secondary-300: #CBD5E1;
--color-secondary-400: #94A3B8;
--color-secondary-500: #64748B;  /* Main Secondary */
--color-secondary-600: #475569;
--color-secondary-700: #334155;
--color-secondary-800: #1E293B;
--color-secondary-900: #0F172A;
--color-secondary-950: #020617;
```

### Accent Palette — Amber
Untuk highlight, warning, dan elemen yang perlu perhatian.

```css
--color-accent-50:  #FFFBEB;
--color-accent-100: #FEF3C7;
--color-accent-200: #FDE68A;
--color-accent-300: #FCD34D;
--color-accent-400: #FBBF24;
--color-accent-500: #F59E0B;  /* Main Accent */
--color-accent-600: #D97706;
--color-accent-700: #B45309;
--color-accent-800: #92400E;
--color-accent-900: #78350F;
```

### Semantic Colors

```css
/* Success */
--color-success: #10B981;
--color-success-light: #D1FAE5;
--color-success-dark: #065F46;

/* Warning */
--color-warning: #F59E0B;
--color-warning-light: #FEF3C7;
--color-warning-dark: #92400E;

/* Error / Danger */
--color-error: #EF4444;
--color-error-light: #FEE2E2;
--color-error-dark: #991B1B;

/* Info */
--color-info: #3B82F6;
--color-info-light: #DBEAFE;
--color-info-dark: #1E40AF;
```

### Neutral / Background

```css
--color-bg-primary:   #FFFFFF;
--color-bg-secondary: #F8FAFC;
--color-bg-tertiary:  #F1F5F9;
--color-bg-sidebar:   #0F172A;  /* Dark sidebar */

--color-text-primary:   #0F172A;
--color-text-secondary: #475569;
--color-text-tertiary:  #94A3B8;
--color-text-inverse:   #FFFFFF;

--color-border:       #E2E8F0;
--color-border-focus: #10B981;
--color-divider:      #F1F5F9;
```

---

## 🔤 Typography

### Font Family
```css
/* Primary Font (UI) */
font-family: 'Plus Jakarta Sans', 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;

/* Monospace (code, NIP, timestamps) */
font-family: 'JetBrains Mono', 'Fira Code', monospace;
```

### Font Scale

| Name     | Size   | Weight    | Line Height | Usage                          |
| -------- | ------ | --------- | ----------- | ------------------------------ |
| Display  | 36px   | Bold (700)| 1.2         | Halaman utama, hero            |
| H1       | 30px   | Bold (700)| 1.3         | Page title                     |
| H2       | 24px   | SemiBold  | 1.35        | Section title                  |
| H3       | 20px   | SemiBold  | 1.4         | Card title, subsection         |
| H4       | 18px   | Medium    | 1.4         | Sub-subsection                 |
| Body LG  | 16px   | Regular   | 1.6         | Body text utama                |
| Body     | 14px   | Regular   | 1.5         | Body text default              |
| Body SM  | 13px   | Regular   | 1.5         | Secondary text, helper         |
| Caption  | 12px   | Regular   | 1.4         | Label, timestamp, metadata     |
| Tiny     | 11px   | Medium    | 1.3         | Badge, tag                     |

---

## 📐 Spacing System

Menggunakan 4px base unit:

| Token | Value | Usage                    |
| ----- | ----- | ------------------------ |
| xs    | 4px   | Tight spacing            |
| sm    | 8px   | Between related items    |
| md    | 12px  | Default inner padding    |
| base  | 16px  | Standard gap             |
| lg    | 20px  | Section padding          |
| xl    | 24px  | Card padding             |
| 2xl   | 32px  | Section gap              |
| 3xl   | 40px  | Page section gap         |
| 4xl   | 48px  | Large section separation |
| 5xl   | 64px  | Page-level spacing       |

---

## 🧩 Component Design

### Sidebar Navigation
- **Lebar**: 260px (expanded), 72px (collapsed)
- **Background**: Dark (Slate 900 → 950) dengan subtle gradient
- **Item aktif**: Background primary-500 dengan opacity, teks putih
- **Item hover**: Background slate-800
- **Logo**: Bagian atas sidebar, dengan nama "e-Notulen"
- **Profil user**: Bagian bawah sidebar (avatar, nama, role)
- **Animasi**: Smooth transition 300ms saat collapse/expand
- **Ikon**: Lucide React icons

### Top Bar
- **Tinggi**: 64px
- **Background**: White dengan subtle bottom border
- **Konten**: Breadcrumb (kiri), Search bar (tengah), Notifications + User menu (kanan)
- **Shadow**: Sangat subtle (`shadow-sm`)

### Cards
- **Background**: White
- **Border**: 1px solid `border` color
- **Border Radius**: 12px
- **Padding**: 24px
- **Shadow**: `0 1px 3px rgba(0,0,0,0.05)` (default), `0 4px 12px rgba(0,0,0,0.1)` (hover)
- **Hover**: Slight elevation increase, border color berubah ke primary-200
- **Transition**: 200ms ease

### Buttons

#### Primary Button
```
bg: primary-600 → primary-700 (hover)
text: white
border-radius: 8px
padding: 10px 20px
font-weight: 600
shadow: 0 1px 2px rgba(0,0,0,0.1)
hover: translateY(-1px), shadow increase
active: translateY(0)
transition: all 150ms ease
```

#### Secondary Button
```
bg: white
text: secondary-700
border: 1px solid border color
hover: bg secondary-50, border secondary-300
```

#### Danger Button
```
bg: error-600 → error-700 (hover)
text: white
```

#### Ghost Button
```
bg: transparent
text: secondary-600
hover: bg secondary-100
```

### Button Sizes

| Size | Padding       | Font Size | Height |
| ---- | ------------- | --------- | ------ |
| SM   | 6px 12px      | 13px      | 32px   |
| MD   | 10px 20px     | 14px      | 40px   |
| LG   | 12px 24px     | 16px      | 48px   |

### Form Inputs
```
height: 40px (default), 48px (large)
bg: white
border: 1px solid border color
border-radius: 8px
padding: 0 12px
font-size: 14px
focus: border primary-500, ring 3px primary-500/20
error: border error-500, ring 3px error-500/20
disabled: bg secondary-50, text secondary-400
placeholder: text-secondary-400
transition: border 150ms, shadow 150ms
```

### Tables
```
header-bg: secondary-50
header-text: secondary-600, font-weight 600, font-size 12px, uppercase, letter-spacing 0.05em
row-border: 1px solid divider color
row-hover: bg primary-50/50
row-padding: 12px 16px
striped: alternate row bg secondary-50/50 (optional)
border-radius: 12px (wrapper)
```

### Badges / Tags

| Variant   | Background     | Text          |
| --------- | -------------- | ------------- |
| Default   | secondary-100  | secondary-700 |
| Primary   | primary-100    | primary-700   |
| Success   | success-light  | success-dark  |
| Warning   | warning-light  | warning-dark  |
| Danger    | error-light    | error-dark    |
| Info      | info-light     | info-dark     |

```
border-radius: 6px
padding: 2px 10px
font-size: 12px
font-weight: 500
```

### Toast / Notification
- **Posisi**: Top-right
- **Width**: 360px
- **Auto dismiss**: 5 detik
- **Animasi**: Slide in dari kanan
- **Varian**: Success (green), Error (red), Warning (amber), Info (blue)

### Modal / Dialog
- **Overlay**: Black 50% opacity, blur backdrop
- **Container**: White, border-radius 16px, max-width sesuai size
- **Sizes**: SM (400px), MD (520px), LG (680px), XL (800px), Full (90vw)
- **Animasi**: Scale from 95% + fade in (150ms)
- **Header**: Border bottom, padding 24px
- **Body**: Padding 24px, max-height 60vh dengan scroll
- **Footer**: Border top, padding 16px 24px, flex end

### Empty State
- **Ilustrasi**: Simple line illustration atau icon (64px)
- **Judul**: H3, secondary-700
- **Deskripsi**: Body SM, secondary-400
- **CTA Button**: Primary button (opsional)
- **Padding**: 48px vertikal

---

## 📄 Notulen Editor (Tiptap)

### Toolbar
- **Posisi**: Sticky top saat scroll
- **Background**: White dengan bottom border
- **Grouped items**: Divider vertikal antar group
- **Groups**:
  1. **Text Format**: Bold, Italic, Underline, Strikethrough
  2. **Heading**: H1, H2, H3
  3. **List**: Ordered List, Unordered List
  4. **Alignment**: Left, Center, Right, Justify
  5. **Insert**: Table, Horizontal Rule
  6. **History**: Undo, Redo

### Editor Area
```
min-height: 500px
padding: 40px 60px (simulates paper)
bg: white
border: 1px solid border
border-radius: 8px (bottom)
font-family: 'Plus Jakarta Sans'
font-size: 14px
line-height: 1.8
```

### Auto-Save Indicator
- **Posisi**: Kanan atas editor
- **States**:
  - 💾 "Tersimpan" — teks hijau + ikon check
  - ⏳ "Menyimpan..." — teks amber + spinner
  - ❌ "Gagal menyimpan" — teks merah + retry button
- **Animasi**: Fade transition antar state

---

## 📱 Responsive Breakpoints

| Breakpoint | Width    | Layout                        |
| ---------- | -------- | ----------------------------- |
| Mobile     | < 640px  | Sidebar hidden, hamburger     |
| Tablet     | 640-1024 | Sidebar collapsed (icon only) |
| Desktop    | > 1024px | Sidebar expanded              |

---

## 🎭 Animations & Transitions

### Page Transition
```css
/* Fade + slide up */
@keyframes pageIn {
  from { opacity: 0; transform: translateY(8px); }
  to   { opacity: 1; transform: translateY(0); }
}
animation: pageIn 300ms ease-out;
```

### Skeleton Loading
```css
/* Shimmer effect */
background: linear-gradient(90deg, #f1f5f9 25%, #e2e8f0 50%, #f1f5f9 75%);
background-size: 200% 100%;
animation: shimmer 1.5s infinite;
```

### Micro-interactions
- **Button press**: Scale(0.97) + darker shade (100ms)
- **Card hover**: Elevation increase + subtle border color shift (200ms)
- **Sidebar toggle**: Width transition (300ms cubic-bezier)
- **Dropdown**: Scale(0.95) + opacity(0) → Scale(1) + opacity(1) (150ms)
- **Toast**: SlideIn from right (300ms) + SlideOut (200ms)
- **Status change**: Background color pulse (once)
- **Save indicator**: Smooth fade between states (200ms)

---

## 🖼️ Iconography

- **Library**: [Lucide React](https://lucide.dev/)
- **Size**: 16px (inline), 20px (default), 24px (large), 32px (hero)
- **Stroke Width**: 1.75 (default), 2 (bold/emphasis)
- **Color**: Inherit from parent text color
- **Consistency**: Selalu gunakan Lucide icons, jangan campur dengan library lain

---

## 📊 Dashboard Widgets

### Stat Card
```
layout: horizontal (icon kiri, angka + label kanan)
bg: white
border: 1px solid border
border-radius: 12px
padding: 20px
icon-wrapper: 48px × 48px, rounded-10px, bg primary-100
value: font-size 28px, font-weight 700, secondary-900
label: font-size 13px, font-weight 400, secondary-500
trend: optional, small percentage dengan arrow up/down
```

### Chart Card
```
bg: white
border: 1px solid border
border-radius: 12px
header: padding 20px, title + filter dropdown
body: padding 20px, chart area
chart-library: Recharts
chart-colors: primary-500, accent-500, info-500, secondary-400
```

---

## 📝 PDF Output Design

PDF yang di-generate harus menyerupai format notulen resmi pemerintahan:

### Layout
- **Paper**: A4 (210mm × 297mm)
- **Margin**: Top 30mm, Bottom 25mm, Left 30mm, Right 25mm
- **Font**: Times New Roman atau serif equivalent
- **Font Size**: 12pt (body), 14pt (judul), 11pt (keterangan)

### Sections
1. **Kop Surat**: Logo OPD (kiri), Nama instansi (tengah, bold, uppercase), Alamat + kontak
2. **Garis pembatas**: Double line di bawah kop surat
3. **Judul**: "NOTULEN" + nama rapat (center, bold, underline)
4. **Metadata**: Format tabel tanpa border (Hari/Tanggal, Waktu, Tempat, Dipimpin Oleh)
5. **Daftar Hadir**: Numbered list dengan kolom Nama dan Instansi
6. **Isi/Pembahasan**: Rich text content
7. **Kesimpulan**: Bullet points
8. **Tanda Tangan**: 2 kolom (kiri: Pimpinan Rapat, kanan: Notulis) — Nama, Jabatan, NIP
9. **Dokumentasi**: Halaman terpisah, judul "DOKUMENTASI KEGIATAN", foto-foto grid

---

## 🌙 Dark Mode (Future)

Disiapkan untuk implementasi dark mode di masa depan:
- Semua warna menggunakan CSS variables
- Komponen tidak hardcode warna, selalu referensi ke design tokens
- Gunakan `prefers-color-scheme` media query ready

---

## ✅ Design Checklist

Sebelum release, pastikan:
- [ ] Semua warna sesuai design tokens
- [ ] Typography konsisten
- [ ] Responsive di semua breakpoint
- [ ] Loading state ada di semua data-fetching area
- [ ] Empty state ada di semua list/table
- [ ] Error state ada di semua form
- [ ] Animasi smooth, tidak janky
- [ ] Ikon konsisten (semua dari Lucide)
- [ ] Contrast ratio minimal 4.5:1 (WCAG AA)
- [ ] Focus state visible untuk keyboard navigation

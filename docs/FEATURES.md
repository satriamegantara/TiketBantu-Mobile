# FEATURES.md — Daftar Fitur e-Notulen

## 📋 Overview

Dokumen ini berisi daftar lengkap fitur aplikasi e-Notulen, dikelompokkan berdasarkan modul dan prioritas pengembangan.

### Legenda Prioritas
| Label | Arti                              |
| ----- | --------------------------------- |
| 🔴 P0 | Must Have — Fitur wajib, MVP      |
| 🟡 P1 | Should Have — Penting, post-MVP   |
| 🟢 P2 | Nice to Have — Enhancement, v2    |

### Legenda Role
| Kode | Role                              |
| ---- | --------------------------------- |
| A    | Admin (Super Admin)               |
| O    | Operator (User Kantor/OPD)        |
| F    | Officer (User Pegawai)            |

### Data Source
| Data          | Sumber                                          |
| ------------- | ----------------------------------------------- |
| User          | JWT Token dari Keycloak (realm: `apps`)         |
| OPD/SKPD      | API External (`api.pegawai.e-kinerja...`)       |
| Notulen       | PostgreSQL (local database)                     |
| Foto/File     | MinIO (local object storage)                    |

---

## 1. 🔐 Modul Autentikasi

| #   | Fitur                                   | Prioritas | Role  | Deskripsi |
| --- | --------------------------------------- | --------- | ----- | --------- |
| 1.1 | Login via Keycloak (OIDC)               | 🔴 P0     | A/O/F | User login melalui halaman Keycloak (`accounts.purbalinggakab.go.id`), redirect balik ke aplikasi dengan JWT token |
| 1.2 | Logout                                  | 🔴 P0     | A/O/F | Logout dari aplikasi dan Keycloak session, clear cookies |
| 1.3 | Auto Token Refresh                      | 🔴 P0     | A/O/F | Refresh token otomatis sebelum expired tanpa gangguan UX |
| 1.4 | Session Management                      | 🔴 P0     | A/O/F | Redirect ke login jika session expired, protect semua route |
| 1.5 | User Context dari JWT                   | 🔴 P0     | A/O/F | Extract role, nama, OPD, NIP dari JWT token tanpa query database |

---

## 2. 📊 Modul Dashboard

| #   | Fitur                                   | Prioritas | Role  | Deskripsi |
| --- | --------------------------------------- | --------- | ----- | --------- |
| 2.1 | Dashboard Overview                      | 🔴 P0     | A/O/F | Halaman utama setelah login dengan ringkasan data |
| 2.2 | Statistik Total Notulen                 | 🔴 P0     | A/O/F | Jumlah total notulen (sesuai akses role) |
| 2.3 | Statistik Notulen Bulan Ini             | 🔴 P0     | A/O/F | Jumlah notulen yang dibuat bulan ini |
| 2.4 | Statistik Draft vs Final                | 🔴 P0     | A/O/F | Perbandingan notulen draft dan final |
| 2.5 | Notulen Terbaru                         | 🔴 P0     | A/O/F | Daftar 5-10 notulen terbaru |
| 2.6 | Grafik Notulen per Bulan                | 🟡 P1     | A/O   | Bar/line chart jumlah notulen 12 bulan terakhir |
| 2.7 | Grafik Notulen per OPD                  | 🟡 P1     | A     | Pie/bar chart distribusi notulen per OPD |
| 2.8 | Kalender Kegiatan                       | 🟢 P2     | A/O/F | Calendar view notulen berdasarkan tanggal rapat |
| 2.9 | Quick Actions                           | 🟡 P1     | A/O/F | Shortcut buttons: Buat Notulen, Lihat Semua, dll |

---

## 3. 📝 Modul Notulen (Inti)

### 3.1 Manajemen Notulen

| #     | Fitur                                 | Prioritas | Role  | Deskripsi |
| ----- | ------------------------------------- | --------- | ----- | --------- |
| 3.1.1 | Buat Notulen Baru                     | 🔴 P0     | A/O/F | Form pembuatan notulen baru dengan field metadata |
| 3.1.2 | Edit Notulen                          | 🔴 P0     | A/O/F | Edit notulen yang sudah dibuat (sesuai akses) |
| 3.1.3 | Hapus Notulen (Soft Delete)           | 🔴 P0     | A/O/F | Hapus notulen ke trash (sesuai akses), bisa di-restore |
| 3.1.4 | Lihat Detail Notulen                  | 🔴 P0     | A/O/F | Tampilkan notulen lengkap dalam mode read-only |
| 3.1.5 | Daftar Notulen (List View)            | 🔴 P0     | A/O/F | Tabel/list semua notulen dengan pagination |
| 3.1.6 | Status Notulen (Draft/Final)          | 🔴 P0     | A/O/F | Toggle status antara Draft dan Final |
| 3.1.7 | Duplikat Notulen                      | 🟡 P1     | A/O/F | Salin notulen existing sebagai notulen baru (template) |
| 3.1.8 | Restore Notulen                       | 🟡 P1     | A/O   | Kembalikan notulen yang sudah dihapus |

### 3.2 Metadata Notulen

| #     | Fitur                                 | Prioritas | Role  | Deskripsi |
| ----- | ------------------------------------- | --------- | ----- | --------- |
| 3.2.1 | Judul Rapat                           | 🔴 P0     | A/O/F | Input judul rapat |
| 3.2.2 | Hari & Tanggal                        | 🔴 P0     | A/O/F | Date picker untuk tanggal rapat |
| 3.2.3 | Waktu (Mulai - Selesai)               | 🔴 P0     | A/O/F | Time picker waktu mulai, waktu selesai (bisa "selesai") |
| 3.2.4 | Tempat Rapat                          | 🔴 P0     | A/O/F | Input lokasi rapat |
| 3.2.5 | Dipimpin Oleh                         | 🔴 P0     | A/O/F | Input nama + jabatan + NIP pemimpin rapat |
| 3.2.6 | Notulis                               | 🔴 P0     | A/O/F | Input nama + jabatan + NIP notulis |
| 3.2.7 | OPD (Instansi)                        | 🔴 P0     | A/O/F | Auto-filled dari JWT token (Operator: `kode_unit`, Officer: `officer.unit.id`). Admin bisa pilih dari dropdown API SKPD |
| 3.2.8 | Pilihan Tipe Input                    | 🔴 P0     | A/O/F | Pilih antara "Tulis Langsung (Editor)" atau "Upload File (PDF/Docx)" |

### 3.3 Daftar Hadir

| #     | Fitur                                 | Prioritas | Role  | Deskripsi |
| ----- | ------------------------------------- | --------- | ----- | --------- |
| 3.3.1 | Tambah Peserta                        | 🔴 P0     | A/O/F | Input manual: nama + instansi + jabatan (opsional) |
| 3.3.2 | Edit Peserta                          | 🔴 P0     | A/O/F | Edit data peserta yang sudah ditambahkan |
| 3.3.3 | Hapus Peserta                         | 🔴 P0     | A/O/F | Hapus peserta dari daftar hadir |
| 3.3.4 | Urutkan Peserta (Drag & Drop)         | 🟡 P1     | A/O/F | Drag & drop untuk mengubah urutan peserta |
| 3.3.5 | Bulk Input Peserta                    | 🟢 P2     | A/O/F | Paste dari spreadsheet (tab-separated) untuk bulk add |
| 3.3.6 | Jumlah Peserta Otomatis               | 🔴 P0     | A/O/F | Hitung otomatis jumlah peserta yang terdaftar |

### 3.4 Rich Text Editor (Tiptap)

| #     | Fitur                                 | Prioritas | Role  | Deskripsi |
| ----- | ------------------------------------- | --------- | ----- | --------- |
| 3.4.1 | Text Formatting (Bold, Italic, dll)   | 🔴 P0     | A/O/F | Format teks: bold, italic, underline, strikethrough |
| 3.4.2 | Heading Levels (H1-H3)               | 🔴 P0     | A/O/F | Heading 1, 2, 3 untuk struktur konten |
| 3.4.3 | Ordered & Unordered List              | 🔴 P0     | A/O/F | Numbered list dan bullet list |
| 3.4.4 | Text Alignment                        | 🟡 P1     | A/O/F | Rata kiri, tengah, kanan, justify |
| 3.4.5 | Table                                 | 🟡 P1     | A/O/F | Insert dan edit tabel (untuk data terstruktur) |
| 3.4.6 | Horizontal Rule                       | 🟡 P1     | A/O/F | Garis pembatas horizontal |
| 3.4.7 | Undo/Redo                             | 🔴 P0     | A/O/F | Undo dan redo perubahan editor |
| 3.4.8 | Placeholder Text                      | 🔴 P0     | A/O/F | Placeholder "Mulai menulis isi notulen..." |
| 3.4.9 | Word Count                            | 🟢 P2     | A/O/F | Jumlah kata dan karakter di footer editor |

### 3.5 Auto-Save

| #     | Fitur                                 | Prioritas | Role  | Deskripsi |
| ----- | ------------------------------------- | --------- | ----- | --------- |
| 3.5.1 | Auto-Save Content                     | 🔴 P0     | A/O/F | Otomatis simpan konten 2 detik setelah berhenti mengetik |
| 3.5.2 | Save Status Indicator                 | 🔴 P0     | A/O/F | Indikator visual: Tersimpan / Menyimpan / Gagal |
| 3.5.3 | Manual Save                           | 🔴 P0     | A/O/F | Tombol save manual (Ctrl+S) |
| 3.5.4 | Auto-Save History                     | 🟡 P1     | A/O/F | Lihat riwayat auto-save (20 terakhir), bisa restore |
| 3.5.5 | Offline Queue                         | 🟢 P2     | A/O/F | Queue auto-save saat offline, sync saat online kembali |

### 3.6 Dokumentasi Foto

| #     | Fitur                                 | Prioritas | Role  | Deskripsi |
| ----- | ------------------------------------- | --------- | ----- | --------- |
| 3.6.1 | Upload Foto                           | 🔴 P0     | A/O/F | Upload multiple foto dokumentasi kegiatan |
| 3.6.2 | Preview Foto                          | 🔴 P0     | A/O/F | Preview foto dalam grid/carousel |
| 3.6.3 | Hapus Foto                            | 🔴 P0     | A/O/F | Hapus foto yang sudah di-upload |
| 3.6.4 | Reorder Foto                          | 🟡 P1     | A/O/F | Drag & drop untuk mengubah urutan foto |
| 3.6.5 | Lightbox View                         | 🟡 P1     | A/O/F | Klik foto untuk lihat full-size dalam lightbox |
| 3.6.6 | Image Compression                     | 🔴 P0     | -     | Backend auto-compress foto (max 1920px, 85% quality) |
| 3.6.7 | File Size Limit                       | 🔴 P0     | -     | Maksimal 10MB per foto, format JPG/PNG |
| 3.6.8 | Max Upload Count                      | 🔴 P0     | -     | Maksimal 20 foto per notulen |

---

## 4. 📥 Modul Unduh / Export

| #   | Fitur                                   | Prioritas | Role  | Deskripsi |
| --- | --------------------------------------- | --------- | ----- | --------- |
| 4.1 | Unduh Notulen (Generate PDF)            | 🔴 P0     | A/O/F | Generate PDF jika input menggunakan Editor Tulis Langsung |
| 4.2 | Unduh Notulen (File Asli)               | 🔴 P0     | A/O/F | Download file asli (PDF/Docx) jika input menggunakan Upload File |
| 4.3 | PDF dengan Kop Surat                    | 🔴 P0     | -     | PDF termasuk kop surat OPD (logo, nama, alamat) dari `opd_settings` (hanya untuk generate PDF) |
| 4.3 | PDF dengan Daftar Hadir                 | 🔴 P0     | -     | PDF termasuk daftar hadir lengkap |
| 4.4 | PDF dengan Tanda Tangan                 | 🔴 P0     | -     | PDF termasuk blok tanda tangan (Pimpinan & Notulis) dengan NIP |
| 4.5 | PDF dengan Dokumentasi                  | 🟡 P1     | -     | PDF termasuk halaman foto dokumentasi kegiatan |
| 4.6 | Preview PDF                             | 🟡 P1     | A/O/F | Preview PDF di browser sebelum download |

---

## 5. 🔍 Modul Pencarian & Filter

| #   | Fitur                                   | Prioritas | Role  | Deskripsi |
| --- | --------------------------------------- | --------- | ----- | --------- |
| 5.1 | Pencarian Global                        | 🔴 P0     | A/O/F | Search bar di top bar, cari berdasarkan judul |
| 5.2 | Full-Text Search                        | 🔴 P0     | A/O/F | Pencarian di judul + isi notulen (PostgreSQL FTS) |
| 5.3 | Filter by OPD                           | 🔴 P0     | A     | Filter notulen berdasarkan OPD (dropdown dari API SKPD) |
| 5.4 | Filter by Tanggal                       | 🔴 P0     | A/O/F | Filter berdasarkan range tanggal rapat |
| 5.5 | Filter by Status                        | 🔴 P0     | A/O/F | Filter berdasarkan status (Draft/Final/Semua) |
| 5.6 | Sort Options                            | 🔴 P0     | A/O/F | Sort by tanggal, judul, dibuat (asc/desc) |
| 5.7 | Search Highlight                        | 🟡 P1     | A/O/F | Highlight kata yang dicari di hasil pencarian |
| 5.8 | Advanced Filter                         | 🟢 P2     | A/O/F | Filter kombinasi: peserta, pimpinan rapat, lokasi |
| 5.9 | Saved Search / Filter                   | 🟢 P2     | A/O/F | Simpan filter yang sering digunakan |

---

## 6. 📈 Modul Rekap Laporan

| #   | Fitur                                   | Prioritas | Role  | Deskripsi |
| --- | --------------------------------------- | --------- | ----- | --------- |
| 6.1 | Rekap per Bulan                         | 🔴 P0     | A/O   | Jumlah notulen per bulan dalam setahun (tabel + chart) |
| 6.2 | Rekap per OPD                           | 🔴 P0     | A     | Jumlah notulen per OPD (ranking + chart) |
| 6.3 | Rekap per Tahun                         | 🔴 P0     | A/O   | Perbandingan antar tahun |
| 6.4 | Daftar Kegiatan Rapat                   | 🔴 P0     | A/O   | Tabel daftar semua kegiatan rapat (judul, tanggal, OPD, status) |
| 6.5 | Statistik Peserta                       | 🟡 P1     | A/O   | Total peserta rapat, rata-rata per rapat |
| 6.6 | Statistik Lokasi                        | 🟡 P1     | A/O   | Lokasi rapat paling sering digunakan |
| 6.7 | Export Laporan ke Excel                 | 🔴 P0     | A/O   | Download rekap laporan dalam format Excel (.xlsx) |
| 6.8 | Filter Laporan by Periode              | 🔴 P0     | A/O   | Filter laporan berdasarkan range tanggal / bulan / tahun |
| 6.9 | Print Laporan                           | 🟡 P1     | A/O   | Print-friendly layout untuk cetak langsung |
| 6.10| Trend Analysis                          | 🟢 P2     | A     | Analisis tren kegiatan rapat (naik/turun) |

---

## 7. 🏢 Modul Setting Kop Surat OPD

> **Note**: Data master OPD diambil dari API external SKPD. Modul ini hanya untuk mengelola pengaturan kop surat (logo, alamat, dll) yang disimpan lokal di tabel `opd_settings`.

| #   | Fitur                                   | Prioritas | Role  | Deskripsi |
| --- | --------------------------------------- | --------- | ----- | --------- |
| 7.1 | Lihat Setting Kop Surat OPD            | 🔴 P0     | A/O   | Lihat konfigurasi kop surat OPD sendiri (atau semua untuk Admin) |
| 7.2 | Upload Logo OPD                         | 🔴 P0     | A/O   | Upload logo OPD ke MinIO untuk dipakai di kop surat PDF |
| 7.3 | Edit Informasi Kop Surat               | 🔴 P0     | A/O   | Edit alamat, telepon, fax, email untuk kop surat |
| 7.4 | Preview Kop Surat                       | 🟡 P1     | A/O   | Preview tampilan kop surat sebelum digunakan di PDF |
| 7.5 | Daftar OPD (dari API)                  | 🔴 P0     | A     | Admin bisa lihat daftar semua OPD dari API SKPD external |

---

## 8. ⚙️ Modul Pengaturan

| #   | Fitur                                   | Prioritas | Role  | Deskripsi |
| --- | --------------------------------------- | --------- | ----- | --------- |
| 8.1 | Profil Saya                             | 🟡 P1     | A/O/F | Lihat profil sendiri dari JWT (nama, email, NIP, jabatan, OPD) — read-only |
| 8.2 | Ganti Password                          | 🟡 P1     | A/O/F | Redirect ke Keycloak Account Console untuk ganti password |

> **Note**: Manajemen user (tambah/edit/hapus/role) dilakukan via **Keycloak Admin Console** oleh administrator sistem, bukan dari aplikasi e-Notulen.

---

## 9. 🛠️ Modul Sistem (Non-UI)

| #    | Fitur                                  | Prioritas | Deskripsi |
| ---- | -------------------------------------- | --------- | --------- |
| 9.1  | Health Check API                       | 🔴 P0     | Endpoint `/health` dan `/health/db` untuk monitoring |
| 9.2  | Structured Logging                     | 🔴 P0     | JSON format logging di backend |
| 9.3  | CORS Configuration                     | 🔴 P0     | Whitelist frontend domain |
| 9.4  | Rate Limiting                          | 🟡 P1     | Rate limit pada endpoint upload dan search |
| 9.5  | Database Migrations                    | 🔴 P0     | Auto-run migrations saat startup |
| 9.6  | Auto-Save Cleanup Job                  | 🟡 P1     | Scheduled job: hapus auto-save lama (> 20 per notulen) |
| 9.7  | Soft Delete Cleanup                    | 🟢 P2     | Scheduled job: permanent delete setelah 30 hari di trash |
| 9.8  | Export File Cleanup                    | 🟡 P1     | Hapus temporary export files setelah 24 jam |
| 9.9  | Image Resize on Upload                 | 🔴 P0     | Auto resize dan compress foto saat upload |
| 9.10 | Database Backup Script                 | 🟡 P1     | Script backup PostgreSQL (cron daily) |
| 9.11 | OPD API Cache                          | 🔴 P0     | Cache response API SKPD dengan TTL 1 jam |
| 9.12 | JWT JWKS Cache                         | 🔴 P0     | Cache Keycloak JWKS public keys untuk validasi JWT |

---

## 📊 Ringkasan Prioritas

| Prioritas | Jumlah Fitur | Fokus                          |
| --------- | ------------ | ------------------------------ |
| 🔴 P0     | ~45 fitur    | MVP — Core functionality       |
| 🟡 P1     | ~20 fitur    | Post-MVP — Enhancement         |
| 🟢 P2     | ~8 fitur     | Future — Nice to have          |

---

## 🗓️ Roadmap Pengembangan

### Phase 1 — MVP (6-8 minggu)
- Setup infrastructure (Docker, DB, MinIO)
- Keycloak integration (login/logout/JWT validation)
- CRUD Notulen (metadata + Tiptap editor + daftar hadir)
- Auto-save
- Download PDF (dengan kop surat)
- Dashboard basic (statistik + notulen terbaru)
- Pencarian & filter
- OPD settings (kop surat)

### Phase 2 — Enhancement (3-5 minggu)
- Rekap laporan lengkap + export Excel
- Foto dokumentasi + PDF documentation page
- Auto-save history & restore
- Dashboard charts (Recharts)
- Improved search (highlight, advanced filter)

### Phase 3 — Polish (2-3 minggu)
- Responsive mobile optimization
- Performance optimization
- Offline queue
- Advanced features (P2)
- Security hardening
- Documentation

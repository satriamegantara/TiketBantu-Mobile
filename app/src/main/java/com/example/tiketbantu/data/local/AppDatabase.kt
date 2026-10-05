package com.example.tiketbantu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tiketbantu.data.local.dao.CategoryDao
import com.example.tiketbantu.data.local.dao.CommentDao
import com.example.tiketbantu.data.local.dao.SupportDao
import com.example.tiketbantu.data.local.dao.TicketDao
import com.example.tiketbantu.data.local.dao.UserDao
import com.example.tiketbantu.data.local.entity.CategoryEntity
import com.example.tiketbantu.data.local.entity.CommentEntity
import com.example.tiketbantu.data.local.entity.TicketEntity
import com.example.tiketbantu.data.local.entity.TicketSupportEntity
import com.example.tiketbantu.data.local.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        TicketEntity::class,
        TicketSupportEntity::class,
        CommentEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun ticketDao(): TicketDao
    abstract fun supportDao(): SupportDao
    abstract fun commentDao(): CommentDao

    companion object {
        const val DATABASE_NAME = "tiketbantu_local.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                ensureMasterData(db)
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                ensureMasterData(db)
            }
        }

        val MIGRATION_1_3 = object : Migration(1, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                ensureMasterData(db)
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                ensureMasterData(db)
                seedInitialData(db)
            }
        }

        val MIGRATION_1_4 = object : Migration(1, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                ensureMasterData(db)
                seedInitialData(db)
            }
        }

        val MIGRATION_2_4 = object : Migration(2, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                ensureMasterData(db)
                seedInitialData(db)
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                ensureMasterData(db)
            }
        }

        /**
         * Builds the Room Database instance with pre-populated campus mock data.
         */
        fun buildDatabase(context: Context, coroutineScope: CoroutineScope): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_1_3, MIGRATION_3_4, MIGRATION_1_4, MIGRATION_2_4, MIGRATION_4_5)
                .addCallback(DatabasePrepopulateCallback(coroutineScope))
                .fallbackToDestructiveMigration()
                .build()
        }

        private data class SeedUser(
            val id: Long,
            val name: String,
            val email: String,
            val nimNip: String,
            val passwordHash: String,
            val role: String
        )

        fun ensureMasterData(db: SupportSQLiteDatabase) {
            // Pastikan 4 Kategori resmi tersedia tanpa menghapus kategori yang sedang direferensikan tiket
            db.execSQL("INSERT OR IGNORE INTO categories (id, name) VALUES (1, 'Jaringan')")
            db.execSQL("INSERT OR IGNORE INTO categories (id, name) VALUES (2, 'Hardware')")
            db.execSQL("INSERT OR IGNORE INTO categories (id, name) VALUES (3, 'Software')")
            db.execSQL("INSERT OR IGNORE INTO categories (id, name) VALUES (4, 'Fasilitas')")
            db.execSQL("UPDATE categories SET name = 'Jaringan' WHERE id = 1")
            db.execSQL("UPDATE categories SET name = 'Hardware' WHERE id = 2")
            db.execSQL("UPDATE categories SET name = 'Software' WHERE id = 3")
            db.execSQL("UPDATE categories SET name = 'Fasilitas' WHERE id = 4")

            // Pastikan seluruh 10 akun resmi terdaftar tanpa menghapus akun pengguna lain
            val officialUsers = listOf(
                SeedUser(1, "Admin Sarpras", "admin@kampus.ac.id", "198001012010121001", "admin123", "ADMIN"),
                SeedUser(2, "Agen Jaringan", "agen.jaringan@tiketbantu.com", "198703122010", "agen123", "AGEN"),
                SeedUser(3, "satcarzensyaf", "satcarzensyaf@kampus.ac.id", "2021110045", "user123", "PELAPOR"),
                SeedUser(4, "Ahmad Dosen", "dosen@kampus.ac.id", "198505052012011003", "dosen123", "PELAPOR"),
                SeedUser(5, "Agen Hardware", "agen.hardware@tiketbantu.com", "198901012015011005", "agen123", "AGEN"),
                SeedUser(6, "Agen Software", "agen.software@tiketbantu.com", "199002022016021006", "agen123", "AGEN"),
                SeedUser(7, "Agen Fasilitas", "agen.fasilitas@tiketbantu.com", "199103032017031007", "agen123", "AGEN"),
                SeedUser(8, "Rina Kartika", "rina.kartika@kampus.ac.id", "2021110052", "user123", "PELAPOR"),
                SeedUser(9, "Dimas Putra", "dimas.putra@kampus.ac.id", "2021110088", "user123", "PELAPOR"),
                SeedUser(10, "Nadia Safitri", "nadia.safitri@kampus.ac.id", "2021110114", "user123", "PELAPOR")
            )
            for (u in officialUsers) {
                db.execSQL("""
                    INSERT OR IGNORE INTO users (id, name, email, nimNip, passwordHash, role, isActive)
                    VALUES (${u.id}, '${u.name}', '${u.email}', '${u.nimNip}', '${u.passwordHash}', '${u.role}', 1)
                """)
                db.execSQL("""
                    UPDATE users SET name = '${u.name}', email = '${u.email}', nimNip = '${u.nimNip}', passwordHash = '${u.passwordHash}', role = '${u.role}', isActive = 1
                    WHERE id = ${u.id}
                """)
            }
        }

        fun seedInitialData(db: SupportSQLiteDatabase) {
            ensureMasterData(db)

            val now = System.currentTimeMillis()
            val hour2 = now - 2 * 3600 * 1000L
            val hour3 = now - 3 * 3600 * 1000L
            val hour4 = now - 4 * 3600 * 1000L
            val hour5 = now - 5 * 3600 * 1000L
            val hour8 = now - 8 * 3600 * 1000L
            val hour12 = now - 12 * 3600 * 1000L
            val hour18 = now - 18 * 3600 * 1000L
            val day1 = now - 24 * 3600 * 1000L
            val day2 = now - 2 * 24 * 3600 * 1000L
            val day3 = now - 3 * 24 * 3600 * 1000L
            val day4 = now - 4 * 24 * 3600 * 1000L
            val day5 = now - 5 * 24 * 3600 * 1000L
            val day6 = now - 6 * 24 * 3600 * 1000L

            // Gunakan INSERT OR IGNORE agar tidak menghapus atau melanggar constraint foreign key

            // ── SEED 10 TIKET DUMMY TESTING ──────────────────────────────────
            // Tiket 1: Jaringan - Diproses oleh Agen Jaringan (id: 2)
            db.execSQL("""
                INSERT OR IGNORE INTO tickets (id, title, description, categoryId, locationBuilding, locationFloor, locationRoom, status, imageUrl, reporterId, agentId, createdAt, updatedAt, deletedAt)
                VALUES (1, 'Koneksi Wi-Fi Kampus Putus-Nyambung di Lantai 2 Gedung Perpustakaan', 'Sejak 2 hari terakhir, akses Wi-Fi kampus di area ruang baca mandiri lantai 2 sering request timed out (RTO) dan sinyal drop setiap 15 menit. Sangat mengganggu mahasiswa yang sedang menyusun skripsi dan mencari referensi jurnal.', 1, 'Perpustakaan Pusat', 'Lantai 2', 'Ruang Baca Mandiri', 'DIPROSES', NULL, 3, 2, $day3, $hour4, NULL)
            """)

            // Tiket 2: Hardware - Diproses oleh Agen Hardware (id: 5)
            db.execSQL("""
                INSERT OR IGNORE INTO tickets (id, title, description, categoryId, locationBuilding, locationFloor, locationRoom, status, imageUrl, reporterId, agentId, createdAt, updatedAt, deletedAt)
                VALUES (2, 'Proyektor Ruang 304 Mati Total Saat Perkuliahan Praktikum', 'Proyektor plafon di Ruang 304 tidak mau menyala saat saklar diaktifkan. Lampu indikator berkedip merah dan kipas terdengar mendengung kasar sebelum akhirnya padam. Kabel HDMI di meja dosen juga terlihat longgar.', 2, 'Gedung Thomas Aquinas', 'Lantai 3', 'Ruang Teori 304', 'DIPROSES', NULL, 4, 5, $day2, $hour2, NULL)
            """)

            // Tiket 3: Software - Selesai oleh Agen Software (id: 6)
            db.execSQL("""
                INSERT OR IGNORE INTO tickets (id, title, description, categoryId, locationBuilding, locationFloor, locationRoom, status, imageUrl, reporterId, agentId, createdAt, updatedAt, deletedAt)
                VALUES (3, 'Portal SIAKAD Mengalami Error 500 Saat Pengisian KRS Mahasiswa', 'Menu pemilihan mata kuliah pada portal akademik mahasiswa menampilkan pesan error 500 internal server error saat tombol Simpan KRS diklik. Hal ini dialami serentak oleh banyak mahasiswa semester ganjil.', 3, 'Gedung Rektorat & IT', 'Lantai 2', 'Pusat Komputer & Data', 'SELESAI', NULL, 8, 6, $day4, $day1, NULL)
            """)

            // Tiket 4: Fasilitas - Diproses oleh Agen Fasilitas (id: 7)
            db.execSQL("""
                INSERT OR IGNORE INTO tickets (id, title, description, categoryId, locationBuilding, locationFloor, locationRoom, status, imageUrl, reporterId, agentId, createdAt, updatedAt, deletedAt)
                VALUES (4, 'AC Sentral Ruang Kuliah Bersama Mengeluarkan Bunyi Berisik & Hawa Panas', 'Unit pendingin ruangan AC di sisi barat mengeluarkan suara gemuruh cukup keras dan hawa yang keluar tidak dingin sama sekali. Selain itu air kondensasi mulai menetes membasahi karpet lantai dekat pintu masuk.', 4, 'Gedung Kuliah Bersama', 'Lantai 2', 'Ruang Kuliah 201', 'DIPROSES', NULL, 9, 7, $day1, $hour3, NULL)
            """)

            // Tiket 5: Fasilitas - Menunggu (BARU) / Unassigned (agentId = null)
            db.execSQL("""
                INSERT OR IGNORE INTO tickets (id, title, description, categoryId, locationBuilding, locationFloor, locationRoom, status, imageUrl, reporterId, agentId, createdAt, updatedAt, deletedAt)
                VALUES (5, 'Stop Kontak Selasar Lantai 1 Gedung B Mengeluarkan Percikan Api', 'Stop kontak dinding di area tempat duduk selasar timur longgar dan sempat memercikkan api kecil saat dicolokkan adaptor laptop. Kondisi penutup stop kontak sudah retak dan rawan tersentuh tangan mahasiswa.', 4, 'Gedung Perkuliahan B', 'Lantai 1', 'Selasar Dekat Tangga Timur', 'BARU', NULL, 10, NULL, $hour8, $hour8, NULL)
            """)

            // Tiket 6: Hardware - Menunggu (BARU) / Unassigned (agentId = null)
            db.execSQL("""
                INSERT OR IGNORE INTO tickets (id, title, description, categoryId, locationBuilding, locationFloor, locationRoom, status, imageUrl, reporterId, agentId, createdAt, updatedAt, deletedAt)
                VALUES (6, 'PC Laboratorium Komputer 02 Mati Total dan Tidak Mau Booting', 'Komputer PC nomor 14 di Lab Komputer 02 tidak ada respon ketika ditekan tombol power. Indikator PSU di bagian belakang mati dan kabel power sudah dicoba dipindah ke colokan lain tetap tidak menyala.', 2, 'Laboratorium Terpadu', 'Lantai 2', 'Lab Komputer 02 (Meja 14)', 'BARU', NULL, 3, NULL, $hour5, $hour5, NULL)
            """)

            // Tiket 7: Software - Menunggu (BARU) / Unassigned (agentId = null)
            db.execSQL("""
                INSERT OR IGNORE INTO tickets (id, title, description, categoryId, locationBuilding, locationFloor, locationRoom, status, imageUrl, reporterId, agentId, createdAt, updatedAt, deletedAt)
                VALUES (7, 'Software MATLAB & SPSS pada Komputer Lab Statistik Belum Diaktivasi Lisensi', 'Aplikasi MATLAB R2023b dan IBM SPSS di seluruh PC Lab Statistik memunculkan dialog License Expired sehingga modul praktikum komputasi statistika mahasiswa minggu ini tidak dapat dijalankan.', 3, 'Gedung FTI', 'Lantai 3', 'Lab Komputasi Statistika', 'BARU', NULL, 4, NULL, $hour12, $hour12, NULL)
            """)

            // Tiket 8: Jaringan - Diproses oleh Agen Jaringan (id: 2)
            db.execSQL("""
                INSERT OR IGNORE INTO tickets (id, title, description, categoryId, locationBuilding, locationFloor, locationRoom, status, imageUrl, reporterId, agentId, createdAt, updatedAt, deletedAt)
                VALUES (8, 'Switch Hub Jaringan di Laboratorium Komputer Sering Mengalami Packet Loss', 'Switch 24-port di rak server Lab Jaringan mengalami packet loss tinggi saat simulasi konfigurasi routing protokol antar workstation. Port 1 hingga 8 sering berkedip oranye.', 1, 'Laboratorium Terpadu', 'Lantai 3', 'Lab Jaringan & Sistem', 'DIPROSES', NULL, 9, 2, $hour18, $hour2, NULL)
            """)

            // Tiket 9: Hardware - Selesai oleh Agen Hardware (id: 5)
            db.execSQL("""
                INSERT OR IGNORE INTO tickets (id, title, description, categoryId, locationBuilding, locationFloor, locationRoom, status, imageUrl, reporterId, agentId, createdAt, updatedAt, deletedAt)
                VALUES (9, 'Keyboard dan Mouse Komputer Nomor 08 di Lab Multimedia Rusak', 'Tombol spasi pada keyboard mekanik macet dan tombol klik kiri mouse optik tidak responsif saat digunakan mahasiswa praktikum desain multimedia.', 2, 'Gedung Thomas Aquinas', 'Lantai 3', 'Lab Multimedia R.301', 'SELESAI', NULL, 8, 5, $day5, $day2, NULL)
            """)

            // Tiket 10: Fasilitas - Selesai oleh Agen Fasilitas (id: 7)
            db.execSQL("""
                INSERT OR IGNORE INTO tickets (id, title, description, categoryId, locationBuilding, locationFloor, locationRoom, status, imageUrl, reporterId, agentId, createdAt, updatedAt, deletedAt)
                VALUES (10, 'Plafon Gypsum Koridor Lantai 3 Gedung Rektorat Lapuk dan Rawan Ambruk', 'Plafon gypsum di lorong sayap utara lantai 3 melengkung ke bawah dan berwarna kecokelatan akibat rembesan air hujan dari atap. Sangat berisiko jatuh menimpa orang yang melintas.', 4, 'Gedung Rektorat', 'Lantai 3', 'Koridor Sayap Utara', 'SELESAI', NULL, 3, 7, $day6, $day3, NULL)
            """)

            // ── SEED DUKUNGAN TIKET (SAYA JUGA MENGALAMI) ───────────────────
            // Tiket 1 (6 dukungan: pembuat=3, user 4, 8, 9, 10, 1)
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (1, 3, $day3)")
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (1, 4, $day2)")
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (1, 8, $day1)")
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (1, 9, $hour18)")
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (1, 10, $hour12)")
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (1, 1, $hour5)")

            // Tiket 2 (4 dukungan: pembuat=4, user 3, 8, 9)
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (2, 4, $day2)")
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (2, 3, $day1)")
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (2, 8, $hour18)")
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (2, 9, $hour8)")

            // Tiket 3 (4 dukungan: pembuat=8, user 3, 9, 10)
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (3, 8, $day4)")
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (3, 3, $day3)")
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (3, 9, $day2)")
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (3, 10, $day2)")

            // Tiket 4 (3 dukungan: pembuat=9, user 3, 8)
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (4, 9, $day1)")
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (4, 3, $hour18)")
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (4, 8, $hour8)")

            // Tiket 5 (3 dukungan: pembuat=10, user 3, 4)
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (5, 10, $hour8)")
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (5, 3, $hour5)")
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (5, 4, $hour2)")

            // Tiket 6 (2 dukungan: pembuat=3, user 9)
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (6, 3, $hour5)")
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (6, 9, $hour3)")

            // Tiket 7 (2 dukungan: pembuat=4, user 8)
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (7, 4, $hour12)")
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (7, 8, $hour4)")

            // Tiket 8 (1 dukungan: pembuat=9)
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (8, 9, $hour18)")

            // Tiket 9 (1 dukungan: pembuat=8)
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (9, 8, $day5)")

            // Tiket 10 (1 dukungan: pembuat=3)
            db.execSQL("INSERT OR IGNORE INTO ticket_supports (ticketId, userId, createdAt) VALUES (10, 3, $day6)")

            // ── SEED KOMENTAR DISKUSI ADUAN ──────────────────────────────────
            // Komentar Tiket 1 (2 komentar)
            db.execSQL("""
                INSERT OR IGNORE INTO comments (ticketId, userId, content, createdAt)
                VALUES (1, 8, 'Betul sekali, tadi siang saya coba sambung ke SSID kampus juga sering stuck di Obtaining IP address.', $day1)
            """)
            db.execSQL("""
                INSERT OR IGNORE INTO comments (ticketId, userId, content, createdAt)
                VALUES (1, 2, 'Laporan sedang ditindaklanjuti. Tim NOC sedang merestart access point sektor barat dan memperluas alokasi IP DHCP pool perpustakaan.', $hour4)
            """)

            // Komentar Tiket 2 (2 komentar)
            db.execSQL("""
                INSERT OR IGNORE INTO comments (ticketId, userId, content, createdAt)
                VALUES (2, 3, 'Kemarin saat kelas Pemrograman Web juga sempat mati mendadak di tengah presentasi kelompok.', $day1)
            """)
            db.execSQL("""
                INSERT OR IGNORE INTO comments (ticketId, userId, content, createdAt)
                VALUES (2, 5, 'Tiket sudah diklaim. Kami sudah siapkan unit proyektor pengganti (spare) dan tangga teknisi untuk instalasi siang ini.', $hour2)
            """)

            // Komentar Tiket 3 (3 komentar)
            db.execSQL("""
                INSERT OR IGNORE INTO comments (ticketId, userId, content, createdAt)
                VALUES (3, 9, 'Konfirmasi, saya coba submit KRS juga muncul error yang sama dan sesi langsung keluar otomatis.', $day3)
            """)
            db.execSQL("""
                INSERT OR IGNORE INTO comments (ticketId, userId, content, createdAt)
                VALUES (3, 6, 'Terjadi lonjakan antrean query pada database master. Connection pool telah kami optimasi dan database server sudah di-tuning.', $day2)
            """)
            db.execSQL("""
                INSERT OR IGNORE INTO comments (ticketId, userId, content, createdAt)
                VALUES (3, 8, 'Alhamdulillah sekarang pengisian KRS sudah normal kembali dan berhasil disimpan. Terima kasih banyak tim IT!', $day1)
            """)

            // Komentar Tiket 4 (1 komentar)
            db.execSQL("""
                INSERT OR IGNORE INTO comments (ticketId, userId, content, createdAt)
                VALUES (4, 7, 'Teknisi sarpras sedang menuju lokasi untuk memeriksa kompresor AC dan membersihkan saluran drainase pipa yang tersumbat.', $hour3)
            """)

            // Komentar Tiket 5 (1 komentar)
            db.execSQL("""
                INSERT OR IGNORE INTO comments (ticketId, userId, content, createdAt)
                VALUES (5, 4, 'Mohon segera ditindaklanjuti dan dipasang tanda peringatan sementara agar tidak ada korban tersengat arus listrik.', $hour2)
            """)

            // Tiket 6: 0 Komentar (Empty comment state)
            // Tiket 7: 0 Komentar (Empty comment state)

            // Komentar Tiket 8 (1 komentar)
            db.execSQL("""
                INSERT OR IGNORE INTO comments (ticketId, userId, content, createdAt)
                VALUES (8, 2, 'Kami sedang memeriksa jalur kabel patch cord UTP dan konfigurasi VLAN port switch lab.', $hour2)
            """)

            // Komentar Tiket 9 (2 komentar)
            db.execSQL("""
                INSERT OR IGNORE INTO comments (ticketId, userId, content, createdAt)
                VALUES (9, 5, 'Keyboard dan mouse meja 08 telah diganti baru dari gudang inventaris sarpras.', $day2)
            """)
            db.execSQL("""
                INSERT OR IGNORE INTO comments (ticketId, userId, content, createdAt)
                VALUES (9, 8, 'Sudah dicoba dan berfungsi dengan baik. Terima kasih banyak atas respon cepatnya!', $day2)
            """)

            // Komentar Tiket 10 (1 komentar)
            db.execSQL("""
                INSERT OR IGNORE INTO comments (ticketId, userId, content, createdAt)
                VALUES (10, 7, 'Sumber bocoran talang air sudah ditambal dengan pelapis anti bocor dan 2 panel gypsum plafon yang lapuk telah diganti baru.', $day3)
            """)
        }
    }

    /**
     * Database callback that seeds and guarantees campus data upon creation and open.
     */
    private class DatabasePrepopulateCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            ensureMasterData(db)
            seedInitialData(db)
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            ensureMasterData(db)
            try {
                val cursor = db.query("SELECT COUNT(*) FROM tickets WHERE deletedAt IS NULL")
                val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
                cursor.close()
                if (count == 0) {
                    seedInitialData(db)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

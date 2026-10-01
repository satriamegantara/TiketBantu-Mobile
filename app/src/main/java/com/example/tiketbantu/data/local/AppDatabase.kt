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
    version = 1,
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

        /**
         * Placeholder migration builder for schema evolution.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Prepared for future version upgrades
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
                .addCallback(DatabasePrepopulateCallback(coroutineScope))
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    /**
     * Database callback that seeds initial campus data upon first database creation.
     */
    private class DatabasePrepopulateCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Prepopulate executed asynchronously
            scope.launch(Dispatchers.IO) {
                // Prepopulation SQL executed directly via SupportSQLiteDatabase
                seedInitialData(db)
            }
        }

        private fun seedInitialData(db: SupportSQLiteDatabase) {
            // 1. Seed Categories
            db.execSQL("INSERT INTO categories (id, name) VALUES (1, 'Teknologi & IT')")
            db.execSQL("INSERT INTO categories (id, name) VALUES (2, 'Fasilitas Ruangan')")
            db.execSQL("INSERT INTO categories (id, name) VALUES (3, 'Infrastruktur Umum')")

            // 2. Seed Users (Admin, Agen, Pelapor)
            db.execSQL("""
                INSERT INTO users (id, name, email, nim_nip, password_hash, role, is_active)
                VALUES (1, 'Admin Sarpras', 'admin@kampus.ac.id', '198001012010121001', 'admin123', 'ADMIN', 1)
            """)
            db.execSQL("""
                INSERT INTO users (id, name, email, nim_nip, password_hash, role, is_active)
                VALUES (2, 'Budi Teknisi', 'agen@kampus.ac.id', '199002022015041002', 'agen123', 'AGEN', 1)
            """)
            db.execSQL("""
                INSERT INTO users (id, name, email, nim_nip, password_hash, role, is_active)
                VALUES (3, 'Siti Mahasiswa', 'user@kampus.ac.id', '2100018001', 'user123', 'PELAPOR', 1)
            """)
            db.execSQL("""
                INSERT INTO users (id, name, email, nim_nip, password_hash, role, is_active)
                VALUES (4, 'Ahmad Dosen', 'dosen@kampus.ac.id', '198505052012011003', 'dosen123', 'PELAPOR', 1)
            """)

            val now = System.currentTimeMillis()
            val hourAgo = now - 3600000L
            val dayAgo = now - 86400000L

            // 3. Seed Tickets (Public Complaints)
            // Tiket 1: WiFi Lemot (Status: BARU, Most Liked target)
            db.execSQL("""
                INSERT INTO tickets (id, title, description, category_id, location_building, location_floor, location_room, status, image_url, reporter_id, agent_id, created_at, updated_at, deleted_at)
                VALUES (1, 'WiFi Kampus di Perpustakaan Lt 2 Sangat Lambat & Putus-Putus', 'Koneksi internet di area baca lantai 2 sering request timed out sejak pagi. Sangat mengganggu mahasiswa yang mengerjakan tugas akhir.', 1, 'Perpustakaan Pusat', 'Lantai 2', 'Area Ruang Baca', 'BARU', NULL, 3, NULL, $dayAgo, $dayAgo, NULL)
            """)

            // Tiket 2: Proyektor Mati (Status: DIPROSES, diklaim oleh Budi Teknisi)
            db.execSQL("""
                INSERT INTO tickets (id, title, description, category_id, location_building, location_floor, location_room, status, image_url, reporter_id, agent_id, created_at, updated_at, deleted_at)
                VALUES (2, 'Proyektor Ruang 302 Mati Total & Berbunyi Bising', 'Proyektor tidak mau menyala saat kabel VGA/HDMI dicolokkan ke laptop, lampu indikator berkedip merah dan kipas berbunyi keras.', 2, 'Gedung Kuliah Bersama (GKB)', 'Lantai 3', 'Ruang 302', 'DIPROSES', NULL, 4, 2, $hourAgo, $now, NULL)
            """)

            // Tiket 3: Keran Air Bocor (Status: SELESAI -> Menempati posisi terbawah dengan centang hijau)
            db.execSQL("""
                INSERT INTO tickets (id, title, description, category_id, location_building, location_floor, location_room, status, image_url, reporter_id, agent_id, created_at, updated_at, deleted_at)
                VALUES (3, 'Keran Wastafel Toilet Lantai 1 Bocor Terus-Menerus', 'Air mengalir terus dari pipa sambungan wastafel menyebabkan lantai toilet menjadi licin dan tergenang air.', 3, 'Gedung B', 'Lantai 1', 'Toilet Pria Barat', 'SELESAI', NULL, 3, 2, $dayAgo, $hourAgo, NULL)
            """)

            // 4. Seed Ticket Supports ("Saya Juga Mengalami" / Most Liked)
            // Tiket 1 didukung oleh Siti Mahasiswa (id 3) dan Ahmad Dosen (id 4) -> Total 2 dukungan (Most Liked)
            db.execSQL("INSERT INTO ticket_supports (ticket_id, user_id, created_at) VALUES (1, 3, $dayAgo)")
            db.execSQL("INSERT INTO ticket_supports (ticket_id, user_id, created_at) VALUES (1, 4, $hourAgo)")

            // Tiket 2 didukung oleh Siti Mahasiswa (id 3) -> Total 1 dukungan
            db.execSQL("INSERT INTO ticket_supports (ticket_id, user_id, created_at) VALUES (2, 3, $hourAgo)")

            // 5. Seed Comments
            db.execSQL("""
                INSERT INTO comments (ticket_id, user_id, content, created_at)
                VALUES (2, 2, 'Laporan diterima. Tim teknisi sedang menyiapkan unit cadangan untuk dilakukan penggantian proyektor di R.302 siang ini.', $now)
            """)
            db.execSQL("""
                INSERT INTO comments (ticket_id, user_id, content, created_at)
                VALUES (3, 2, 'Pipa keran wastafel sudah diperbaiki dan segel telah diganti baru. Kondisi sudah normal.', $hourAgo)
            """)
        }
    }
}

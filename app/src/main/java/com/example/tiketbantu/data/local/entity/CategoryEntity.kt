package com.example.tiketbantu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for Facility Complaint Categories.
 * e.g., Teknologi & IT, Fasilitas Ruangan, Infrastruktur Umum.
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String
)

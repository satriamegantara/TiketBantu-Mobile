package com.example.tiketbantu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Table "categories". */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

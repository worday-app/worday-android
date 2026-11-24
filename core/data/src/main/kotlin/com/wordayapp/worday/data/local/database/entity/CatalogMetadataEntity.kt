package com.wordayapp.worday.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalog_metadata")
data class CatalogMetadataEntity(
    @PrimaryKey val id: Int = 1,
    val version: Int
)

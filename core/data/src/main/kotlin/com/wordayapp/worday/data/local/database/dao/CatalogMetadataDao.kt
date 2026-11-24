package com.wordayapp.worday.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wordayapp.worday.data.local.database.entity.CatalogMetadataEntity

@Dao
interface CatalogMetadataDao {

    @Query("SELECT version FROM catalog_metadata WHERE id = 1")
    suspend fun getVersion(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setVersion(metadata: CatalogMetadataEntity)
}

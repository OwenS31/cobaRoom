package com.example.cobaroom.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface historyBarangDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(history: historyBarang)

    @Query("SELECT * FROM historyBarang ORDER BY id ASC")
    fun selectAll(): MutableList<historyBarang>

}
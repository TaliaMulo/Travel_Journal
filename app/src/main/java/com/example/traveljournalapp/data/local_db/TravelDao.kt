package com.example.traveljournalapp.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.traveljournalapp.data.model.Travel

@Dao
interface TravelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTravel(travel: Travel)

    @Delete
    suspend fun deleteTravel(vararg travel: Travel)

    @Update
    suspend fun updateTravel(travel: Travel)

    @Query("DELETE FROM travels")
    suspend fun deleteAll()

    @Query("SELECT * FROM travels ORDER BY country_name ASC")
    fun getItem() : LiveData<List<Travel>>

    @Query("SELECT * FROM travels WHERE id LIKE :id")
    fun getItem(id : Int) : Travel
}
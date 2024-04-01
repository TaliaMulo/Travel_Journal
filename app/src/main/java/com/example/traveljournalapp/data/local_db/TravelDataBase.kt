package com.example.traveljournalapp.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.traveljournalapp.data.model.Travel

@Database(entities = arrayOf(Travel::class) , version = 1 , exportSchema = false)
abstract class TravelDataBase : RoomDatabase() {

    abstract fun travelsDao() : TravelDao

    companion object{

        @Volatile
        private var instance : TravelDataBase? = null

        fun getDatabase(context: Context) = instance ?: synchronized(this){
            Room.databaseBuilder(context.applicationContext , TravelDataBase::class.java , "travels_db")
                .build()
        }
    }
}
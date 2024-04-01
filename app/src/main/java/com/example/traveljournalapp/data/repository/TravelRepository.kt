package com.example.traveljournalapp.data.repository

import android.app.Application
import com.example.traveljournalapp.data.local_db.TravelDao
import com.example.traveljournalapp.data.local_db.TravelDataBase
import com.example.traveljournalapp.data.model.Travel

class TravelRepository (application: Application){

    private var travelDao : TravelDao?

    init {
        val db = TravelDataBase.getDatabase(application.applicationContext)
        travelDao = db?.travelsDao()
    }

    fun getTravels() = travelDao?.getItem()

    suspend fun addTravel(travel: Travel){
        travelDao?.addTravel(travel)
    }

    suspend fun deleteTravel(travel: Travel){
        travelDao?.deleteTravel(travel)
    }

    fun getTravel(id : Int) = travelDao?.getItem(id)

    suspend fun updateTravel(travel: Travel){
        travelDao?.updateTravel(travel)
    }

    suspend fun deleteAll(){
        travelDao?.deleteAll()
    }
}
package com.example.traveljournalapp.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "travels")
data class Travel(
    @ColumnInfo(name = "country_name")
    val countryName : String,
    @ColumnInfo(name = "destination_name")
    val destinationName : String,
    @ColumnInfo(name = "destination_description")
    val destinationDescription : String,
    @ColumnInfo(name = "image")
    val photo : String?,
    @ColumnInfo(name = "location")
    val location : String,
    @ColumnInfo(name = "date")
    val date : String) : Parcelable {

        @PrimaryKey(autoGenerate = true)
        var id : Int = 0
}
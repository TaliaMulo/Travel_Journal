package com.example.traveljournalapp.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.traveljournalapp.data.LocationUpdatesLiveData
import com.example.traveljournalapp.data.model.Travel
import com.example.traveljournalapp.data.repository.TravelRepository
import kotlinx.coroutines.launch

class TravelsViewModel(application: Application) : AndroidViewModel(application) {

    val location : LiveData<String> = LocationUpdatesLiveData(application.applicationContext)

    private val repository = TravelRepository(application)

    val travels : LiveData<List<Travel>>? = repository.getTravels()

    private val _chosenTravel = MutableLiveData<Travel>()

    val chosenTravel : LiveData<Travel> get() = _chosenTravel

    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> = _selectedImageUri

    private val _selectedLocation = MutableLiveData<String>()
    val selectedLocation: LiveData<String> = _selectedLocation

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> = _selectedDate

    private val _editSelectedImageUri = MutableLiveData<Uri?>()
    val editSelectedImageUri: LiveData<Uri?> = _editSelectedImageUri

    private val _editSelectedLocation = MutableLiveData<String>()
    val editSelectedLocation: LiveData<String> = _editSelectedLocation

    private val _editSelectedDate = MutableLiveData<String>()
    val editSelectedDate: LiveData<String> = _editSelectedDate

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun setSelectedLocation(location: String) {
        _selectedLocation.value = location
    }

    fun editSetSelectedDate(date: String) {
        _editSelectedDate.value = date
    }

    fun editSetSelectedImageUri(uri: Uri?) {
        _editSelectedImageUri.value = uri
    }

    fun editSetSelectedLocation(location: String) {
        _editSelectedLocation.value = location
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun setTravel(travel: Travel){
        _chosenTravel.value = travel
    }

    fun addTravel(travel: Travel){
        viewModelScope.launch {
            repository.addTravel(travel)
        }
    }

    fun deleteTravel(travel: Travel){
        viewModelScope.launch {
            repository.deleteTravel(travel)
        }
    }

    fun updateTravel(travel: Travel){
        viewModelScope.launch {
            repository.updateTravel(travel)
        }
    }

    fun deleteAll(){
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}
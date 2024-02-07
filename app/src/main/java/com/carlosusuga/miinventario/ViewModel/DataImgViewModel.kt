package com.carlosusuga.miinventario.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.carlosusuga.miinventario.Entities.DataImgEntity
import com.carlosusuga.miinventario.Repository.DataImgRepository

//@HiltViewModel
class DataImgViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataImgRepository

    val allPhoto: LiveData<List<DataImgEntity>>

    init {
        repository = DataImgRepository(application)
        allPhoto = repository.allPhotos
    }

    fun insertPhoto(dataImgEntity: DataImgEntity){
        repository.insertPhoto(dataImgEntity)
    }

    fun updatePhoto(dataImgEntity: DataImgEntity){
        repository.updatePhoto(dataImgEntity)
    }

    fun detelePhoto(dataImgEntity: DataImgEntity){
        repository.deletePhoto(dataImgEntity)
    }

    fun getItemById(itemId: Int): LiveData<DataImgEntity> {
        return repository.getItemById(itemId)
    }
}
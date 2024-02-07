package com.carlosusuga.miinventario.Repository

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.carlosusuga.miinventario.DaosInterface.DataImgDao
import com.carlosusuga.miinventario.DataBase.GalleryDatabase.Companion.getInstance
import com.carlosusuga.miinventario.Entities.DataImgEntity

class DataImgRepository(application: Application) {

    private val dataImgDao: DataImgDao

    val allPhotos: LiveData<List<DataImgEntity>>

    init {
        val database = getInstance(application)
        dataImgDao = database!!.dataImgDaos()
        allPhotos = dataImgDao.allPhoto()
    }

    fun insertPhoto(dataImgEntity: DataImgEntity){
        InsertPhotoAsyncTask(dataImgDao).execute(dataImgEntity)
    }

    fun updatePhoto(dataImgEntity: DataImgEntity){
        UpdatePhotoAsyncTask(dataImgDao).execute(dataImgEntity)
    }

    fun deletePhoto(dataImgEntity: DataImgEntity){
        DeletePhotoAsyncTask(dataImgDao).execute(dataImgEntity)
    }

    fun getItemById(itemId: Int): LiveData<DataImgEntity> {
        return dataImgDao.getItemImgById(itemId)
    }

    private class InsertPhotoAsyncTask(private val dataImgDao: DataImgDao) :
        AsyncTask<DataImgEntity, Void?, Void?>() {

        override fun doInBackground(vararg photos: DataImgEntity): Void? {
            dataImgDao.insertPhoto(photos[0])
            return null
        }
    }

    private class UpdatePhotoAsyncTask(private val dataImgDao: DataImgDao) :
        AsyncTask<DataImgEntity, Void?, Void?>(){
        override fun doInBackground(vararg photos: DataImgEntity): Void? {
            dataImgDao.updatePhoto(photos[0])
            return null
        }
    }

    private class DeletePhotoAsyncTask(private val dataImgDao: DataImgDao) :
        AsyncTask<DataImgEntity, Void?, Void?>(){
        override fun doInBackground(vararg photos: DataImgEntity): Void? {
            dataImgDao.deletePhoto(photos[0])
            return null
        }
    }

//    private class GetPhotoByIdAsynTask(private val dataImgDao: DataImgDao) : AsyncTask<DataImgEntity, Void?, Void?>(){
//        override fun doInBackground(vararg photos: DataImgEntity): Void? {
//            dataImgDao.getItemImgById(photos[0])
//            return null
//        }
//
//    }
}
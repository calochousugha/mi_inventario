package com.carlosusuga.miinventario.DataBase

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.carlosusuga.miinventario.DaosInterface.DataImgDao
import com.carlosusuga.miinventario.Entities.DataImgEntity

@Database(entities = [DataImgEntity::class], version = 1, exportSchema = false)
abstract class GalleryDatabase : RoomDatabase(){

    abstract fun dataImgDaos(): DataImgDao

    private class PopulateDbAsyncTask(db: GalleryDatabase?) : AsyncTask<Void, Void, Void>(){
        private val dataImgDao: DataImgDao
        override fun doInBackground(vararg voids: Void?): Void? {
//            dataImgDao.insertPhoto(DataImgEntity(0, "Bedo", "Mutatá", "05/02/2024", "/sadasd/dasd", ))
            return null
        }

        init {
            dataImgDao =  db!!.dataImgDaos()
        }
    }


    companion object {
        private var instance: GalleryDatabase? = null
        @JvmStatic
        @Synchronized
        fun getInstance(context: Context /*scope: CoroutineScope*/): GalleryDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    GalleryDatabase::class.java, "inventario_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()
            }
            return instance
        }

        private val roomCallback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                PopulateDbAsyncTask(instance).execute()
            }
        }
    }
}
package com.carlosusuga.miinventario.DaosInterface

import androidx.lifecycle.LiveData
import androidx.room.*
import com.carlosusuga.miinventario.Entities.DataImgEntity

@Dao
interface DataImgDao {
    @Insert
    fun insertPhoto(dataImgEntity: DataImgEntity)

    @Update
    fun updatePhoto(dataImgEntity: DataImgEntity)

    @Delete
    fun deletePhoto(dataImgEntity: DataImgEntity)

    @Query("SELECT * FROM img_table WHERE id = :itemId")
    fun getItemImgById(itemId: Int): LiveData<DataImgEntity>

    @Query("SELECT * FROM img_table ORDER BY id ASC")
    fun allPhoto(): LiveData<List<DataImgEntity>>
}
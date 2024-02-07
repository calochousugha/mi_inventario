package com.carlosusuga.miinventario.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "img_table")
data class DataImgEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "name_photo")
    var namePhoto: String,

    @ColumnInfo(name = "details_photo")
    var detailsPhoto: String,

    @ColumnInfo(name = "date_photo")
    var datePhoto: String,

    @ColumnInfo(name = "image_path")
    var imagePath: String,

//    @ColumnInfo(name = "image_bytes")
//    var imageBytes: ByteArray
)
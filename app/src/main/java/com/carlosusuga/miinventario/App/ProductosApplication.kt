package com.carlosusuga.miinventario.App

import android.app.Application
import com.carlosusuga.miinventario.DataBase.GalleryDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ProductosApplication : Application(){

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { GalleryDatabase.getInstance(this) }
//    val repository by lazy { ProductoRepository(database.productosDaos()) }
}
package com.carlosusuga.miinventario.Activity

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carlosusuga.miinventario.Entities.DataImgEntity
import com.carlosusuga.miinventario.ListAdapters.DataImgAdapter
import com.carlosusuga.miinventario.R
import com.carlosusuga.miinventario.ViewModel.DataImgViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity(), DataImgAdapter.OnItemClickListener {

    private var dataImgViewModel: DataImgViewModel? = null
    val adapter = DataImgAdapter()
    var toolbar: MaterialToolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitle(R.string.app_name)
        setSupportActionBar(toolbar)

        verifyPermission()

        val btnAddPhoto = findViewById<FloatingActionButton>(R.id.button_add_photo)
        btnAddPhoto.setOnClickListener {
            val intent = Intent(this@MainActivity, AddPhotoActivity::class.java)
            startActivityForResult(intent, ADD_PHOTO_REQUEST)
            onPause()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.setOnItemClickListener(this)

        dataImgViewModel = ViewModelProvider(this@MainActivity).get(DataImgViewModel::class.java)
        dataImgViewModel!!.allPhoto.observe(this) { photos ->
            //Actualizar recyclerview
            adapter.submitList(photos)
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //Dialogo para confirmar si se elimina la tarjeta o no
                showDeleteConfirmationDialog(viewHolder.adapterPosition)
            }
        }).attachToRecyclerView(recyclerView)
    }

    // Implementar la función onItemClick para manejar la navegación
    override fun onItemClick(dataImgEntity: DataImgEntity) {
        val intent = Intent(this, DetailsPhototoActivity::class.java)
        intent.putExtra("id", dataImgEntity.id)
        intent.putExtra("name", dataImgEntity.namePhoto)
        intent.putExtra("details", dataImgEntity.detailsPhoto)
        intent.putExtra("date", dataImgEntity.datePhoto)
        intent.putExtra("uri", dataImgEntity.imagePath)
//        val selectedItem = runBlocking { dataImgViewModel?.imgItemById(id) }
        startActivity(intent)
        onPause()
    }

    //Funcion que permite elimitar una tarjeta
    private fun showDeleteConfirmationDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Imagen")
        builder.setMessage("¿Estás seguro de que deseas eliminar esta imagen?")

        builder.setPositiveButton("Eliminar") { _, _ ->
            // Si el usuario elige eliminar, realiza la eliminación
            dataImgViewModel!!.detelePhoto(adapter.getPhotoAt(position)!!)
            Toast.makeText(this@MainActivity, "Imagen Eliminada", Toast.LENGTH_LONG).show()
        }

        builder.setNegativeButton("Cancelar") { _, _ ->

            adapter.notifyItemChanged(position)
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PHOTO_REQUEST && resultCode == RESULT_OK) {

            val id = data!!.getIntExtra(AddPhotoActivity.EXTRA_ID, 0)
            val namePhoto = data.getStringExtra(AddPhotoActivity.EXTRA_NAME_PHOTO)
            val detailsPhoto = data.getStringExtra(AddPhotoActivity.EXTRA_DETAILS_PHOTO)
            val datePhoto = data.getStringExtra(AddPhotoActivity.EXTRA_DATE_PHOTO)
            val uriPhoto = data.getStringExtra(AddPhotoActivity.EXTRA_URI_PHOTO)

            val imgData = DataImgEntity(
                id,
                namePhoto.toString(),
                detailsPhoto.toString(),
                datePhoto.toString(),
                uriPhoto.toString()
            )

            dataImgViewModel!!.insertPhoto(imgData)
            Toast.makeText(this, "Imagen Guardada", Toast.LENGTH_LONG).show()

        } else if (requestCode == EDIT_PHOTO_REQUEST && resultCode == RESULT_OK) {
            val id = data!!.getIntExtra(AddPhotoActivity.EXTRA_ID, -1)

            if (id == -1) {
                Toast.makeText(this, "No se puede actualizar datos", Toast.LENGTH_LONG).show()
                return
            }

            val nombreFoto = data.getStringExtra(AddPhotoActivity.EXTRA_NAME_PHOTO)
            val detalleFoto = data.getStringExtra(AddPhotoActivity.EXTRA_DETAILS_PHOTO)
            val fechaFoto = data.getStringExtra(AddPhotoActivity.EXTRA_DATE_PHOTO)
            val uriFoto = data.getStringExtra(AddPhotoActivity.EXTRA_URI_PHOTO)

            val producto = DataImgEntity(
                id,
                nombreFoto.toString(),
                detalleFoto.toString(),
                fechaFoto.toString(),
                uriFoto.toString()
            )
            producto.id = id

            dataImgViewModel!!.updatePhoto(producto)

            Toast.makeText(this, "Imagen Actualizada", Toast.LENGTH_LONG).show()

        } else {
            Toast.makeText(this, " No  se pudo guardar los datos", Toast.LENGTH_LONG).show()
        }
    }


    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                val result = isGranted
                Log.i("r", "Mi resultado $result")
            } else {
                executeDialogNegativePermission(false) {

                }
            }
        }

    //Pedir permiso ala aplicación para ejecutar corractamente
    private fun verifyPermission() {

        val permissionToCheck: String

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionToCheck = Manifest.permission.READ_MEDIA_IMAGES
        } else {
            permissionToCheck = Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                this,
                permissionToCheck
            ) == PackageManager.PERMISSION_GRANTED -> {
                // El permiso ya está otorgado
            }
            shouldShowRequestPermissionRationale(permissionToCheck) -> {
                // Mostrar diálogo explicando por qué necesitas el permiso
                executeDialogNegativePermission(true) {
                    requestPermissionLauncher.launch(permissionToCheck)
                }
            }
            else -> {
                // Solicitar el permiso directamente
                requestPermissionLauncher.launch(permissionToCheck)
            }
        }
    }

    private fun executeDialogNegativePermission(isRationale: Boolean, callback: () -> Unit){
        MaterialAlertDialogBuilder(this)
            .setTitle("Por favor aceptar los permisos")
            .setMessage("El programa no podria funcionar bien si no consede los permisos"
            )
            .setPositiveButton("Aceptar") { dialog, _ ->
                callback.invoke()
                if (!isRationale){
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", packageName, null)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                dialog.dismiss()
            }
            .show()
    }

    companion object {
        const val ADD_PHOTO_REQUEST = 1
        const val EDIT_PHOTO_REQUEST = 2
        const val REQUEST_CODE = 200
    }
}
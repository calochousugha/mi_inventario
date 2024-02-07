package com.carlosusuga.miinventario.Activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.text.method.TextKeyListener.Capitalize
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.carlosusuga.miinventario.R
import com.carlosusuga.miinventario.ViewModel.DataImgViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddPhotoActivity : AppCompatActivity() {

    companion object{
        const val EXTRA_ID = "com.carlosusuga.miinventario.Activity.EXTRA_ID"
        const val EXTRA_NAME_PHOTO = "com.carlosusuga.miinventario.Activity.EXTRA_NAME_PHOTO"
        const val EXTRA_DETAILS_PHOTO = "com.carlosusuga.miinventario.Activity.EXTRA_DETAILS_PHOTO"
        const val EXTRA_DATE_PHOTO = "com.carlosusuga.miinventario.Activity.EXTRA_DATE_PHOTO"
        const val EXTRA_URI_PHOTO = "com.carlosusuga.miinventario.Activity.EXTRA_URI_PHOTO"
    }

    private val PICK_IMAGE_REQUEST = 1

    private var dataImgViewModel: DataImgViewModel? = null

    private var editNombreFoto: EditText? = null
    private var editDetalleFoto: EditText? = null

    private var btnGuardarFoto: Button? = null

    private var dateTime: TextView? = null
    private var textViewFilePath: TextView? = null

    var toolbar: MaterialToolbar? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        animatedFuntion()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitle(R.string.app_name)
        setSupportActionBar(toolbar)

        editNombreFoto = findViewById(R.id.edtNamePhoto)
        editDetalleFoto = findViewById(R.id.edtDetalleFoto)
        dateTime = findViewById(R.id.tvDateTime)
        textViewFilePath = findViewById(R.id.textViewFilePath)

        val btnFloatAddImg = findViewById<FloatingActionButton>(R.id.fltAddImg)

        btnFloatAddImg.setOnClickListener { openGallery() }

        supportActionBar!!.setHomeAsUpIndicator(R.drawable.backspace_reverse_outline)

        formatoFecha()
        val intent = intent
        val imgId = intent.getIntExtra("id", 0)
        if (imgId != 0){
            title = "Editar Foto"
            obtenerDetallesDeLaFoto(imgId)
        } else {
            title = "Agregar Foto"
        }

        btnGuardarFoto = findViewById(R.id.btnAddPhoto)
        btnGuardarFoto?.setOnClickListener {
            saveProduct()
            Intent(this, MainActivity::class.java)
            finish()
        }
    }

    private fun animatedFuntion() {

        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)

        // Set the transition name, which matches Activity A’s start view transition name, on
        // the root view.
        findViewById<View>(android.R.id.content).transitionName = "shared_element_container"

        // Attach a callback used to receive the shared elements from Activity A to be
        // used by the container transform transition.
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())

        // Set this Activity’s enter and return transition to a MaterialContainerTransform
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 300L
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 250L
        }

    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri: Uri = data.data!!

            // Mostrar la imagen en un ImageView
            val imgvAdd = findViewById<ImageView>(R.id.shapeableImageView)
            imgvAdd.setImageURI(selectedImageUri)

            // Obtener la ruta del archivo desde la URI
            val imagePath = getPathFromUri(selectedImageUri)

            // Mostrar la ruta en un TextView
            val textViewFilePath = findViewById<TextView>(R.id.textViewFilePath)
            textViewFilePath.text = imagePath
        }
    }


    // Función para obtener los detalles de la foto por ID y actualizar los EditText
    private fun obtenerDetallesDeLaFoto(photoId: Int) {
        // Usar el ViewModel para obtener los detalles de la foto por ID
        dataImgViewModel?.getItemById(photoId)?.observe(this) { photoDetails ->
            // Actualizar las vistas con los detalles de la foto
            editNombreFoto?.setText(photoDetails.namePhoto)
            editDetalleFoto?.setText(photoDetails.detailsPhoto)
            dateTime?.text = photoDetails.datePhoto
            textViewFilePath?.text = photoDetails.imagePath
        }
    }

    private fun getPathFromUri(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val filePath = cursor.getString(columnIndex)
        cursor.close()
        return filePath
    }

    //funcion que da el formato de fecha
    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatoFecha(){
        //Obtener la fecha actual
        val currentDate = LocalDate.now()

        //Se define el tipo de formato
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyy")

        //Formatea el formato deseado
        val formattedDate = currentDate.format(formatter)

        //Se muestra la fecha en el textView
        var dateTime = findViewById<TextView>(R.id.tvDateTime)

        dateTime.text = formattedDate
    }

    //Funcion que permite guardar en la base de datos
    private fun saveProduct(){

        val nombreFoto = editNombreFoto!!.text.toString()
        val detalleFoto = editDetalleFoto!!.text.toString()
        val fechaFoto = dateTime!!.text.toString()
        val uriFoto = textViewFilePath!!.text.toString()

        if (nombreFoto.trim().isEmpty() || uriFoto.equals("")){
            Toast.makeText(this, "Ingrese toda la información", Toast.LENGTH_LONG).show()
        } else {

            val data = Intent()
            data.putExtra(EXTRA_NAME_PHOTO, nombreFoto)
            data.putExtra(EXTRA_DETAILS_PHOTO, detalleFoto)
            data.putExtra(EXTRA_DATE_PHOTO, fechaFoto)
            data.putExtra(EXTRA_URI_PHOTO, uriFoto)

            val id = intent.getIntExtra(EXTRA_ID, -1)
            if (id != -1) {
                data.putExtra(EXTRA_ID, id)
            }

            setResult(RESULT_OK, data)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_back, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.itemBack -> {
                showCAncelAddImgDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showCAncelAddImgDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cancelar")
        builder.setMessage("¿Estás seguro de que deseas cancelar la operación?")

        builder.setPositiveButton("Aceptar") { _, _ ->
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton("Cancelar") { _, _ ->

        }

        val dialog = builder.create()
        dialog.show()
    }
}
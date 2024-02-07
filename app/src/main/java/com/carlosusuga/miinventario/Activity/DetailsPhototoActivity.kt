package com.carlosusuga.miinventario.Activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
//import androidx.compose.ui.text.android.style.ShadowSpan
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.carlosusuga.miinventario.R
import com.carlosusuga.miinventario.ViewModel.DataImgViewModel
import com.google.android.material.appbar.MaterialToolbar

class DetailsPhototoActivity : AppCompatActivity() {

    var toolbar: MaterialToolbar? = null

    private lateinit var dataImgViewModel: DataImgViewModel
    private var nombreFoto: String? = null
    private var detalleFoto:String? = null
    private var fechaFoto: String? = null
    private var uriFoto: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_phototo)

        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitle(R.string.app_name)
        setSupportActionBar(toolbar)

        supportActionBar!!.setHomeAsUpIndicator(R.drawable.backspace_reverse_outline)

        var imgbEditPhoto = findViewById<ImageButton>(R.id.imgbEditPhoto)
        var imgbDetailsPhoto = findViewById<ImageButton>(R.id.imgbDetailsPhoto)

        val imgId = intent.getIntExtra("id", 0)

        dataImgViewModel = ViewModelProvider(this).get(DataImgViewModel::class.java)

        dataImgViewModel.getItemById(imgId).observe(this) { img ->
           findViewById<TextView>(R.id.tvNombreFotDetalle).text = img.namePhoto
            findViewById<TextView>(R.id.tvDetallesFotoDetalle).text = img.detailsPhoto
            findViewById<TextView>(R.id.tvFechaFoto).text = img.datePhoto
            findViewById<TextView>(R.id.tvRutaFoto).text = img.imagePath

            val imgPhoto = findViewById<ImageView>(R.id.shapeableImageView)
            Glide.with(this)
                .load(img.imagePath)
                .into(imgPhoto)

            nombreFoto = img.namePhoto
            detalleFoto = img.detailsPhoto
            fechaFoto = img.datePhoto
            uriFoto = img.imagePath
            resaltarTexto(nombreFoto.toString())
            resaltarTexto(detalleFoto.toString())
            resaltarTexto(fechaFoto.toString())
            resaltarTexto(uriFoto.toString())
        }

        imgbEditPhoto.setOnClickListener {
            var intent = Intent(this, AddPhotoActivity::class.java)
            intent.putExtra("id", imgId)
            intent.putExtra("name", nombreFoto)
            intent.putExtra("details", detalleFoto)
            intent.putExtra("date", fechaFoto)
            intent.putExtra("uri", uriFoto)
            startActivity(intent)
            finish()
        }

        imgbDetailsPhoto.setOnClickListener {
            showMoreDetailsDialog()
        }
    }

    private fun resaltarTexto(texto: String): SpannableString {
        // Crear una SpannableString para aplicar estilos
        val spannable = SpannableString(texto)

        // Aplicar fondo resaltado
        val backgroundColorSpan = BackgroundColorSpan(Color.YELLOW)
        spannable.setSpan(backgroundColorSpan, 0, texto.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Aplicar sombra
//        val shadowSpan = ShadowSpan(4f, 2f, 2f, Color.BLACK)
//        spannable.setSpan(shadowSpan, 0, texto.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Aplicar tamaño relativo al texto original
        val relativeSizeSpan = RelativeSizeSpan(1.5f)
        spannable.setSpan(relativeSizeSpan, 0, texto.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannable
    }

    private fun showMoreDetailsDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Detalles")
        builder.setMessage(
            "-Nombre de la foto: $nombreFoto\n" +
                    "-Detalle de la foto: $detalleFoto\n" +
                    "-Fecha: $fechaFoto\n" +
                    "-Ruta de la imagen: $uriFoto"
        )


        builder.setNegativeButton("Cerrar") { _, _ ->

        }

        val dialog = builder.create()
        dialog.show()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_back, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.itemBack -> {
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
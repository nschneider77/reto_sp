package com.example.appsophos.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.appsophos.databinding.ActivityDocsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.provider.MediaStore

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import android.content.ActivityNotFoundException
import com.example.appsophos.ApiService


class DocsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDocsBinding
    var correoTraido: String = ""
    var bitmapFinal: String=""
    var bitMapImagenTomada: String=""

    val REQUEST_IMAGE_CAPTURE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle = intent.extras
        val correoTraido = bundle?.get("CORREO")  // trae el correo que se ingresó en el main

        binding.textoCorreoAuto.text="$correoTraido"

        binding.botonEnviarDocs.setOnClickListener { enviarDocs() }
        binding.botonCargarImg.setOnClickListener { permisoAdjuntarImagen() }
        binding.botonTomarFoto.setOnClickListener {

            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Error: " + e.localizedMessage, Toast.LENGTH_SHORT).show()
            } }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            bitMapImagenTomada = imageBitmap.toBase64String()
            binding.textView.text = "Imagen cargada correctamente"
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun permisoAdjuntarImagen() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {

                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    pickPhotoFromGallery()
                }

                else -> requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else {
            // Se llamará a la función para APIs 22 o inferior
            // Esto debido a que se aceptaron los permisos
            // al momento de instalar la aplicación
            pickPhotoFromGallery()
        }

    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->

        if (isGranted){
            pickPhotoFromGallery()
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForActivityResult.launch(intent)
    }

    private val startForActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            val imageUri = result.data?.data // obtiene la uri del archivo seleccionado
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri) // obtiene e bitmap de la uri
            val bitmapReducido = getResizedBitmap(bitmap,1500) //reduce la imagen
            bitmapFinal=bitmapReducido.toBase64String()
            //binding.imageView2.setImageBitmap(bitmapFinal.toBitmap()) // la muestra en el imageview
            binding.textView.text = "Imagen cargada correctamente"
        }
    }
    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {  // metodo para reducir el bitmap
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

     private fun Bitmap.toBase64String():String{ // funcion para codificar bitmap a base 64
        ByteArrayOutputStream().apply {
            compress(Bitmap.CompressFormat.JPEG,90,this)
            return Base64.encodeToString(toByteArray(),Base64.DEFAULT)
        }
    }
    private fun String.toBitmap():Bitmap?{  // funcion para decodificar de base64 a bitmap
        Base64.decode(this,Base64.DEFAULT).apply {
            return BitmapFactory.decodeByteArray(this,0,size)
        }
    }


    private fun enviarDocs() {
        // obtiene los datos de los elementos de la activity ingresados por usuario
        val tipoDoc = binding.spinnerDocs.selectedItem.toString()
        val numDoc= binding.textCedula.text.toString()
        val nombres = binding.textoNombre.text.toString()
        val apellidos = binding.textoApellidos.text.toString()
        val sede = binding.spinnerLoc.selectedItem.toString()
        val tipoAdj = binding.spinnerTipo.selectedItem.toString()
        val  correo = binding.textoCorreoAuto.text.toString()
        var adjunto:String = ""
        if(bitmapFinal!= ""){
            adjunto = bitmapFinal
        }else{
            adjunto = bitMapImagenTomada
        }

        // llama al post con los datos ingresados
        postEnvioDocumentos(tipoDoc,numDoc,nombres, apellidos, sede, correo, tipoAdj,adjunto)
        binding.textView.text=""
    }

    private fun postRetrofit(): Retrofit{   // funcion para hacer la peticion
        return Retrofit.Builder()
            .baseUrl("https://6w33tkx4f9.execute-api.us-east-1.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun postEnvioDocumentos(tipoDocumento:String, numeroDocumento: String, nombres: String,
                apellidos:String,sede:String,correo:String, tipoAdj: String, adjunto:String){

        val jsonObject = JSONObject()       // json para hacer la peticion

        jsonObject.put("TipoId", tipoDocumento)
        jsonObject.put("Identificacion", numeroDocumento)
        jsonObject.put("Nombre", nombres)
        jsonObject.put("Apellido", apellidos)
        jsonObject.put("Ciudad", sede)
        jsonObject.put("Correo", correo)
        jsonObject.put("TipoAdjunto", tipoAdj)
        jsonObject.put("Adjunto", adjunto)

        val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull()) // se convierte de json a requestbody

        // corrutina para hacer request al servicio
        CoroutineScope(Dispatchers.IO).launch {

            val call = postRetrofit().create(ApiService::class.java).PostDocuments(body) // se hace la request y se almacena en call

            val respuesta = call.body() // obtiene solo el body de la respuesta

            runOnUiThread{
                if(respuesta?.put == true){ // si la respuesta es put = true es que es exitoso

                    showSuccess()


                }else{

                    showError(respuesta.toString())

                }

            }

        }

    }

    private fun showSuccess() {
        Toast.makeText(this, "Envío exitoso", Toast.LENGTH_SHORT).show()
        binding.textView.text = ""
    }

    private fun showError(respuesta: String) {
        Toast.makeText(this, "Ocurrió un error enviando sus datos", Toast.LENGTH_SHORT).show()
    }




}
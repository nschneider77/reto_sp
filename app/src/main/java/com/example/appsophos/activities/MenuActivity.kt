package com.example.appsophos.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appsophos.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    var correoOrig: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        obtenerNombre() // llama a la funcion para obtener el nombre del usuario
        binding.botonEnviarDocumentos.setOnClickListener { pantallaEnvioDocs() }
        binding.botonVerDocumentos.setOnClickListener { verDocumentos() }
        binding.botonOficinas.setOnClickListener { verOficinas() }



    }

    private fun verOficinas() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)

    }

    private fun verDocumentos() {
        val intent = Intent(this, DocViewActivity::class.java) // se crea el intent
        startActivity(intent) // instancia la otra activity
    }

    private fun pantallaEnvioDocs() { // funcion para aparecer la activity de enviar docs
        val intent = Intent(this, DocsActivity::class.java) // se crea el intent
        intent.putExtra("CORREO",correoOrig)
        startActivity(intent) // instancia la otra activity
    }

    private fun obtenerNombre() { // funcion para mostrar el nombre del usuario en pantalla
        val bundle = intent.extras // llama al objeto intent para recuperar cosas de la pantalla anterior
        val nombre = bundle?.get("NOMBRE") // busca el objeto con la clave "NOMBRE"
        val apellido = bundle?.get("APELLIDO") // busca el objeto con la clave "APELLIDO"
        correoOrig = bundle?.get("CORREO").toString()
        binding.textNombre.text = "$nombre $apellido" // muestra el nombre en pantalla



    }
}
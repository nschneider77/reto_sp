package com.example.appsophos.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.appsophos.ApiService
import com.example.appsophos.R
import com.example.appsophos.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapBinding
    lateinit var map: GoogleMap
    var listaLong = mutableListOf<String>()
    var listaLat = mutableListOf<String>()
    var listaNombre = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getOficinas()



    }


    private fun getRetrofit(): Retrofit { // retrofit para hacer la peticion
        return Retrofit.Builder()
            .baseUrl("https://6w33tkx4f9.execute-api.us-east-1.amazonaws.com/") // url del servicio
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private fun getOficinas() {
        CoroutineScope(Dispatchers.IO).launch {   // corrutina para llamar al servicio en otro hilo
            val call = getRetrofit().create(ApiService::class.java)
                .GetOffsData("RS_Oficinas") // llamada a la url(queda la respuesta aca)

            val respuesta = call.body() // extrae el body de la respuesta

            runOnUiThread {
                // muestra en pantalla lo que se hace en la corrutina
                if (respuesta?.Items != null) {

                    for (valor in 0..respuesta.Count - 1) { // for para recuperar en las listas los datos del mapa
                        listaLong.add(respuesta.Items[valor]["Longitud"].toString())
                        listaLat.add(respuesta.Items[valor]["Latitud"].toString())
                        listaNombre.add(respuesta.Items[valor]["Nombre"].toString())

                    }
                    createFragment()

                }
            }

        }
    }



    private fun createFragment() { // crea el fragment del mapa para ver en pantalla
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) { // se llama cuando el mapa se carga
        map=googleMap
        crearMarcador()
    }
    private fun crearMarcador(){ // crea el marcador para ubicarlo en el mapa
        val coordenadas = listToCoord(listaLat,listaLong)

        for(valor in 0..coordenadas.size-1){

            val marker = MarkerOptions().position(coordenadas[valor]).title(listaNombre[valor]) // acomoda el marcador con el nombre y coordenadas
            map.addMarker(marker)

        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenadas[4],15f),4000,null)


    }

    private fun listToCoord(listaLat: List<String>, listaLong :List<String>): List<LatLng>{
    // metodo para convertir las listas del GET en listas de coordenadas

        var coordenadas= mutableListOf<LatLng>()
        for (valor in 0..listaLat.size-1){
            if(valor < 1){
                coordenadas.add(LatLng(listaLat[valor].toDouble()*-1,listaLong[valor].toDouble()))
            }else{
                coordenadas.add(LatLng(listaLat[valor].toDouble(),listaLong[valor].toDouble()))
            }


        }
        return coordenadas

    }


}
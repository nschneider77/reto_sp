package com.example.appsophos.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appsophos.ApiService
import com.example.appsophos.dataclasses.DocsItems
import com.example.appsophos.ItemsProvider
import com.example.appsophos.adapter.ItemsAdapter
import com.example.appsophos.databinding.ActivityDocViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DocViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDocViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDocViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        obtenerDocumentos()

    }

    private fun getRetrofit(): Retrofit { // retrofit para hacer la peticion
        return Retrofit.Builder()
            .baseUrl("https://6w33tkx4f9.execute-api.us-east-1.amazonaws.com/") // url del servicio
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private fun obtenerDocumentos(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).GetDocsInfo("RS_Documentos?correo=nicsh99@gmail.com")
            val respuesta = call.body()
            var listaFechas = mutableListOf<String>()
            var listaNombres = mutableListOf<String>()
            var listaApellidos = mutableListOf<String>()
            var listaID = mutableListOf<String>()
            var listaTipoAdj = mutableListOf<String>()

            ItemsProvider.listaDocumentos.clear() // limpia la lista para actualizarla luego

            // en este get obtener fecha, tipo de adjunto y nombre, ademas del idRegistro para mostrar la imagen adjunta
            runOnUiThread{ // recomendable crear una lista de los elementos a mostrar usando un for y luego si mostrarlos usando recyclerview
                if(respuesta?.Items!=null) {

                    for (valor in 0..respuesta.Count-1){ // for para recuperar en las listas los datos del mapa
                        listaFechas.add(respuesta.Items[valor]["Fecha"].toString())
                        listaID.add(respuesta.Items[valor]["IdRegistro"].toString())
                        listaTipoAdj.add(respuesta.Items[valor]["TipoAdjunto"].toString())
                        listaNombres.add(respuesta.Items[valor]["Nombre"].toString())
                        listaApellidos.add(respuesta.Items[valor]["Apellido"].toString())
                        ItemsProvider.listaDocumentos.add(
                            DocsItems(listaFechas[valor],
                        listaTipoAdj[valor],listaNombres[valor],listaApellidos[valor],listaID[valor])
                        )
                    }

                    initRecyclerView()

                }
            }
        }
    }


    private fun initRecyclerView() {
        val manager = LinearLayoutManager(this)
        val decoration = DividerItemDecoration(this,manager.orientation)
        binding.recyclerDocumentos.layoutManager = manager
        binding.recyclerDocumentos.adapter = ItemsAdapter(
            ItemsProvider.listaDocumentos
        ) { onItemSelected(it) }
        binding.recyclerDocumentos.addItemDecoration(decoration)
    }

    private fun onItemSelected(it: DocsItems) {
        Toast.makeText(this, it.ids, Toast.LENGTH_SHORT).show()

    }




}
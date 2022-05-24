package com.example.appsophos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appsophos.dataclasses.DocsItems
import com.example.appsophos.R


// esta clase recibe la lista de items

class ItemsAdapter(private val listaItems: List<DocsItems>, private val onClickListener: (DocsItems)-> Unit): RecyclerView.Adapter<ItemsViewHolder>() {
 // para llamar al recyclerview hay que instanciar la clase en el lugar donde se requiera
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        // encargado de pintar los items (necesita un layout para modificar)
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemsViewHolder(layoutInflater.inflate(R.layout.item_docs,parent,false))
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        // pasa por los items y llama al render del objeto en la lista visual
        val item = listaItems[position] // ingresa la lista de objetos y la recorre con position
        holder.render(item, onClickListener) // llama al metodo render del holder

    }

    override fun getItemCount(): Int {
        return listaItems.size
    }


}
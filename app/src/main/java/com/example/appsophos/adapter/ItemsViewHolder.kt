package com.example.appsophos.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.appsophos.dataclasses.DocsItems
import com.example.appsophos.databinding.ItemDocsBinding

class ItemsViewHolder(view: View):RecyclerView.ViewHolder(view) {
    val binding = ItemDocsBinding.bind(view)

    fun render(docsItems: DocsItems, onClickListener: (DocsItems)-> Unit) {   // llena el archivo xml que se va a modificar
        binding.viewItems.text = docsItems.fecha.substring(0, 10) +
                " - " + docsItems.tipoDoc + "\n" + docsItems.nombre + " " +
                docsItems.apellidos

        itemView.setOnClickListener {onClickListener(docsItems)}

    }
}




package com.example.listadelivros

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class LivroAdapter(private val books: List<Map<String, Any>>, private val onItemClick: (Map<String, Any>) -> Unit) :
    RecyclerView.Adapter<LivroAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.nome)
        val editora: TextView = itemView.findViewById(R.id.editoras)
        val genero: TextView = itemView.findViewById(R.id.generos)
        val imagem: ImageView = itemView.findViewById(R.id.imagem_livro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_livro, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.titulo.text = book["titulo"] as? String ?: "Título desconhecido"
        holder.editora.text = book["editora"] as? String ?: "Editora desconhecida"
        holder.genero.text = book["genero"] as? String ?: "Gênero desconhecido"
        val imageUrl = book["imageUrl"] as? String

        if (imageUrl != null) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .into(holder.imagem)
        }

        holder.itemView.setOnClickListener {
            onItemClick(book)
        }
    }

    override fun getItemCount(): Int {
        return books.size
    }
}

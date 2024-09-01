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
        holder.titulo.text = book["Titulo"] as? String ?: "Título desconhecido"
        holder.editora.text = book["Editora"] as? String ?: "Editora desconhecida"
        holder.genero.text = book["Genero"] as? String ?: "Gênero desconhecido"

        val imageUrl = book["imageUri"] as? String

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.baseline_menu_book_24)
                .error(R.drawable.baseline_menu_book_24)
                .into(holder.imagem)
        } else {
            Glide.with(holder.itemView.context)
                .load(R.drawable.baseline_menu_book_24)
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

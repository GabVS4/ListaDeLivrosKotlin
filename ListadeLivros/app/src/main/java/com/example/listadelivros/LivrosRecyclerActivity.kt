package com.example.listadelivros

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listadelivros.databinding.ActivityLivrosRecyclerBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class LivrosRecyclerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLivrosRecyclerBinding
    private val db = Firebase.firestore
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLivrosRecyclerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.imageVoltar.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }

        getAllBooks { books ->
            if (books != null) {
                setupRecyclerView(books)
            } else {
                Log.w("ERROR", "Nenhum livro encontrado ou ocorreu um erro.")
            }
        }
    }

    private fun setupRecyclerView(books: List<Map<String, Any>>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = LivroAdapter(books) { book ->
            val intent = Intent(this, LivroActivity::class.java)
            intent.putExtra("titulo", book["Titulo"].toString())
            intent.putExtra("editora", book["Editora"].toString())
            intent.putExtra("genero", book["Genero"].toString())
            intent.putExtra("sinopse", book["Sinopse"].toString())
            intent.putExtra("id", book["id"].toString())
            startActivity(intent)
        }
    }

    private fun getAllBooks(callback: (List<Map<String, Any>>?) -> Unit) {
        val auth = Firebase.auth
        val user = auth.currentUser

        if (user == null) {
            callback(null)
            return
        }

        db.collection("Usuarios").document(user.uid).collection("Livros").get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val books = querySnapshot.documents.map { document ->
                        val book = document.data?.toMutableMap() ?: mutableMapOf()
                        book["id"] = document.id
                        book
                    }
                    callback(books)
                } else {
                    Log.d("ERROR", "Nenhum livro encontrado.")
                    callback(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ERROR", "Erro ao recuperar os livros: ", exception)
                callback(emptyList())
            }
    }
    override fun onResume() {
        super.onResume()
        getAllBooks { books ->
            if (books != null) {
                setupRecyclerView(books)
            } else {
                Log.w("ERROR", "Nenhum livro encontrado ou ocorreu um erro.")
            }
        }
    }
}

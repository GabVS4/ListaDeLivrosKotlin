package com.example.listadelivros

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.listadelivros.databinding.ActivityLivroBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class LivroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLivroBinding
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_livro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityLivroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idColecao = intent.getStringExtra("id")

        binding.imageVoltar.setOnClickListener{
            finish()
        }

        binding.buttonEditar.setOnClickListener {
            val editarIntent = Intent(this, EditarLivroActivity::class.java)
            editarIntent.putExtra("id", idColecao)
            editarIntent.putExtra("titulo", binding.titulo.text.toString())
            editarIntent.putExtra("editora", binding.editora.text.toString())
            editarIntent.putExtra("genero", binding.genero.text.toString())
            editarIntent.putExtra("sinopse", binding.sinopse.text.toString())
            startActivity(editarIntent)
        }

        binding.buttonDeletar.setOnClickListener {
            if (idColecao != null) {
                db.collection("Usuarios").document(Firebase.auth.currentUser!!.uid)
                    .collection("Livros").document(idColecao)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Livro deletado com sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.w("LivroActivity", "Erro ao deletar livro", e)
                        Toast.makeText(this, "Erro ao deletar livro", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        db.collection("Usuarios").document(Firebase.auth.currentUser!!.uid)
            .collection("Livros").document(idColecao!!)
            .addSnapshotListener { documento, error ->
                if (documento != null) {
                    binding.titulo.text = documento.getString("Titulo")
                    binding.editora.text = documento.getString("Editora")
                    binding.genero.text = documento.getString("Genero")
                    binding.sinopse.text = documento.getString("Sinopse")
                    val imageUrl = documento.getString("imageUri")

                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.baseline_menu_book_24)
                            .error(R.drawable.baseline_menu_book_24)
                            .into(binding.imagemLivro)
                    } else {
                        Glide.with(this)
                            .load(R.drawable.baseline_menu_book_24)
                            .into(binding.imagemLivro)
                    }
                } else {
                    Log.e("LivroActivity", "Erro ao carregar os dados do livro", error)
                    Toast.makeText(this, "Erro ao carregar os dados do livro", Toast.LENGTH_SHORT).show()
                }
            }


    }
}
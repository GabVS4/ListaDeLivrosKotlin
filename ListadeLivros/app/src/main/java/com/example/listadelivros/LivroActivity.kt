package com.example.listadelivros

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.listadelivros.databinding.ActivityLivroBinding
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

        binding.imageVoltar.setOnClickListener{
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }

        binding.buttonEditar.setOnClickListener{
            val editarIntent = Intent(this, EditarLivroActivity::class.java)
            startActivity(editarIntent)
        }

        binding.buttonDeletar.setOnClickListener{

        }

        val idColecao = intent.getStringExtra("id")

        db.collection("Livros").document(idColecao!!)
            .addSnapshotListener{documento, error ->
            if(documento != null){
                binding.titulo.text = documento.getString("titulo")
                binding.editora.text = documento.getString("editora")
                binding.genero.text = documento.getString("genero")
                binding.sinopse.text = documento.getString("sinopse")
                val urlimage = documento.getString("imagemUri")

                Glide.with(this)
                    .load(urlimage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(binding.imagemLivro)
            }
        }

    }
}
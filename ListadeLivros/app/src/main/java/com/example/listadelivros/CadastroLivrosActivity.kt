package com.example.listadelivros

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.listadelivros.databinding.ActivityCadastroLivrosBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class CadastroLivrosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroLivrosBinding
    private var uriImagem: Uri? = null

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroLivrosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCancelar.setOnClickListener {
            finish()
        }

        binding.buttonCadastrar.setOnClickListener {
            val titulo = binding.titulo.text.toString()
            val editora = binding.editora.text.toString()
            val genero = binding.genero.text.toString()
            val sinopse = binding.sinopse.text.toString()

            if (titulo.isNotEmpty() && editora.isNotEmpty() && genero.isNotEmpty() && sinopse.isNotEmpty()) {
                if (uriImagem != null) {

                    val storageReference = storage.reference.child("Images/${UUID.randomUUID()}")
                    storageReference.putFile(uriImagem!!)
                        .addOnSuccessListener { task ->
                            task.metadata!!.reference!!.downloadUrl.addOnSuccessListener { url ->
                                val imagemUri = url.toString()
                                cadastrarLivro(titulo, editora, genero, sinopse, imagemUri)
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show()
                        }
                } else {

                    val imagemUri = "android.resource://$packageName/${R.drawable.ic_launcher_foreground}"
                    cadastrarLivro(titulo, editora, genero, sinopse, imagemUri)
                }
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.imagemLivro.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 22)
        }
    }

    private fun cadastrarLivro(titulo: String, editora: String, genero: String, sinopse: String, imagemUri: String) {
        val user = Firebase.auth.currentUser
        val livro = hashMapOf(
            "Titulo" to titulo,
            "Editora" to editora,
            "Genero" to genero,
            "Sinopse" to sinopse,
            "imageUri" to imagemUri
        )

        db.collection("Usuarios").document(user!!.uid).collection("Livros").add(livro)
            .addOnSuccessListener {
                Toast.makeText(this, "Livro cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao cadastrar o livro", Toast.LENGTH_LONG).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 22 && resultCode == RESULT_OK && data != null) {
            uriImagem = data.data
            binding.imagemLivro.setImageURI(uriImagem)
        }
    }
}

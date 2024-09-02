package com.example.listadelivros

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.listadelivros.databinding.ActivityEditarLivroBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class EditarLivroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarLivroBinding
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var uriImagem: Uri? = null
    private var idLivro: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarLivroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idLivro = intent.getStringExtra("id")

        db.collection("Usuarios").document(Firebase.auth.currentUser!!.uid)
            .collection("Livros").document(idLivro!!)
            .get()
            .addOnSuccessListener { documento ->
                if (documento != null) {
                    binding.titulo.setText(documento.getString("Titulo"))
                    binding.editora.setText(documento.getString("Editora"))
                    binding.genero.setText(documento.getString("Genero"))
                    binding.sinopse.setText(documento.getString("Sinopse"))
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
                    Log.e("EditarLivroActivity", "Erro ao carregar os dados do livro")
                    Toast.makeText(this, "Erro ao carregar os dados do livro", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("EditarLivroActivity", "Erro ao carregar o documento", e)
                Toast.makeText(this, "Erro ao carregar o livro", Toast.LENGTH_SHORT).show()
            }

        binding.buttonCancelar.setOnClickListener {
            finish()
        }

        binding.buttonConfirmar.setOnClickListener {
            atualizarLivro()
        }

        binding.imagemLivro.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, 22)
        }
    }

    private fun atualizarLivro() {
        val tituloAtualizado = binding.titulo.text.toString()
        val editoraAtualizada = binding.editora.text.toString()
        val generoAtualizado = binding.genero.text.toString()
        val sinopseAtualizada = binding.sinopse.text.toString()

        val livroAtualizado = hashMapOf(
            "Titulo" to tituloAtualizado,
            "Editora" to editoraAtualizada,
            "Genero" to generoAtualizado,
            "Sinopse" to sinopseAtualizada
        )

        val userId = Firebase.auth.currentUser!!.uid
        val livroRef = db.collection("Usuarios").document(userId).collection("Livros").document(idLivro!!)

        if (uriImagem != null) {
            val storageRef = storage.reference.child("Images/$idLivro.jpg")
            storageRef.putFile(uriImagem!!)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        livroAtualizado["imageUri"] = uri.toString()
                        livroRef.update(livroAtualizado as Map<String, Any>)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Livro atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.e("EditarLivroActivity", "Erro ao atualizar o livro", e)
                                Toast.makeText(this, "Erro ao atualizar o livro", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("EditarLivroActivity", "Erro ao fazer upload da imagem", e)
                    Toast.makeText(this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show()
                }
        } else {
            livroRef.update(livroAtualizado as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "Livro atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("EditarLivroActivity", "Erro ao atualizar o livro", e)
                    Toast.makeText(this, "Erro ao atualizar o livro", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 22 && resultCode == RESULT_OK && data != null) {
            val imageBitmap = data.extras?.get("data") as Bitmap

            binding.imagemLivro.setImageBitmap(imageBitmap)

            val tempFile = File.createTempFile("image", "jpg", cacheDir)
            val outputStream = FileOutputStream(tempFile)
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            uriImagem = Uri.fromFile(tempFile)
        }
    }
}

package com.example.listadelivros

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.listadelivros.databinding.ActivityCadastroLivrosBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class CadastroLivrosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroLivrosBinding

    private var uriImagem: Uri? = null

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro_livros)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityCadastroLivrosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCancelar.setOnClickListener{
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }

        val user = Firebase.auth.currentUser

        binding.buttonCadastrar.setOnClickListener{
            val titulo = binding.titulo.text.toString()
            val editora = binding.editora.text.toString()
            val genero = binding.genero.text.toString()
            val sinopse = binding.sinopse.text.toString()

            var storageReference = storage.getReference("Images")
            uriImagem?.let { imagem->
                storageReference.child(titulo).putFile(imagem).addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl.addOnSuccessListener { url ->
                        val imagemUri = url.toString()
                        Log.i("storage","operação realizada com sucesso")

                        var hashmap = hashMapOf<String?,Any>()
                        hashmap.put("titulo", titulo)
                        hashmap.put("editora", editora)
                        hashmap.put("genero", genero)
                        hashmap.put("sinopse", sinopse)
                        hashmap.put("imagemUri", imagemUri)

                        val livro = hashMapOf(
                            "Titulo" to titulo,
                            "Editora" to editora,
                            "Genero" to genero,
                            "Sinopse" to sinopse,
                            "imageUri" to imagemUri
                        )

                        var dbreference = db.collection("Usuarios").document(user!!.uid).collection("Livros").add(livro)
                            .addOnSuccessListener {
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("idLastLivro", titulo)
                            startActivity(intent)
                        }.addOnFailureListener{
                            Toast.makeText(this, "Erro", Toast.LENGTH_LONG).show()
                        }
                    }
                }.addOnFailureListener{
                    Toast.makeText(this, "ocorreu um erro ao enviar a imagem", Toast.LENGTH_SHORT).show()
                }
            }

        }

        binding.imagemLivro.setOnClickListener{
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns. MIME_TYPE, "image/jpeg")
            val resolver = contentResolver
            uriImagem =
                resolver.insert(MediaStore.Images.Media. EXTERNAL_CONTENT_URI, contentValues)
            intent.addFlags(Intent. FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImagem)
            startActivityForResult(intent,22)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.imagemLivro.setImageURI(uriImagem)
    }
}
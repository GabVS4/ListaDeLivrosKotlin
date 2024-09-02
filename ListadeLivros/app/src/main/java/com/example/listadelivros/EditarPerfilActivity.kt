package com.example.listadelivros

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.listadelivros.databinding.ActivityEditarPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var uriImagem: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        carregarDadosUsuario()

        binding.buttonEditarEmail.setOnClickListener {
            val usuario = firebaseAuth.currentUser
            val email = binding.emailPerfilEditar.text.toString()

            if (email.isNotEmpty()) {
                usuario?.verifyBeforeUpdateEmail(email)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        atualizarFirestoreEmail(usuario.uid, email)
                    } else {
                        Toast.makeText(this, "Erro ao alterar o email, tente novamente", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "O campo precisa ser preenchido", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonEditarSenha.setOnClickListener {
            val usuario = firebaseAuth.currentUser
            val senha = binding.senhaPerfilEditar.text.toString()

            if (senha.isNotEmpty()) {
                usuario?.updatePassword(senha)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val perfilIntent = Intent(this, PerfilActivity::class.java)
                        startActivity(perfilIntent)
                        finish()
                        Toast.makeText(this, "Senha alterada com sucesso", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Erro ao alterar a senha, tente novamente", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "O campo precisa ser preenchido", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonEditarNome.setOnClickListener {
            val novoNome = binding.nomePerfilEditar.text.toString()

            if (novoNome.isNotEmpty()) {
                atualizarNome(novoNome)
            } else {
                Toast.makeText(this, "O campo precisa ser preenchido", Toast.LENGTH_SHORT).show()
            }
        }

        binding.imagemPerfilEditar.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, 22)
        }

        binding.cancelarEdicao.setOnClickListener {
            val perfilIntent = Intent(this, PerfilActivity::class.java)
            startActivity(perfilIntent)
            finish()
        }
    }

    private fun carregarDadosUsuario() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        db.collection("Usuarios").document(userId).get()
            .addOnSuccessListener { documento ->
                if (documento != null) {
                    val nome = documento.getString("nome")
                    val email = documento.getString("email")
                    val imageUrl = documento.getString("imageUri")

                    binding.nomePerfilEditar.setText(nome)
                    binding.emailPerfilEditar.setText(email)

                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.baseline_person_24)
                            .error(R.drawable.baseline_person_24)
                            .into(binding.imagemPerfilEditar)
                    } else {
                        binding.imagemPerfilEditar.setImageResource(R.drawable.baseline_person_24)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar os dados do usuário", Toast.LENGTH_SHORT).show()
            }
    }

    private fun atualizarNome(novoNome: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        db.collection("Usuarios").document(userId)
            .update("nome", novoNome)
            .addOnSuccessListener {
                Toast.makeText(this, "Nome atualizado com sucesso", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao atualizar o nome", Toast.LENGTH_SHORT).show()
            }
    }

    private fun atualizarFirestoreEmail(userId: String, novoEmail: String) {
        db.collection("Usuarios").document(userId)
            .update("email", novoEmail)
            .addOnSuccessListener {
                Toast.makeText(this, "Email atualizado com sucesso", Toast.LENGTH_SHORT).show()
                val perfilIntent = Intent(this, PerfilActivity::class.java)
                startActivity(perfilIntent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao atualizar o email no Firestore", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 22 && resultCode == RESULT_OK && data != null) {
            val imageBitmap = data.extras?.get("data") as Bitmap

            binding.imagemPerfilEditar.setImageBitmap(imageBitmap)

            salvarImagemNoStorage(imageBitmap)
        }
    }

    private fun salvarImagemNoStorage(imageBitmap: Bitmap) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("ProfilePictures/$userId.jpg")
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                atualizarImagemPerfil(uri.toString())
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Erro ao salvar a imagem", Toast.LENGTH_SHORT).show()
        }
    }

    private fun atualizarImagemPerfil(imageUrl: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        db.collection("Usuarios").document(userId)
            .update("imageUri", imageUrl)
            .addOnSuccessListener {
                Toast.makeText(this, "Imagem de perfil atualizada com sucesso", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao atualizar a imagem no Firestore", Toast.LENGTH_SHORT).show()
            }
    }
}

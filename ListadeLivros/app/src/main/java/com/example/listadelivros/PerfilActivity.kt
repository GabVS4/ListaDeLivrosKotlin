package com.example.listadelivros

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.listadelivros.databinding.ActivityPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        carregarDadosUsuario()

        // Ação para desconectar
        binding.Desconectar.setOnClickListener {
            firebaseAuth.signOut()
            val logoutIntent = Intent(this, LoginActivity::class.java)
            startActivity(logoutIntent)
            finish()
        }

        // Ação para voltar
        binding.imageVoltar.setOnClickListener {
            val voltarIntent = Intent(this, MainActivity::class.java)
            startActivity(voltarIntent)
            finish()
        }

        // Ação para deletar usuário
        binding.buttonDeletar.setOnClickListener {
            deletarUsuario()
        }

        // Ação para editar perfil
        binding.buttonEditar.setOnClickListener {
            val editarIntent = Intent(this, EditarPerfilActivity::class.java)
            startActivity(editarIntent)
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

                    binding.nomePerfil.text = nome
                    binding.emailPerfil.text = email

                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.baseline_person_24)
                            .error(R.drawable.baseline_person_24)
                            .into(binding.imagemPerfil)
                    } else {
                        binding.imagemPerfil.setImageResource(R.drawable.baseline_person_24)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar os dados do usuário", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deletarUsuario() {
        val usuario = firebaseAuth.currentUser
        val userId = usuario?.uid ?: return

        // Deletar o documento do usuário no Firestore
        db.collection("Usuarios").document(userId).delete()
            .addOnSuccessListener {
                // Deletar a conta do usuário no Firebase Authentication
                usuario.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Conta deletada com sucesso", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, RegistroActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Falha ao deletar a conta, tente novamente", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Falha ao deletar os dados do Firestore, tente novamente", Toast.LENGTH_SHORT).show()
            }
    }
}

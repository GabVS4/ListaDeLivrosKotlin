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

        binding.Desconectar.setOnClickListener {
            firebaseAuth.signOut()
            val logoutIntent = Intent(this, LoginActivity::class.java)
            startActivity(logoutIntent)
            finish()
        }

        binding.imageVoltar.setOnClickListener {
            val voltarIntent = Intent(this, MainActivity::class.java)
            startActivity(voltarIntent)
            finish()
        }

        binding.buttonDeletar.setOnClickListener {
            deletarUsuario()
        }

        binding.buttonEditar.setOnClickListener {
            val editarIntent = Intent(this, EditarPerfilActivity::class.java)
            startActivity(editarIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        carregarDadosUsuario()
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
                } else {
                    Log.e("PerfilActivity", "Erro ao carregar os dados do usuário")
                    Toast.makeText(this, "Erro ao carregar os dados do usuário", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("PerfilActivity", "Erro ao carregar o documento", e)
                Toast.makeText(this, "Erro ao carregar o perfil", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deletarUsuario() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        db.collection("Usuarios").document(userId).delete()
            .addOnSuccessListener {
                firebaseAuth.currentUser?.delete()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Conta deletada com sucesso", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, RegistroActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Falha ao deletar a conta, tente novamente", Toast.LENGTH_SHORT).show()
                        Log.e("error: ", task.exception.toString())
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Falha ao deletar os dados do Firestore, tente novamente", Toast.LENGTH_SHORT).show()
            }
    }
}

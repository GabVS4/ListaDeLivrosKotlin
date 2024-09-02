package com.example.listadelivros

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.listadelivros.databinding.ActivityRegistroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.buttonRegistrar.setOnClickListener {
            val nome = binding.registroNome.text.toString()
            val email = binding.registroEmail.text.toString()
            val senha = binding.registroSenha.text.toString()
            val confirmarSenha = binding.registroSenhaConfirmar.text.toString()

            if (nome.isNotEmpty() && email.isNotEmpty() && senha.isNotEmpty() && confirmarSenha.isNotEmpty()) {
                if (senha == confirmarSenha) {
                    firebaseAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val userId = firebaseAuth.currentUser!!.uid

                            val imagemUri = "android.resource://$packageName/${R.drawable.baseline_person_24}"
                            salvarUsuarioNoFirestore(userId, nome, email, imagemUri)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Certifique-se de preencher todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginRedirecionamento.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    private fun salvarUsuarioNoFirestore(userId: String, nome: String, email: String, imagemUri: String) {
        val usuario = hashMapOf(
            "nome" to nome,
            "email" to email,
            "imageUri" to imagemUri
        )

        db.collection("Usuarios").document(userId).set(usuario)
            .addOnSuccessListener {
                Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar os dados do usuário", Toast.LENGTH_SHORT).show()
            }
    }
}

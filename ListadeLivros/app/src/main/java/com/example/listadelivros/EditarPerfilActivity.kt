package com.example.listadelivros

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.listadelivros.databinding.ActivityEditarPerfilBinding
import com.google.firebase.auth.FirebaseAuth

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var firebaseAuth: FirebaseAuth

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

        binding.buttonEditarEmail.setOnClickListener{
            val usuario = firebaseAuth.currentUser
            val email = binding.emailPerfilEditar.text.toString()

            if(email.isNotEmpty()){
                usuario?.verifyBeforeUpdateEmail(email)?.addOnCompleteListener{
                    if(it.isSuccessful){
                        val perfilIntent = Intent(this, PerfilActivity::class.java)
                        startActivity(perfilIntent)
                        finish()
                        Toast.makeText(this, "Email alterado com sucesso", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Erro ao alterar o email, tente novamente", Toast.LENGTH_SHORT).show()
                    }
                }
            } else{
                Toast.makeText(this, "O campo precisa ser preenchido", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonEditarSenha.setOnClickListener{
            val usuario = firebaseAuth.currentUser
            val senha = binding.senhaPerfilEditar.text.toString()

            if(senha.isNotEmpty()){
                usuario?.updatePassword(senha)?.addOnCompleteListener{
                    if(it.isSuccessful){
                        val perfilIntent = Intent(this, PerfilActivity::class.java)
                        startActivity(perfilIntent)
                        finish()
                        Toast.makeText(this, "Senha alterada com sucesso", Toast.LENGTH_SHORT).show()
                    } else{
                        Toast.makeText(this, "Erro ao alterar a senha, tente novamente", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "O campo precisa ser preenchido", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelarEdicao.setOnClickListener{
            val perfilIntent = Intent(this, PerfilActivity::class.java)
            startActivity(perfilIntent)
            finish()
        }
    }
}
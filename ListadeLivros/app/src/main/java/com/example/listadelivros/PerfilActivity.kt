package com.example.listadelivros

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.listadelivros.databinding.ActivityPerfilBinding
import com.google.firebase.auth.FirebaseAuth

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var firebaseAuth: FirebaseAuth

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

        val emailPerfil:TextView = findViewById(R.id.email_perfil)
        val senhaPefil:TextView = findViewById(R.id.senha_perfil)
        emailPerfil.text = firebaseAuth.currentUser?.email
        senhaPefil.text = "********"

        binding.Desconectar.setOnClickListener{
            firebaseAuth.signOut()

            val logoutIntent = Intent(this, LoginActivity::class.java)
            startActivity(logoutIntent)
            finish()
        }

        binding.imageVoltar.setOnClickListener{
            val voltarIntent = Intent(this, MainActivity::class.java)
            startActivity(voltarIntent)
        }

        binding.buttonDeletar.setOnClickListener{
            val user = firebaseAuth.currentUser
            user?.delete()?.addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(this, "Conta deletada com sucesso", Toast.LENGTH_SHORT).show()
                    val deletIntent = Intent(this, LoginActivity::class.java)
                    startActivity(deletIntent)
                    finish()
                } else{
                    Toast.makeText(this, "Falha ao deletar a conta", Toast.LENGTH_SHORT).show()
                    Log.e("error: ", it.exception.toString())
                }
            }
        }

        binding.buttonEditar.setOnClickListener{
            val editarIntent = Intent(this, EditarPerfilActivity::class.java)
            startActivity(editarIntent)
        }
    }
}
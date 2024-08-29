package com.example.listadelivros

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.listadelivros.databinding.ActivityEditarLivroBinding
import com.google.firebase.auth.FirebaseAuth

class EditarLivroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarLivroBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_livro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityEditarLivroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCancelar.setOnClickListener{
            val livroIntent = Intent(this, LivroActivity::class.java)
            startActivity(livroIntent)
        }

        binding.buttonConfirmar.setOnClickListener{

        }

        binding.imagemLivro.setOnClickListener{

        }
    }
}
package com.example.listadelivros

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.listadelivros.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idLastlivro = intent.getStringExtra(/* name = */ "idLastLivro")

        binding.imageVoltar.setOnClickListener{
            val voltarIntent = Intent(this, LoginActivity::class.java)
            startActivity(voltarIntent)
        }

        binding.imagemPerfil.setOnClickListener{
            val perfilIntent = Intent(this, PerfilActivity::class.java)
            startActivity(perfilIntent)
        }

        binding.buttonCadastrar.setOnClickListener{
            val cadastroIntent = Intent(this, CadastroLivrosActivity::class.java)
            startActivity(cadastroIntent)
        }

        binding.buttonVerLivro.setOnClickListener{
            val livrosIntent = Intent(this, LivrosRecyclerActivity::class.java)
            livrosIntent.putExtra("id", idLastlivro)
            startActivity(livrosIntent)
        }

    }
}
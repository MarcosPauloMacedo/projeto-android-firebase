package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Cadastro : AppCompatActivity() {

    private lateinit var editNome : EditText
    private lateinit var editEmail : EditText
    private lateinit var editPassword : EditText
    private lateinit var btnCadastrar : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)
        getSupportActionBar()?.hide()

        editNome = findViewById(R.id.editUsername)
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)
        btnCadastrar = findViewById(R.id.btEnter)

        btnCadastrar.setOnClickListener {
            val nome = editNome.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()

            if (nome.isEmpty() || email.isEmpty() || password.isEmpty()) {
                val mensagemErro = "Campos não preenchidos, tente novamente"
                val snackbar = Snackbar.make(it, mensagemErro, Snackbar.LENGTH_LONG)
                snackbar.show()
            } else {
                handleCadastrarUsuario(it)
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun handleCadastrarUsuario (it : View) {
        val email = editEmail.text.toString().trim()
        val password = editPassword.text.toString().trim()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{task ->
                if (task.isSuccessful){
                    salvarDadosUsuario()
                    val mensagemOk = "Cadastro Realizado Com Sucesso"
                    val snackbar = Snackbar.make(it, mensagemOk, Snackbar.LENGTH_LONG)
                    snackbar.show()
                }else{
                    val mensagemErro = "Erro ao Cadastrar Usuario"
                    val snackbar = Snackbar.make(it, mensagemErro, Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
            }
    }

    fun salvarDadosUsuario () {
        val db = FirebaseFirestore.getInstance()
        val nome = editNome.text.toString().trim()
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
        val email = FirebaseAuth.getInstance().currentUser?.email

        if (usuarioId != null && email != null) {
            val usuarios = hashMapOf(
                "nome" to nome,
                "email" to email,
                "uid" to usuarioId
            )

            db.collection("Usuarios")
                .add(usuarios)
                .addOnSuccessListener { documentReference ->
                    println("Documento adicionado com ID: ${documentReference.id}")
                }
                .addOnFailureListener{e ->
                    println("Erro ao adicionar documento: $e")
                }
        }else{
            println("Erro na autenticação")
        }
    }
}
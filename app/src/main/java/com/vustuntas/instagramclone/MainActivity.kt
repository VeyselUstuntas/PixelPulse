package com.vustuntas.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.TransformationMethod
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vustuntas.instagramclone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth : FirebaseAuth
    private var email : String? = null
    private var password : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.checkboxPassword.setOnCheckedChangeListener { compoundButton, result ->
            if(result){
                binding.userPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            else{
                binding.userPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        auth = Firebase.auth

        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this@MainActivity,FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    fun signIn(view : View){
        email = binding.userName.text.toString()
        password = binding.userPassword.text.toString()
        if(!email.equals("") && !password.equals("")){
            auth.signInWithEmailAndPassword(email!!,password!!)
                .addOnSuccessListener {
                    val intent = Intent(this@MainActivity,FeedActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                }
        }
        else{
            Toast.makeText(this@MainActivity,"Enter email and password",Toast.LENGTH_LONG).show()
        }

    }

    fun signUp(view : View){
        email = binding.userName.text.toString()
        password = binding.userPassword.text.toString()
        if(!email.equals("") && !password.equals("")){
            auth.createUserWithEmailAndPassword(email!!,password!!)
                .addOnSuccessListener {
                //Başarılı gerçekleşirse bunu yap
                    val intent = Intent(this@MainActivity,FeedActivity::class.java)
                    startActivity(intent)
                    finish()
            }
                .addOnFailureListener {
                    //Hata
                    Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                    //localizedMessage hata mesajını kullanıcının anlayacağo şekile çevirme işlemi
                }
        }
        else
            Toast.makeText(this@MainActivity,"Enter email and password",Toast.LENGTH_LONG).show()

    }


}
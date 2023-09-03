package com.vustuntas.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import com.vustuntas.instagramclone.databinding.ActivityFeedBinding
import com.vustuntas.instagramclone.databinding.ActivityMainBinding
import java.util.ArrayList

class FeedActivity : AppCompatActivity() {
    private lateinit var binding : ActivityFeedBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var fireStore : FirebaseFirestore

    private lateinit var postArrayList : ArrayList<Post>
    private lateinit var recyclerAdapter : RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFeedBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        postArrayList = ArrayList<Post>()
        recyclerAdapter = RecyclerAdapter(postArrayList)
        auth = Firebase.auth
        fireStore = Firebase.firestore
        getData()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = recyclerAdapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_tool_bar,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logout){
            auth.signOut()
            val intent = Intent(this@FeedActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else if(item.itemId == R.id.addContent){
            val intent = Intent(this@FeedActivity,UploadActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }


    fun getData(){
        fireStore.collection("Posts")
            .orderBy("date",Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->

                if(error != null){
                    Toast.makeText(this@FeedActivity,error.localizedMessage,Toast.LENGTH_LONG).show()
                }
                else{
                    if(value != null){
                        if(!value.isEmpty){
                            val documents = value.documents
                            postArrayList.clear()
                            for(i in documents){
                                val comment = i.get("comment")  as String // bize get içerisinde anahtarı soruyor. Casting işlemi
                                val userEmail  =i.get("userEmail") as String
                                val downloadURL = i.get("downloadUrl") as String // bunları bir model oluşturcaz ona atayacaz

                                val postObject = Post(userEmail,downloadURL,comment)
                                postArrayList.add(postObject)
                            }
                            recyclerAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }


}
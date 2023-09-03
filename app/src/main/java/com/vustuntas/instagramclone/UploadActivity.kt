package com.vustuntas.instagramclone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.view.View
import android.webkit.PermissionRequest
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.vustuntas.instagramclone.databinding.ActivityMainBinding
import com.vustuntas.instagramclone.databinding.ActivityUploadBinding
import java.io.OutputStream
import java.util.UUID

class UploadActivity : AppCompatActivity() {
    private lateinit var binding : ActivityUploadBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionsResultLauncher: ActivityResultLauncher<String>
    private var selectedImageUri : Uri? = null

    private lateinit var fireAuth : FirebaseAuth
    private lateinit var fireStorage : FirebaseStorage
    private lateinit var fireFireStore : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerLauncher()

        fireStorage = Firebase.storage
        fireFireStore = Firebase.firestore
        fireAuth = Firebase.auth
    }

    fun uploadContent(view:View){
        val uuid = UUID.randomUUID()
        val imageName = "${uuid}.jpg"
        val referance = fireStorage.reference
        val imageReferances = referance.child("images").child(imageName)
        if(selectedImageUri != null){
            imageReferances.putFile(selectedImageUri!!)
                .addOnSuccessListener { // Eğer resim Storage düzgün bir şekilde yüklenirse.
                    val uploadImages = fireStorage.reference.child("images").child(imageName) // yüklenen resmi aldık urlsini çekecez
                    uploadImages.downloadUrl.addOnSuccessListener {
                        val downloadUrl = it.toString()
                        // url alındaı şimdi bunu ve commenti veritabanına yazmak kaldı.
                        if(fireAuth.currentUser != null){
                            val postMap = HashMap<String,Any>()
                            postMap.put("downloadUrl",downloadUrl)
                            postMap.put("userEmail",fireAuth.currentUser!!.email!!)
                            postMap.put("comment",binding.commentTextView.text.toString())
                            postMap.put("date",Timestamp.now())

                            fireFireStore.collection("Posts").add(postMap) // Posts isimli koleksiyona yükle
                                .addOnSuccessListener{
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                                }
                        }

                    }

                }
                .addOnFailureListener {

                }
        }
    }

    fun selectImage(view: View){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this@UploadActivity,Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(binding.root,"İzin Verilmemiş İzin Verilsin mi?",Snackbar.LENGTH_INDEFINITE).setAction("Evet"){result ->
                        //izin iste
                        permissionsResultLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }.show()
                }
                else{
                    //izin iste
                    permissionsResultLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
            else{
                //izin verilmiş
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)
            }
        }
        else{
            if(ContextCompat.checkSelfPermission(this@UploadActivity,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(binding.root,"İzin Verilmemiş İzin Verilsin mi?",Snackbar.LENGTH_INDEFINITE).setAction("Evet"){result ->
                        //izin iste
                        permissionsResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
                }
                else{
                    //izin iste
                    permissionsResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            else{
                //izin verilmiş
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)
            }
        }
    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if(result.resultCode == RESULT_OK){
                val imageIntent = result.data
                if(imageIntent != null){
                    val imageUri = imageIntent.data
                    if(imageUri != null){
                        selectedImageUri = imageUri
                        if(Build.VERSION.SDK_INT >= 28){
                            val source = ImageDecoder.createSource(this.contentResolver,imageUri)
                            val selectedBitmapImage = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(selectedBitmapImage)
                        }
                        else{
                            val selectedBitmapImage = MediaStore.Images.Media.getBitmap(this.contentResolver,imageUri)
                            binding.imageView.setImageBitmap(selectedBitmapImage)
                        }
                    }
                }
            }
        }
        permissionsResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if(result){
                //izin alındı olumlu
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)
            }
            else{
                Toast.makeText(this@UploadActivity,"İzin Verilmemiş",Toast.LENGTH_LONG).show()
            }
        }
    }

}
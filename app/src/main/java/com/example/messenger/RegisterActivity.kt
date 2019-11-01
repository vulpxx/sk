package com.example.messenger

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.accessibility.AccessibilityEventSource
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register_button_register.setOnClickListener {
            performRegister()
              }

        already_have_account_text_view.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        selectphoto_button_register.setOnClickListener{
            Toast.makeText(this@RegisterActivity, "Try to show photo selector:", Toast.LENGTH_SHORT).show()

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, requestCode@0)

        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data!= null)
            Toast.makeText(this@RegisterActivity, "Photo Was Selected:", Toast.LENGTH_SHORT).show()

        selectedPhotoUri= data?.data

        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

        selectphoto_imageview_register.setImageBitmap(bitmap)

        selectphoto_button_register.alpha = 0f


       // val bitmapDrawable = BitmapDrawable(bitmap)
       // selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
    }

    private fun performRegister(){
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        if (email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(this@RegisterActivity, "Please enter the email/pw:", Toast.LENGTH_SHORT).show()
            return
        }
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener{
             Toast.makeText(this@RegisterActivity, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
          }
      }

    private fun uploadImageToFirebaseStorage() {

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener{
                Toast.makeText(this@RegisterActivity,  "Successfully uploaded image:, ${it.metadata?.path}",
                Toast.LENGTH_SHORT).show()


        ref.downloadUrl.addOnSuccessListener{
            Toast.makeText(this@RegisterActivity,  "File Location:, ${it}",
            Toast.LENGTH_SHORT).show()

            saveUserToFirebaseStorage(it.toString())
        }
            .addOnFailureListener{

            }
            }
    }

    private fun  saveUserToFirebaseStorage(profileImageUrl: String) {

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this@RegisterActivity,  "Finally we saved the user to Firebase Database:",
                Toast.LENGTH_SHORT).show()
            }

    }
}

private fun AccessibilityEventSource.setImageBitmap(bitmap: Bitmap?) {

}

class User(val uid: String, val username: String, val_profileImageUrl: String){

}






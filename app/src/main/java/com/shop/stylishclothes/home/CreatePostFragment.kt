package com.shop.stylishclothes.home

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.shop.stylishclothes.MainActivity
import com.shop.stylishclothes.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import kotlin.properties.Delegates


class CreatePostFragment : Fragment() {

    private val PICK_IMAGE = 1
    lateinit var image: ImageView
    var firstByteImg: ByteArray? = null
    var secondByteImg: ByteArray? = null
    var numImg: Int by Delegates.notNull()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        var rootView = inflater.inflate(R.layout.fragment_create_post, container, false)


        //Initialisation
        var firstImageView = rootView.findViewById<ImageView>(R.id.first_post_image_view)
        var secondImageView = rootView.findViewById<ImageView>(R.id.second_post_image_view)
        var uploadFirstButton = rootView.findViewById<Button>(R.id.first_upload_button)
        var uploadSecondButton = rootView.findViewById<Button>(R.id.second_upload_button)
        var createButton =
            rootView.findViewById<ExtendedFloatingActionButton>(R.id.create_post_button)


        //Buttons
        uploadFirstButton.setOnClickListener {
            numImg = 0
            image = firstImageView
            imageIntent()
        }

        uploadSecondButton.setOnClickListener {
            numImg = 1
            image = secondImageView
            imageIntent()
        }

        createButton.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (firstByteImg != null) {
                var firstIdEditText =
                    rootView.findViewById<EditText>(R.id.first_product_id_edit_text)
                if (firstIdEditText.text.trim().isNotEmpty()) {


                    val storageRef = FirebaseStorage.getInstance().getReference("ImageDB")
                        .child(java.util.UUID.randomUUID().toString())
                    var uploadTask = storageRef.putBytes(firstByteImg!!)

                    val urlTask = uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        storageRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val downloadUri = task.result //Uri of the first image
                            FirebaseDatabase.getInstance()
                                .getReference("Home/timeline/$currentTime/p_1/id")
                                .setValue(firstIdEditText.text.toString().trim())
                            FirebaseDatabase.getInstance()
                                .getReference("Home/timeline/$currentTime/p_1/img")
                                .setValue(downloadUri.toString())
                        }
                    }

                    if (secondByteImg != null) {
                        var secondIdEditText =
                            rootView.findViewById<EditText>(R.id.second_product_id_edit_text)
                        if (secondIdEditText.text.trim().isNotEmpty()) {

                            val storageRef = FirebaseStorage.getInstance().getReference("ImageDB")
                                .child(java.util.UUID.randomUUID().toString())
                            var uploadTask = storageRef.putBytes(secondByteImg!!)
                            val urlTask = uploadTask.continueWithTask { task ->
                                if (!task.isSuccessful) {
                                    task.exception?.let {
                                        throw it
                                    }
                                }
                                storageRef.downloadUrl
                            }.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    //Uri of the first image
                                    val downloadUri = task.result //Uri of the first image
                                    FirebaseDatabase.getInstance()
                                        .getReference("Home/timeline/$currentTime/p_2/id")
                                        .setValue(secondIdEditText.text.toString().trim())
                                    FirebaseDatabase.getInstance()
                                        .getReference("Home/timeline/$currentTime/p_2/img")
                                        .setValue(downloadUri.toString())

                                }
                            }
                            startActivity(Intent(context, MainActivity::class.java))
                        } else {
                            secondIdEditText.setError("Введіть id!")
                            secondIdEditText.requestFocus()
                        }
                    } else {
                        startActivity(Intent(context, MainActivity::class.java))
                    }
                } else {
                    firstIdEditText.setError("Введіть id!")
                    firstIdEditText.requestFocus()
                }
            } else {
                Toast.makeText(context, "Виберіть перше зображення!", Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {
            var imageUri = data?.data
            var bitmap = MediaStore.Images.Media.getBitmap(
                requireActivity().applicationContext.contentResolver,
                imageUri
            )
            image.setImageBitmap(bitmap)

            when (numImg) {
                0 -> firstByteImg = imageViewToByte(image)
                1 -> secondByteImg = imageViewToByte(image)
            }

            while (bitmap.byteCount > 500000) {
                bitmap =
                    Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, false)
            }
            image.setImageBitmap(bitmap)
        }
    }

    private fun imageViewToByte(image: ImageView): ByteArray {
        var bitmap = (image.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()

        while (bitmap.byteCount > 25000000) {
            bitmap = Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width / 1.1).toInt(), (bitmap.height / 1.1).toInt(), false
            )
        }

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun imageIntent() {
        val gallery = Intent()
        gallery.type = "image/*"
        gallery.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE)
    }

}
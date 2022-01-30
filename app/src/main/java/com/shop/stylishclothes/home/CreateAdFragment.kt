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
import com.shop.stylishclothes.MainActivity
import com.shop.stylishclothes.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class CreateAdFragment : Fragment() {

    private val PICK_IMAGE = 1
    lateinit var image: ImageView
    var byteImg: ByteArray? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_create_ad, container, false)

        var imageView = rootView.findViewById<ImageView>(R.id.ad_image_view)

        var uploadButton = rootView.findViewById<Button>(R.id.upload_button)
        uploadButton.setOnClickListener {
            image = imageView
            imageIntent()
        }

        var createAdButton = rootView.findViewById<Button>(R.id.create_ad_button)
        createAdButton.setOnClickListener {

            val currentTime = System.currentTimeMillis()
            if (byteImg != null) {
                var productIdEditText = rootView.findViewById<EditText>(R.id.product_id_edit_text)


                val storageRef = FirebaseStorage.getInstance().getReference("ImageDB")
                    .child(java.util.UUID.randomUUID().toString())
                var uploadTask = storageRef.putBytes(byteImg!!)
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
                        if (productIdEditText.text.trim().toString().isNotEmpty()) {
                            FirebaseDatabase.getInstance()
                                .getReference("Home/advertisement/$currentTime/id")
                                .setValue(productIdEditText.text.toString().trim())
                        }
                        FirebaseDatabase.getInstance()
                            .getReference("Home/advertisement/$currentTime/img")
                            .setValue(downloadUri.toString())
                    }
                }

                startActivity(Intent(context, MainActivity::class.java))


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

            byteImg = imageViewToByte(image)


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
package com.example.stylishclothes.home

import android.app.AlertDialog
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.stylishclothes.MainActivity
import com.example.stylishclothes.R
import com.example.stylishclothes.catalog.OneProductActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.util.*

class HomeFragment : Fragment() {

    var viewPager: ViewPager? = null
    var adImagesPath = arrayListOf<String>()
    private var timer: Timer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        requireActivity().title = "Stylish Clothes"

        //Advertisement (viewpager)
        val databaseAdReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Home/advertisement/")
        databaseAdReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    adImagesPath.add(snapshot.child("${it.key}").child("img").value as String)
                }
                setViewPager(rootView)

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: can't load the images path", Toast.LENGTH_SHORT).show()
            }
        })

        //Timeline
        val timelineLayout: LinearLayout = rootView.findViewById(R.id.timeline_layout)
        val databaseTimelineReference =
            FirebaseDatabase.getInstance().getReference("Home/timeline/")
        databaseTimelineReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                try {
                snapshot.children.reversed().forEach { key ->
                    val linearLayout: LinearLayout =
                        LinearLayout(ContextWrapper(context), null, R.style.LinearLayoutMain)

                    FirebaseDatabase.getInstance().getReference("Home/timeline/" + key.key)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                try {
                                    snapshot.children.forEach {
                                        val imageView = ImageView(
                                            ContextWrapper(context),
                                            null,
                                            R.style.ImageViewMain
                                        )

                                        val productId =
                                            snapshot.child("${it.key}").child("id").value
                                        linearLayout.addView(imageView)
                                        Picasso.get().load(
                                            snapshot.child("${it.key}").child("img").value as String
                                        ).into(imageView)
                                        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                                        val margins: Int = TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP,
                                            1.2f,
                                            resources.displayMetrics
                                        )
                                            .toInt()
                                        imageView.layoutParams.apply {
                                            (this as LinearLayout.LayoutParams).weight = 1f
                                            (this as ViewGroup.MarginLayoutParams).setMargins(
                                                margins,
                                                margins,
                                                margins,
                                                margins
                                            )
                                        }
                                        imageView.layoutParams.width =
                                            LinearLayout.LayoutParams.MATCH_PARENT
                                        imageView.layoutParams.height = TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP,
                                            450f,
                                            resources.displayMetrics
                                        )
                                            .toInt()

                                        imageView.setOnClickListener {
                                            val intent =
                                                Intent(context, OneProductActivity::class.java)
                                            intent.putExtra("ProductId", "$productId")
                                            startActivity(intent)
                                        }

                                        //for deleting of product (ONLY FOR ADMIN ) todo
                                        imageView.setOnLongClickListener {

                                            val closeBuilder = AlertDialog.Builder(context)
                                            closeBuilder.setMessage("Are you sure you want to delete this post?")
                                            closeBuilder.setCancelable(true)

                                            closeBuilder.setPositiveButton(
                                                "Yes"
                                            ) { _, _ ->
                                                FirebaseDatabase.getInstance().getReference("Home/timeline/${key.key}")
                                                    .addValueEventListener(object : ValueEventListener {
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            try {
                                                                FirebaseStorage.getInstance().getReferenceFromUrl(snapshot.child("p_1/img").value.toString()).delete()
                                                                FirebaseStorage.getInstance().getReferenceFromUrl(snapshot.child("p_2/img").value.toString()).delete()
                                                            } catch (e : Exception) {
                                                                e.printStackTrace()
                                                            }
                                                            snapshot.ref.removeValue()
                                                        }
                                                        override fun onCancelled(error: DatabaseError) {}
                                                    })
                                            }

                                            closeBuilder.setNegativeButton(
                                                "No"
                                            ) { dialog, _ -> dialog.cancel() }

                                            val closeAlert = closeBuilder.create()
                                            closeAlert.show()

                                            true
                                        }

                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(
                                    context,
                                    "Error: can't the timeline images",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        })
                    timelineLayout.addView(linearLayout)
                }
            } catch (e : Exception) {
                e.printStackTrace()
            }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: can't the timeline images", Toast.LENGTH_SHORT).show()
            }

        })

        //Floating add button (ONLY FOR ADMIN) TODO
        var fab : FloatingActionButton = rootView.findViewById(R.id.floating_add_button)
        fab.setOnClickListener {
            startActivity(Intent(context, CreatePostActivity::class.java))
        }

        return rootView
    }

    private fun setViewPager(rootView: View) {
        viewPager = rootView.findViewById<View>(R.id.pager) as ViewPager
        val adapter: PagerAdapter = HomeViewPagerAdapter(this@HomeFragment, adImagesPath, context)
        viewPager!!.adapter = adapter
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                viewPager!!.post {
                    viewPager!!.currentItem = (viewPager!!.currentItem + 1) % adImagesPath.size
                }
            }
        }
        timer = Timer()
        timer!!.schedule(timerTask, 6000, 6000)
    }

}
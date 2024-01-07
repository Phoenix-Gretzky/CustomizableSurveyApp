package com.nasa.customizablesurveyapp.ui.activity

import android.R.attr.value
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.nasa.customizablesurveyapp.FullscreenActivity
import com.nasa.customizablesurveyapp.data.model.SurveyDataItem
import com.nasa.customizablesurveyapp.databinding.ActivityMainBinding
import com.nasa.customizablesurveyapp.databinding.PopupEditPhotoBinding
import com.nasa.customizablesurveyapp.ui.adapter.RecyclerViewAdapter
import com.nasa.customizablesurveyapp.viewmodel.SurveyViewModel
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val CAMERA_PICK = 1
    private val PICK_FROM_GALLERY = 2
    val TYPE_SINGLE_CHOICE = "SINGLE_CHOICE"
    val TYPE_PHOTO_CHOICE = "PHOTO"
    val TYPE_COMMENT_CHOICE = "COMMENT"
    private lateinit var binding: ActivityMainBinding
    private val surveyViewModel: SurveyViewModel by viewModels();
    var surveyDataItem: SurveyDataItem? = null
    var viewBinding: ViewBinding? = null
    var position: Int = 0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)

        setViewProperties();
    }


    private fun setViewProperties() {
        try {
            observeViewModel()
            getData()
            binding.progressCircular.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getData() {
        surveyViewModel.callApi(applicationContext);
    }


    private fun observeViewModel() {
        try {
            surveyViewModel.observableApiData?.observe(this) {
                if (it != null) {
                    binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    val adapter = RecyclerViewAdapter(it,binding)
                    binding.recyclerView.adapter = adapter
                    binding.recyclerView.visibility = View.VISIBLE

                    adapter.SetOnClickListener(object : RecyclerViewAdapter.OnClickListener {
                        override fun onClick(
                            position: Int,
                            binding: ViewBinding,
                            surveyDataItem: SurveyDataItem,
                        ) {
                            saveData(surveyDataItem, binding, position)
                            if (surveyDataItem.exists == null) {
                                // if we call showDialog() from here it will show additional functionality to
                                // pick from gallery or click from camera
                                takePhotoFromCamera()
                            } else {
                                //open full screen activity for image view
                                val myIntent =
                                    Intent(applicationContext, FullscreenActivity::class.java)
                                myIntent.putExtra("bitmap", surveyDataItem.exists)
                                startActivity(myIntent)

                            }
                        }
                    })

                } else {
                    Toast.makeText(
                        applicationContext,
                        "Something went wrong please restart the app.",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                binding.progressCircular.visibility = View.GONE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode === Activity.RESULT_OK) {
            if (requestCode === PICK_FROM_GALLERY) {
                if (data != null) {
                    val contentURI: Uri = data?.data!!
                    try {

                        val bitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                        //convert uri to bitmap and store it in this variable to make it work with gallery photos also
                        surveyDataItem?.exists = bitmap
                        binding.recyclerView.adapter!!.notifyItemChanged(position)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            (viewBinding?.root!!.context as MainActivity),
                            "Failed!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else if (requestCode == CAMERA_PICK) {
                if (data != null) {
//                    val contentURI: Uri = data?.data!!
                    try {


                        (data.extras?.get("data") as Bitmap).let {
                            surveyDataItem?.exists = it
                        }

//                        surveyDataItem?.exists=contentURI.toString();
                        binding.recyclerView.adapter!!.notifyItemChanged(position)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            (viewBinding?.root!!.context as MainActivity),
                            "Failed!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    fun showDialog() {
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(viewBinding?.root!!.context)
        builder.setTitle("Choose Image Source")
        var popupEditPhotoBinding = PopupEditPhotoBinding.inflate(layoutInflater);

        popupEditPhotoBinding.cameraImageView.setOnClickListener {
            takePhotoFromCamera()
            dialog?.dismiss()
        }
        popupEditPhotoBinding.galleryImageView.setOnClickListener {
            choosePhotoFromGallary()
            dialog?.dismiss()

        }
        builder.setView(popupEditPhotoBinding.root)
        dialog = builder.create()
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show()


    }


    private fun takePhotoFromCamera() {
        val resultUri: Uri?


        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager).also {
                startActivityForResult(takePictureIntent, CAMERA_PICK)
            }

        }


    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(galleryIntent, PICK_FROM_GALLERY)
    }


    fun saveData(surveyDataItem: SurveyDataItem, binding: ViewBinding, position: Int) {
        this.viewBinding = binding
        this.surveyDataItem = surveyDataItem;
        this.position = position

    }

}
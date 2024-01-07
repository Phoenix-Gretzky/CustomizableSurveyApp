package com.nasa.customizablesurveyapp.ui.adapter


import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nasa.customizablesurveyapp.R
import com.nasa.customizablesurveyapp.data.model.SurveyData
import com.nasa.customizablesurveyapp.data.model.SurveyDataItem
import com.nasa.customizablesurveyapp.databinding.ActivityMainBinding
import com.nasa.customizablesurveyapp.databinding.PopupEditPhotoBinding
import com.nasa.customizablesurveyapp.databinding.RecyclerUserTileBinding
import com.nasa.customizablesurveyapp.databinding.TypeCommentBinding
import com.nasa.customizablesurveyapp.databinding.TypeSingleChoiceBinding
import com.nasa.customizablesurveyapp.ui.activity.MainActivity
import java.util.Objects


class RecyclerViewAdapter(val surveyData: SurveyData,activityMainBinding: ActivityMainBinding) :
    RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {


    val hashMap:HashMap<String,String?> = HashMap()
    var onClickListener: OnClickListener? = null
    val TYPE_SINGLE_CHOICE = "SINGLE_CHOICE"
    val TYPE_PHOTO_CHOICE = "PHOTO"
    val TYPE_COMMENT_CHOICE = "COMMENT"
    private val CAMERA_PICK = 1
    private val PICK_FROM_GALLERY = 2


    class MyViewHolder(val binding: RecyclerUserTileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // to load image into imageview using glide url
        fun LoadImagesIntoTheImagesView(
            mContext: Context,
            glideUrl: String?,
            imageView: ImageView?
        ) {
            try {
                (mContext as Activity).runOnUiThread {
                    Glide.with(mContext)
                        .load(glideUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.baseline_add_circle_24)
                        .error(R.drawable.baseline_error_24)
                        .into(imageView!!)

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        fun hideEverything() {
            binding.typeImage.root.visibility = View.GONE
            binding.typeSingleChoice.root.visibility = View.GONE
            binding.typeComment.root.visibility = View.GONE
        }


    }

    init {
        for (survey in surveyData)
        {
           hashMap.put(survey.id,null)
        }


        activityMainBinding.submit.setOnClickListener{
          for ((k,v) in hashMap)
             Log.i("Values Submit","key : $k -- value : $v")
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context);
        val binding = RecyclerUserTileBinding.inflate(layoutInflater, parent, false)
        return MyViewHolder(binding)
    }

    // A function to bind the onclickListener.
    fun SetOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(
            position: Int,
            binding: ViewBinding,
            surveyDataItem: SurveyDataItem
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val surveyDataItem = surveyData[position]

        holder.hideEverything()
        lateinit var binding: ViewBinding;
        when {
            surveyDataItem.type == TYPE_PHOTO_CHOICE -> {
                binding = holder.binding.typeImage

                binding.root.visibility = View.VISIBLE;
                binding.heading.text = surveyDataItem.title

                if (surveyDataItem.exists != null) {
                    holder.binding.typeImage.photo.setImageBitmap(surveyDataItem.exists)
                    binding.delete.visibility = View.VISIBLE

                    enterDataInHashMap(surveyDataItem.id,surveyDataItem.exists.toString())

                }

                binding.delete.setOnClickListener {
                    surveyDataItem.exists = null
                    holder.binding.typeImage.photo.setImageResource(R.drawable.baseline_add_circle_24)
                    it.visibility = View.GONE
                }


                var mainActivity: MainActivity = binding.root.context as MainActivity

                binding.photo.setOnClickListener {
                    if (onClickListener != null) {
                        onClickListener!!.onClick(
                            position,
                            binding,
                            surveyDataItem
                        )
                    }
                }
            }


            surveyDataItem.type == TYPE_SINGLE_CHOICE -> {
                binding = holder.binding.typeSingleChoice as TypeSingleChoiceBinding
                binding.root.visibility = View.VISIBLE;

                binding.heading.text = surveyDataItem.title

                for (i in 0 until 3) {
                    val optionBinding = when (i) {
                        0 -> binding.option1
                        1 -> binding.option2
                        2 -> binding.option3
                        else -> null
                    }

                    optionBinding?.apply {
                        text =
                            if (i < surveyDataItem.dataMap.options.size) surveyDataItem.dataMap.options[i] else ""
                        visibility =
                            if (i < surveyDataItem.dataMap.options.size) View.VISIBLE else View.GONE
                    }
                   (hashMap[surveyDataItem.id]).let {
                       if(optionBinding?.text?.trim()==it?.trim())
                       optionBinding?.isChecked =true
                   }
                }

                binding.radioGroup.setOnCheckedChangeListener(object :RadioGroup.OnCheckedChangeListener{
                    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {

                            enterDataInHashMap(surveyDataItem.id,(group?.findViewById(checkedId) as RadioButton).text.toString() )

                    }
                })
            }

            surveyDataItem.type == TYPE_COMMENT_CHOICE -> {
                binding = holder.binding.typeComment as TypeCommentBinding
                binding.root.visibility = View.VISIBLE;

                binding.heading.text = surveyDataItem.title

                (hashMap[surveyDataItem.id]).let {
                    if(it!=null) {
                        binding.switch1.isChecked = true
                        binding.ETLayout.visibility = View.VISIBLE;
                        binding.editText.text = Editable.Factory.getInstance().newEditable(it);
                    }
                }

                binding.switch1.setOnClickListener {


                    binding.ETLayout.visibility =
                        if ((it as SwitchCompat).isChecked) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                }


                binding.editText.doAfterTextChanged {
                    enterDataInHashMap(surveyDataItem.id,it.toString() )

                }

            }
        }

    }


    fun ShowDialog(context: Context) {
        val photoBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        var dialogBinding = PopupEditPhotoBinding.inflate(LayoutInflater.from(context))



        dialogBinding.cameraImageView.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(it.context as Activity, cameraIntent, CAMERA_PICK, null)
        }
        dialogBinding.galleryImageView.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*" //set type for files (image type)

            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                it.context as Activity,
                Intent.createChooser(intent, "Select Picture"),
                PICK_FROM_GALLERY,
                null
            )

        }





        photoBuilder.setView(dialogBinding.root)
        var alertDialog = photoBuilder.create()
        alertDialog.show()
    }


    override fun getItemCount(): Int {
        return surveyData.size
    }



    fun enterDataInHashMap(key:String,value:String)
    {
        if(hashMap.containsKey(key))
        {
            hashMap.set(key, value)
        }
        else
        {
            hashMap.put(key, value)
        }
    }

    /* fun filter(query: String) {
         filteredList = appList .filter { it.app_name.startsWith(query, ignoreCase = true) } as ArrayList<App>
         notifyDataSetChanged()
     }
 */
}
package com.nasa.customizablesurveyapp.ui.fragment


import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.icu.text.CaseMap.Title
import android.os.Bundle
import android.transition.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nasa.customizablesurveyapp.databinding.FragmentUserDetailsBinding

/**
 * A simple [Fragment] subclass.
 * Use the [UserDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserDetailsFragment : Fragment() {

    lateinit var binding: FragmentUserDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserDetailsBinding.inflate(inflater, container, false)

        setViewProperties()

        return binding.root
    }


    private fun setViewProperties() {
        try {

            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * This function will load the image into imageview using glide library and also transform the image to circle using the CircleTranform method
     *
     * @param mContext  -- Context of activity
     * @param resId     -- Int ( Image Resource Id)
     * @param imageView -- Imageview to which image is to be loaded
     */
    fun LoadCircleImageIntoImageView(mContext: Context, imageView: ImageView?) {
        try {
            // set shape of the image and divider line
            // set oval shape of the image stroke border
            val gradientDrawable = GradientDrawable()
            // SHape == 1 means Oval Shape
            gradientDrawable.shape = GradientDrawable.OVAL
            gradientDrawable.setStroke(2, Color.parseColor("#737373"))
            imageView!!.setBackground(gradientDrawable)
            (mContext as Activity).runOnUiThread {
                // using glide library to set image into image view
                Glide.with(mContext)
                    .load("url")
                    .circleCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(
                        imageView
                    )
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onDetach() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.onDetach()
    }
}
























